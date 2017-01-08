package educa.evaluation.service;

import educa.evaluation.data.*;
import educa.evaluation.domain.User;
import educa.evaluation.repository.SectionResponseRepository;
import educa.evaluation.repository.UserRepository;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@Transactional(readOnly = true)
public class EvaluationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SectionResponseRepository sectionResponseRepository;

    @Autowired
    private DimensionService dimensionService;

    @Autowired
    private SecurityService securityService;

    public QuestionnaireResults educaAverage() {
        List<String> usernames =
                StreamSupport.stream(userRepository.findAll().spliterator(), false)
                        .filter(u -> u.getInstitution() != null)
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
                                            .points(accDr.getPoints() + currDr.getPoints())
                                            .questions(accDr.getQuestions() + currDr.getQuestions())
                                            .build();
                                })
                                .collect(Collectors.toMap(dr -> dr.getId().getNumber(), dr -> dr)))
                        .build())
                .map(r -> QuestionnaireResults.builder()
                        .maxPoints(r.getMaxPoints() / partialResults.size())
                        .maxQuestions(r.getMaxQuestions() / partialResults.size())
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
        return listResults(/*securityService.getCurrentUser()*/ userRepository.findByUsername("DAMAS").getUsername());
    }

    public QuestionnaireResults listResults(@NotNull String username) {
        User user = userRepository.findByUsername(username);
        QuestionnaireData questionnaireData = dimensionService.listQualityModelDimensions(user);

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
                                                    .map(o -> {
                                                        if(o.isValuable()) {
                                                            return question.getPriority();
                                                        } else {
                                                            return 0;
                                                        }
                                                    })
                                                    .orElse(0);
                                            return QuestionAcc.builder()
                                                    .value(value)
                                                    .questions(Optional.ofNullable(option)
                                                            .map(o -> 1)
                                                            .orElse(0))
                                                    .maxValue(question.getPriority())
                                                    .maxQuestions(1)
                                                    .build();
                                        })
                                        .reduce((acc, current) -> {
                                            return QuestionAcc.builder()
                                                    .maxValue(acc.getMaxValue() + current.getMaxValue())
                                                    .maxQuestions(acc.getMaxQuestions() + current.getMaxQuestions())
                                                    .questions(acc.getQuestions() + current.getQuestions())
                                                    .value(acc.getValue() + current.getValue())
                                                    .build();
                                        })
                                        .orElse(QuestionAcc.builder().build());
                                return SubdimensionResults.builder()
                                        .id(subDimensionData.getId())
                                        .maxQuestions(subdimensionAcc.getMaxQuestions())
                                        .maxPoints(subdimensionAcc.getMaxValue())
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
                                        .maxPoints(acc.getMaxPoints() + curr.getMaxPoints())
                                        .questions(acc.getQuestions() + curr.getQuestions())
                                        .points(acc.getPoints() + curr.getPoints())
                                        .build();
                            })
                            .map(sub -> {
                                return DimensionResults.builder()
                                        .id(dimensionData.getId())
                                        .maxQuestions(sub.getMaxQuestions())
                                        .maxPoints(sub.getMaxPoints())
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
                        .questions(d.getQuestions())
                        .points(d.getPoints())
                        .institutionName(user.getInstitution().getName())
                        .institutionType(user.getInstitution().getType())
                        .internship(user.getInstitution().getInternship())
                        .initialEducation(user.getInstitution().getInitialEducation())
                        .preschool(user.getInstitution().getPreschool())
                        .basic(user.getInstitution().getBasic())
                        .secondary(user.getInstitution().getSecondary())
                        .highSchool(user.getInstitution().getHighSchool())
                        .build())
                .orElse(QuestionnaireResults.builder().build());
    }

    @Data
    @Builder
    private static class QuestionAcc {
        private int value;
        private int questions;
        private int maxValue;
        private int maxQuestions;
    }

}
