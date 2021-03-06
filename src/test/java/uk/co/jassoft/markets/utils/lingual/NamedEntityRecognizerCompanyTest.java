/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.jassoft.markets.utils.lingual;

import uk.co.jassoft.markets.datamodel.exclusion.Exclusion;
import uk.co.jassoft.markets.datamodel.story.NamedEntities;
import uk.co.jassoft.markets.datamodel.story.NamedEntity;
import uk.co.jassoft.markets.repository.ExclusionRepository;
import junit.framework.TestCase;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Jonny
 */
public class NamedEntityRecognizerCompanyTest extends TestCase
{
    private final String sampleText = "Apple (ticker symbol: AAPL) is one of the world's leading consumer electronics and personal computer companies. The Cupertino, California-based company was established in 1977 as Apple Computer Inc. It dropped the \"Computer\" from its name in early 2007.\n" +
                            "\n" +
                            "Apple's current place in the global marketplace is a far cry from its humble beginnings, with Steve Jobs, Steve Wozniak and Ronald Wayne selling hand-made personal computer kits in the late '70s. The company continued to focus on personal computers for the following decades, but in recent years that focus has shifted more to consumer electronics such as the iPhone, iPad and iPod. However, Apple also sells a range of related software, services and applications, with some of the most prominent non-electronics products being the iCloud, iOS, Mac OS and Apple TV. In addition, the company sells and delivers digital applications and software through its iTunes Store, App Store, iBookstore and Mac App Store.\n" +
                            "\n" +
                            "Apple has remained focused on developing its own hardware, software, operating systems and services to provide its customers with the best user experience possible. A significant fraction of the company's efforts also go toward marketing and advertising as it believes such efforts are essential to the development and sale of its products.\n" +
                            "\n" +
                            "The company - the principal executive offices of which are located at 1 Infinite Loop, Cupertino, California 95014 - has retail stores around the world, with more than 300 locations as of 2012. Apple has five reportable operating segments: Americas, Europe, Japan, Asia-Pacific and Retail. The geographic-based segments do not include the results of the Retail segment.\n" +
                            "\n" +
                            "Despite Apple's market-leading position, the company still faces a number of risk factors, which include changing global economic conditions, fluctuating consumer demand, worldwide-competition and potential supply chain disruptions.";

    private NamedEntityRecognizer companyEntityRecognizer;

    public NamedEntityRecognizerCompanyTest(String testName)
    {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        ExclusionRepository exclusionRepository = Mockito.mock(ExclusionRepository.class);

        Mockito.when(exclusionRepository.findByName(Mockito.anyString())).thenReturn(new ArrayList<Exclusion>());

        companyEntityRecognizer = new NamedEntityRecognizer("StanfordCoreNLP_Company");
        companyEntityRecognizer.setExclusionRepository(exclusionRepository);
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testCompanyAnalysisPicksUpOrganisationsInText() throws IOException
    {
        NamedEntities entities = companyEntityRecognizer.analyseCompany(sampleText);

        assertEquals(11, entities.getOrganisations().size());

        assertFalse(entities.getOrganisations().contains(new NamedEntity("Mac")));
        assertTrue(entities.getOrganisations().contains(new NamedEntity("Apple")));
        assertTrue(entities.getOrganisations().contains(new NamedEntity("Apple Computer")));
        assertTrue(entities.getOrganisations().contains(new NamedEntity("Apple Computer Inc")));
    }

    public void testCompanyAnalysisPicksUpLocationsInText() throws IOException
    {
        NamedEntities entities = companyEntityRecognizer.analyseCompany(sampleText);

        assertEquals(5, entities.getLocations().size());

        assertTrue(entities.getLocations().contains(new NamedEntity("Cupertino")));
    }

    public void testCompanyAnalysisPicksUpPeopleInText() throws IOException
    {
        NamedEntities entities = companyEntityRecognizer.analyseCompany(sampleText);

        assertEquals(3, entities.getPeople().size());

        assertFalse(entities.getPeople().contains(new NamedEntity("Steve")));
        assertTrue(entities.getPeople().contains(new NamedEntity("Steve Jobs")));
    }

    public void testCompanyAnalysisPicksUpMiscNamesInText() throws IOException
    {
        NamedEntities entities = companyEntityRecognizer.analyseCompany(sampleText);

        assertEquals(1, entities.getMisc().size());

        assertTrue(entities.getMisc().contains(new NamedEntity("California-based")));
    }

    public void testCompanyAnalysisDoesNotDetectSentimentInText() throws IOException
    {
        NamedEntities entities = companyEntityRecognizer.analyseCompany(sampleText);

        for(NamedEntity entity : entities.getOrganisations())
        {
            assertTrue(entity.getSentiments().isEmpty());
        }
    }

    public void testCompanyAnalysisGroupsNeighbouringNamesAsTheSame() throws IOException
    {
        NamedEntities entities = companyEntityRecognizer.analyseCompany("Apple's current place in the global marketplace is a far cry from its humble beginnings, with Steve Jobs, Steve Wozniak and Ronald Wayne selling hand-made personal computer kits in the late '70s.");

        assertEquals(3, entities.getPeople().size()); // Steve, Steve Jobs, Steve Wozniak, Ronald, Ronald Wayne

        assertTrue(entities.getPeople().contains(new NamedEntity("Steve Jobs")));
        assertTrue(entities.getPeople().contains(new NamedEntity("Steve Wozniak")));
        assertTrue(entities.getPeople().contains(new NamedEntity("Ronald Wayne")));
    }
}
