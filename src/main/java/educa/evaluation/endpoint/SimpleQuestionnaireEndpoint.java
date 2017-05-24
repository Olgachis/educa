package educa.evaluation.endpoint;

import educa.evaluation.data.QuestionnaireResponses;
import educa.evaluation.data.SimpleQuestionnaireData;
import educa.evaluation.data.SimpleQuestionnaireResponse;
import educa.evaluation.data.SimpleQuestionnaires;
import educa.evaluation.service.SimpleQuestionnaireService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import java.util.List;

@Slf4j
@Component
@Path("/simpleQuestionnaire")
@Produces("application/json")
public class SimpleQuestionnaireEndpoint {

    @Autowired
    private SimpleQuestionnaireService simpleQuestionnaireService;

    @GET
    @Path("/list")
    public SimpleQuestionnaires listSimpleQuestionnaire() {
        return SimpleQuestionnaires.builder()
            .questionnaires(simpleQuestionnaireService.listSimpleQuestionnaires())
            .build();
    }

    @GET
    @Path("/{id}/responses")
    public QuestionnaireResponses listResponses(@PathParam("id") String id) {
        return simpleQuestionnaireService.listResponses(id);
    }

    @GET
    @Path("/responses/{id}")
    public SimpleQuestionnaireResponse getResponse(@PathParam("id") String id) {
        return simpleQuestionnaireService.getResponse(id);
    }

    @POST
    public SimpleQuestionnaireData saveQuestionnaire(SimpleQuestionnaireData data) throws Exception {
        simpleQuestionnaireService.save(data);
        return data;
    }

    @GET
    @Path("/{id}/listFullResponses")
    public QuestionnaireResponses listFullResponses(@PathParam("id") String id) {
        System.out.print("----------- listFullResponses -----------");
        return simpleQuestionnaireService.listFullResponses(id);
    }

}
