package educa.evaluation.repository;

import educa.evaluation.domain.Campus;
import educa.evaluation.domain.Institution;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CampusRepository extends PagingAndSortingRepository<Campus, String> {
    Campus findByInstitutionAndPrimaryCampus(Institution institution, boolean primaryCampus);
}
