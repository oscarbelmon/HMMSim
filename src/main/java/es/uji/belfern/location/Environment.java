package es.uji.belfern.location;

import es.uji.belfern.util.CSVReader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Environment implements Serializable {
    private Map<String, Location> locations = new HashMap<>();
    private transient String trainDataFile;
    private transient String headerClassName;
    private transient CSVReader csvReader;

    public Environment(String trainDataFile, String headerClassName, final int nodes, final int iterations) {
        this.trainDataFile = trainDataFile;
        this.headerClassName = headerClassName;
        readCSVData();
        createLocations(nodes, iterations);
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

    private void createLocations(final int nodes, final int iterations) {
        for(String location: csvReader.getLocations()) {
            System.out.println("Location: " + location);
            locations.put(location, createLocation(location, nodes, iterations));
        }
    }

    private Location createLocation(String locationName, final int nodes, final int iterations) {
        List<String> waps = csvReader.getHeaderNames();
        List<Integer> readings;
        Map<String, List<Integer>> data = new HashMap<>();
        for(String wap: waps) {
            readings = csvReader.getDataLocationWAP(locationName,wap);
            data.put(wap, readings);
        }
        Location location = new Location(locationName, data, nodes, iterations);

        return location;
    }

    private void readCSVData() {
        csvReader = new CSVReader(trainDataFile, headerClassName);
    }

    public String estimateLocationProbability(Map<String, List<Integer>> measures) {
        String estimatedLocation = "";
        double maximum = Integer.MIN_VALUE, current;
        for(String location: locations.keySet()) {
            current = locations.get(location).estimateLocationProbability(measures);
            if(current > maximum) {
                maximum = current;
                estimatedLocation = location;
            }
        }
//        System.out.println("---");
        return estimatedLocation;
    }

    public List<String> getLocations() {
        return new ArrayList<>(locations.keySet());
    }
}
