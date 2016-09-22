package com.jassoft.markets.repository;

import com.jassoft.markets.datamodel.system.SystemProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Jonny on 12/08/2014.
 */
//@Repository
public interface SystemProfileRepository extends MongoRepository<SystemProfile, String>
{
}
