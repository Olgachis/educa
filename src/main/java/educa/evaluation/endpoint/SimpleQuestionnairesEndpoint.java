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
@Path("/questionnaire")
@Produces("application/json")
public class SimpleQuestionnairesEndpoint {
    @Autowired
    private SimpleQuestionnaireService simpleQuestionnaireService;


    @POST
    public SimpleQuestionnaireData createQuestionnaire(SimpleQuestionnaireData data) throws Exception {

        simpleQuestionnaireService.create(data);
        return data;
    }
}
