package uk.co.jassoft.markets.utils;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.jassoft.markets.datamodel.Direction;
import uk.co.jassoft.markets.datamodel.company.sentiment.SentimentByDate;
import uk.co.jassoft.markets.datamodel.company.sentiment.StorySentiment;
import uk.co.jassoft.markets.exceptions.sentiment.SentimentCalculationException;
import uk.co.jassoft.markets.exceptions.sentiment.SentimentDifferenceCalculationException;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by jonshaw on 03/09/15.
 */
public class SentimentUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SentimentUtil.class);

    public static Direction getPreviousSentimentDirection(List<StorySentiment> storySentiments, Date latestQuoteDate) throws Exception {

        List<Integer> sentiments = getSentiments(storySentiments, latestQuoteDate);

        if(sentiments.isEmpty()) {
            throw new SentimentCalculationException(String.format("Not enough data to determine Sentiment direction - [No Sentiment Data] Number of story Sentiments before filtering [%s]", storySentiments.size()));
        }

        List<Double> trend = new ArrayList<>();

        int N = 3;
        double[] a = new double[N];
        double sum = 0.0;
        for (int i = 0; i < sentiments.size(); i++) {
            sum -= a[i % N];
            a[i % N] = sentiments.get(i);
            sum += a[i % N];
            if (i >= N) {
                trend.add(sum / N);
            }
        }

        if(trend.isEmpty()) {
            throw new SentimentCalculationException(String.format("Not enough data to determine Sentiment direction - [Not enough data to calculate Trend] Sentiments [%s] TrendSize [%s]", sentiments.size(), trend.size()));
        }

        if(trend.get(0) < trend.get(trend.size() -1))
            return Direction.Up;

        if(trend.get(0) == trend.get(trend.size() -1))
            return Direction.None;

        if(trend.get(0) > trend.get(trend.size() -1))
            return Direction.Down;

        throw new SentimentCalculationException("Could not determine Sentiment direction");
    }

    public static double getLastSentimentDifferenceFromAverage(List<StorySentiment> storySentiments, Date latestQuoteDate) throws Exception {
        List<Integer> sentiments = getSentiments(storySentiments, latestQuoteDate);

        if(sentiments.isEmpty()) {
            throw new SentimentCalculationException("Not enough data to determine Sentiment direction - [No Sentiment Data]");
        }

        int lastSentiment = getLastSentiment(storySentiments);

        OptionalDouble average = sentiments.stream().mapToInt(i -> i).average(); // 100

        if(average.isPresent())
            return lastSentiment - average.getAsDouble();

        throw new SentimentDifferenceCalculationException("Could not determine Sentiment difference from average");
    }

    private static List<Integer> getSentiments(List<StorySentiment> storySentiments, Date latestQuoteDate) {
        return storySentiments.stream()
                .filter(inLastDays(7))
                .filter(isBeforeDate(latestQuoteDate))
                .sorted((s1, s2) -> s1.getStoryDate().compareTo(s2.getStoryDate()))
                .map(storySentiment -> {
                    return new SentimentByDate(storySentiment.getStoryDate(), storySentiment.getEntitySentiment().stream().collect(Collectors.summingInt(value ->
                    {
                        if (value == null) {
                            LOG.warn("StorySentiment [{}] has null EntitySentiment value", storySentiment.getId());
                            return 0;
                        }
                        return value.getSentiment();
                    }
                    )));
                })
                .collect(Collectors.groupingBy(sentimentByDate -> DateUtils.truncate(sentimentByDate.getDate(), Calendar.DATE), Collectors.summingInt(value1 -> value1.getSentiment())))
                .entrySet().stream().sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey())).map(dateIntegerEntry -> dateIntegerEntry.getValue()).collect(Collectors.toList());
    }

    public static Integer getLastSentiment(List<StorySentiment> storySentiments) {
        List<Integer> sentiments = storySentiments.stream()
                .filter(inLastDays(7))
                .sorted((s1, s2) -> s1.getStoryDate().compareTo(s2.getStoryDate()))
                .map(storySentiment -> new SentimentByDate(storySentiment.getStoryDate(), storySentiment.getEntitySentiment().stream().collect(Collectors.summingInt(value -> value.getSentiment()))))
                .collect(Collectors.groupingBy(sentimentByDate -> DateUtils.truncate(sentimentByDate.getDate(), Calendar.DATE), Collectors.summingInt(value1 -> value1.getSentiment())))
                .entrySet().stream().sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey())).map(dateIntegerEntry -> dateIntegerEntry.getValue()).collect(Collectors.toList());

        return sentiments.get(sentiments.size() -1);
    }

    public static Predicate<StorySentiment> inLastDays(int days) {
        return s -> new DateTime().minusDays(days).isBefore(s.getStoryDate().getTime());
    }

    public static Predicate<StorySentiment> isBeforeDate(Date date) {
        return s -> s.getStoryDate().before(date);
    }

}
