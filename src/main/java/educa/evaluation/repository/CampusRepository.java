package educa.evaluation.repository;

import educa.evaluation.domain.Campus;
import educa.evaluation.domain.Institution;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CampusRepository extends PagingAndSortingRepository<Campus, String> {
    Campus findByInstitutionAndPrimaryCampus(Institution institution, boolean primaryCampus);

    List<Campus> findAllByInstitution(Institution institution);
}
