package educa.evaluation.repository;

import educa.evaluation.domain.Section;
import educa.evaluation.domain.SectionResponse;
import educa.evaluation.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface SectionResponseRepository extends PagingAndSortingRepository<SectionResponse, String> {

    SectionResponse findByUserAndSection(User user, Section section);

}
