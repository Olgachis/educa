package educa.evaluation.repository;

import educa.evaluation.domain.Institution;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InstitutionRepository extends PagingAndSortingRepository<Institution, String> {
}
