package uk.co.jassoft.markets.repository;

import uk.co.jassoft.markets.datamodel.company.Company;
import uk.co.jassoft.markets.datamodel.company.CompanyBuilder;
import uk.co.jassoft.markets.datamodel.company.CompanyCount;
import uk.co.jassoft.markets.datamodel.story.NameCount;
import uk.co.jassoft.markets.datamodel.story.NamedEntitiesBuilder;
import uk.co.jassoft.markets.datamodel.story.NamedEntityBuilder;
import uk.co.jassoft.markets.datamodel.story.StoryBuilder;
import uk.co.jassoft.utils.BaseRepositoryTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by jonshaw on 19/08/15.
 */
public class CompanyRepositoryImplTest extends BaseRepositoryTest {

    private CompanyRepository companyRepository;
    private StoryRepository storyRepository;
    private String exchangeId;
    private String appleId;
    private String amazonId;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        companyRepository = new CompanyRepositoryImpl(getMongoRepositoryFactory(), getTemplate());
        storyRepository = new StoryRepositoryImpl(getMongoRepositoryFactory(), getTemplate());

        loadTestData();
    }

    @After
    public void tearDown() throws Exception {
        getTemplate().dropCollection(Company.class);
    }

    private void loadTestData() {
        exchangeId = UUID.randomUUID().toString();

        appleId = companyRepository.save(CompanyBuilder.aCompany().withTickerSymbol("AAPL")
                .withName("Apple")
                .withExchange(exchangeId)
                .withEntities(NamedEntitiesBuilder.aNamedEntities()
                        .withOrganisation(NamedEntityBuilder.aNamedEntity().withName("Apple").build())
                        .withOrganisation(NamedEntityBuilder.aNamedEntity().withName("Computer").build())
                        .build())
                .build()).getId();
        amazonId = companyRepository.save(CompanyBuilder.aCompany().withTickerSymbol("AMZN")
                .withName("Amazon.com Inc")
                .withExchange(exchangeId)
                .withEntities(NamedEntitiesBuilder.aNamedEntities()
                        .withOrganisation(NamedEntityBuilder.aNamedEntity().withName("Amazon").build())
                        .withOrganisation(NamedEntityBuilder.aNamedEntity().withName("Computer").build())
                        .build())
                .build()).getId();
        companyRepository.save(CompanyBuilder.aCompany().withTickerSymbol("OTHER").withName("Another Company").withExchange(UUID.randomUUID().toString()).build());

        storyRepository.save(new StoryBuilder().setMatchedCompanies(Arrays.asList(appleId, amazonId)).createStory());
    }

    @Test
    public void testFindByExchange() throws Exception {
        List<Company> companies = companyRepository.findByExchange(exchangeId);

        assertEquals(2, companies.size());
    }

    @Test
    public void testFindSummaryByName() throws Exception {
        List<Company> companies = companyRepository.findSummaryByName("App", new PageRequest(0, 10));

        assertEquals(1, companies.size());

        Company returnedCompany = companies.get(0);

        assertEquals("Apple", returnedCompany.getName());
        assertNull(returnedCompany.getEntities());
        assertNull(returnedCompany.getCompanyInformation());
    }

    @Test
    public void testFindByExchangeSummary() throws Exception {
        List<Company> companies = companyRepository.findByExchangeSummary(exchangeId);

        assertEquals(2, companies.size());
        Company returnedCompany = companies.get(0);
        assertNull(returnedCompany.getEntities());
        assertNull(returnedCompany.getCompanyInformation());
    }

    @Test
    public void testFindByExchangeSummaryPageable() throws Exception {
        Page<Company> companies = companyRepository.findByExchangeSummaryPageable(exchangeId, new PageRequest(0, 10));

        assertEquals(2l, companies.getTotalElements());
        assertEquals(1, companies.getTotalPages());
    }

    @Test
    @Ignore("Fails on Duplicate Key Exception")
    public void testSearchByExchangeSummaryPageable() throws Exception {
        Page<Company> companies = companyRepository.searchByExchangeSummaryPageable(exchangeId, "App", new PageRequest(0, 10));

        assertEquals(1l, companies.getTotalElements());
        assertEquals(1, companies.getTotalPages());
    }

    @Test
    public void testSearchSummaryPageable() throws Exception {
        Page<Company> companies = companyRepository.searchSummaryPageable("App", new PageRequest(0, 10));

        assertEquals(1l, companies.getTotalElements());
        assertEquals(1, companies.getTotalPages());
    }

    @Test
    public void testFindAllSummary() throws Exception {
        List<Company> companies = companyRepository.findAllSummary();

        assertEquals(3, companies.size());
        Company returnedCompany = companies.get(0);
        assertNull(returnedCompany.getEntities());
        assertNull(returnedCompany.getCompanyInformation());
    }

    @Test
    public void testFindAllSummary_pageable() throws Exception {
        Page<Company> companies = companyRepository.findAllSummary(new PageRequest(0, 2));

        assertEquals(3l, companies.getTotalElements());
        assertEquals(2, companies.getTotalPages());
    }

    @Test
    public void testFindAllSummary_byIds() throws Exception {
        List<String> ids = new ArrayList<>();
        ids.add(appleId);

        List<Company> companies = companyRepository.findAllSummary(ids);

        assertEquals(1, companies.size());
        Company returnedCompany = companies.get(0);
        assertNull(returnedCompany.getEntities());
        assertNull(returnedCompany.getCompanyInformation());
    }

    @Test
    public void testFindOneName() throws Exception {
        Company company = companyRepository.findOneName(appleId);

        assertEquals("Apple", company.getName());
        assertNull(company.getTickerSymbol());
    }

    @Test
    public void testFindOneByExchangeAndTickerSymbol() throws Exception {
        Company company = companyRepository.findOneByExchangeAndTickerSymbol(exchangeId, "AAPL");

        assertEquals("Apple", company.getName());
        assertEquals("AAPL", company.getTickerSymbol());
        assertNull(company.getCompanyInformation());
    }

    @Test
    public void testFindCommonNamedEntities() throws Exception {
        List<NameCount> commonNames = companyRepository.findCommonNamedEntities(new PageRequest(0, 2));

        assertEquals(2, commonNames.size());

        assertEquals("Computer", commonNames.get(0).getName());
        assertEquals(2, commonNames.get(0).getCount());

        assertEquals(1, commonNames.get(1).getCount());
    }

    @Test
    public void testFindWithNamedEntity() throws Exception {
        List<Company> companies = companyRepository.findWithNamedEntity("Apple");

        assertEquals(1, companies.size());

        List<Company> companies2 = companyRepository.findWithNamedEntity("Computer");

        assertEquals(2, companies2.size());
    }

    @Test
    @Ignore("This actually fails")
    public void testFindRelatedCompanies() throws Exception {
        List<CompanyCount> companyCounts = companyRepository.findRelatedCompanies(appleId, new PageRequest(0, 2));

        assertEquals(1, companyCounts.size());
    }
}