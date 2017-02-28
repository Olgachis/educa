package educa.evaluation.data;

import lombok.Builder;
import lombok.Data;

import java.util.*;

@Data
@Builder
public class QuestionnaireResults {

    private Map<String, DimensionResults> dimensionResults;

    private String institutionId;
    private String institutionName;
    private String institutionType;
    private String institutionLevel;

    private boolean internship;
    private boolean initialEducation;
    private boolean preschool;
    private boolean basic;
    private boolean secondary;
    private boolean highSchool;

    private float maxQuestions;
    private float maxCountingQuestions;
    private float minimumRequiredQuestions;
    private float maxPoints;
    private float questions;
    private float points;

    private int totalAnswered;
    private int total;

    private boolean openQuestionnaire;

}
