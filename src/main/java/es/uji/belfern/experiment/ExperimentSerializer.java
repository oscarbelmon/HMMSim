package es.uji.belfern.experiment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExperimentSerializer {
    private final String trainFileName;
    private final String testFileName;
    private final int batchSize;
    private List<String> algorithms = new ArrayList<>();
    private List<String> classes = new ArrayList<>();
    private ResultSet resultSets = new ResultSet();

    public static class Result {
        public final String algorithm;
        public final String className;
        public final double probability;

        public Result(final String algorithm, final String className, final double probability) {
            this.algorithm = algorithm;
            this.className = className;
            this.probability = probability;
        }
    }

    public static class AlgorithmsResults {
//        List<Result> algorithmResults = new ArrayList<>();
        Map<String, Result> algorithmResults = new HashMap<>();

        public void addResult(final Result result) {
//            algorithmResults.add(result);
            algorithmResults.put(result.algorithm, result);
        }

        public Map<String, Integer> estimatedClasses(final List<String> algorithms) {
            Map<String, Integer> results = new HashMap<>();
            int previous;
            for(Result result: algorithmResults.values()) {
                if(algorithms.contains(result.algorithm)) {
                    if (results.containsKey(result.className) == false) {
                        results.put(result.className, 0);
                    }
                    previous = results.get(result.className);
                    results.put(result.className, previous + 1);
                }
            }
            return results;
        }
    }

    public static class ResultSet {
        Map<String, List<AlgorithmsResults>> results = new HashMap<>();

        public void addResult(final String className, final AlgorithmsResults results) {
            if(this.results.containsKey(className) == false) {
                this.results.put(className, new ArrayList<>());
            }
            this.results.get(className).add(results);
        }
    }

    public long numResultsForClass(final String className) {
        return resultSets.results.get(className).size();
    }

    public Result forClassAndAlgorithmGetResultAt(final String className, final String algorithmName, final int index) {
        return resultSets.results.get(className).get(index).algorithmResults.get(algorithmName);
    }

    public Map<String, Integer> forClassGetEstimatesAt(final String className, final int index, final List<String> algorithms) {
        return resultSets.results.get(className).get(index).estimatedClasses(algorithms);
    }

    public String getTrainFileName() {
        return trainFileName;
    }

    public String getTestFileName() {
        return testFileName;
    }

    public List<String> getAlgorithms() {
        return algorithms;
    }

    public List<String> getClasses() {
        return classes;
    }

    public ExperimentSerializer(final String trainFileName, final String testFileName, final int batchSize) {
        this.trainFileName = trainFileName;
        this.testFileName = testFileName;
        this.batchSize = batchSize;
    }

    public ExperimentSerializer withAlgorithms(final List<String> algorithms) {
        this.algorithms = algorithms;
        return this;
    }

    public ExperimentSerializer withClasses(final List<String> classes) {
        this.classes = classes;
        return this;
    }

    public void addResult(final String className, final AlgorithmsResults result) {
        resultSets.addResult(className, result);
    }

    public String toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public static ExperimentSerializer fromJson(final String jsonFileName) throws FileNotFoundException {
        FileReader fr = new FileReader(jsonFileName);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.fromJson(fr, ExperimentSerializer.class);
    }
}
