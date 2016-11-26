package educa.evaluation.repository;

import educa.evaluation.domain.Questionnaire;
import educa.evaluation.domain.Section;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SectionRepository extends PagingAndSortingRepository<Section, String> {


    Section findByQuestionnaireAndSubdimensionId(Questionnaire questionnaire, String subdimensionId);

}
