package uk.co.jassoft.markets.repository;

import uk.co.jassoft.markets.datamodel.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by Jonny on 12/08/2014.
 */
public interface UserRepository extends MongoRepository<User, String>
{
    User findByEmail(String email);

    User findByActivationId(String activationId);

    User findByEmailAndToken(String email, String token);

    List<User> findByWatchedCompanies(String watchedCompanies);
}
