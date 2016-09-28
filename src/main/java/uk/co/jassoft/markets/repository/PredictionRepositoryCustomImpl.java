package uk.co.jassoft.markets.repository;

import uk.co.jassoft.markets.datamodel.prediction.Prediction;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by Jonny on 19/08/2014.
 */
public class PredictionRepositoryCustomImpl extends SimpleMongoRepository<Prediction, String> implements PredictionRepositoryCustom
{
    private final MongoOperations mongoOperations;
    
    public PredictionRepositoryCustomImpl(MongoEntityInformation<Prediction, String> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
    }

    @Autowired
    public PredictionRepositoryCustomImpl(MongoRepositoryFactory factory, MongoOperations mongoOperations) {
        this(factory.<Prediction, String>getEntityInformation(Prediction.class), mongoOperations);
    }

    private List<Pair<Date, Float>> retrievePredictionAccuracyByDay(final Aggregation agg) {

        AggregationResults<CorrectCount> aggregationResults = this.mongoOperations.aggregate(agg, Prediction.class, CorrectCount.class);

        Map<Date, MutablePair<Long, Long>> dateCalculations = new HashMap<>();

        List<Pair<Date, Float>> pairs = new ArrayList<>();

        for(CorrectCount correctCount : aggregationResults.getMappedResults()) {

            Date date = new DateTime(correctCount.getYear(), correctCount.getMonth(), correctCount.getDay(), 0, 0).toDate();

            if(dateCalculations.containsKey(date)) {
                MutablePair<Long, Long> pair = dateCalculations.get(date);

                if(correctCount.getCorrect().equals("true")) {
                    pair.setLeft(correctCount.getTotal());
                }

                if(correctCount.getCorrect().equals("false")) {
                    pair.setRight(correctCount.getTotal());
                }
            }
            else {
                if(correctCount.getCorrect().equals("true")) {
                    dateCalculations.put(date, new MutablePair<>(correctCount.getTotal(), 0l));
                }

                if(correctCount.getCorrect().equals("false")) {
                    dateCalculations.put(date, new MutablePair<>(0l, correctCount.getTotal()));
                }
            }
        }

        // Loop over dateCalculations and generate list
        for (Map.Entry<Date, MutablePair<Long, Long>> entry : dateCalculations.entrySet())
        {
            pairs.add(new ImmutablePair<>(entry.getKey(), (float) entry.getValue().getLeft() / (entry.getValue().getLeft() + entry.getValue().getRight()) * 100));
        }

        pairs.sort((o1, o2) -> o1.getLeft().compareTo(o2.getLeft()));

       return pairs;

    }

    @Override
    public List<Pair<Date, Float>> getPredictionAccuracyByDayAndCertainty(Double certainty) {

        Aggregation agg = newAggregation(
                project()
                        .andExpression("year(predictionDate)").as("year")
                        .andExpression("month(predictionDate)").as("month")
                        .andExpression("dayOfMonth(predictionDate)").as("day")
                        .andExpression("correct").as("correct")
                        .andExpression("certainty").as("certainty"),
                match(Criteria.where("correct").exists(true)),
                match(Criteria.where("certainty").gte(certainty)),
                group(fields("year", "month", "day", "correct")).count().as("total"),
                project("total")
                        .and("correct").as("correct")
                        .and("year").as("year")
                        .and("month").as("month")
                        .and("day").as("day")
        );

        return retrievePredictionAccuracyByDay(agg);

    }

    @Override
    public List<Pair<Date, Float>> getPredictionAccuracyByDayAndByCompany(String companyId) {
        Aggregation agg = newAggregation(
                match(Criteria.where("company").is(companyId)),
                project()
                        .andExpression("year(predictionDate)").as("year")
                        .andExpression("month(predictionDate)").as("month")
                        .andExpression("dayOfMonth(predictionDate)").as("day")
                        .andExpression("correct").as("correct"),
                match(Criteria.where("correct").exists(true)),
                group(fields("year", "month", "day", "correct")).count().as("total"),
                project("total")
                        .and("correct").as("correct")
                        .and("year").as("year")
                        .and("month").as("month")
                        .and("day").as("day")
        );

        return retrievePredictionAccuracyByDay(agg);
    }
}

class CorrectCount {

    private String correct;
    private int year;
    private int month;
    private int day;

    private long total;

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
