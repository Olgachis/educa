package educa.evaluation.service;

import com.google.gson.Gson;
import educa.evaluation.data.QuestionnaireResponses;
import educa.evaluation.data.SimpleQuestionnaireData;
import educa.evaluation.data.SimpleQuestionnaireResponse;
import educa.evaluation.domain.SimpleResponse;
import educa.evaluation.repository.SimpleQuestionnaireRepository;
import educa.evaluation.repository.SimpleResponseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@Transactional
public class SimpleQuestionnaireService {

    @Autowired
    private SimpleQuestionnaireRepository simpleQuestionnaireRepository;

    @Autowired
    private SimpleResponseRepository simpleResponseRepository;

    public List<SimpleQuestionnaireData> listSimpleQuestionnaires() {
        Gson gson = new Gson();
        List<SimpleQuestionnaireData> result = new ArrayList<>();
        simpleQuestionnaireRepository.findAll().forEach((domain) -> {
            SimpleQuestionnaireData simpleQuestionnaireData = new SimpleQuestionnaireData();
            simpleQuestionnaireData.setId(domain.getId());
            simpleQuestionnaireData.setName(domain.getTitle());
            simpleQuestionnaireData.setQuestionnaire(gson.fromJson(domain.getQuestionsJson(), Map.class));
            result.add(simpleQuestionnaireData);
        });
        return result;
    }

    public void save(SimpleQuestionnaireData data) throws ScriptException {
        Gson gson = new Gson();
        SimpleResponse response = Optional.ofNullable(data.getResponseId())
                .map(simpleResponseRepository::findOne)
                .orElse(new SimpleResponse());
        response.setQuestionnaire(simpleQuestionnaireRepository.findOne(data.getId()));
        response.setSimpleResponse(gson.toJson(data.getQuestionnaire()));
        response.setTitle(
            getName((Map<String, Object>)data.getQuestionnaire(),
            response.getQuestionnaire().getTitleFunc())
        );
        response = simpleResponseRepository.save(response);
    }

    private String getName(Map<String, Object> questionnaire, String script) throws ScriptException {
        List<Map<String, Object>> questions = (List<Map<String, Object>>)
                questionnaire.getOrDefault("questions", new ArrayList<Map<String, Object>>());
        questions.stream()
                .collect(Collectors.groupingBy(q -> q.get("id")))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .map(e -> e.getKey())
                .forEach(k -> log.info("K {}", k));
        Map<String, String> data = questions.stream()
                .collect(Collectors.toMap(
                    q -> (String) q.get("id"),
                    q -> (String) q.getOrDefault("value", "")
                ));
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        engine.put("response", data);
        return (String)engine.eval(script);
    }

    public QuestionnaireResponses listResponses(String id) {
        List<SimpleQuestionnaireResponse> responses = simpleResponseRepository.findAllResponsesByQuestionnaireId(id)
                .stream()
                .sorted((r1, r2) -> r1.getDateCreated().compareTo(r2.getDateCreated()))
                .map(r -> {
                    SimpleQuestionnaireResponse response = new SimpleQuestionnaireResponse();
                    response.setId(r.getId());
                    response.setTitle(r.getTitle());
                    return response;
                })
                .collect(Collectors.toList());
        return QuestionnaireResponses.builder()
                .responses(responses)
                .build();
    }

    public SimpleQuestionnaireResponse getResponse(String id) {
        Gson gson = new Gson();
        SimpleResponse response = simpleResponseRepository.findOne(id);
        SimpleQuestionnaireResponse res = new SimpleQuestionnaireResponse();
        res.setTitle(response.getTitle());
        res.setId(response.getId());
        res.setData(gson.fromJson(response.getSimpleResponse(), Map.class));
        return res;
    }
}
