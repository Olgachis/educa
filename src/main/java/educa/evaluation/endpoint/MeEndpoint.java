package educa.evaluation.endpoint;

import educa.evaluation.data.UserData;
import educa.evaluation.domain.Campus;
import educa.evaluation.domain.User;
import educa.evaluation.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Optional;

@Slf4j
@Component
@Path("/me")
@Produces("application/json")
public class MeEndpoint {

    @Autowired
    private SecurityService securityService;

    @GET
    public UserData getUser() {
        User user = securityService.getCurrentUser();
        String institutionName = Optional.ofNullable(user)
                .map(User::getCampus)
                .map(Campus::getName)
                .orElse(null);
        if(user == null) {
            return null;
        }
        boolean openQuestionnaire = Optional.ofNullable(user)
                .map(User::getCampus)
                .map(Campus::getOpenQuestionnaire)
                .orElse(false);
        return new UserData(institutionName, user.getUsername(), user.getRole().getId(), openQuestionnaire);
    }

}
