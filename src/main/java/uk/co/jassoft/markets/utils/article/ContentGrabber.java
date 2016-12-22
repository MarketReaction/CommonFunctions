/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.jassoft.markets.utils.article;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.jassoft.markets.datamodel.story.date.MissingDateFormat;
import uk.co.jassoft.markets.exceptions.article.ArticleContentException;
import uk.co.jassoft.markets.repository.MissingDateFormatRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Jonny
 */
@Component
public class ContentGrabber {

    private static final Logger LOG = LoggerFactory.getLogger(ContentGrabber.class);

    @Autowired
    private MissingDateFormatRepository missingDateFormatRepository;

    private static final Pattern TITLE_TAG =
            Pattern.compile("\\<title>(.*)\\</title>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    public String getContentFromWebsite(String html) throws ArticleContentException {
        try {
            return ArticleExtractor.INSTANCE.getText(html);
        } catch (Exception exception) {
            throw new ArticleContentException("Failed to get content from website", exception);
        }
    }

    public String geTitleFromWebsite(String html) {
        try {
            // extract the title
            Matcher matcher = TITLE_TAG.matcher(html);
            if (matcher.find()) {
                /* replace any occurrences of whitespace (which may
                 * include line feeds and other uglies) as well
                 * as HTML brackets with a space */
                return matcher.group(1).replaceAll("[\\s\\<>]+", " ").trim();
            } else
                return null;
        } catch (Exception exception) {
            LOG.error("Failed to get title from website", exception);
        }

        return null;
    }

    public Date getPublishedDate(String html) {
        try {
            Document doc = Jsoup.parse(html);

            List<Date> possibleDates = new ArrayList<>();

            for (String selector : getSelectors()) {
                Elements metalinks = doc.select(selector);

                if (metalinks.isEmpty())
                    continue;

                int initialSize = possibleDates.size();

                for (int i = 0; i < metalinks.size(); i++) {
                    for (String attribute : getAttributes()) {
                        String contents = metalinks.get(i).attributes().get(attribute);

                        if (contents.isEmpty()) {
                            continue;
                        }

                        Date value = getDateValue(contents);

                        if (value != null)
                            possibleDates.add(value);

                    }
                    Date value = getDateValue(metalinks.get(i).html());

                    if (value != null)
                        possibleDates.add(value);
                }

                if(possibleDates.size() == initialSize) {
                    LOG.info("Date Format Not recognised for [{}]", metalinks.get(0).toString());
                    missingDateFormatRepository.save(new MissingDateFormat(metalinks.get(0).toString(), new Date()));
                }
            }

            if(!possibleDates.isEmpty()) {
                if(possibleDates.size() > 1) {
                    possibleDates.sort(Date::compareTo);
                }
                return possibleDates.get(possibleDates.size() -1);
            }

            return null;
        } catch (Exception exception) {
            LOG.error("Failed to get Published Date", exception);
            return null;
        }
    }

    private Date getDateValue(final String contentsToCheck) {
        if (contentsToCheck.isEmpty())
            return null;

        Parser parser = new Parser();
        List<DateGroup> groups = parser.parse(contentsToCheck);

        Date possibleDate = null;
        boolean confirmedDate = false;
        boolean confirmedTime = false;

        for (DateGroup group : groups) {
            List<Date> dates = group.getDates();

            for (Date publishedDate : dates) {
                if (new DateTime(DateTimeZone.UTC).plusDays(1).isBefore(publishedDate.getTime())) {
                    LOG.debug("Date is over 1 day in the future [{}]", publishedDate.toString());
                    continue;
                }

                if(!group.isDateInferred()) {
                    confirmedDate = true;
                }

                if(!group.isTimeInferred()) {
                    confirmedTime = true;
                }

                if (possibleDate == null) {
                    possibleDate = publishedDate;

                    if (group.isTimeInferred()) {
                        possibleDate = new DateTime(publishedDate).withTime(0, 0, 0, 0).toDate();
                    }
                    continue;
                }

                DateTime latestPublishedDate = new DateTime(publishedDate);

                if (!group.isTimeInferred()) {
                    possibleDate = new DateTime(possibleDate).withTime(latestPublishedDate.getHourOfDay(), latestPublishedDate.getMinuteOfHour(), latestPublishedDate.getSecondOfMinute(), latestPublishedDate.getMillisOfSecond()).toDate();
                }

                if (!group.isDateInferred()) {
                    possibleDate = new DateTime(possibleDate).withDate(latestPublishedDate.getYear(), latestPublishedDate.getMonthOfYear(), latestPublishedDate.getDayOfMonth()).toDate();
                }
            }
        }

        if (possibleDate != null && confirmedDate && confirmedTime) {
            return possibleDate;
        }

        return null;
    }

    private List<String> getSelectors() {
        List<String> selectors = new ArrayList<>();

        selectors.add("date");
        selectors.add("time");
        selectors.add("meta[name*=date]");
        selectors.add("meta[name*=time]");
        selectors.add("meta[itemprop*=date]");

        return selectors;
    }

    private List<String> getAttributes() {
        List<String> attribute = new ArrayList<>();

        attribute.add("content");
        attribute.add("datetime");

        return attribute;
    }
}
