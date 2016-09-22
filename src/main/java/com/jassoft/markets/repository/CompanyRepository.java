package com.jassoft.markets.repository;

import com.jassoft.markets.datamodel.company.Company;
import com.jassoft.markets.datamodel.company.CompanyCount;
import com.jassoft.markets.datamodel.story.NameCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by Jonny on 12/08/2014.
 */
public interface CompanyRepository extends MongoRepository<Company, String>
{
    
    List<Company> findByExchange(String exchange);

    List<Company> findSummaryByName(String name, PageRequest pageable);

    List<Company> findByExchangeSummary(String exchange);

    Page<Company> findByExchangeSummaryPageable(String exchange, PageRequest pageable);
    
    Page<Company> searchByExchangeSummaryPageable(String exchange, String searchTerm, PageRequest pageable);
    
    Page<Company> searchSummaryPageable(String searchTerm, PageRequest pageable);
    
    List<Company> findAllSummary();

    Page<Company> findAllSummary(PageRequest pageable);

    List<Company> findAllSummary(Iterable<String> ids);

    Company findOneName(String id);
    
    Company findOneByExchangeAndTickerSymbol(String exchange, String tickerSymbol);
    
    List<Company> findWithSentimentsAfterDate(Date date);
    
    Company findCompanySentimentForStory(String company, String story);

    List<NameCount> findCommonNamedEntities(PageRequest pageable);

    List<Company> findWithNamedEntity(String name);

    List<CompanyCount> findRelatedCompanies(String company, PageRequest pageable);
}
