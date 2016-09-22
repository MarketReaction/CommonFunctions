/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jassoft.markets.utils.article;

import com.jassoft.markets.datamodel.story.date.DateFormat;
import com.jassoft.markets.datamodel.story.date.MissingDateFormat;
import com.jassoft.markets.exceptions.article.ArticleContentException;
import com.jassoft.markets.repository.DateFormatRepository;
import com.jassoft.markets.repository.MissingDateFormatRepository;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private DateFormatRepository dateFormatRepository;

    @Autowired
    private MissingDateFormatRepository missingDateFormatRepository;

    private static final Pattern TITLE_TAG =
            Pattern.compile("\\<title>(.*)\\</title>", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);

    public void setDateFormatRepository(DateFormatRepository dateFormatRepository) {
        this.dateFormatRepository = dateFormatRepository;
    }

    public String getContentFromWebsite(String html) throws ArticleContentException
    {
        try
        {
            return ArticleExtractor.INSTANCE.getText(html);
        }
        catch (Exception exception)
        {
            throw new ArticleContentException("Failed to get content from website", exception);
        }
    }

    public String geTitleFromWebsite(String html)
    {
        try
        {
            // extract the title
            Matcher matcher = TITLE_TAG.matcher(html);
            if (matcher.find()) {
                /* replace any occurrences of whitespace (which may
                 * include line feeds and other uglies) as well
                 * as HTML brackets with a space */
                return matcher.group(1).replaceAll("[\\s\\<>]+", " ").trim();
            }
            else
                return null;
        }
        catch (Exception exception)
        {
            LOG.error("Failed to get title from website", exception);
        }

        return null;
    }

    public Date getPublishedDate(String html)
    {
        try
        {
            Document doc = Jsoup.parse(html);
            
            for(String selector : getSelectors())
            {
                Elements metalinks = doc.select(selector);

                if(metalinks.isEmpty())
                    continue;

                for(int i=0; i < metalinks.size(); i++) {
                    for (String attribute : getAttributes()) {
                        String contents = metalinks.get(i).attributes().get(attribute);

                        Date value = getDateValue(contents);

                        if(value != null)
                            return value;

                    }
                    Date value = getDateValue(metalinks.get(i).html());

                    if(value != null)
                        return value;
                }

                List<MissingDateFormat> missingDateFormats = missingDateFormatRepository.findByMetatag(metalinks.get(0).toString());

                if(missingDateFormats.isEmpty()) {
                    LOG.info("Date Format Not recognised for [{}]", metalinks.get(0).toString());
                    missingDateFormatRepository.save(new MissingDateFormat(metalinks.get(0).toString()));
                }
            }
            
            return null;
        }
        catch(Exception exception)
        {
            LOG.error("Failed to get Published Date", exception);
            return null;
        }
    }

    private Date getDateValue(final String contentsToCheck) {
        if (contentsToCheck.isEmpty())
            return null;

        final String cleanedContent = clean(contentsToCheck);

        for (String dateFormat : getDateFormats()) {
            try {
                Date publishedDate = new SimpleDateFormat(dateFormat).parse(cleanedContent);

                if (new DateTime(DateTimeZone.UTC).plusDays(1).isBefore(publishedDate.getTime())) {
                    LOG.debug("Date is over 1 day in the future [{}]", publishedDate.toString());
                    continue;
                }

                return publishedDate;
            } catch (ParseException ignored) {
            }
        }

//        for (Locale locale : Locale.getAvailableLocales()) {
//            try {
//
//                Date publishedDate = new SimpleDateFormat(LocaleProviderAdapter.getResourceBundleBased().getLocaleResources(locale)
//                        .getDateTimePattern(3, 3, null), locale).parse(cleanedContent);
//
//                if (new DateTime(DateTimeZone.UTC).plusDays(1).isBefore(publishedDate.getTime())) {
//                    LOG.debug("Date is over 1 day in the future [{}]", publishedDate.toString());
//                    continue;
//                }
//
//                return publishedDate;
//
//            } catch (ParseException ig\nored) {
//            }
//        }

        return null;
    }
    
    private List<String> getSelectors()
    {
        List<String> selectors = new ArrayList<>();
        
        selectors.add("date");
        selectors.add("time");
        selectors.add("meta[name*=date]");
        selectors.add("meta[name*=time]");
        selectors.add("meta[itemprop*=date]");

        return selectors;
    }
    
    private List<String> getAttributes()
    {
        List<String> attribute = new ArrayList<>();
        
        attribute.add("datetime");
        attribute.add("content");
        
        return attribute;
    }
    
    private List<String> getDateFormats()
    {
        List<String> formats = new ArrayList<>();

        for(DateFormat format : dateFormatRepository.findAll())
        {
            formats.add(format.getFormat());
        }

        return formats;
    }

    private String clean(final String contentToClean) {
        String cleanedContent = contentToClean.trim();

        if (cleanedContent.contains("p.m.")) {
            cleanedContent = cleanedContent.replace("p.m.", "PM");
        }

        if (cleanedContent.contains("a.m.")) {
            cleanedContent = cleanedContent.replace("a.m.", "AM");
        }

        if (cleanedContent.contains(".")) {
            cleanedContent = cleanedContent.replace(".", "");
        }

        if (cleanedContent.contains(",")) {
            cleanedContent = cleanedContent.replace(",", "");
        }

        if (cleanedContent.contains("Updated ")) {
            cleanedContent = cleanedContent.replace("Updated ", "");
        }

        return cleanedContent;
    }
}
