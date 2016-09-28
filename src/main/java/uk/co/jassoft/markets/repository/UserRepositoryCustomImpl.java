package uk.co.jassoft.markets.repository;

import uk.co.jassoft.markets.datamodel.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

/**
 * Created by Jonny on 19/08/2014.
 */
public class UserRepositoryCustomImpl extends SimpleMongoRepository<User, String> implements UserRepositoryCustom
{
    private final MongoOperations mongoOperations;

    public UserRepositoryCustomImpl(MongoEntityInformation<User, String> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
    }

    @Autowired
    public UserRepositoryCustomImpl(MongoRepositoryFactory factory, MongoOperations mongoOperations) {
        this(factory.<User, String>getEntityInformation(User.class), mongoOperations);
    }

    @Override
    public User watchCompany(String objectId, String company) {
        this.mongoOperations.updateFirst(
                Query.query(Criteria.where("id").is(objectId)),
                new Update().push("watchedCompanies", company), User.class);

        return findOne(objectId);
    }

    @Override
    public User unwatchCompany(String objectId, String company) {
        this.mongoOperations.updateFirst(
                Query.query(Criteria.where("id").is(objectId)),
                new Update().pull("watchedCompanies", company), User.class);

        return findOne(objectId);
    }
}
