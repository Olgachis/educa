package educa.evaluation.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import educa.evaluation.data.SimpleQuestionnaireResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class ExportService {



    @Autowired
    private SimpleQuestionnaireService simpleQuestionnaireService;

    public String generateCsv(String id) {
        StringBuilder stringBuilder = new StringBuilder();
        //lista para el orden de las preguntas
        List<String> questionsOrder = new ArrayList<String>();
        List<SimpleQuestionnaireResponse> responses =
            simpleQuestionnaireService.listFullResponses(id)
                .getResponses();

        if(responses.size() > 0) {
            stringBuilder.append(buildHeader(responses.get(0).getData(), questionsOrder));
        }

        responses
            .stream()
            .forEach(response -> {
                stringBuilder
                    .append(response.getId())
                    .append("|")
                    .append(response.getTitle())
                    .append("|")
                    .append(buildResponses(response.getData(), questionsOrder))
                    .append("\n");
            });

        return stringBuilder.toString();
    }

    private String buildHeader(Map<String, Object> data, List<String> questionsOrder) {
        StringBuilder header = new StringBuilder();

        List<Object> questions = (List<Object>) data.get("questions");
        header
            .append("id")
            .append("|")
            .append("name")
            .append("|")
            .append(questions
                .stream()
                .map(q -> {
                    Map<String, Object> question = (Map<String, Object>) q;
                    String questionData;
                    questionsOrder.add((String) question.get("displayName"));
                    if(question.get("type").equals("multioptions")){
                      questionData = buildMultivalueQuestion(question);
                    }else{
                      questionData = (String) question.get("displayName");
                    }
                    return questionData;
                })
                .collect(Collectors.joining("|")))
            .append("\n");
        return header.toString();
    }

    private String buildMultivalueQuestion(Map<String, Object> question){
      List<Object> options = (List<Object>) question.get("options");
      String questionData = (String) question.get("displayName");
      return options
      .stream()
      .map(o ->{
        Map<String, Object> optionQuestion = (Map<String, Object>) o;
        String option = (String) optionQuestion.get("name");
        return questionData + ":" + option;
      })
      .collect(Collectors.joining("|"));
    }

    private String buildResponses(Map<String, Object> data, List<String> questionsOrder) {
      List<Object> questions = (List<Object>) data.get("questions");

      return questionsOrder.stream()
        .map(q ->{
        Optional<Object> recoverQuestion = questions
          .stream()
          .filter(a -> {
            Map<String, Object> question = (Map<String, Object>) a;
            String displayName = (String) question.get("displayName");
            return displayName.equals(q);
          })
          .findFirst();
          return responseToString(recoverQuestion.get());
      })
        .collect(Collectors.joining("|"));
      /*
        return questions
            .stream()
            .map(q -> {
                Map<String, Object> question = (Map<String, Object>) q;
                StringBuilder stringBuilder = new StringBuilder();
                if(question.get("type").equals("options")){
                  stringBuilder.append(buildOptionResponse(question));
                  return stringBuilder.toString();
                }
                if(question.get("type").equals("multioptions") ){
                  return (buildMultivalueResponse(question));
                }
                return (String) question.get("value");
            })
            .collect(Collectors.joining("|"));
            */
    }

    private String responseToString(Object recoverQuestion){
      Map<String, Object> question = (Map<String, Object>) recoverQuestion;
      StringBuilder stringBuilder = new StringBuilder();
      if(question.get("type").equals("options")){
        stringBuilder.append(buildOptionResponse(question));
        return stringBuilder.toString();
      }
      if(question.get("type").equals("multioptions") ){
        return (buildMultivalueResponse(question));
      }
      return (String) question.get("value");
    }

    private String buildMultivalueResponse(Map<String, Object> question){
      List<Object> options = (List<Object>) question.get("options");
      String questionData = (String) question.get("displayName");
      StringBuilder responseBuilder = new StringBuilder();
      return options
      .stream()
      .map(o ->{
        Map<String, Object> option = (Map<String, Object>) o;
          String selected;
          if(option.get("value") != null){
            selected = "seleccionada";
            if(option.get("other") != null){
              selected += (String) option.get("otherValue");
            }
          }else{
            selected = "No seleccionada";
          }
        return selected;
      })
      .collect(Collectors.joining("|"));
    }

  private String buildOptionResponse( Map<String, Object> question){
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append((String) question.get("value"));
    List<Object> options = (List<Object>) question.get("options");
    options
      .stream()
      .forEach(o -> {
        Map<String, Object> response = (Map<String, Object>) o;
        if(question.get("value") != null && response.get("other") != null){
          stringBuilder.append(" : ");
          stringBuilder.append((String)response.get("otherValue"));
        }
      });

    return stringBuilder.toString();
  }
}
