package com.jassoft.markets.repository;

import com.jassoft.markets.datamodel.story.date.DateFormat;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Jonny on 12/08/2014.
 */
//@Repository
public interface DateFormatRepository extends MongoRepository<DateFormat, String>
{
}
