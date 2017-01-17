package educa.evaluation.repository;

import educa.evaluation.domain.Campus;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InstitutionRepository extends PagingAndSortingRepository<Campus, String> {
}
