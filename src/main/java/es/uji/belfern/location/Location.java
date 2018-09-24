package es.uji.belfern.location;

import es.uji.belfern.hmm.HMM;
import es.uji.belfern.hmm.TabulatedCSVProbabilityEmitter;
import es.uji.belfern.util.CSVReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Location {
    private final String trainDataFile;
    private final String headerClassName;
    private final String locationName;
    private CSVReader csvReader;
    private Map<String, HMM<String, Integer>> hmms = new HashMap<>();

    public Location(String trainDataFile, String headerClassName, String locationName) {
        this.trainDataFile = trainDataFile;
        this.headerClassName = headerClassName;
        this.locationName = locationName;
        readData();
    }

    private void readData() {
        csvReader = new CSVReader(trainDataFile, headerClassName);
    }

    void createHMMForAllWAP() {
        List<String> waps =  csvReader.getHeaderNames();
        for(String wap: waps) {
            System.out.println("WAP: " + wap);
            createHMMForWAP(wap);
        }
    }

    void createHMMForWAP(String wap) {
        Random random = new Random(0);
        int nodes = 3;
        List<Integer> wapReadings = csvReader.getDataLocationWAP(locationName, wap);
        TabulatedCSVProbabilityEmitter emitter = new TabulatedCSVProbabilityEmitter(wapReadings);
        HMM<String, Integer> hmm =  new HMM<>();

        for(int i = 0; i < nodes; i++) {
            hmm.instanceNode(i+"", emitter);
        }

        String start, end;
        double[] probabilities = new double[nodes];
        double accum = 0;
        for(int i = 0; i < nodes; i++) {
            start = i + "";
            for(int j = 0; j < nodes; j++) {
                probabilities[j] = random.nextDouble();
                accum += probabilities[j];
            }
            for(int j = 0; j  < nodes; j++) {
                probabilities[j] /= accum;
            }
            for(int j = 0; j < nodes; j++) {
                end = j + "";
                hmm.instanceEdge(start, end, probabilities[j]);
            }
        }

        for(int i = 0; i < nodes; i++) {
            probabilities[i] = random.nextDouble();
            accum += probabilities[i];
        }
        for(int i = 0; i  < nodes; i++) {
            probabilities[i] /= accum;
        }
        for(int i = 0; i < nodes; i++) {
            start = i + "";
            hmm.addInitialNode(start, probabilities[i]);
        }

        List<Integer> emissionSet = wapReadings.stream()
                .distinct()
                .collect(Collectors.toList());

        int iterations = 10;

        HMM<String, Integer> hmmEstimated = hmm.EM(emissionSet, wapReadings, iterations);
        hmms.put(wap, hmmEstimated);
//        System.out.println(hmmEstimated);

//        System.out.println(hmmEstimated.forward(wapReadings.subList(50, 55)));
    }

    void estimateLocationProbability(Map<String, List<Integer>> measures) {
        for(String wap: measures.keySet()) {
            System.out.println(hmms.get(wap).forward(measures.get(wap)));
        }
    }
}
