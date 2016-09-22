package com.jassoft.markets.repository;

import com.jassoft.markets.datamodel.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Jonny on 12/08/2014.
 */
public interface UserRepositoryCustom extends MongoRepository<User, String>
{
    User watchCompany(String objectId, String url);

    User unwatchCompany(String objectId, String url);
}
