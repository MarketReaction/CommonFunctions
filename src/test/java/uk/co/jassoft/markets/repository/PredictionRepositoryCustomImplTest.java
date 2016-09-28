package uk.co.jassoft.markets.repository;

import uk.co.jassoft.markets.datamodel.company.Company;
import uk.co.jassoft.markets.datamodel.prediction.PredictionBuilder;
import uk.co.jassoft.utils.BaseRepositoryTest;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by jonshaw on 18/12/2015.
 */
public class PredictionRepositoryCustomImplTest extends BaseRepositoryTest {

    private PredictionRepositoryCustomImpl predictionRepositoryCustom;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        predictionRepositoryCustom = new PredictionRepositoryCustomImpl(getMongoRepositoryFactory(), getTemplate());

        loadTestData();
    }

    @After
    public void tearDown() throws Exception {
        getTemplate().dropCollection(Company.class);
    }

    private void loadTestData() {

//        2015,12,1 - 66% correct
        predictionRepositoryCustom.save(PredictionBuilder.aPrediction().withPredictionDate(new DateTime(2015,12,1,12,0).toDate()).withCertainty(0.1).withCorrect(true).build());
        predictionRepositoryCustom.save(PredictionBuilder.aPrediction().withPredictionDate(new DateTime(2015,12,1,12,0).toDate()).withCertainty(0.2).withCorrect(true).build());
        predictionRepositoryCustom.save(PredictionBuilder.aPrediction().withPredictionDate(new DateTime(2015,12,1,12,0).toDate()).withCertainty(0.2).withCorrect(false).build());

//        2015,12,2 - 0% correct
        predictionRepositoryCustom.save(PredictionBuilder.aPrediction().withPredictionDate(new DateTime(2015,12,2,12,0).toDate()).withCertainty(0.1).withCorrect(false).build());
        predictionRepositoryCustom.save(PredictionBuilder.aPrediction().withPredictionDate(new DateTime(2015,12,2,12,0).toDate()).withCertainty(0.2).withCorrect(false).build());
        predictionRepositoryCustom.save(PredictionBuilder.aPrediction().withPredictionDate(new DateTime(2015,12,2,12,0).toDate()).withCertainty(0.3).withCorrect(false).build());
        predictionRepositoryCustom.save(PredictionBuilder.aPrediction().withPredictionDate(new DateTime(2015,12,2,12,0).toDate()).withCertainty(0.4).withCorrect(false).build());
        predictionRepositoryCustom.save(PredictionBuilder.aPrediction().withPredictionDate(new DateTime(2015,12,2,12,0).toDate()).withCertainty(0.5).withCorrect(false).build());

//        2015,12,3 - 20% correct
        predictionRepositoryCustom.save(PredictionBuilder.aPrediction().withPredictionDate(new DateTime(2015,12,3,12,0).toDate()).withCertainty(0.1).withCorrect(false).build());
        predictionRepositoryCustom.save(PredictionBuilder.aPrediction().withPredictionDate(new DateTime(2015,12,3,12,0).toDate()).withCertainty(0.2).withCorrect(false).build());
        predictionRepositoryCustom.save(PredictionBuilder.aPrediction().withPredictionDate(new DateTime(2015,12,3,12,0).toDate()).withCertainty(0.3).withCorrect(true).build());
        predictionRepositoryCustom.save(PredictionBuilder.aPrediction().withPredictionDate(new DateTime(2015,12,3,12,0).toDate()).withCertainty(0.4).withCorrect(true).build());
        predictionRepositoryCustom.save(PredictionBuilder.aPrediction().withPredictionDate(new DateTime(2015,12,3,12,0).toDate()).withCertainty(0.5).withCorrect(false).build());
    }

    @Test
    public void testGetPredictionAccuracyByDay() throws Exception {
        List<Pair<Date, Float>> predictionAccuracyByDay = predictionRepositoryCustom.getPredictionAccuracyByDayAndCertainty(0.0);

        Pair<Date, Float> pair1 = predictionAccuracyByDay.stream().filter(dateFloatPair -> dateFloatPair.getLeft().equals(new DateTime(2015,12,1,0,0).toDate())).findFirst().get();

        assertEquals(pair1.getRight(), new Float(66.66667));

        Pair<Date, Float> pair2 = predictionAccuracyByDay.stream().filter(dateFloatPair -> dateFloatPair.getLeft().equals(new DateTime(2015,12,2,0,0).toDate())).findFirst().get();

        assertEquals(pair2.getRight(), new Float(0));

        Pair<Date, Float> pair3 = predictionAccuracyByDay.stream().filter(dateFloatPair -> dateFloatPair.getLeft().equals(new DateTime(2015,12,3,0,0).toDate())).findFirst().get();

        assertEquals(pair3.getRight(), new Float(40));

    }

    @Test
    public void testGetPredictionAccuracyByDayWithCertaintyFilter() throws Exception {
        List<Pair<Date, Float>> predictionAccuracyByDay = predictionRepositoryCustom.getPredictionAccuracyByDayAndCertainty(0.3);

        Optional<Pair<Date, Float>> pair1 = predictionAccuracyByDay.stream().filter(dateFloatPair -> dateFloatPair.getLeft().equals(new DateTime(2015,12,1,0,0).toDate())).findFirst();

        assertFalse(pair1.isPresent());

        Pair<Date, Float> pair2 = predictionAccuracyByDay.stream().filter(dateFloatPair -> dateFloatPair.getLeft().equals(new DateTime(2015,12,2,0,0).toDate())).findFirst().get();

        assertEquals(pair2.getRight(), new Float(0));

        Pair<Date, Float> pair3 = predictionAccuracyByDay.stream().filter(dateFloatPair -> dateFloatPair.getLeft().equals(new DateTime(2015,12,3,0,0).toDate())).findFirst().get();

        assertEquals(pair3.getRight(), new Float(66.66667));

    }
}