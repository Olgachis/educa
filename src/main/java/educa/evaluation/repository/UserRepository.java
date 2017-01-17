package educa.evaluation.repository;

import educa.evaluation.domain.Campus;
import educa.evaluation.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, String> {

    User findByUsername(String username);

    User findByCampus(Campus campus);

}
