package educa.evaluation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private DimensionService dimensionService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        dimensionService.sortQuestions();
    }
}
