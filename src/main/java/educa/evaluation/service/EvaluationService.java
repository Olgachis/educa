package educa.evaluation.service;

import com.google.gson.Gson;
import educa.evaluation.data.*;
import educa.evaluation.domain.Campus;
import educa.evaluation.domain.QuestionnaireResponse;
import educa.evaluation.domain.User;
import educa.evaluation.repository.CampusRepository;
import educa.evaluation.repository.QuestionnaireResponseRepository;
import educa.evaluation.repository.SectionResponseRepository;
import educa.evaluation.repository.UserRepository;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@Transactional
public class EvaluationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SectionResponseRepository sectionResponseRepository;

    @Autowired
    private DimensionService dimensionService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private CampusRepository campusRepository;

    @Autowired
    private QuestionnaireResponseRepository questionnaireResponseRepository;

    public QuestionnaireResults educaAverage() {
        List<String> usernames =
                StreamSupport.stream(userRepository.findAll().spliterator(), false)
                        .filter(u -> u.getCampus() != null)
                        .map(User::getUsername)
                        .collect(Collectors.toList());


        List<QuestionnaireResults> partialResults = usernames
                .stream()
                .map(u -> listResults(u))
                .filter(results -> results.getQuestions() == results.getMaxQuestions())
                .collect(Collectors.toList());

        return partialResults.stream()
                .reduce((acc, curr) -> QuestionnaireResults.builder()
                        .maxPoints(curr.getMaxPoints() + acc.getMaxPoints())
                        .maxQuestions(curr.getMaxQuestions() + acc.getMaxQuestions())
                        .maxCountingQuestions(curr.getMaxCountingQuestions() + acc.getMaxCountingQuestions())
                        .points(curr.getPoints() + acc.getPoints())
                        .questions(curr.getQuestions() + acc.getQuestions())
                        .dimensionResults(curr.getDimensionResults().keySet().stream()
                                .map(k -> {
                                    DimensionResults accDr = acc.getDimensionResults().get(k);
                                    DimensionResults currDr = curr.getDimensionResults().get(k);
                                    return DimensionResults.builder()
                                            .id(accDr.getId())
                                            .maxPoints(accDr.getMaxPoints() + currDr.getMaxPoints())
                                            .maxQuestions(accDr.getMaxQuestions() + currDr.getMaxQuestions())
                                            .maxCountingQuestions(accDr.getMaxCountingQuestions() + currDr.getMaxCountingQuestions())
                                            .points(accDr.getPoints() + currDr.getPoints())
                                            .questions(accDr.getQuestions() + currDr.getQuestions())
                                            .build();
                                })
                                .collect(Collectors.toMap(dr -> dr.getId().getNumber(), dr -> dr)))
                        .build())
                .map(r -> QuestionnaireResults.builder()
                        .maxPoints(r.getMaxPoints() / partialResults.size())
                        .maxQuestions(r.getMaxQuestions() / partialResults.size())
                        .maxCountingQuestions(r.getMaxCountingQuestions() / partialResults.size())
                        .points(r.getPoints() / partialResults.size())
                        .questions(r.getQuestions() / partialResults.size())
                        .totalAnswered(partialResults.size())
                        .total(usernames.size())
                        .dimensionResults(r.getDimensionResults().entrySet().stream()
                                .map(e -> {
                                    DimensionResults dr = e.getValue();
                                    dr.setMaxPoints(dr.getMaxPoints() / partialResults.size());
                                    dr.setMaxQuestions(dr.getMaxQuestions() / partialResults.size());
                                    dr.setPoints(dr.getPoints() / partialResults.size());
                                    dr.setQuestions(dr.getQuestions() / partialResults.size());
                                    return e;
                                })
                                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())))
                        .build())
                .orElse(QuestionnaireResults.builder().build());
    }

    public QuestionnaireResults listResults() {
        return listResults(securityService.getCurrentUser().getUsername());
    }

    public QuestionnaireResults listResults(@NotNull String username) {
        User user = userRepository.findByUsername(username);
        QuestionnaireData questionnaireData = dimensionService.listQualityModelDimensions(user, false);

        List<DimensionResults> dimensionResults = questionnaireData.getDimensions().values()
                .stream()
                .map(dimensionData -> {
                    List<SubdimensionResults> subdimensionResults = dimensionData.getSubdimensions().values()
                            .stream()
                            .map(subDimensionData -> {
                                QuestionAcc subdimensionAcc = subDimensionData.getQuestions().stream()
                                        .map(question -> {
                                            List<OptionData> options = Optional.ofNullable(question.getOptions())
                                                    .orElse(Arrays.asList(
                                                            new OptionData("true", true),
                                                            new OptionData("false", false)
                                                    ));
                                            OptionData option = options.stream()
                                                    .filter(optionData -> optionData.getName().equals(question.getValue()))
                                                    .findFirst()
                                                    .orElse(null);
                                            return QuestionAcc.builder()
                                                    .value(Optional.ofNullable(option)
                                                            .map(o -> o.isValuable()? question.getPriority():0)
                                                            .orElse(0))
                                                    .questions(Optional.ofNullable(option)
                                                            .map(o -> 1)
                                                            .orElse(0))
                                                    .maxValue(question.getPriority())
                                                    .maxQuestions(1)
                                                    .maxCountingQuestions(Optional.ofNullable(option)
                                                            .map(o -> o.isValuable()?1:0)
                                                            .orElse(0))
                                                    .build();
                                        })
                                        .reduce((acc, current) -> {
                                            return QuestionAcc.builder()
                                                    .maxValue(acc.getMaxValue() + current.getMaxValue())
                                                    .maxQuestions(acc.getMaxQuestions() + current.getMaxQuestions())
                                                    .maxCountingQuestions(acc.getMaxCountingQuestions() + current.getMaxCountingQuestions())
                                                    .questions(acc.getQuestions() + current.getQuestions())
                                                    .value(acc.getValue() + current.getValue())
                                                    .build();
                                        })
                                        .orElse(QuestionAcc.builder().build());
                                return SubdimensionResults.builder()
                                        .id(subDimensionData.getId())
                                        .maxQuestions(subdimensionAcc.getMaxQuestions())
                                        .maxCountingQuestions(subdimensionAcc.getMaxCountingQuestions())
                                        .maxPoints(subdimensionAcc.getMaxValue())
                                        .minimumRequiredQuestions(subdimensionAcc.getMaxQuestions() * 0.7f)
                                        .questions(subdimensionAcc.getQuestions())
                                        .points(subdimensionAcc.getValue())
                                        .sortOrder(subDimensionData.getSortOrder())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    DimensionResults ds = subdimensionResults.stream()
                            .reduce((acc, curr) -> {
                                return SubdimensionResults.builder()
                                        .maxQuestions(acc.getMaxQuestions() + curr.getMaxQuestions())
                                        .maxCountingQuestions(acc.getMaxCountingQuestions() + curr.getMaxCountingQuestions())
                                        .maxPoints(acc.getMaxPoints() + curr.getMaxPoints())
                                        .questions(acc.getQuestions() + curr.getQuestions())
                                        .points(acc.getPoints() + curr.getPoints())
                                        .build();
                            })
                            .map(sub -> {
                                return DimensionResults.builder()
                                        .id(dimensionData.getId())
                                        .maxQuestions(sub.getMaxQuestions())
                                        .maxCountingQuestions(sub.getMaxCountingQuestions())
                                        .maxPoints(sub.getMaxPoints())
                                        .minimumRequiredQuestions(sub.getMaxQuestions() * 0.7f)
                                        .questions(sub.getQuestions())
                                        .points(sub.getPoints())
                                        .subdimensionResults(subdimensionResults.stream()
                                                .collect(Collectors.toMap(sd -> sd.getId().getNumber(), sd -> sd)))
                                        .build();
                            })
                            .get();

                    return ds;
                })
                .collect(Collectors.toList());

        return dimensionResults.stream()
                .reduce((acc, curr) -> {
                    return DimensionResults.builder()
                            .maxQuestions(acc.getMaxQuestions() + curr.getMaxQuestions())
                            .maxCountingQuestions(acc.getMaxCountingQuestions() + curr.getMaxCountingQuestions())
                            .maxPoints(acc.getMaxPoints() + curr.getMaxPoints())
                            .questions(acc.getQuestions() + curr.getQuestions())
                            .points(acc.getPoints() + curr.getPoints())
                            .build();
                })
                .map(d -> QuestionnaireResults.builder()
                        .dimensionResults(dimensionResults.stream()
                                .collect(Collectors.toMap(dr -> dr.getId().getNumber(), dr -> dr)))
                        .maxPoints(d.getMaxPoints())
                        .maxQuestions(d.getMaxQuestions())
                        .maxCountingQuestions(d.getMaxCountingQuestions())
                        .minimumRequiredQuestions(d.getMaxQuestions() * 0.7f)
                        .questions(d.getQuestions())
                        .points(d.getPoints())
                        .openQuestionnaire(user.getCampus().getOpenQuestionnaire())
                        .institutionId(user.getCampus().getId())
                        .institutionName(user.getCampus().getName())
                        .institutionType(user.getCampus().getType())
                        .internship(user.getCampus().getInternship())
                        .initialEducation(user.getCampus().getInitialEducation())
                        .preschool(user.getCampus().getPreschool())
                        .basic(user.getCampus().getBasic())
                        .secondary(user.getCampus().getSecondary())
                        .highSchool(user.getCampus().getHighSchool())
                        .build())
                .orElse(QuestionnaireResults.builder().build());
    }

    public ImprovementPlan getImprovementPlan(boolean filerResults) {
        return getImprovementPlan(securityService.getCurrentUser().getCampus().getId(), filerResults);
    }

    public ImprovementPlan getImprovementPlan(String institutionId, boolean filterResults) {
        Campus campus = campusRepository.findOne(institutionId);
        Campus primaryCampus = campusRepository.findByInstitutionAndPrimaryCampus(campus.getInstitution(), true);
        QuestionnaireResponse response = questionnaireResponseRepository.findByCampus(campus);
        QuestionnaireResponse primaryResponse = questionnaireResponseRepository.findByCampus(primaryCampus);

        ImprovementPlan plan = Optional.ofNullable(response)
                .map(this::mapQuestionnaire)
                .orElseGet(() -> {
                    return getDefaultMap(campus);
                });

        ImprovementPlan primaryPlan = Optional.ofNullable(response)
                .map(this::mapQuestionnaire)
                .orElseGet(() -> {
                    return getDefaultMap(campus);
                });

        Map<String, Boolean> showCampusRelevant = dimensionService.showCampusRelevant(campus);
        Map<String, ImprovementQuestion> primaryQuestions = plan.getQuestions().stream()
                .collect(Collectors.toMap(ImprovementQuestion::getId, Function.identity()));

        plan.setQuestions(plan.getQuestions().stream()
          .filter(q -> {
            String id = q.getDimensionId().getNumber() + ":" + q.getSubdimensionId().getNumber();
            if(filterResults) {
              return campus.getPrimaryCampus() || showCampusRelevant.get(id);
            } else {
              return true;
            }
          })
          .map(q -> {
            String id = q.getDimensionId().getNumber() + ":" + q.getSubdimensionId().getNumber();
            if(showCampusRelevant.get(id)) {
              return q;
            } else {
              return primaryQuestions.get(q.getId());
            }
          })
          .collect(Collectors.toList())
        );

        return plan;
    }

    private ImprovementPlan getDefaultMap(Campus campus) {
        User user = userRepository.findByCampus(campus);
        QuestionnaireData data = dimensionService.listQualityModelDimensions(user, false);
        QuestionnaireResults results = listResults(user.getUsername());
        int priorityYear = (int)Math.floor((results.getMaxQuestions() - results.getMaxCountingQuestions()) / 4.0d);
        List<ImprovementQuestion> questions = data.getDimensions().values().stream()
                .sorted((d1, d2) -> {
                    return d1.getId().getSortOrder().compareTo(d2.getId().getSortOrder());
                })
                .flatMap((dimension) -> {
                    return dimension.getSubdimensions().values().stream()
                            .sorted((s1, s2) -> {
                                return s1.getSortOrder().compareTo(s2.getSortOrder());
                            })
                            .flatMap((subdimension) -> {
                                int maxPoints = subdimension.getQuestions().stream()
                                        .mapToInt(Question::getPriority)
                                        .sum();
                                int points = subdimension.getQuestions().stream()
                                        .filter(Question::isValuable)
                                        .mapToInt(Question::getPriority)
                                        .sum();
                                int maxQuestions = subdimension.getQuestions().stream()
                                        .mapToInt(q -> 1)
                                        .sum();
                                int answeredQuestions = subdimension.getQuestions().stream()
                                        .filter(Question::isValuable)
                                        .mapToInt(q -> 1)
                                        .sum();
                                final int weight;
                                if(maxQuestions != answeredQuestions) {
                                    weight = maxPoints - points;
                                } else {
                                    weight = -1;
                                }
                                return subdimension.getQuestions().stream()
                                        .filter(q -> !q.isValuable())
                                        .map((question) -> {
                                            ImprovementQuestion qres = new ImprovementQuestion();
                                            qres.setQuestion(question.getQuestion());
                                            qres.setId(question.getId());
                                            qres.setDimensionId(dimension.getId());
                                            qres.setSubdimensionId(subdimension.getId());
                                            qres.setSubdimensionWeights(weight);
                                            qres.setPriority(question.getPriority());
                                            qres.setCampusPriority(3);
                                            return qres;
                                        });
                            });
                })
                .collect(Collectors.toList());

        List<ImprovementQuestion> questionData = new ArrayList<>(questions);

        questions = questions.stream()
                .sorted((q1, q2) -> {
                    return (q2.getPriority() * 1000 + q2.getSubdimensionWeights() * 100) -
                           (q1.getPriority() * 1000 + q1.getSubdimensionWeights() * 100);
                })
                .collect(Collectors.toList());


        questionData.stream()
                .sorted((q1, q2) -> {
                    return (q2.getPriority() * 1000 + q2.getSubdimensionWeights() * 100) -
                            (q1.getPriority() * 1000 + q1.getSubdimensionWeights() * 100);
                })
                .limit(priorityYear)
                .forEach(q -> {
                    q.setSelected(true);
                });

        ImprovementPlan result = new ImprovementPlan();

        result.setQuestions(questions);

        return result;
    }

    private ImprovementPlan mapQuestionnaire(QuestionnaireResponse response) {
        return new Gson().fromJson(response.getResponseJson(), ImprovementPlan.class);
    }

    public ImprovementPlan saveImprovementPlan(ImprovementPlan plan) {
        Gson gson = new Gson();
        String jsonPlan = gson.toJson(plan);
        User user = securityService.getCurrentUser();

        QuestionnaireResponse response = Optional.ofNullable(questionnaireResponseRepository.findByCampus(user.getCampus()))
                .orElseGet(() -> {
                        QuestionnaireResponse newResponse = new QuestionnaireResponse();
                        newResponse.setCampus(user.getCampus());
                        return newResponse;
                });

        response.setResponseJson(jsonPlan);
        questionnaireResponseRepository.save(response);

        return plan;
    }

    public QuestionnaireResults listPrediction() {
        return listPrediction(securityService.getCurrentUser().getUsername());
    }

    private QuestionnaireResults listPrediction(String username) {
        User user = userRepository.findByUsername(username);
        ImprovementPlan improvementPlan = getImprovementPlan(user.getCampus().getId(), false);
        QuestionnaireData questionnaireData = dimensionService.listQualityModelDimensions(user, false);

        Map<String, Boolean> plannedResults = improvementPlan.getQuestions().stream()
                .collect(Collectors.toMap(q -> q.getId(), q->q.isSelected()));

        List<DimensionResults> dimensionResults = questionnaireData.getDimensions().values()
                .stream()
                .map(dimensionData -> {
                    List<SubdimensionResults> subdimensionResults = dimensionData.getSubdimensions().values()
                            .stream()
                            .map(subDimensionData -> {
                                QuestionAcc subdimensionAcc = subDimensionData.getQuestions().stream()
                                        .map(question -> {
                                            List<OptionData> options = Optional.ofNullable(question.getOptions())
                                                    .orElse(Arrays.asList(
                                                            new OptionData("true", true),
                                                            new OptionData("false", false)
                                                    ));
                                            OptionData option = options.stream()
                                                    .filter(optionData -> optionData.getName().equals(question.getValue()))
                                                    .findFirst()
                                                    .orElse(null);
                                            int value = Optional.ofNullable(option)
                                                            .map(o -> o.isValuable()? question.getPriority():0)
                                                            .orElse(0);
                                            int questionAnswered = Optional.ofNullable(option)
                                                            .map(o -> 1)
                                                            .orElse(0);
                                            int maxCountingQuestion = Optional.ofNullable(option)
                                                            .map(o -> o.isValuable()?1:0)
                                                            .orElse(0);
                                            if(Optional.ofNullable(plannedResults.get(question.getId())).orElse(false)) {
                                                value = question.getPriority();
                                                maxCountingQuestion = 1;
                                            }
                                            return QuestionAcc.builder()
                                                    .value(value)
                                                    .questions(questionAnswered)
                                                    .maxValue(question.getPriority())
                                                    .maxQuestions(1)
                                                    .maxCountingQuestions(maxCountingQuestion)
                                                    .build();
                                        })
                                        .reduce((acc, current) -> {
                                            return QuestionAcc.builder()
                                                    .maxValue(acc.getMaxValue() + current.getMaxValue())
                                                    .maxQuestions(acc.getMaxQuestions() + current.getMaxQuestions())
                                                    .maxCountingQuestions(acc.getMaxCountingQuestions() + current.getMaxCountingQuestions())
                                                    .questions(acc.getQuestions() + current.getQuestions())
                                                    .value(acc.getValue() + current.getValue())
                                                    .build();
                                        })
                                        .orElse(QuestionAcc.builder().build());
                                return SubdimensionResults.builder()
                                        .id(subDimensionData.getId())
                                        .maxQuestions(subdimensionAcc.getMaxQuestions())
                                        .maxCountingQuestions(subdimensionAcc.getMaxCountingQuestions())
                                        .maxPoints(subdimensionAcc.getMaxValue())
                                        .minimumRequiredQuestions(subdimensionAcc.getMaxQuestions() * 0.7f)
                                        .questions(subdimensionAcc.getQuestions())
                                        .points(subdimensionAcc.getValue())
                                        .sortOrder(subDimensionData.getSortOrder())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    DimensionResults ds = subdimensionResults.stream()
                            .reduce((acc, curr) -> {
                                return SubdimensionResults.builder()
                                        .maxQuestions(acc.getMaxQuestions() + curr.getMaxQuestions())
                                        .maxCountingQuestions(acc.getMaxCountingQuestions() + curr.getMaxCountingQuestions())
                                        .maxPoints(acc.getMaxPoints() + curr.getMaxPoints())
                                        .questions(acc.getQuestions() + curr.getQuestions())
                                        .points(acc.getPoints() + curr.getPoints())
                                        .build();
                            })
                            .map(sub -> {
                                return DimensionResults.builder()
                                        .id(dimensionData.getId())
                                        .maxQuestions(sub.getMaxQuestions())
                                        .maxCountingQuestions(sub.getMaxCountingQuestions())
                                        .maxPoints(sub.getMaxPoints())
                                        .minimumRequiredQuestions(sub.getMaxQuestions() * 0.7f)
                                        .questions(sub.getQuestions())
                                        .points(sub.getPoints())
                                        .subdimensionResults(subdimensionResults.stream()
                                                .collect(Collectors.toMap(sd -> sd.getId().getNumber(), sd -> sd)))
                                        .build();
                            })
                            .get();

                    return ds;
                })
                .collect(Collectors.toList());

        return dimensionResults.stream()
                .reduce((acc, curr) -> {
                    return DimensionResults.builder()
                            .maxQuestions(acc.getMaxQuestions() + curr.getMaxQuestions())
                            .maxCountingQuestions(acc.getMaxCountingQuestions() + curr.getMaxCountingQuestions())
                            .maxPoints(acc.getMaxPoints() + curr.getMaxPoints())
                            .questions(acc.getQuestions() + curr.getQuestions())
                            .points(acc.getPoints() + curr.getPoints())
                            .build();
                })
                .map(d -> QuestionnaireResults.builder()
                        .dimensionResults(dimensionResults.stream()
                                .collect(Collectors.toMap(dr -> dr.getId().getNumber(), dr -> dr)))
                        .maxPoints(d.getMaxPoints())
                        .maxQuestions(d.getMaxQuestions())
                        .maxCountingQuestions(d.getMaxCountingQuestions())
                        .minimumRequiredQuestions(d.getMaxQuestions() * 0.7f)
                        .questions(d.getQuestions())
                        .points(d.getPoints())
                        .openQuestionnaire(user.getCampus().getOpenQuestionnaire())
                        .institutionId(user.getCampus().getId())
                        .institutionName(user.getCampus().getName())
                        .institutionType(user.getCampus().getType())
                        .internship(user.getCampus().getInternship())
                        .initialEducation(user.getCampus().getInitialEducation())
                        .preschool(user.getCampus().getPreschool())
                        .basic(user.getCampus().getBasic())
                        .secondary(user.getCampus().getSecondary())
                        .highSchool(user.getCampus().getHighSchool())
                        .build())
                .orElse(QuestionnaireResults.builder().build());
    }

    public List<QuestionnaireResults> listQuestionnaires() {
        return StreamSupport.stream(userRepository.findAllByRoleName("Institución").spliterator(), false)
                .map(User::getUsername)
                .map(this::listResults)
                .sorted((c1, c2) -> c1.getInstitutionName().compareTo(c2.getInstitutionName()))
                .collect(Collectors.toList());
    }

    public boolean closeQuestionnaire(String id) {
        Campus campus = campusRepository.findOne(id);
        campus.setOpenQuestionnaire(false);
        campusRepository.save(campus);
        return true;
    }

    public boolean openQuestionnaire(String id) {
        Campus campus = campusRepository.findOne(id);
        campus.setOpenQuestionnaire(true);
        campusRepository.save(campus);
        return true;
    }

    @Data
    @Builder
    private static class QuestionAcc {
        private int value;
        private int questions;
        private int maxValue;
        private int maxQuestions;
        private int maxCountingQuestions;
    }

}
