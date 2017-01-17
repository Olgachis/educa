package educa.evaluation.repository;

import educa.evaluation.domain.Campus;
import educa.evaluation.domain.Campus;
import educa.evaluation.domain.QuestionnaireResponse;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface QuestionnaireResponseRepository extends PagingAndSortingRepository<QuestionnaireResponse, String> {

    QuestionnaireResponse findByCampus(Campus campus);

}
