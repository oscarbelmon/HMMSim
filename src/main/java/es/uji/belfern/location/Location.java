package es.uji.belfern.location;

import es.uji.belfern.hmm.HMM;
import es.uji.belfern.hmm.TabulatedCSVProbabilityEmitter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Location implements Serializable {
    private transient final String locationName;
    private Map<String, HMM<String, Integer>> hmms = new HashMap<>();
    private transient Map<String, List<Integer>> readings;

    public Location(String locationName, Map<String, List<Integer>> readings) {
        this.locationName = locationName;
        this.readings = readings;
        createHMMForAllWAP();
    }

    void createHMMForAllWAP() {
        for(String wap: readings.keySet()) {
            System.out.println("WAP: " + wap);
            hmms.put(wap, createHMMForWAP(readings.get(wap)));
        }
    }

    HMM<String, Integer> createHMMForWAP(List<Integer> wapReadings) {
        Random random = new Random(1);
        int nodes = 2;
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

        int iterations = 5;

        HMM<String, Integer> hmmEstimated = hmm.EM(emissionSet, wapReadings, iterations);
        return hmmEstimated;
    }

    double estimateLocationProbability(Map<String, List<Integer>> measures) {
//        double maxProbability = Double.MIN_VALUE;
        double totalLogProbability = 0;
        double logProbability = 0;
        double maxProbability, probability;
        for(String wap: measures.keySet()) {
            if(hmms.get(wap) != null) {
                maxProbability = hmms.get(wap).maxProbability(measures.size());
                probability = hmms.get(wap).forward(measures.get(wap));
//                logProbability = Math.log(hmms.get(wap).forward(measures.get(wap)) / maxProbability);
                logProbability = Math.log(probability / maxProbability);
            System.out.println("Probability: " + probability + ", maxProbability: " + maxProbability);
                totalLogProbability += logProbability;
//                if (logProbability > maxProbability) maxProbability = logProbability;
            }
        }
        System.out.println("Total probability: " + totalLogProbability);
        System.out.println("---");

        return totalLogProbability;
//        return maxProbability;
    }
}
