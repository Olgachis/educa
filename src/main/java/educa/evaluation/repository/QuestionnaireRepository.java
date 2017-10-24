package educa.evaluation.repository;

import educa.evaluation.domain.Questionnaire;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface QuestionnaireRepository extends PagingAndSortingRepository<Questionnaire, String> {

  Questionnaire findByNameAndPeriod(String name, Integer period);
}
