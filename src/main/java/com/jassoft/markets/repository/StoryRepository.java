package com.jassoft.markets.repository;

import com.jassoft.markets.datamodel.story.Story;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by Jonny on 12/08/2014.
 */
public interface StoryRepository extends MongoRepository<Story, String>
{
    List<Story> findByParentSource(String parentSource, Pageable page);

    List<Story> findByMatchedCompaniesIn(List<String> companies, Pageable page);

    Story findOneByUrl(String url);

    Page<Story> findByTitle(String title, Pageable page);

    List<Story> findByCompaniesBetweenDates(List<String> companies, Date from, Date to);

    List<Pair<Date, Integer>> getStoryCountPerDay(Date from, Date to);

    List<Triple<String, Date, Long>> getStoryProcessingTimes(Date from, Date to);

    List<Story> findMetricsBetweenDates(Date from, Date to);
}


