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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


@Slf4j
@Service
@Transactional
public class ExportService {


    private static final String FILE_NAME = "/inademweb/repository/MyFirstExcel.xlsx";

    @Autowired
    private SimpleQuestionnaireService simpleQuestionnaireService;

    public void generaDocument(List<String> stringData){
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Datatypes in Java");
        int rowNum = 0;

        for (String item : stringData) {
          //System.out.println("item --------------" + item);
          Row row = sheet.createRow(rowNum++);
          Cell cell = row.createCell(0);
          cell.setCellValue((String) item);
        }

        try {
          FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
          workbook.write(outputStream);
          workbook.close();
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }

        System.out.println("Done");
    }


    public String generateCsv(String id) {

        StringBuilder stringBuilder = new StringBuilder();
        List<String> listResponses = new ArrayList<String>();
        //lista para el orden de las preguntas
        List<String> questionsOrder = new ArrayList<String>();
        List<SimpleQuestionnaireResponse> responses =
            simpleQuestionnaireService.listFullResponses(id)
                .getResponses();

        if(responses.size() > 0) {
            stringBuilder.append(buildHeader(responses.get(responses.size()-1).getData(), questionsOrder));
          //listResponses.add(stringBuilder);
        }

        responses
            .stream()
            .forEach(response -> {
                listResponses.add(response.getId() + "|" + response.getTitle() + "|" + buildResponses(response.getData(), questionsOrder));
                stringBuilder
                    .append(response.getId())
                    .append("|")
                    .append(response.getTitle())
                    .append("|")
                    .append(buildResponses(response.getData(), questionsOrder))
                    .append("\n");

            });
        generaDocument(listResponses);
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

          //System.out.println("recoverQuestion" + recoverQuestion);
          if(recoverQuestion.isPresent()){
            return responseToString(recoverQuestion.get());
          }
          return "";
      })
        .collect(Collectors.joining("|"));
    }

    private String responseToString(Object recoverQuestion){
      Map<String, Object> question = (Map<String, Object>) recoverQuestion;
      StringBuilder stringBuilder = new StringBuilder();

      String questionData = (String) question.get("displayName");

      //System.out.println("questionData: " + questionData + " : " + question.get("type").toString());

      if(question.get("type").equals("options")){
        stringBuilder.append(buildOptionResponse(question));
        return stringBuilder.toString();
      }
      if(question.get("type").equals("multioptions") ){
        return (buildMultivalueResponse(question));
      }

      String value = (String) question.get("value");
      if(value != null){
        value = value.replace("\n", "");
      }else {
        value = "n/d";
      }

      return value;
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
