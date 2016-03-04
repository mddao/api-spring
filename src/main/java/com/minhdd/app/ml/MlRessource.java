package com.minhdd.app.ml;


import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.service.LinearResgresionService;
import com.minhdd.app.ml.service.MLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

/**
 * Created by minhdao on 18/02/16.
 */
@RestController
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
@RequestMapping("/spark/ml")
public class MlRessource {
    private final Logger logger = LoggerFactory.getLogger(MlRessource.class);

    @Inject
    LinearResgresionService ml;

    @RequestMapping(value = "/lr", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> get() {
        MLConfiguration conf = new MLConfiguration();
        conf.setMaxIteration(10).setRegParam(0.3).setElasticNetParam(0.8);
        ml.loadFile("libsvm", "data/mllib/sample_linear_regression_data.txt").configure(conf).train();
        Map<String, Object> responses = ml.getResults();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}
