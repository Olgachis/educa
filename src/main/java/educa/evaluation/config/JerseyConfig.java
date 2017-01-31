package educa.evaluation.config;

import educa.evaluation.endpoint.MeEndpoint;
import educa.evaluation.endpoint.PasswordEndpoint;
import educa.evaluation.endpoint.QuestionnaireEndpoint;
import educa.evaluation.endpoint.SummaryEndpoint;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(SummaryEndpoint.class);
        register(MeEndpoint.class);
        register(PasswordEndpoint.class);
        register(QuestionnaireEndpoint.class);
    }

}
