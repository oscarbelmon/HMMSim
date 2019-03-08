package es.uji.belfern.main;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.RandomVariates;
import weka.core.Utils;
import weka.core.converters.CSVLoader;
import weka.core.converters.Loader;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Ensemble {
    public static void main(String[] args) {
        new Ensemble().go("src/main/resources/validacion1.csv");
    }

    private void go(String fileName) {
        String knnOptions = "-K 1 -W 0 -A \"weka.core.neighboursearch.LinearNNSearch -A \\\"weka.core.EuclideanDistance -R first-last\\\"\"";
        DescriptiveStatistics statistics = new DescriptiveStatistics();
        try {
            File dataFile = new File(fileName);
            CSVLoader loader = new CSVLoader();
            loader.setSource(dataFile);
            Instances allInstances = loader.getDataSet();
            int numAttr = allInstances.numAttributes();
            allInstances.setClassIndex(numAttr - 1);
            Instances resampledInstances;
            int size = allInstances.size();
            Instances train, test;
            IBk knn = new IBk();
            for(int i = 0; i < 100; i++) {
                resampledInstances = allInstances.resample(new RandomVariates());

                train = new Instances(resampledInstances, 0, size / 2);
                test = new Instances(resampledInstances, size / 2, size / 2);

                knn.setOptions(Utils.splitOptions(knnOptions));
                knn.buildClassifier(train);

                Evaluation evaluation = new Evaluation(train);
                evaluation.evaluateModel(knn, test);
                statistics.addValue(evaluation.fMeasure(2));
            }
            System.out.println(statistics.getMean());
            System.out.println(statistics.getStandardDeviation());
            System.out.println(statistics.getMin());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
