package com.jassoft.markets.repository;

import com.jassoft.markets.datamodel.story.date.MissingDateFormat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by Jonny on 12/08/2014.
 */
//@Repository
public interface MissingDateFormatRepository extends MongoRepository<MissingDateFormat, String>
{
    List<MissingDateFormat> findByMetatag(String metatag);
}
