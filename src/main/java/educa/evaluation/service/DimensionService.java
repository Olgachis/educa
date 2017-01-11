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
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.lang.reflect.Array;
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
        types.put("A.C.", "ac");
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

    public QuestionnaireData sortQuestions() {
        log.info("Resorting");
        for(Questionnaire questionnaire : questionnaireRepository.findAll()) {
            int i = 0;
            int digits = questionnaire.getSections().stream()
                    .map(Section::getSubdimensionId)
                    .mapToInt(s -> StringUtils.countOccurrencesOf(s, "."))
                    .max()
                    .orElse(-1) + 1;

            int base = questionnaire.getSections().stream()
                    .map(Section::getSubdimensionId)
                    .flatMapToInt(s -> Arrays.asList(s.split("\\.")).stream().mapToInt(Integer::valueOf))
                    .max()
                    .orElse(-1) + 1;

            for(Section s : questionnaire.getSections().stream().sorted((s1, s2) -> {
                Integer v1 = evalNumber(s1.getSubdimensionId(), digits, base);
                Integer v2 = evalNumber(s2.getSubdimensionId(), digits, base);
                return v1 - v2;
            }).collect(Collectors.toList())) {
                DbQuestions qs = gson.fromJson(s.getQuestionJson(), DbQuestions.class);
                int j = 0;
                for(DbQuestion q : qs.getQuestions()) {
                    q.setSortOrder(j++);
                }
                s.setSortOrder(i++);
                s.setQuestionJson(gson.toJson(qs));
                log.debug("ASSIGNED ORDER {} {}", s.getSubdimensionId(), s.getSortOrder());
            }
            questionnaireRepository.save(questionnaire);
        }


        return null;
    }

    public QuestionnaireData listQualityModelDimensions(User user) {
        Questionnaire questionnaire = questionnaireRepository.findOne(types.get(user.getInstitution().getType()));

        List<String> dimensionNames = questionnaire.getSections().stream()
                .sorted((s1, s2) -> Integer.parseInt(s1.getDimensionId()) - Integer.parseInt(s2.getDimensionId()))
                .map(Section::getDimension)
                .distinct()
                .collect(Collectors.toList());


        Map<String, DimensionData> dimensions = questionnaire.getSections().stream()
                .collect(Collectors.groupingBy(section -> {
                    return new DimensionData.DimensionDataId(section.getDimensionId(), section.getDimension(), dimensionNames.indexOf(section.getDimension()));
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
                                        .sorted((q1, q2) -> q1.getSortOrder().compareTo(q2.getSortOrder()))
                                        .map(q -> {
                                            String response = findResponse(sectionResponse, q.getId());
                                            boolean valuable = "true".equals(response) || Optional.ofNullable(q.getOptions())
                                                    .map(options -> {
                                                        return options.stream()
                                                                .filter(OptionData::isValuable)
                                                                .map(OptionData::getName)
                                                                .collect(Collectors.toList())
                                                                .contains(response);
                                                    })
                                                    .orElse(false);
                                            return new Question(q.getId(), "checkbox", q.getQuestion(), response, valuable, q.getOptions(), q.getPriority());
                                        })
                                        .collect(Collectors.toList());

                                return new SubDimensionData(
                                        new DimensionData.DimensionDataId(section.getSubdimensionId(), section.getSubdimension(), dimensionNames.indexOf(section.getDimension())),
                                        questions,
                                        Optional.ofNullable(sectionResponse).map(SectionResponse::getComments).orElse(null),
                                        section.getSortOrder()
                                );
                            })
                            .collect(Collectors.toMap(s -> s.getId().getNumber(), s-> s));
                    return new DimensionData(entry.getKey(), subdimensions);
                })
                .collect(Collectors.toMap(d -> d.getId().getNumber(), d -> d));

        return QuestionnaireData.builder()
                .dimensions(dimensions)
                .institutionName(user.getInstitution().getName())
                .institutionType(user.getInstitution().getType())
                .internship(user.getInstitution().getInternship())
                .initialEducation(user.getInstitution().getInitialEducation())
                .preschool(user.getInstitution().getPreschool())
                .basic(user.getInstitution().getBasic())
                .secondary(user.getInstitution().getSecondary())
                .highSchool(user.getInstitution().getHighSchool())
                .build();
    }

    public QuestionnaireData listQualityModelDimensions() {
        return listQualityModelDimensions(securityService.getCurrentUser());
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
                        (questions.getDependsOn().equals("high_school") && institution.getHighSchool()) ||
                        (questions.getDependsOn().equals("preschool") && institution.getPreschool()) ||
                        (questions.getDependsOn().equals("secondary") && institution.getSecondary()));
    }

    private Integer evalNumber(String dotted, int maxDigits, int base) {
        Integer[] digits = new Integer[maxDigits];
        int i = 0;
        for(String part : dotted.split("\\.")) {
            digits[i++] = Integer.parseInt(part);
        }
        for(; i < maxDigits; i++) {
            digits[i] = 0;
        }
        int result = 0;
        for(int j = maxDigits - 1; j >= 0; j--) {
            result += digits[maxDigits - j - 1] * Math.pow(base, j);
        }
        return result;
    }


}
