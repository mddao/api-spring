package com.minhdd.app.ml.service;

import org.apache.spark.sql.DataFrame;

/**
 * Created by minhdao on 05/03/16.
 */
public class DataSet {
    public DataFrame getData() {
        return data;
    }

    public DataFrame getTraining() {
        return training;
    }

    public DataFrame getCrossValidation() {
        return crossValidation;
    }

    public DataFrame getTest() {
        return test;
    }

    public DataSet(DataFrame data, DataFrame training, DataFrame crossValidation, DataFrame test) {
        this.data = data;
        this.training = training;
        this.crossValidation = crossValidation;
        this.test = test;
    }

    private DataFrame data;
    private DataFrame training;
    private DataFrame crossValidation;
    private DataFrame test;
}