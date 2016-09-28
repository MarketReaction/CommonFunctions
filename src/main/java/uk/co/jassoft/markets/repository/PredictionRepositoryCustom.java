package uk.co.jassoft.markets.repository;

import uk.co.jassoft.markets.datamodel.prediction.Prediction;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by Jonny on 12/08/2014.
 */
public interface PredictionRepositoryCustom extends MongoRepository<Prediction, String>
{
    List<Pair<Date, Float>> getPredictionAccuracyByDayAndCertainty(Double certainty);

    List<Pair<Date, Float>> getPredictionAccuracyByDayAndByCompany(String companyId);
}
