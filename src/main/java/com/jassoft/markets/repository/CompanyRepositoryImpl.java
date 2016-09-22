package com.jassoft.markets.repository;

import com.jassoft.markets.datamodel.company.Company;
import com.jassoft.markets.datamodel.company.CompanyCount;
import com.jassoft.markets.datamodel.story.NameCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by Jonny on 01/09/2014.
 */
public class CompanyRepositoryImpl extends SimpleMongoRepository<Company, String> implements CompanyRepository
{
    private final MongoOperations mongoOperations;
    
    public CompanyRepositoryImpl(MongoEntityInformation<Company, String> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
    }

    @Autowired
    public CompanyRepositoryImpl(MongoRepositoryFactory factory, MongoOperations mongoOperations) {
        this(factory.<Company, String>getEntityInformation(Company.class), mongoOperations);
    }

    @Override
    public List<Company> findByExchange(String exchange) {
        Query query = Query.query(Criteria.where("exchange").is(exchange));

        return this.mongoOperations.find(query, Company.class);
    }

    @Override
    public List<Company> findSummaryByName(String name, PageRequest pageable) {
        Query query = Query.query(Criteria.where("name").regex(name, "i"));

        query.fields().exclude("entities").exclude("companyInformation").exclude("storySentiments");

        query.limit(pageable.getPageSize());
        query.skip(pageable.getOffset());

        return this.mongoOperations.find(query, Company.class);
    }

    @Override
    public List<Company> findByExchangeSummary(String exchange) {
        Query query = Query.query(Criteria.where("exchange").is(exchange));

        query.fields().exclude("entities").exclude("companyInformation");

        return this.mongoOperations.find(query, Company.class);
    }

    @Override
    public Page<Company> findByExchangeSummaryPageable(String exchange, PageRequest pageable) {
        Query query = Query.query(Criteria.where("exchange").is(exchange));

        query.fields().exclude("entities").exclude("companyInformation").exclude("storySentiments");

        query.limit(pageable.getPageSize());
        query.skip(pageable.getOffset());

        query.with(new Sort(Sort.Direction.ASC, "name"));

        List<Company> list = this.mongoOperations.find(query, Company.class);

        long count = this.mongoOperations.count(Query.query(Criteria.where("exchange").is(exchange)), Company.class);

        return new PageImpl<>(list, pageable, count);
    }

    @Override
    public Page<Company> searchByExchangeSummaryPageable(String exchange, String searchTerm, PageRequest pageable)
    {
        TextCriteria criteria = TextCriteria.forDefaultLanguage()
                .matchingAny(searchTerm);

        Query query = TextQuery.queryText(criteria)
                .sortByScore();

        query.addCriteria(Criteria.where("exchange").is(exchange));

        query.fields().exclude("entities").exclude("companyInformation").exclude("storySentiments");

        query.limit(pageable.getPageSize());
        query.skip(pageable.getOffset());

        List<Company> list = this.mongoOperations.find(query, Company.class);

        Query countQuery = TextQuery.queryText(criteria)
                .sortByScore();

        countQuery.addCriteria(Criteria.where("exchange").is(exchange));

        long count = this.mongoOperations.count(countQuery, Company.class);

        return new PageImpl<>(list, pageable, count);
    }

    @Override
    public Page<Company> searchSummaryPageable(String searchTerm, PageRequest pageable) {
        Query query = Query.query(Criteria.where("name").regex(searchTerm, "i"));

        query.fields().exclude("entities").exclude("companyInformation").exclude("storySentiments");

        query.limit(pageable.getPageSize());
        query.skip(pageable.getOffset());

        query.with(new Sort(Sort.Direction.ASC, "name"));

        List<Company> list = this.mongoOperations.find(query, Company.class);

        Query countQuery = Query.query(Criteria.where("name").regex(searchTerm, "i"));

        long count = this.mongoOperations.count(countQuery, Company.class);

        return new PageImpl<>(list, pageable, count);
    }

    @Override
    public List<Company> findAllSummary() {
        Query query = new Query();

        query.fields().exclude("entities").exclude("companyInformation").exclude("storySentiments");

        return this.mongoOperations.find(query, Company.class);
    }

    @Override
    public Page<Company> findAllSummary(PageRequest pageable) {
        Query query = new Query();

        query.fields().exclude("entities").exclude("companyInformation").exclude("storySentiments");

        query.limit(pageable.getPageSize());
        query.skip(pageable.getOffset());

        List<Company> list = this.mongoOperations.find(query, Company.class);

        return new PageImpl<>(list, pageable, count());
    }

    @Override
    public List<Company> findAllSummary(Iterable<String> ids) {
        Set<String> parameters = new HashSet<>();
        for (String id : ids) {
            parameters.add(id);
        }

        Query query = new Query(new Criteria("id").in(parameters));

        query.fields().exclude("entities").exclude("companyInformation").exclude("storySentiments");

        return this.mongoOperations.find(query, Company.class);
    }

    @Override
    public Company findOneName(String id) {
        Query query = Query.query(Criteria.where("id").is(id));

        query.fields().include("name");

        return this.mongoOperations.findOne(query, Company.class);
    }

    @Override
    public Company findOneByExchangeAndTickerSymbol(String exchange, String tickerSymbol) {
        Query query = Query.query(Criteria.where("exchange").is(exchange)
                .and("tickerSymbol").is(tickerSymbol));

        query.fields().include("name").include("tickerSymbol");

        return this.mongoOperations.findOne(query, Company.class);
    }

    @Override
    public List<Company> findWithSentimentsAfterDate(Date date) {
        Query query = Query.query(new Criteria("storySentiments.storyDate").gte(date));

        query.fields().exclude("entities").exclude("companyInformation");

        return this.mongoOperations.find(query, Company.class);
    }


    public Company findCompanySentimentForStory(String company, String story) {
        Query query = Query.query(Criteria.where("id").is(company).and("storySentiments.story").is(story));

        query.fields().include("storySentiments.$");

        return this.mongoOperations.findOne(query, Company.class);
    }

    @Override
    public List<NameCount> findCommonNamedEntities(PageRequest pageable) {

        Aggregation agg = newAggregation(
                project().and("entities.organisations.name").as("name"),
                unwind("name"),
                group(fields("name")).count().as("count"),
                project("count").and("_id").as("name"),
                sort(Sort.Direction.DESC, "count"),
                skip(pageable.getOffset()),
                limit(pageable.getPageSize())
        );

        AggregationResults<NameCount> aggregationResults = this.mongoOperations.aggregate(agg, Company.class, NameCount.class);

        return aggregationResults.getMappedResults();
    }

    @Override
    public List<Company> findWithNamedEntity(String name) {
        Query query = Query.query(new Criteria("entities.organisations.name").is(name));

        query.fields().exclude("entities").exclude("companyInformation");

        return this.mongoOperations.find(query, Company.class);
    }

    @Override
    public List<CompanyCount> findRelatedCompanies(String company, PageRequest pageable) {

//        Aggregation agg = newAggregation(
//                match(new Criteria("matchedCompanies").in(company)),
//                project().and("matchedCompanies").as("company"),
//                unwind("company"),
//                group(fields("company")).count().as("count"),
//                project("count").and("_id").as("company"),
//                sort(Sort.Direction.DESC, "count"),
//                skip(pageable.getOffset()),
//                limit(pageable.getPageSize())
//        );
//
//        AggregationResults<CompanyCount> aggregationResults = this.mongoOperations.aggregate(agg, Story.class, CompanyCount.class);
//
//        return aggregationResults.getMappedResults();

        return new ArrayList<>();
    }

}