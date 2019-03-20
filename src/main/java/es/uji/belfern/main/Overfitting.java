package es.uji.belfern.main;

import es.uji.belfern.batch.BatchClassifier;
import es.uji.belfern.batch.BatchClassifierHMM;
import es.uji.belfern.experiment.ExperimentSerializer;
import es.uji.belfern.location.Environment;
import es.uji.belfern.statistics.Estimate;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Overfitting {
    private static final String knnOptions = "-K 1 -W 0 -A \"weka.core.neighboursearch.LinearNNSearch -A \\\"weka.core.EuclideanDistance -R first-last\\\"\"";
    private static final String rfOptions = "-I 100 -K 0 -S 1";
    private static final String mlpOptions = "-L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a";
    private static final String DIRECTORY = "src/main/resources/";
    private List<String> classes;
    private String testFileName;
    private String hmmFileName;
    private String jsonFileName;

    public Overfitting withTestFileName(final String testFileName) {
        this.testFileName = testFileName;
        return this;
    }

    public Overfitting withHmmFileName(final String hmmFileName) {
        this.hmmFileName = hmmFileName;
        return this;
    }

    public Overfitting withJsonFileName(final String jsonFileName) {
        this.jsonFileName = jsonFileName;
        return this;
    }

    public Overfitting withClasses(final List<String> classes) {
        this.classes = classes;
        return this;
    }

    public void createJsonResultFile(){
        try {
            int batchSize = 10;
            ExperimentSerializer serializer = new ExperimentSerializer("", testFileName, batchSize)
                    .withClasses(classes)
                    .withAlgorithms(Arrays.asList("hmm"));


            Environment environment = Environment.readEnvironmentFromFile(hmmFileName);
            BatchClassifier hmmClassifier = new BatchClassifierHMM(environment);

            CSVLoader testLoader = new CSVLoader();
            testLoader.setSource(new File(testFileName));
            Instances test = testLoader.getDataSet();
            test.setClassIndex(test.numAttributes() - 1);

            List<Instance> instances;
            Estimate hmmEstimate;
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

                algorithmsResults.addResult(new ExperimentSerializer.Result("hmm", hmmEstimate.label, hmmEstimate.probability));
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

    private void runOverfitting() {
        List<String> users = Arrays.asList("arturo", "emilio");
        withClasses(Arrays.asList("Baño", "Cocina", "Comedor", "Despacho", "Dormitorio"));
        for(String user: users) {
            for(int iterations = 5; iterations < 51; iterations += 5) {
                withHmmFileName(createHmmFileName(user, iterations));
                withTestFileName(createTestFileName(user));
                withJsonFileName(createResultsFileName(user, iterations));
                System.out.println("Creating: " + createResultsFileName(user, iterations));
                createJsonResultFile();
            }
        }

        withClasses(Arrays.asList("Cocina", "Comedor", "Despacho", "Dormitorio"));
        users = Arrays.asList("marta");
        for(String user: users) {
            for(int iterations = 5; iterations < 51; iterations += 5) {
                withHmmFileName(createHmmFileName(user, iterations));
                withTestFileName(createTestFileName(user));
                withJsonFileName(createResultsFileName(user, iterations));
                System.out.println("Creating: " + createResultsFileName(user, iterations));
                createJsonResultFile();
            }
        }
    }

    private String createResultsFileName(final String user, final int iterations) {
        StringBuilder sb = new StringBuilder();
        sb.append(DIRECTORY);
        sb.append("overfitting/");
        sb.append(user);
        sb.append("_overfitting_results_2_");
        sb.append(iterations);
        sb.append("_10.json");
        return sb.toString();
    }

    private String createHmmFileName(final String user, final int iterations) {
        StringBuilder sb = new StringBuilder();
        sb.append(DIRECTORY);
        sb.append("overfitting/");
        sb.append(user);
        sb.append("_overfitting_2_");
        sb.append(iterations);
        sb.append("_10.bin");
        return sb.toString();
    }

    private String createTestFileName(final String user) {
        return DIRECTORY +  user + "_test.csv";
    }

    private void confusionMatrix() {
        ExperimentSerializer serializer = null;
        int succes = 0, total = 0;
        try {
            serializer = ExperimentSerializer.fromJson(jsonFileName);

            Map<String, Integer> resultados = new HashMap<>();
            for (String className : serializer.getClasses()) {
                for (int i = 0; i < serializer.numResultsForClass(className); i++) {
                    ExperimentSerializer.Result results = serializer.forClassAndAlgorithmGetResultAt(className, "hmm", i);
                    if (resultados.containsKey(results.className) == false) {
                        resultados.put(results.className, 0);
                    }
                    if(className.equals(results.className)) {
                        succes++;
                    }
                    total++;
                    int previous = resultados.get(results.className);
                    resultados.put(results.className, previous + 1);
                }
            }
//            System.out.println("Success: " + succes + ",  Total: " + total);
            System.out.println(succes*100.0/total);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void runConfussionMatrix() {
        List<String> users = Arrays.asList("arturo", "emilio", "marta");
        for(String user: users) {
            for(int iterations = 5; iterations < 51; iterations += 5) {
                withJsonFileName(createResultsFileName(user, iterations));
//                System.out.println("Creating: " + createResultsFileName(user, iterations));
                confusionMatrix();
            }
            System.out.println("\n");
        }
    }

    public static void main(String[] args) {
//        new Overfitting().runOverfitting();
        new Overfitting().runConfussionMatrix();
    }
}
