package uk.co.jassoft.markets.repository;

import uk.co.jassoft.markets.datamodel.company.sentiment.StorySentiment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by Jonny on 12/08/2014.
 */
public interface StorySentimentRepository extends MongoRepository<StorySentiment, String>
{
    List<StorySentiment> findByCompany(String company);

    List<StorySentiment> findByCompanyAndStoryDateGreaterThan(String company, Date storyDate);

    List<StorySentiment> findByStoryDateGreaterThan(Date storyDate);
}
