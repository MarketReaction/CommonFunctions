package com.jassoft.markets.repository;

import com.jassoft.markets.datamodel.sources.Source;
import com.jassoft.markets.datamodel.sources.SourceType;
import com.jassoft.markets.datamodel.sources.SourceUrl;
import com.jassoft.utils.BaseRepositoryTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by jonshaw on 20/08/15.
 */
public class SourceRepositoryImplTest extends BaseRepositoryTest {

    private SourceRepository sourceRepository;
    String testSourceId;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        sourceRepository = new SourceRepositoryImpl(getMongoRepositoryFactory(), getTemplate());

        loadTestData();
    }

    @After
    public void tearDown() throws Exception {
        getTemplate().dropCollection(Source.class);
    }

    private void loadTestData() {
        Source source = new Source("Test Source", new SourceUrl("http://bbc.co.uk"), SourceType.Crawler, null, null, 0);
        source.setDisabled(false);
        testSourceId = sourceRepository.save(source).getId();
    }

    @Test
    public void testPushExclusion() throws Exception {
        Source source = sourceRepository.findOne(testSourceId);

        assertTrue(source.getExclusionList().isEmpty());

        sourceRepository.pushExclusion(testSourceId, "TestExclusion");

        Source resultSource = sourceRepository.findOne(testSourceId);

        assertFalse(resultSource.getExclusionList().isEmpty());
        assertEquals(1, resultSource.getExclusionList().size());
        assertEquals("TestExclusion", resultSource.getExclusionList().get(0));
    }

    @Test
    public void testPushUrl() throws Exception {
        Source source = sourceRepository.findOne(testSourceId);

        assertEquals(1, source.getUrls().size());

        sourceRepository.pushUrl(testSourceId, "TestUrl");

        Source resultSource = sourceRepository.findOne(testSourceId);
        assertEquals(2, resultSource.getUrls().size());
    }

    @Test
    public void testPullExclusion() throws Exception {
        Source source = sourceRepository.findOne(testSourceId);

        assertTrue(source.getExclusionList().isEmpty());

        sourceRepository.pushExclusion(testSourceId, "TestExclusion");

        Source resultSource = sourceRepository.findOne(testSourceId);

        assertFalse(resultSource.getExclusionList().isEmpty());
        assertEquals(1, resultSource.getExclusionList().size());
        assertEquals("TestExclusion", resultSource.getExclusionList().get(0));

        sourceRepository.pullExclusion(testSourceId, "TestExclusion");

        Source resultResultSource = sourceRepository.findOne(testSourceId);
        assertTrue(resultResultSource.getExclusionList().isEmpty());

    }

    @Test
    public void testPullUrl() throws Exception {
        Source source = sourceRepository.findOne(testSourceId);

        assertEquals(1, source.getUrls().size());

        sourceRepository.pushUrl(testSourceId, "TestUrl");

        Source resultSource = sourceRepository.findOne(testSourceId);
        assertEquals(2, resultSource.getUrls().size());

        sourceRepository.pullUrl(testSourceId, "TestUrl");

        Source resultResultSource = sourceRepository.findOne(testSourceId);

        assertEquals(1, resultResultSource.getUrls().size());
    }

    @Test
    public void testFindByTypeAndDisabled() throws Exception {
        List<Source> sources = sourceRepository.findByTypeAndDisabled(SourceType.Crawler, false);

        assertEquals(1, sources.size());
    }
}