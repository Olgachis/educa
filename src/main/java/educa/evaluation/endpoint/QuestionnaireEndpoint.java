package educa.evaluation.endpoint;

import educa.evaluation.data.*;
import educa.evaluation.service.DimensionService;
import educa.evaluation.service.EvaluationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import java.util.List;

@Slf4j
@Component
@Path("/qualityEvaluation")
@Produces("application/json")
public class QuestionnaireEndpoint {

    @Autowired
    private DimensionService dimensionService;

    @Autowired
    private EvaluationService evaluationService;

    @GET
    public QuestionnaireData getQualityEvaluationData() {
        return dimensionService.listQualityModelDimensions(true);
    }

    @GET
    @Path("/results")
    public QuestionnaireResults getQualityEvaluationResults() {
        return evaluationService.listResults();
    }

    @GET
    @Path("/prediction")
    public QuestionnaireResults getPredictionResults() {
        return evaluationService.listPrediction();
    }

    @GET
    @Path("/improvementPlan")
    public ImprovementPlan getImprovementPlan() {
        return evaluationService.getImprovementPlan(true);
    }

    @POST
    @Path("/improvementPlan")
    public ImprovementPlan getImprovementPlan(ImprovementPlan plan) {
        return evaluationService.saveImprovementPlan(plan);
    }

    @GET
    @Path("/averageResults")
    public QuestionnaireResults getAverateQualityEvaluationResults() {
        return evaluationService.educaAverage();
    }

    @POST
    @Path("{subdimensionId}")
    public QuestionnaireData saveEvaluation(@PathParam("subdimensionId") String subdimensionId, SubDimensionData subdimension) {
        return dimensionService.saveSubdimension(subdimensionId, subdimension, true);
    }

    @GET
    @Path("sort")
    public QuestionnaireData sortQuestions() {
        return dimensionService.sortQuestions();
    }


}
