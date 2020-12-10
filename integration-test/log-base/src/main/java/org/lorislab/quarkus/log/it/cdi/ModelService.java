package org.lorislab.quarkus.log.it.cdi;

import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;

@Slf4j
@ApplicationScoped
public class ModelService {

    public Model model(Model model) {
        log.info("Model: {}", model.param);
        return model;
    }

    public SubModel subModel(SubModel model) {
        log.info("SubModel: {}/{}", model.param, model.param2);
        return model;
    }

    public ModelToString modeToString(ModelToString model) {
        log.info("ModelToString: {}", model.param);
        return model;
    }
}
