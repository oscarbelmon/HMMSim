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
        List<Result> algorithmResults = new ArrayList<>();

        public void addResult(final Result result) {
            algorithmResults.add(result);
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
