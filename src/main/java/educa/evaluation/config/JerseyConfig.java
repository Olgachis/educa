package educa.evaluation.config;

import educa.evaluation.endpoint.MeEndpoint;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(MeEndpoint.class);
    }

}
