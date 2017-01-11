package educa.evaluation.repository;

import educa.evaluation.domain.Institution;
import educa.evaluation.domain.QuestionnaireResponse;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface QuestionnaireResponseRepository extends PagingAndSortingRepository<QuestionnaireResponse, String> {

    QuestionnaireResponse findByInstitution(Institution institution);

}
