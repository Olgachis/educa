package educa.evaluation.service;

import com.google.gson.Gson;
import educa.evaluation.data.QuestionnaireResponses;
import educa.evaluation.data.SimpleQuestionnaireData;
import educa.evaluation.data.SimpleQuestionnaireResponse;
import educa.evaluation.domain.SimpleQuestionnaire;
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
import java.nio.charset.StandardCharsets;
import java.util.UUID;

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

    public QuestionnaireResponses listFullResponses(String id) {
        Gson gson = new Gson();
        List<SimpleQuestionnaireResponse> responses = simpleResponseRepository.findAllResponsesByQuestionnaireId(id)
                .stream()
                .sorted((r1, r2) -> r1.getDateCreated().compareTo(r2.getDateCreated()))
                .map(r -> {
                    SimpleQuestionnaireResponse response = new SimpleQuestionnaireResponse();
                    response.setData(gson.fromJson(r.getSimpleResponse(), Map.class));
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

    public void create(SimpleQuestionnaireData data) throws ScriptException {
        Gson gson = new Gson();
        System.out.println("---- create ----");
        System.out.println("---- data ----" + data.getId());
        SimpleQuestionnaire simpleQuestionnaire = Optional.ofNullable(data.getId())
                .map(simpleQuestionnaireRepository::findOne)
                .orElse(new SimpleQuestionnaire());

        if(simpleQuestionnaire.getId() == null){
            simpleQuestionnaire.setTitle(generateTilte(data.getName()));
            simpleQuestionnaire.setTitleFunc("title");
        }


        simpleQuestionnaire.setQuestionsJson("{\"text\":\"A continuación responda cada una de las preguntas en relación al ahorro que lleva a cabo el niño en la cuenta de ahorro que abrió con el apoyo de Fundación EDUCA.\",\"questions\":[{\"id\":\"0parents-account_school_name\",\"displayName\":\"Nombre de la Escuela\",\"type\":\"text\"},{\"id\":\"1parents-account_school_name\",\"displayName\":\"Nombre de la Escuela\",\"type\":\"text\"},{\"id\":\"2parents-account_parent_name\",\"displayName\":\"Nombre del Padre\",\"type\":\"text\"},{\"id\":\"3parents-account_kid_name\",\"displayName\":\"Nombre del niño\",\"type\":\"text\"},{\"id\":\"4parents-account_grade\",\"displayName\":\"Grado:\",\"type\":\"text\"},{\"id\":\"5parents-account_group\",\"displayName\":\"Grupo:\",\"type\":\"text\"},{\"id\":\"6parents-account_bank_account\",\"displayName\":\" ¿Usted continúa usando su cuenta bancaria que abrió con apoyo de Fundación Educa? \",\"type\":\"options\",\"options\":[\"Sí\",\"No es lo que esperaba\",\"No me es útil\",\"No sé cómo utilizarla\",{\"name\":\"Otra: \",\"other\":true}]},{\"id\":\"7parents-account_how much _save\",\"displayName\":\"¿Cuánto ha ahorrado por mes desde que se abrió la cuenta? (Responda a partir de la fecha en que apertura la cuenta)\",\"type\":\"options\",\"options\":[\"Octubre\",\"Noviembre\",\"Diciembre\",\"Enero\",\"Febrero\"]},{\"id\":\"8parents-account_total_amount\",\"displayName\":\"¿Al día de hoy cual es el monto total ahorrado?\",\"type\":\"text\"},{\"id\":\"9parents-account_same_acount\",\"displayName\":\"¿La cuenta de ahorro que abrió tiene algún fin específico?\",\"type\":\"text\"},{\"id\":\"10parents-account_other_movements\",\"displayName\":\"¿Ha realizado algún movimiento en la cuenta además de los depósitos de ahorro?\",\"type\":\"options\",\"options\":[{\"name\":\"Sí, explique \",\"other\":true},\"no\"]},{\"id\":\"11parents-account_account_helped\",\"displayName\":\"¿El abrir una cuenta le ha ayudado a manejar mejor sus finanzas personales o de su familia? \",\"type\":\"options\",\"options\":[\"Sí\",\"No\",\"No lo sé\"]},{\"id\":\"12arents-account_account_comment\",\"displayName\":\"Comentarios\",\"type\":\"text\"}]}");
        System.out.println("---- simpleQuestionnaire ----" + simpleQuestionnaire);
        simpleQuestionnaire = simpleQuestionnaireRepository.save(simpleQuestionnaire);
    }

    private String generateTilte(String simpleQuestionnaireName){
        return  UUID.randomUUID().toString() + simpleQuestionnaireName  ;
    }

}
