package com.jassoft.markets.repository;

import com.jassoft.markets.datamodel.prediction.Prediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by Jonny on 12/08/2014.
 */
public interface PredictionRepository extends MongoRepository<Prediction, String>
{
    List<Prediction> findByCorrectIsNull();

    Page<Prediction> findByCompany(String company, Pageable pageable);

    List<Prediction> findByCompanyAndCorrectIsNull(String company);

    Page<Prediction> findByCertaintyGreaterThanEqual(Double certainty, Pageable pageable);
}
