package com.minhdd.app.ml.service.kaggle;

import com.google.common.collect.ImmutableMap;
import com.minhdd.app.Application;
import com.minhdd.app.config.Constants;
import com.minhdd.app.ml.outil.CsvUtil;
import com.minhdd.app.ml.outil.DataFrameUtil;
import com.minhdd.app.ml.service.kaggle.scs.FilesConstants;
import org.apache.spark.ml.feature.*;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.DataTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

import static org.apache.spark.sql.functions.lit;

/**
 * Created by minhdao on 10/03/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles(Constants.SPRING_PROFILE_DEVELOPMENT)
public class FeaturesTransformation {

    @Inject
    SQLContext sqlContext;

    //you have to remove first folder FilesConstants.SCALER
    @Test
    public void scaler() throws IOException {
        DataFrame train = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_ORIGINAL_KAGGLE, true, true).drop("TARGET").drop("ID");
        DataFrame test = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TEST_KAGGLE, true, true).drop("ID");
        DataFrame data = train.unionAll(test);
        data = data.na().replace("var3", ImmutableMap.of(-999999, 2));
        data = data.withColumn("n0", lit(0.0));
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(DataFrameUtil.getFeatureColumns(data))
                .setOutputCol("assembledFeatures");

        DataFrame df = assembler.transform(data);
//
//        MinMaxScaler scaler = new MinMaxScaler().setMin(0).setMax(1)
//                .setInputCol("assembledFeatures")
//                .setOutputCol("features");

//        MinMaxScalerModel scalerModel = scaler.fit(df);
        StandardScaler scaler = new StandardScaler()
                .setInputCol("assembledFeatures")
                .setOutputCol("features")
                .setWithStd(true)
                .setWithMean(false);

        StandardScalerModel scalerModel = scaler.fit(df);
        scalerModel.save(FilesConstants.SCALER);
    }

    @Test
    public void applyScaler() {
        MinMaxScalerModel scalerModel = MinMaxScalerModel.load(FilesConstants.OUTPUT_DIR + "scaler.all.model");
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_MIN, true, true);
        String[] columns = DataFrameUtil.getFeatureColumns(data);
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(columns)
                .setOutputCol("assembledFeatures");

        DataFrame df = assembler.transform(data);
        DataFrame scaledData = scalerModel.transform(df);
        scaledData.show(false);
    }

    @Test
    public void pcaModel() {
        DataFrame data = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_ORIGINAL_KAGGLE, true, true);
        data = data.na().replace("var3", ImmutableMap.of(-999999, 2)).withColumn("n0", lit(0.0));
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(DataFrameUtil.getFeatureColumns(data))
                .setOutputCol("pcain");
        DataFrame df = assembler.transform(data);
        PCAModel pca = new PCA()
                .setInputCol("pcain")
                .setOutputCol("pcaout")
                .setK(261)
                .fit(df);
        try {
            pca.save(FilesConstants.PCA_261);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        DataFrame pcad = pca.transform(df).select("ID", "pca", "TARGET");
//        DataFrame result = DataFrameUtil.splitVectorColumn(sqlContext, pcad, "pca", 2, "ID").drop("pca");
//        System.out.println(df.count());
//        System.out.println(result.count());
//        result.filter("TARGET = 1").show(false);
//        CsvUtil.save(result, FilesConstants.LOCAL_DIR + "pca.csv", true);
    }

    @Test
    public void chisqSelector() {
        DataFrame df = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_ORIGINAL_KAGGLE, true, true);
        df = DataFrameUtil.assembled(df, "chisqin");
        //PCAModel pcaModel = PCAModel.load(FilesConstants.PCA_200);
//        DataFrame pcaOutput = pcaModel.transform(df);
        DataFrame data = df.withColumn("label", df.col("TARGET").cast(DataTypes.DoubleType));
        ChiSqSelector selector = new ChiSqSelector()
                .setNumTopFeatures(1)
                .setFeaturesCol("chisqin")
                .setLabelCol("label")
                .setOutputCol("features");
        ChiSqSelectorModel model = selector.fit(data);
        try {
            model.save(FilesConstants.CHISQ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getColumnName() {
        DataFrame df = CsvUtil.loadCsvFile(sqlContext, FilesConstants.TRAIN_ORIGINAL_KAGGLE, true, true);
        System.out.println(DataFrameUtil.getFeatureColumns(df)[139]);
    }

}
