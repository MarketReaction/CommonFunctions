package com.jassoft.markets.repository;

import com.jassoft.markets.datamodel.company.Exchange;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by Jonny on 12/08/2014.
 */
public interface ExchangeRepository extends MongoRepository<Exchange, String>
{
    List<Exchange> findByEnabledIsTrue(Sort sort);

    Exchange findOneByCode(String code);
}
