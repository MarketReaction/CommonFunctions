package uk.co.jassoft.markets.repository;

import uk.co.jassoft.markets.datamodel.story.date.DateFormat;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Jonny on 12/08/2014.
 */
//@Repository
public interface DateFormatRepository extends MongoRepository<DateFormat, String>
{
}
