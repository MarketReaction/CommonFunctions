package uk.co.jassoft.markets.repository;

import uk.co.jassoft.markets.datamodel.crawler.Link;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Jonny on 12/08/2014.
 */
public interface LinkRepository extends MongoRepository<Link, String>
{
    Link findOneByLink(String link);
}
