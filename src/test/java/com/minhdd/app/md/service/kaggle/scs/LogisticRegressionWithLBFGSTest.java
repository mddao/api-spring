package com.minhdd.app.md.service.kaggle.scs;

import com.minhdd.app.Application;
import com.minhdd.app.config.Constants;
import com.minhdd.app.md.service.kaggle.FilesConstants;
import com.minhdd.app.ml.domain.MLConfiguration;
import com.minhdd.app.ml.domain.MLConstants;
import com.minhdd.app.ml.domain.MLService;
import com.minhdd.app.ml.service.kaggle.scs.SantanderCustomerSatisfaction;
import com.minhdd.app.ml.service.kaggle.scs.SantanderCustomerSatisfactionBinaryClassification;
import com.minhdd.app.ml.service.kaggle.scs.SantanderCustomerSatisfactionRegression;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SQLContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

/**
 * Created by minhdao on 06/03/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles(Constants.SPRING_PROFILE_DEVELOPMENT)
public class LogisticRegressionWithLBFGSTest {
    MLService scfBinaryClassification;
    @Inject SQLContext sqlContext;
    @Inject SparkContext sparkContext;

    @Before
    public void init() {
        scfBinaryClassification = new SantanderCustomerSatisfactionBinaryClassification().context(sqlContext, sparkContext);
    }

    /****
     * * Test using Binary Classification : Modify files input
     ****/

    @Test
    public void trainWithBinaryClassificationAndTest() {
        scfBinaryClassification.setFile(null, FilesConstants.TRAIN_MIN, FilesConstants.VALIDATION_MIN, FilesConstants.TEST_MIN);
        scfBinaryClassification.loadData().train().test().getResults();
    }

    @Test
    public void trainWithBinaryClassificationAndProduce() {
        scfBinaryClassification.setFile(null, FilesConstants.TRAIN_MIN, FilesConstants.VALIDATION_MIN, FilesConstants.TEST_MIN);
        MLConfiguration conf = new MLConfiguration().setAlgorithm(MLConstants.BinaryClassification);
        scfBinaryClassification.configure(conf).loadData().train().save(FilesConstants.BinaryClassification_MODEL);
        scfBinaryClassification.test().produce(FilesConstants.TEST_OUTPUT);
    }

    @Test
    public void getSavedBinaryClassificationAndProduce() {
        scfBinaryClassification.restore(FilesConstants.BinaryClassification_MODEL);
        scfBinaryClassification.loadInput(FilesConstants.TEST_KAGGLE).produce(FilesConstants.TEST_OUTPUT);
    }
}

