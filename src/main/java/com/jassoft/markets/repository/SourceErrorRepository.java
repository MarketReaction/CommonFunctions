package com.jassoft.markets.repository;

import com.jassoft.markets.datamodel.sources.errors.SourceError;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Jonny on 12/08/2014.
 */
public interface SourceErrorRepository extends MongoRepository<SourceError, String>
{
}
