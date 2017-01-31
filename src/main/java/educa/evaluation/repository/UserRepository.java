package educa.evaluation.repository;

import educa.evaluation.domain.Campus;
import educa.evaluation.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserRepository extends PagingAndSortingRepository<User, String> {

    User findByUsername(String username);

    User findByCampus(Campus campus);

    List<User> findAllByRoleName(String name);

}
