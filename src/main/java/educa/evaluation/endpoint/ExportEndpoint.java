package educa.evaluation.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import educa.evaluation.service.ExportService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Slf4j
@Component
@Path("/export/{id}")
public class ExportEndpoint {

    @Autowired
    private ExportService exportService;

    @GET
    public String getCsv(@PathParam("id") String id) {
        return exportService.generateCsv(id);
    }

}
