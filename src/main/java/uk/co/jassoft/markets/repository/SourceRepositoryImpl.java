package uk.co.jassoft.markets.repository;

import uk.co.jassoft.markets.datamodel.sources.Source;
import uk.co.jassoft.markets.datamodel.sources.SourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.util.List;

/**
 * Created by Jonny on 13/08/2014.
 */
public class SourceRepositoryImpl extends SimpleMongoRepository<Source, String> implements SourceRepository
{
    private final MongoOperations mongoOperations;
    
    public SourceRepositoryImpl(MongoEntityInformation<Source, String> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
    }

    @Autowired
    public SourceRepositoryImpl(MongoRepositoryFactory factory, MongoOperations mongoOperations) {
        this(factory.<Source, String>getEntityInformation(Source.class), mongoOperations);
    }

    @Override
    public Source pushExclusion(String objectId, String exclusion)
    {
        this.mongoOperations.updateFirst(
                Query.query(Criteria.where("id").is(objectId)),
                new Update().push("exclusionList", exclusion), Source.class);

        return findOne(objectId);
    }

    @Override
    public Source pushUrl(String objectId, String url)
    {
        this.mongoOperations.updateFirst(
                Query.query(Criteria.where("id").is(objectId)),
                new Update().push("urls", url), Source.class);

        return findOne(objectId);
    }

    @Override
    public Source pullExclusion(String objectId, String exclusion)
    {
        this.mongoOperations.updateFirst(
                Query.query(Criteria.where("id").is(objectId)),
                new Update().pull("exclusionList", exclusion), Source.class);

        return findOne(objectId);
    }

    @Override
    public Source pullUrl(String objectId, String url)
    {
        this.mongoOperations.updateFirst(
                Query.query(Criteria.where("id").is(objectId)),
                new Update().pull("urls", url), Source.class);

        return findOne(objectId);
    }

    @Override
    public List<Source> findByTypeAndDisabled(SourceType type, boolean disabled) {
        Query query = Query.query(Criteria.where("type").is(type)
                .and("disabled").is(disabled));

        return this.mongoOperations.find(query, Source.class);
    }
}
