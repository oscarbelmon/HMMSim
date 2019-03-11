package es.uji.belfern.util;

import java.util.ArrayList;
import java.util.List;

public class ExperimentSerializer {
    public final String trainFileName;
    public final String testFileName;
    public final List<String> algorithms = new ArrayList<>();
    public final List<String> classes = new ArrayList<>();
    public final List<Result> results = new ArrayList<>();

    public class Result {
//        public final long sampleIndex;
        public final String algorithm;
        public final String className;
        public final double probability;

//        public Result(final long sampleIndex, final String algorithm, final String className, final double probability) {
        public Result(final String algorithm, final String className, final double probability) {
//            this.sampleIndex = sampleIndex;
            this.algorithm = algorithm;
            this.className = className;
            this.probability = probability;
        }
    }

    public ExperimentSerializer(final String trainFileName, final String testFileName) {
        this.trainFileName = trainFileName;
        this.testFileName = testFileName;
    }
}
