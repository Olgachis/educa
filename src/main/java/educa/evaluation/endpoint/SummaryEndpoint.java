package educa.evaluation.endpoint;

import educa.evaluation.data.QuestionnaireResults;
import educa.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

@Component
@Path("/summary")
@Produces("application/json")
public class SummaryEndpoint {

    @Autowired
    private EvaluationService evaluationService;

    @GET
    @Path("/list")
    public List<QuestionnaireResults> listResults() {
        return evaluationService.listQuestionnaires();
    }

    @GET
    @Path("/open/{id}")
    public boolean openQuestionnaire(@PathParam("id") String id) {
        return evaluationService.openQuestionnaire(id);
    }

    @GET
    @Path("/close/{id}")
    public boolean closeQuestionnaire(@PathParam("id") String id) {
        return evaluationService.closeQuestionnaire(id);
    }


}
