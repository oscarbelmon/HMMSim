package es.uji.belfern.main;

import es.uji.belfern.batch.BatchClassifier;
import es.uji.belfern.batch.BatchClassifierHMM;
import es.uji.belfern.batch.BatchClassifierWeka;
import es.uji.belfern.location.Environment;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RandomVariates;
import weka.core.Utils;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Ensemble {
    private static final String knnOptions = "-K 1 -W 0 -A \"weka.core.neighboursearch.LinearNNSearch -A \\\"weka.core.EuclideanDistance -R first-last\\\"\"";
    private static final String train = "src/main/resources/arturo_train_1.csv";
    private static final String test = "src/main/resources/arturo_test.csv";
    private static final String hmmFile = "src/main/resources/arturo_coso.bin";
//    private static final String test = "src/main/resources/arturo_test.csv";

    static int init = 0;

    public static void main(String[] args) {
//        new Ensemble().go(train);
//        new Ensemble().go2(train, test);
//        for (int i = 0; i < 15000; i += 500) {
//            init = i;
//            new Ensemble().batchClassifiersWeka(train, test);
//            new Ensemble().batchClassifierHMM(hmmFile, test);
//        }
        new Ensemble().batchClassifier(hmmFile, train, test);
    }

    private void go2(final String trainFileName, final String testFileName) {
        try {
            CSVLoader trainLoader = new CSVLoader();
            trainLoader.setSource(new File(trainFileName));
            Instances train = trainLoader.getDataSet();
            train.setClassIndex(train.numAttributes() - 1);

            CSVLoader testLoader = new CSVLoader();
            testLoader.setSource(new File(testFileName));
            Instances test = testLoader.getDataSet();
            test.setClassIndex(test.numAttributes() - 1);

            IBk knn = new IBk();
            knn.setOptions(Utils.splitOptions(knnOptions));
            knn.buildClassifier(train);

            Evaluation evaluation = new Evaluation(train);
            evaluation.evaluateModel(knn, test);

            System.out.println(evaluation.toSummaryString(true));

            System.out.println(evaluation.toMatrixString());

            System.out.println(evaluation.toClassDetailsString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void go(String fileName) {
        Map<Integer, DescriptiveStatistics> statistics = new HashMap<>();
        try {
            File dataFile = new File(fileName);
            CSVLoader loader = new CSVLoader();
            loader.setSource(dataFile);
            Instances allInstances = loader.getDataSet();
            int numAttributes = allInstances.numAttributes();
            allInstances.setClassIndex(numAttributes - 1);

            allInstances.numClasses();

            for (int i = 0; i < allInstances.numClasses(); i++) {
                statistics.put(i, new DescriptiveStatistics());
            }

            Instances resampledInstances;
            int size = allInstances.size();
            Instances train, test;
            IBk knn = new IBk();
            for (int i = 0; i < 1000; i++) {
                System.out.println(i);
                resampledInstances = allInstances.resample(new RandomVariates());

                train = new Instances(resampledInstances, 0, size / 4);
                test = new Instances(resampledInstances, size / 4, 3 * size / 4);

                knn.setOptions(Utils.splitOptions(knnOptions));
                knn.buildClassifier(train);

                Evaluation evaluation = new Evaluation(train);
                evaluation.evaluateModel(knn, test);
                for (int j = 0; j < allInstances.numClasses(); j++) {
                    statistics.get(j).addValue(evaluation.precision(j));
                }
            }
            for (int i = 0; i < allInstances.numClasses(); i++) {
                System.out.println(allInstances.classAttribute().value(i));
                System.out.println(statistics.get(i).getMean());
                System.out.println(statistics.get(i).getStandardDeviation());
                System.out.println(statistics.get(i).getMin());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void batchClassifiersWeka(final String trainFileName, final String testFileName) {
        try {
            CSVLoader trainLoader = new CSVLoader();
            trainLoader.setSource(new File(trainFileName));
            Instances train = trainLoader.getDataSet();
            train.setClassIndex(train.numAttributes() - 1);

            CSVLoader testLoader = new CSVLoader();
            testLoader.setSource(new File(testFileName));
            Instances test = testLoader.getDataSet();
            test.setClassIndex(test.numAttributes() - 1);

            IBk knn = new IBk();
            knn.setOptions(Utils.splitOptions(knnOptions));
            knn.buildClassifier(train);

            BatchClassifier classifier = new BatchClassifierWeka(knn);
            List<Instance> instances = new ArrayList<>();

            for (int i = init; i < init + 10; i++) {
                instances.add(test.instance(i));
            }
            System.out.println(instances.get(0).attribute(instances.get(0).numAttributes() - 1).value((int) instances.get(0).classValue()));
            System.out.println(classifier.estimate(instances));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void batchClassifierHMM(final String hmmFileName, final String testFileName) {
        try {
            Environment environment = Environment.readEnvironmentFromFile(hmmFileName);
            BatchClassifier classifier = new BatchClassifierHMM(environment);

            CSVLoader testLoader = new CSVLoader();
            testLoader.setSource(new File(testFileName));
            Instances test = testLoader.getDataSet();
            test.setClassIndex(test.numAttributes() - 1);

            List<Instance> instances = new ArrayList<>();
            for (int i = init; i < init + 10; i++) {
                instances.add(test.instance(i));
            }
            System.out.println(classifier.estimate(instances));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void batchClassifier(final String hmmFileName, final String trainFileName, final String testFileName) {
        try {
            Environment environment = Environment.readEnvironmentFromFile(hmmFileName);
            BatchClassifier hmmClassifier = new BatchClassifierHMM(environment);

            CSVLoader trainLoader = new CSVLoader();
            trainLoader.setSource(new File(trainFileName));
            Instances train = trainLoader.getDataSet();
            train.setClassIndex(train.numAttributes() - 1);

            CSVLoader testLoader = new CSVLoader();
            testLoader.setSource(new File(testFileName));
            Instances test = testLoader.getDataSet();
            test.setClassIndex(test.numAttributes() - 1);

            IBk knn = new IBk();
            knn.setOptions(Utils.splitOptions(knnOptions));
            knn.buildClassifier(train);

            BatchClassifier wekaClassifier = new BatchClassifierWeka(knn);
            List<Instance> instances = new ArrayList<>();

            for(int j = 0; j < test.numInstances(); j += 5) {
                for (int i = init; i < init + 5; i++) {
                    instances.add(test.instance(i));
                }
                System.out.println(instances.get(0).attribute(instances.get(0).numAttributes() - 1).value((int) instances.get(0).classValue()));
                System.out.println(hmmClassifier.estimate(instances));
                System.out.println(wekaClassifier.estimate(instances));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
