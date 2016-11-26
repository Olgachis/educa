package educa.evaluation.endpoint;

import educa.evaluation.data.DimensionData;
import educa.evaluation.data.QuestionnaireData;
import educa.evaluation.data.SubDimensionData;
import educa.evaluation.service.DimensionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;

@Slf4j
@Component
@Path("/qualityEvaluation")
@Produces("application/json")
public class QuestionnaireEndpoint {

    @Autowired
    private DimensionService dimensionService;

    @GET
    public QuestionnaireData getQualityEvaluationData() {
        return dimensionService.listQualityModelDimensions();
    }

    @POST
    @Path("{subdimensionId}")
    public QuestionnaireData saveEvaluation(@PathParam("subdimensionId") String subdimensionId, SubDimensionData subdimension) {
        return dimensionService.saveSubdimension(subdimensionId, subdimension);
    }


}
