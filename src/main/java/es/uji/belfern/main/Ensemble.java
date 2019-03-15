package es.uji.belfern.main;

import es.uji.belfern.batch.BatchClassifier;
import es.uji.belfern.batch.BatchClassifierHMM;
import es.uji.belfern.batch.BatchClassifierWeka;
import es.uji.belfern.location.Environment;
import es.uji.belfern.statistics.Estimate;
import es.uji.belfern.experiment.ExperimentSerializer;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RandomVariates;
import weka.core.Utils;
import weka.core.converters.CSVLoader;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Ensemble {
    private static final String knnOptions = "-K 1 -W 0 -A \"weka.core.neighboursearch.LinearNNSearch -A \\\"weka.core.EuclideanDistance -R first-last\\\"\"";
    private static String rfOptions = "-I 100 -K 0 -S 1";
    private static String mlpOptions = "-L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a";
    private static final String train = "src/main/resources/marta_train.csv";
    private static final String test = "src/main/resources/marta_test.csv";
    private static final String hmmFile = "src/main/resources/marta_ensemble.bin";
    private static final String jsonFileName = "src/main/resources/data_marta_ensemble.json";
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
//        new Ensemble().batchClassifier(hmmFile, train, test);
//        new Ensemble().confutionMatrix();
        new Ensemble().coso();
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
            int batchSize = 10;
            ExperimentSerializer serializer = new ExperimentSerializer(train, test, batchSize)
                    .withClasses(Arrays.asList("Baño", "Cocina", "Comedor", "Despacho", "Dormitorio"))
                    .withAlgorithms(Arrays.asList("hmm", "knn", "rf", "mlp", "nb"));


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


            RandomForest rf = new RandomForest();
            rf.setOptions(Utils.splitOptions(rfOptions));
            rf.buildClassifier(train);

            MultilayerPerceptron mlp = new MultilayerPerceptron();
            mlp.setOptions(Utils.splitOptions(mlpOptions));
            mlp.buildClassifier(train);

            NaiveBayes nb = new NaiveBayes();
            nb.buildClassifier(train);

            BatchClassifier knnClassifier = new BatchClassifierWeka(knn);
            BatchClassifier rfClassifier = new BatchClassifierWeka(rf);
            BatchClassifier mlpClassifier = new BatchClassifierWeka(mlp);
            BatchClassifier nbClassifier = new BatchClassifierWeka(nb);
            List<Instance> instances;
            Estimate hmmEstimate, knnEstimate, rfEstimate, mlpEstimate, nbEstimate;
            String realClass;
            ExperimentSerializer.AlgorithmsResults algorithmsResults;
            for (int j = 0; j < test.numInstances() - batchSize; j += batchSize) {
                algorithmsResults = new ExperimentSerializer.AlgorithmsResults();
                instances = new ArrayList<>();
                for (int i = j; i < j + batchSize; i++) {
                    instances.add(test.instance(i));
                }
                realClass = instances.get(0).attribute(instances.get(0).numAttributes() - 1).value((int) instances.get(0).classValue());
                hmmEstimate = hmmClassifier.estimate(instances);
                knnEstimate = knnClassifier.estimate(instances);
                rfEstimate = rfClassifier.estimate(instances);
                mlpEstimate = mlpClassifier.estimate(instances);
                nbEstimate = nbClassifier.estimate(instances);

                algorithmsResults.addResult(new ExperimentSerializer.Result("hmm", hmmEstimate.label, hmmEstimate.probability));
                algorithmsResults.addResult(new ExperimentSerializer.Result("knn", knnEstimate.label, knnEstimate.probability));
                algorithmsResults.addResult(new ExperimentSerializer.Result("rf", rfEstimate.label, rfEstimate.probability));
                algorithmsResults.addResult(new ExperimentSerializer.Result("mlp", mlpEstimate.label, mlpEstimate.probability));
                algorithmsResults.addResult(new ExperimentSerializer.Result("nb", nbEstimate.label, nbEstimate.probability));
                serializer.addResult(realClass, algorithmsResults);
            }
            Path path = Paths.get(jsonFileName);
            FileWriter fw = new FileWriter(path.toFile());
            fw.write(serializer.toJson());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confutionMatrix() {
        ExperimentSerializer serializer = null;
        try {
            serializer = ExperimentSerializer.fromJson(jsonFileName);

            Map<String, Integer> resultados = new HashMap<>();
            for (String className : serializer.getClasses()) {
                System.out.println(className);
                for (int i = 0; i < serializer.numResultsForClass(className); i++) {
                    ExperimentSerializer.Result results = serializer.forClassAndAlgorithmGetResultAt(className, "knn", i);
                    if (resultados.containsKey(results.className) == false) {
                        resultados.put(results.className, 0);
                    }
                    int previous = resultados.get(results.className);
                    resultados.put(results.className, previous + 1);
                }

                System.out.println(resultados);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void coso() {
        ExperimentSerializer serializer = null;
        try {
            serializer = ExperimentSerializer.fromJson(jsonFileName);

            Map<String, Integer> map;
            for (String className : serializer.getClasses()) {
//                System.out.println(className);
                int ok = 0, not = 0;
                for (int i = 0; i < serializer.numResultsForClass(className); i++) {
                    map = serializer.forClassGetEstimatesAt(className, i, Arrays.asList("mlp", "knn", "nb", "rf", "hmm"));
//                    map = serializer.forClassGetEstimatesAt(className, i, Arrays.asList("knn", "nb", "rf", "hmm"));
//                    map = serializer.forClassGetEstimatesAt(className, i, Arrays.asList("mlp", "nb", "rf", "hmm"));
//                    map = serializer.forClassGetEstimatesAt(className, i, Arrays.asList("mlp", "knn", "rf", "hmm"));
//                    map = serializer.forClassGetEstimatesAt(className, i, Arrays.asList("mlp", "knn", "nb", "hmm"));
//                    map = serializer.forClassGetEstimatesAt(className, i, Arrays.asList("mlp", "knn", "nb", "rf"));
                    if (className.equals(map.entrySet()
                            .stream()
                            .max(Comparator.comparingInt(Map.Entry::getValue))
                            .get()
                            .getKey())) {
                        ok++;
                    } else {
                        not++;
                    }

                }
//                System.out.println("Success: " + ok);
//                System.out.println("Not success: " + not);
                System.out.print(" & " + ok + " (" + String.format(Locale.US, "%.2f", ok*100.0/(ok+not)) + "\\%)");
            }
            System.out.println(" \\\\");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
