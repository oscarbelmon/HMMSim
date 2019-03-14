package es.uji.belfern.experiment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ExperimentSerializer {
    private final String trainFileName;
    private final String testFileName;
    private List<String> algorithms = new ArrayList<>();
    private List<String> classes = new ArrayList<>();
    private List<Results> results = new ArrayList<>();
    private transient Results currentResults;

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

    public static class Results {
        public final String realClass;
        List<Result> results = new ArrayList<>();

        public Results(final String realClass) {
            this.realClass = realClass;
        }

        public void addResult(final Result result) {
            results.add(result);
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

    public ExperimentSerializer(final String trainFileName, final String testFileName) {
        this.trainFileName = trainFileName;
        this.testFileName = testFileName;
    }

    public ExperimentSerializer withAlgorithms(final List<String> algorithms) {
        this.algorithms = algorithms;
        return this;
    }

    public ExperimentSerializer withClasses(final List<String> classes) {
        this.classes = classes;
        return this;
    }

    public void newResults(final String className) {
        currentResults = new Results(className);
        results.add(currentResults);
    }

    public void addResult(final Result result) {
        currentResults.addResult(result);
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
