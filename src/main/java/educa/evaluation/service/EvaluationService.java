package educa.evaluation.service;

import com.google.gson.Gson;
import educa.evaluation.data.*;
import educa.evaluation.domain.Campus;
import educa.evaluation.domain.Campus;
import educa.evaluation.domain.QuestionnaireResponse;
import educa.evaluation.domain.User;
import educa.evaluation.repository.InstitutionRepository;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
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
    private InstitutionRepository institutionRepository;

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

    public ImprovementPlan getImprovementPlan() {
        return getImprovementPlan(securityService.getCurrentUser().getCampus().getId());
    }

    public ImprovementPlan getImprovementPlan(String institutionId) {
        Campus campus = institutionRepository.findOne(institutionId);
        QuestionnaireResponse response = questionnaireResponseRepository.findByCampus(campus);

        return Optional.ofNullable(response)
                .map(this::mapQuestionnaire)
                .orElseGet(() -> {
                    return getDefaultMap(campus);
                });
    }

    private ImprovementPlan getDefaultMap(Campus campus) {
        User user = userRepository.findByCampus(campus);
        QuestionnaireData data = dimensionService.listQualityModelDimensions(user, false);
        QuestionnaireResults results = listResults(user.getUsername());
        int priorityYear = (int)Math.ceil((results.getMaxQuestions() - results.getMaxCountingQuestions()) / 4.0f);
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
                                return subdimension.getQuestions().stream()
                                        .filter(q -> !q.isValuable())
                                        .map((question) -> {
                                            ImprovementQuestion qres = new ImprovementQuestion();
                                            qres.setQuestion(question.getQuestion());
                                            qres.setId(question.getId());
                                            qres.setDimensionId(dimension.getId());
                                            qres.setSubdimensionId(subdimension.getId());
                                            qres.setPriority(question.getPriority());
                                            return qres;
                                        });
                            });
                })
                .collect(Collectors.toList());

        List<ImprovementQuestion> questionData = new ArrayList<>(questions);

        questionData.stream()
                .sorted((q1, q2) -> {
                    return (-q1.getPriority()) - (-q2.getPriority());
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
