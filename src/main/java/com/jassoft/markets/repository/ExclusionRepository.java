package com.jassoft.markets.repository;

import com.jassoft.markets.datamodel.exclusion.Exclusion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by Jonny on 12/08/2014.
 */
public interface ExclusionRepository extends MongoRepository<Exclusion, String>
{
    List<Exclusion> findByName(String name);
}
