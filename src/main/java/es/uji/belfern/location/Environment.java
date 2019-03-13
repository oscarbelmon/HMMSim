package es.uji.belfern.location;

import es.uji.belfern.hmm.HMM;
import es.uji.belfern.statistics.Estimate;
import es.uji.belfern.util.CSVReader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Environment implements Serializable {
    private Map<String, Location> locations = new HashMap<>();
    private transient String trainDataFile;
    private transient String headerClassName;
    private transient CSVReader csvReader;

    public Environment(String trainDataFile, String headerClassName, final int nodes, final int iterations, final int sampleSize) {
        this.trainDataFile = trainDataFile;
        this.headerClassName = headerClassName;
        readCSVData();
        createLocations(nodes, iterations, sampleSize);
    }

    public static Environment readEnvironmentFromFile(String fileName) throws IOException {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Environment environment = (Environment)ois.readObject();
            ois.close();
            fis.close();
            return environment;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new IOException("Something went wrong reading the file");
    }

    private void createLocations(final int nodes, final int iterations, final int sampleSize) {
        long start, end;
        for(String location: csvReader.getLocations()) {
            System.out.print("Location: " + location);
            start = System.currentTimeMillis();
            locations.put(location, createLocation(location, nodes, iterations, sampleSize));
            end = System.currentTimeMillis();
            System.out.println(", time: " + (end - start)/1000.0);
        }
    }

    private Location createLocation(String locationName, final int nodes, final int iterations, final int sampleSize) {
        List<String> waps = csvReader.getHeaderNames();
        List<Integer> readings;
        Map<String, List<Integer>> data = new HashMap<>();
        for(String wap: waps) {
            readings = csvReader.getDataLocationWAP(locationName,wap);
            data.put(wap, readings);
        }
        Location location = new Location(locationName, data, nodes, iterations, sampleSize);

        return location;
    }

    private void readCSVData() {
        csvReader = new CSVReader(trainDataFile, headerClassName);
    }

//    public String estimateLocationProbability(Map<String, List<Integer>> measures) {
    public Estimate estimateLocationProbability(Map<String, List<Integer>> measures) {
        String estimatedLocation = "";
        double maximum = Integer.MIN_VALUE, current;
        List<Double> estimated = new ArrayList<>();
        int max_index = 0;
        int index = 0;
        for(String location: locations.keySet()) {
            current = locations.get(location).estimateLocationProbability(measures);
            if(current > maximum) {
                maximum = current;
                estimatedLocation = location;
                max_index = index;
            }
            estimated.add(current);
            index++;
        }

//        System.out.println(estimated);
//        System.out.println("---");
        double module = Math.sqrt(estimated.stream()
                .mapToDouble(e -> e * e)
                .sum());
        estimated = estimated.stream()
                .mapToDouble(e -> e/module)
                .boxed()
                .collect(Collectors.toList());
        double squared = 0;
//        System.out.println(estimated);
//        System.out.println(module + ", " + estimated);
        for(int i = 0; i < estimated.size(); i++) {
            if(i == max_index) {
                squared += (1 - estimated.get(i))*(1 - estimated.get(i));
            } else {
                squared += estimated.get(i) * estimated.get(i);
            }
        }
//        return estimatedLocation;
//        System.out.println(Math.sqrt(squared));
        return new Estimate(estimatedLocation, Math.sqrt(squared));
//        return new Estimate(estimatedLocation, Math.sqrt(1));
    }

    public List<String> getLocations() {
        return new ArrayList<>(locations.keySet());
    }

    public HMM getHMMWAPLocation(final String wap, final String location) {
        return locations.get(location).getHMMWAP(wap);
    }

//    public class Estimate {
//        public final String label;
//        public final double probability;
//
//        public Estimate(String label, double probability) {
//            this.label = label;
//            this.probability = probability;
//        }
//    }
}
