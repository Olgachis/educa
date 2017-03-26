package educa.evaluation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import educa.evaluation.data.SimpleQuestionnaireResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.List;

@Slf4j
@Service
@Transactional
public class ExportService {

    @Autowired
    private SimpleQuestionnaireService simpleQuestionnaireService;

    public String generateCsv(String id) {
        StringBuilder stringBuilder = new StringBuilder();

        List<SimpleQuestionnaireResponse> responses = 
            simpleQuestionnaireService.listFullResponses(id)
                .getResponses();
        
        if(responses.size() > 0) {
            stringBuilder.append(buildHeader(responses.get(0).getData()));
        }

        responses
            .stream()
            .forEach(response -> {
                stringBuilder
                    .append(response.getId())
                    .append("|")
                    .append(response.getTitle())
                    .append("|")
                    .append(buildResponses(response.getData()))
                    .append("\n");
            });

        return stringBuilder.toString();
    }

    private String buildHeader(Map<String, Object> data) {
        StringBuilder header = new StringBuilder();
        List<Object> questions = (List<Object>) data.get("questions");
        header
            .append("id")
            .append("|")
            .append("name")
            .append("|")
            .append(questions
                .stream()
                .sorted((q1, q2) -> {
                    Map<String, Object> question1 = (Map<String, Object>) q1;
                    Map<String, Object> question2 = (Map<String, Object>) q2;
                    return ((String)question1.get("id")).compareTo((String)question2.get("id"));
                })
                .map(q -> {
                    Map<String, Object> question = (Map<String, Object>) q;
                    return (String) question.get("displayName");
                })
                .collect(Collectors.joining("|")))
            .append("\n");
        return header.toString();
    }

    private String buildResponses(Map<String, Object> data) {
        List<Object> questions = (List<Object>) data.get("questions");
        return questions
            .stream()
            .sorted((q1, q2) -> {
                Map<String, Object> question1 = (Map<String, Object>) q1;
                Map<String, Object> question2 = (Map<String, Object>) q2;
                return ((String)question1.get("id")).compareTo((String)question2.get("id"));
            })
            .map(q -> {
                Map<String, Object> question = (Map<String, Object>) q;
                return (String) question.get("value");
            })
            .collect(Collectors.joining("|"));
    }

}
