package educa.evaluation.repository;

import educa.evaluation.domain.SimpleResponse;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SimpleResponseRepository extends PagingAndSortingRepository<SimpleResponse, String> {
    public List<SimpleResponse> findAllResponsesByQuestionnaireId(String id);
}
