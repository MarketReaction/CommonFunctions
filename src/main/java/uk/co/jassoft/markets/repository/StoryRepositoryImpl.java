package uk.co.jassoft.markets.repository;

import uk.co.jassoft.markets.datamodel.story.Story;
import uk.co.jassoft.markets.datamodel.story.metric.Metric;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by Jonny on 08/10/2014.
 */
public class StoryRepositoryImpl extends SimpleMongoRepository<Story, String> implements StoryRepository {

    private final MongoOperations mongoOperations;

    public StoryRepositoryImpl(MongoEntityInformation<Story, String> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
    }

    @Autowired
    public StoryRepositoryImpl(MongoRepositoryFactory factory, MongoOperations mongoOperations) {
        this(factory.<Story, String>getEntityInformation(Story.class), mongoOperations);
    }

    @Override
    public List<Story> findByParentSource(String parentSource, Pageable page) {
        Query query = Query.query(Criteria.where("ParentSource").is(parentSource));

        query.fields().exclude("entities");

        query.limit(page.getPageSize());
        query.skip(page.getOffset());

        query.with(page.getSort());

        return this.mongoOperations.find(query, Story.class);
    }

    @Override
    public List<Story> findByMatchedCompaniesIn(List<String> companies, Pageable page) {
        Set<String> parameters = new HashSet<>();
        for (String id : companies) {
            parameters.add(id);
        }

        Query query = new Query(new Criteria("matchedCompanies").in(parameters));

        query.fields().exclude("entities").exclude("body").exclude("metrics");

        query.limit(page.getPageSize());
        query.skip(page.getOffset());

        query.with(page.getSort());

        return this.mongoOperations.find(query, Story.class);
    }

    @Override
    public List<Story> findByCompaniesBetweenDates(List<String> companies, Date from, Date to) {
        Set<String> parameters = new HashSet<>();
        for (String id : companies) {
            parameters.add(id);
        }

        Query query = new Query(new Criteria("matchedCompanies").in(parameters));
        query.addCriteria(new Criteria("datePublished").gte(from).lte(to));

        query.fields().exclude("entities").exclude("body").exclude("metrics");

        return this.mongoOperations.find(query, Story.class);
    }

    @Override
    public Story findOneByUrl(String url) {
        Query query = Query.query(Criteria.where("url").is(url));

        return this.mongoOperations.findOne(query, Story.class);
    }

    @Override
    public Page<Story> findByTitle(String title, Pageable page) {
        Query query = Query.query(Criteria.where("title").is(title));

        query.fields().exclude("entities");

        query.limit(page.getPageSize());
        query.skip(page.getOffset());

        query.with(page.getSort());

        List<Story> list = this.mongoOperations.find(query, Story.class);

        long count = this.mongoOperations.count(Query.query(Criteria.where("title").is(title)), Story.class);

        return new PageImpl<>(list, page, count);
    }

    @Override
    public List<Pair<Date, Integer>> getStoryCountPerDay(Date from, Date to) {

        Aggregation agg = newAggregation(
                match(Criteria.where("datePublished").exists(true)),
                match(Criteria.where("datePublished").gte(from).lte(to)),
                project()
                        .andExpression("year(datePublished)").as("year")
                        .andExpression("month(datePublished)").as("month")
                        .andExpression("dayOfMonth(datePublished)").as("day"),
                group(fields("year", "month", "day")).count().as("count"),
                project("count")
                        .and("year").as("year")
                        .and("month").as("month")
                        .and("day").as("day")
        );

        AggregationResults<StoryCount> aggregationResults = this.mongoOperations.aggregate(agg, Story.class, StoryCount.class);

        Object o = aggregationResults.getMappedResults().stream()
                .map(storyCount -> new ImmutablePair(new DateTime(storyCount.getYear(), storyCount.getMonth(), storyCount.getDay(), 0, 0).toDate(), storyCount.getCount()))
                .sorted((o11, o21) -> ((Date) o11.getKey()).compareTo((Date) o21.getKey()))
                .collect(Collectors.toList());

        return (List<Pair<Date, Integer>>) o;
    }

    public List<Triple<String, Date, Long>> getStoryProcessingTimes(Date from, Date to) {

        Query query = new Query(new Criteria("datePublished").gte(from).lte(to));

        query.fields().exclude("entities").exclude("body");

        List<Story> stories = this.mongoOperations.find(query, Story.class);

        return stories.stream().parallel()
                .filter(hasSentimentMetric())
                .map(story -> {
                    Date sentimentEnd = story.getMetrics().stream().filter(sentimentMetrics()).findFirst().get().getEnd();

                    return new ImmutableTriple<>(story.getId(), story.getDateFound(), new DateTime(sentimentEnd).minus(story.getDatePublished().getTime()).toDate().getTime());
                }).collect(Collectors.toList());
    }

    @Override
    public List<Story> findMetricsBetweenDates(Date from, Date to) {

        Query query = new Query(new Criteria("datePublished").gte(from).lte(to));

        query.fields().include("metrics");

        return this.mongoOperations.find(query, Story.class);
    }


    public static Predicate<Story> hasSentimentMetric() {
        return story -> story.getMetrics().stream().filter(sentimentMetrics()).findAny().isPresent();
    }

    public static Predicate<Metric> sentimentMetrics() {
        return metric -> metric.getName().equals("Sentiment");
    }



}
class StoryCount {

    private int year;
    private int month;
    private int day;

    private Integer count;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
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
