package educa.evaluation.endpoint;

import educa.evaluation.data.CampusData;
import educa.evaluation.data.QuestionnaireResults;
import educa.evaluation.service.EvaluationService;
import educa.evaluation.service.CampusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

@Component
@Path("/institution")
@Produces("application/json")
public class CampusEndpoint {
    @Autowired
    private CampusService campusService;

    @GET
    @Path("/list")
    public List<CampusData> listPrimary() {
        return campusService.listPrimary();
    }

    @POST
    public CampusData saveCampus(CampusData data) throws Exception {
        campusService.saveCampus(data);
        return data;
    }
}
