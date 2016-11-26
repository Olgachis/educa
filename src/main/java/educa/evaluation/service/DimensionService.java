package educa.evaluation.service;

import com.google.gson.Gson;
import educa.evaluation.data.*;
import educa.evaluation.domain.*;
import educa.evaluation.repository.QuestionnaireRepository;
import educa.evaluation.repository.SectionRepository;
import educa.evaluation.repository.SectionResponseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class DimensionService {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private SectionResponseRepository sectionResponseRepository;

    private Gson gson;

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    private Map<String, String> types;

    public DimensionService() {
        types = new HashMap<>();
        types.put("I.A.P.", "iap");
        gson = new Gson();
    }

    public QuestionnaireData saveSubdimension(String subdimensionId, SubDimensionData subdimension) {
        User user = securityService.getCurrentUser();
        Questionnaire questionnaire = questionnaireRepository.findOne(types.get(user.getInstitution().getType()));

        Section section = sectionRepository.findByQuestionnaireAndSubdimensionId(questionnaire, subdimensionId);
        SectionResponse responseDomain = Optional.ofNullable(sectionResponseRepository.findByUserAndSection(user, section))
                .orElseGet(() -> {
                    SectionResponse r = new SectionResponse();
                    r.setQuestionnaire(questionnaire);
                    r.setUser(user);
                    r.setSection(section);
                    return r;
                });

        List<QuestionResponse> responses = subdimension.getQuestions().stream()
                .map(q -> {
                    return new QuestionResponse(q.getId(), q.getValue());
                })
                .collect(Collectors.toList());

        SubdimensionResponse response = new SubdimensionResponse(responses, subdimension.getComment());
        responseDomain.setResponseJson(gson.toJson(response));
        responseDomain.setComments(subdimension.getComment());

        sectionResponseRepository.save(responseDomain);

        return listQualityModelDimensions();
    }

    public QuestionnaireData listQualityModelDimensions() {
        User user = securityService.getCurrentUser();
        Questionnaire questionnaire = questionnaireRepository.findOne(types.get(user.getInstitution().getType()));


        Map<String, DimensionData> dimensions = questionnaire.getSections().stream()
                .collect(Collectors.groupingBy(section -> {
                    return new DimensionData.DimensionDataId(section.getDimensionId(), section.getDimension());
                }))
                .entrySet()
                .stream()
                .map(entry -> {
                    Map<String, SubDimensionData> subdimensions = entry.getValue().stream()
                            .filter(s -> checkElegibility(user, s))
                            .sorted()
                            .map(section -> {
                                DbQuestions dbQuestions = gson.fromJson(section.getQuestionJson(), DbQuestions.class);
                                SectionResponse sectionResponse = sectionResponseRepository.findByUserAndSection(user, section);

                                List<Question> questions = dbQuestions.getQuestions().stream()
                                        .sorted()
                                        .map(q -> {
                                            return new Question(q.getId(), "checkbox", q.getQuestion(), findResponse(sectionResponse, q.getId()));
                                        })
                                        .collect(Collectors.toList());

                                return new SubDimensionData(
                                        new DimensionData.DimensionDataId(section.getSubdimensionId(), section.getSubdimension()),
                                        questions,
                                        Optional.ofNullable(sectionResponse).map(SectionResponse::getComments).orElse(null)
                                );
                            })
                            .collect(Collectors.toMap(s -> s.getId().getNumber(), s-> s));
                    return new DimensionData(entry.getKey(), subdimensions);
                })
                .collect(Collectors.toMap(d -> d.getId().getNumber(), d -> d));

        return new QuestionnaireData(dimensions);
    }

    private String findResponse(SectionResponse sectionResponse, String id) {
        if(sectionResponse == null) {
            return null;
        }
        SubdimensionResponse response = gson.fromJson(sectionResponse.getResponseJson(), SubdimensionResponse.class);
        return Optional.ofNullable(response.getResponses())
                .orElse(Collections.emptyList())
                .stream()
                .filter(r -> r.getQuestionId().equals(id))
                .findFirst()
                .map(QuestionResponse::getResponse)
                .orElse(null);
    }

    private boolean checkElegibility(User user, Section s) {
        DbQuestions questions = gson.fromJson(s.getQuestionJson(), DbQuestions.class);
        Institution institution = user.getInstitution();

        return questions.getDependsOn() == null ||
                (questions.getDependsOn() != null &&
                    (questions.getDependsOn().equals("internship") && institution.getInternship()) ||
                    (questions.getDependsOn().equals("initialEducation") && institution.getInitialEducation()) ||
                    (questions.getDependsOn().equals("basic") && institution.getBasic()) ||
                    (questions.getDependsOn().equals("highSchool") && institution.getHighSchool()) ||
                    (questions.getDependsOn().equals("preschool") && institution.getPreschool()) ||
                    (questions.getDependsOn().equals("secondary") && institution.getSecondary()));
    }
}
