package uk.co.jassoft.markets.utils;

import uk.co.jassoft.markets.datamodel.company.quote.Quote;
import uk.co.jassoft.markets.datamodel.Direction;
import uk.co.jassoft.markets.exceptions.quote.QuotePriceCalculationException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jonshaw on 01/09/15.
 */
public class QuoteUtils {

    public static double getPriceChange(List<Quote> quotes) throws Exception {

        final List<Double> prices = getPrices(quotes);

        if(prices.size() < 2) {
            throw new QuotePriceCalculationException("Could not get Quote price change - Not enough Data");
        }

        final Double previousPrice = prices.get(prices.size() - 2); // 100
        final Double latestPrice = prices.get(prices.size() - 1); // 40

        return latestPrice - previousPrice; // eg -60
    }

    public static Direction getPreviousPriceDirection(List<Quote> quotes) throws Exception {
        final List<Double> prices = getPrices(quotes);

        List<Double> trend = new ArrayList<>();

        int N = 3;
        double[] a = new double[N];
        double sum = 0.0;
        for (int i = 0; i < prices.size(); i++) {
            sum -= a[i % N];
            a[i % N] = prices.get(i);
            sum += a[i % N];
            if (i >= N) {
                trend.add(sum / N);
            }
        }

        if(trend.isEmpty()) {
            throw new QuotePriceCalculationException("Could not determine Quote price direction - No Trend Data");
        }

        if(trend.size() < 2) {
            throw new QuotePriceCalculationException("Could not determine Quote price direction - Not Enough Trend Data");
        }

        // Calculate the direction of prices over past week
        final Double firstPrice = trend.get(0); // 100
        final Double latestPrice = trend.get(trend.size() - 2); // 40

        if(firstPrice < latestPrice)
            return Direction.Up;

        if(firstPrice == latestPrice)
            return Direction.None;

        if(firstPrice > latestPrice)
            return Direction.Down;

        throw new QuotePriceCalculationException(String.format("Could not determine Quote price direction - FirstPrice [%s] LastPrice [%s]", firstPrice, latestPrice));
    }

    private static List<Double> getPrices(List<Quote> quotes) throws Exception {
        if(quotes == null || quotes.isEmpty())
            throw new QuotePriceCalculationException("Could not calculate price direction as [No Data]");

        if(quotes.size() < 2)
            throw new QuotePriceCalculationException("Could not calculate price direction as [Not enough Data]");

        return quotes.stream().sorted((q1, q2) -> q1.getDate().compareTo(q2.getDate())).map(quote -> quote.getClose()).collect(Collectors.toList());
    }

}
