package uk.co.jassoft.markets.utils;

import uk.co.jassoft.markets.datamodel.Direction;
import uk.co.jassoft.markets.datamodel.company.sentiment.EntitySentiment;
import uk.co.jassoft.markets.datamodel.company.sentiment.StorySentiment;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by jonshaw on 03/09/15.
 */
public class SentimentUtilTest {

    @Test
    public void testGetPreviousSentimentDirection_measuresNegativeTrend() throws Exception {

        List<StorySentiment> storySentimentList = new ArrayList<>();

        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(6).toDate(), 100));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(5).toDate(), 95));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(4).toDate(), 90));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(3).toDate(), 85));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(2).toDate(), 95));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(1).toDate(), 85));
        storySentimentList.add(generateStorySentiment(new DateTime().toDate(), 100));

        Direction resultDirection = SentimentUtil.getPreviousSentimentDirection(storySentimentList, new DateTime().toDate());

        assertEquals(Direction.Down, resultDirection);
    }

    @Test
    public void testGetPreviousSentimentDirection_measuresPositiveTrend() throws Exception {

        List<StorySentiment> storySentimentList = new ArrayList<>();

        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(6).toDate(), 50));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(5).toDate(), 51));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(4).toDate(), 60));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(3).toDate(), 53));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(2).toDate(), 65));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(1).toDate(), 63));
        storySentimentList.add(generateStorySentiment(new DateTime().toDate(), 61));

        Direction resultDirection = SentimentUtil.getPreviousSentimentDirection(storySentimentList, new DateTime().toDate());

        assertEquals(Direction.Up, resultDirection);
    }

    @Test
    public void testGetPreviousSentimentDirection_withIntermittentData() throws Exception {

        List<StorySentiment> storySentimentList = new ArrayList<>();

        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(6).toDate(), 50));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(4).toDate(), 60));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(2).toDate(), 65));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(1).toDate(), 63));
        storySentimentList.add(generateStorySentiment(new DateTime().minusMinutes(1).toDate(), 61));

        Direction resultDirection = SentimentUtil.getPreviousSentimentDirection(storySentimentList, new DateTime().toDate());

        assertEquals(Direction.Up, resultDirection);
    }

    private StorySentiment generateStorySentiment(Date date, Integer sentiment) {
        ArrayList<EntitySentiment> entitySentiments = new ArrayList<>();
        entitySentiments.add(new EntitySentiment("Name", sentiment));

        return new StorySentiment(null, date, "SampleStoryId", entitySentiments);
    }

    @Test
    public void testGetLastSentimentDifferenceFromAverage_measuresPositiveDifference() throws Exception {
        List<StorySentiment> storySentimentList = new ArrayList<>();

        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(6).toDate(), 100));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(5).toDate(), 95));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(4).toDate(), 90));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(3).toDate(), 85));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(2).toDate(), 95));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(1).toDate(), 85));
        storySentimentList.add(generateStorySentiment(new DateTime().toDate(), 100));

        double difference = SentimentUtil.getLastSentimentDifferenceFromAverage(storySentimentList, new DateTime().toDate());

        assertEquals(8.333333333333329, difference, 2);
    }

    @Test
    public void testGetLastSentimentDifferenceFromAverage_measuresNegativeDifference() throws Exception {
        List<StorySentiment> storySentimentList = new ArrayList<>();

        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(6).toDate(), -100));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(5).toDate(), -95));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(4).toDate(), -90));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(3).toDate(), -85));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(2).toDate(), -95));
        storySentimentList.add(generateStorySentiment(new DateTime().minusDays(1).toDate(), -85));
        storySentimentList.add(generateStorySentiment(new DateTime().toDate(), -250));

        double difference = SentimentUtil.getLastSentimentDifferenceFromAverage(storySentimentList, new DateTime().toDate());

        assertEquals(-158.33333333333331, difference, 2);
    }
}