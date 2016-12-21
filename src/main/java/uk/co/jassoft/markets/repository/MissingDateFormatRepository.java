package uk.co.jassoft.markets.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.co.jassoft.markets.datamodel.story.date.MissingDateFormat;

/**
 * Created by Jonny on 12/08/2014.
 */
//@Repository
public interface MissingDateFormatRepository extends MongoRepository<MissingDateFormat, String>
{
}
