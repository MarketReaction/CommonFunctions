package uk.co.jassoft.markets.repository;

import uk.co.jassoft.markets.datamodel.company.quote.Quote;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by Jonny on 12/08/2014.
 */
public interface QuoteRepository extends MongoRepository<Quote, String>
{
    Quote findByCompanyAndDate(String company, Date date);

    Quote findByCompanyAndDateAndIntraday(String company, Date date, boolean intraday);

    List<Quote> findByCompanyAndIntraday(String company, boolean intraday, PageRequest pageable);

    List<Quote> findByCompanyAndIntradayAndDateLessThan(String company, boolean intraday, Date date, PageRequest pageable);

    List<Quote> findByCompanyAndIntradayAndDateBetween(String company, boolean intraday, Date from, Date to);
}
