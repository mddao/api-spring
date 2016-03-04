package com.minhdd.app.ml.service;

import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.ml.regression.LinearRegressionTrainingSummary;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mdao on 04/03/2016.
 */
@Component
public class LinearResgresionService implements MLService {
    private final Logger logger = LoggerFactory.getLogger(LinearResgresionService.class);

    @Inject
    SQLContext sqlContext;

    private DataFrame training;
    private LinearRegression lr;
    private LinearRegressionModel lrModel;

    @Override
    public void loadDataSet(String path) {
        training = sqlContext.read().format("libsvm").load(path);
    }

    @Override
    public void configure() {
        lr = new LinearRegression()
                .setMaxIter(10)
                .setRegParam(0.3)
                .setElasticNetParam(0.8);
    }

    @Override
    public void train() {
        lrModel = lr.fit(training);
    }

    @Override
    public Map<String, Object> getResults() {
        logger.info("Coefficients: " + lrModel.coefficients() + " Intercept: " + lrModel.intercept());

        // Summarize the model over the training set and print out some metrics
        LinearRegressionTrainingSummary trainingSummary = lrModel.summary();
        logger.info("numIterations: " + trainingSummary.totalIterations());
        logger.info("objectiveHistory: " + Vectors.dense(trainingSummary.objectiveHistory()));
        trainingSummary.residuals().show();
        logger.info("RMSE: " + trainingSummary.rootMeanSquaredError());
        logger.info("r2: " + trainingSummary.r2());

        Map<String, Object> responses = new HashMap<>();
        responses.put("intercept", lrModel.intercept());
        responses.put("numIterations", trainingSummary.totalIterations());
        responses.put("RMSE", trainingSummary.rootMeanSquaredError());
        responses.put("r2", trainingSummary.r2());
        return responses;
    }
}
