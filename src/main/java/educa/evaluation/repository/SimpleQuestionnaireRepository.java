package educa.evaluation.repository;

import educa.evaluation.domain.SimpleQuestionnaire;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SimpleQuestionnaireRepository extends PagingAndSortingRepository<SimpleQuestionnaire, String> {
    public SimpleQuestionnaire findById(String id);
}
