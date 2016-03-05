package com.minhdd.app.ml.controller;


import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.example.EstimatorTransformerParam;
import com.minhdd.app.ml.example.LinearRegressionService;
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
public class MachineLearning {
    private final Logger logger = LoggerFactory.getLogger(MachineLearning.class);

    @Inject
    LinearRegressionService linearRegressionService;

    @Inject
    EstimatorTransformerParam estimatorTransformerParam;

    @RequestMapping(value = "/lr", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> lr() {
        MLConfiguration conf = new MLConfiguration().setMaxIteration(10).setRegParam(0.3).setElasticNetParam(0.8);
        linearRegressionService.loadFile("libsvm", "data/mllib/sample_linear_regression_data.txt");
        return new ResponseEntity<>(linearRegressionService.loadData().configure(conf).train().getResults(), HttpStatus.OK);
    }

    @RequestMapping(value = "/etp", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> etp() {
        MLConfiguration conf = new MLConfiguration().setMaxIteration(10).setRegParam(0.01);
        return new ResponseEntity<>(estimatorTransformerParam.loadData().configure(conf).train().getResults(), HttpStatus.OK);
    }
}