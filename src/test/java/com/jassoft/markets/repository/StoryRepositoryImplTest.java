package com.jassoft.markets.repository;

import com.jassoft.markets.datamodel.story.Story;
import com.jassoft.markets.datamodel.story.StoryBuilder;
import com.jassoft.markets.datamodel.story.metric.MetricBuilder;
import com.jassoft.utils.BaseRepositoryTest;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by jonshaw on 15/03/2016.
 */
public class StoryRepositoryImplTest extends BaseRepositoryTest {

    private StoryRepository storyRepository;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        storyRepository = new StoryRepositoryImpl(getMongoRepositoryFactory(), getTemplate());

        loadTestData();
    }

    @After
    public void tearDown() throws Exception {
        getTemplate().dropCollection(Story.class);
    }

    private void loadTestData() {
        storyRepository.save(new StoryBuilder()
                .setMetrics(Arrays.asList(MetricBuilder.aSentimentMetric().withEnd(new DateTime(2016,2,1,2,55,0).toDate()).build())
                ).createStory());
        storyRepository.save(new StoryBuilder().setDatePublished(new DateTime(2016,2,1,2,45,0).toDate())
                .setMetrics(Arrays.asList(MetricBuilder.aSentimentMetric().withEnd(new DateTime(2016,2,1,2,55,0).toDate()).build())
                ).createStory());
        storyRepository.save(new StoryBuilder().setDatePublished(new DateTime(2016,2,1,3,20,0).toDate())
                .setMetrics(Arrays.asList(MetricBuilder.aSentimentMetric().withEnd(new DateTime(2016,2,1,3,34,0).toDate()).build())
                ).createStory());
        storyRepository.save(new StoryBuilder().setDatePublished(new DateTime(2016,2,1,4,14,0).toDate())
                .setMetrics(Arrays.asList(MetricBuilder.aSentimentMetric().withEnd(new DateTime(2016,2,1,4,55,0).toDate()).build())
                ).createStory());
        storyRepository.save(new StoryBuilder().setDatePublished(new DateTime(2016,2,1,5,38,0).toDate())
                .setMetrics(Arrays.asList(MetricBuilder.aSentimentMetric().withEnd(new DateTime(2016,2,1,5,55,0).toDate()).build())
                ).createStory());
        storyRepository.save(new StoryBuilder().setDatePublished(new DateTime(2016,2,2,6,55,0).toDate())
                .setMetrics(Arrays.asList(MetricBuilder.aSentimentMetric().withEnd(new DateTime(2016,2,2,6,56,0).toDate()).build())
                ).createStory());
        storyRepository.save(new StoryBuilder().setDatePublished(new DateTime(2016,2,2,7,29,0).toDate())
                .setMetrics(Arrays.asList(MetricBuilder.aSentimentMetric().withEnd(new DateTime(2016,2,2,7,55,0).toDate()).build())
                ).createStory());
        storyRepository.save(new StoryBuilder().setDatePublished(new DateTime(2016,2,2,8,33,0).toDate())
                .setMetrics(Arrays.asList(MetricBuilder.aSentimentMetric().withEnd(new DateTime(2016,2,2,8,44,0).toDate()).build())
                ).createStory());
        storyRepository.save(new StoryBuilder().setDatePublished(new DateTime(2016,2,2,9,45,0).toDate())
                .setMetrics(Arrays.asList(MetricBuilder.aSentimentMetric().withEnd(new DateTime(2016,2,2,9,55,0).toDate()).build())
                ).createStory());
        storyRepository.save(new StoryBuilder().setDatePublished(new DateTime(2016,2,2,10,46,0).toDate())
                .setMetrics(Arrays.asList(MetricBuilder.aSentimentMetric().withEnd(new DateTime(2016,2,2,10,55,0).toDate()).build())
                ).createStory());
        storyRepository.save(new StoryBuilder().setDatePublished(new DateTime(2016,2,2,11,33,0).toDate())
                .setMetrics(Arrays.asList(MetricBuilder.aSentimentMetric().withEnd(new DateTime(2016,2,2,11,55,0).toDate()).build())
                ).createStory());
        storyRepository.save(new StoryBuilder().setDatePublished(new DateTime(2016,2,3,12,49,0).toDate())
                .setMetrics(Arrays.asList(MetricBuilder.aSentimentMetric().withEnd(new DateTime(2016,2,3,12,55,0).toDate()).build())
                ).createStory());
        storyRepository.save(new StoryBuilder().setDatePublished(new DateTime(2016,2,3,13,23,0).toDate())
                .setMetrics(Arrays.asList(MetricBuilder.aSentimentMetric().withEnd(new DateTime(2016,2,3,13,55,0).toDate()).build())
                ).createStory());
    }

    @Test
    public void testFindByParentSource() throws Exception {

    }

    @Test
    public void testFindByMatchedCompaniesIn() throws Exception {

    }

    @Test
    public void testFindByCompaniesBetweenDates() throws Exception {

    }

    @Test
    public void testFindOneByUrl() throws Exception {

    }

    @Test
    public void testGetStoryCountPerDay() throws Exception {
        List<Pair<Date, Integer>> storyCount = storyRepository.getStoryCountPerDay(new DateTime(2016,2,1,0,0,0).toDate(), new DateTime(2016,2,4,0,0,0).toDate());

        assertEquals(3, storyCount.size());
        assertEquals(new DateTime(2016,2,1,0,0,0).toDate(), storyCount.get(0).getKey());
        assertEquals(4, storyCount.get(0).getValue().intValue());

        assertEquals(new DateTime(2016,2,2,0,0,0).toDate(), storyCount.get(1).getKey());
        assertEquals(6, storyCount.get(1).getValue().intValue());

        assertEquals(new DateTime(2016,2,3,0,0,0).toDate(), storyCount.get(2).getKey());
        assertEquals(2, storyCount.get(2).getValue().intValue());
    }

    @Test
    public void testGetStoryProcessingTimes() throws Exception {
        List<Triple<String, Date, Long>> processingTimes = storyRepository.getStoryProcessingTimes(new DateTime(2016,2,1,0,0,0).toDate(), new DateTime(2016,2,4,0,0,0).toDate());

        assertEquals(12, processingTimes.size());

        for(Triple<String, Date, Long> processingTime : processingTimes) {
            assertTrue(processingTime.getRight() > 0);
        }
    }

    @Test
    public void testFindMetricsBetweenDates() throws Exception {
        List<Story> stories = storyRepository.findMetricsBetweenDates(new DateTime(2016,2,1,0,0,0).toDate(), new DateTime(2016,2,4,0,0,0).toDate());

        assertEquals(12, stories.size());
    }
}