package com.jassoft.markets.repository;

import com.jassoft.markets.datamodel.crawler.Link;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Jonny on 12/08/2014.
 */
public interface LinkRepository extends MongoRepository<Link, String>
{
    Link findOneByLink(String link);
}
