package uk.co.jassoft.markets.repository;

import uk.co.jassoft.markets.datamodel.sources.Source;
import uk.co.jassoft.markets.datamodel.sources.SourceType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by Jonny on 12/08/2014.
 */
public interface SourceRepository extends MongoRepository<Source, String>
{
    Source pushExclusion(String objectId, String exclusion);

    Source pushUrl(String objectId, String url);

    Source pullExclusion(String objectId, String exclusion);

    Source pullUrl(String objectId, String url);

    List<Source> findByTypeAndDisabled(SourceType type, boolean disabled);
}
