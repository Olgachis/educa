package educa.evaluation.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Slf4j
@Component
@Path("/password/{password}")
public class PasswordEndpoint {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GET
    public String getPath(@PathParam("password") String password) {
        return passwordEncoder.encode(password);
    }

}
