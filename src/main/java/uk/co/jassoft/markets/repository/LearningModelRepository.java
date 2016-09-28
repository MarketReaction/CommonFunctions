package uk.co.jassoft.markets.repository;

import uk.co.jassoft.markets.datamodel.Direction;
import uk.co.jassoft.markets.datamodel.learningmodel.LearningModelRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by Jonny on 12/08/2014.
 */
public interface LearningModelRepository extends MongoRepository<LearningModelRecord, String>
{
    List<LearningModelRecord> findByCompany(String company);

    List<LearningModelRecord> findByCompanyAndPreviousQuoteDirectionAndPreviousSentimentDirection(String company, Direction previousQuoteDirection, Direction previousSentimentDirection);
}
