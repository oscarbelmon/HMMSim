package es.uji.belfern.location;

import es.uji.belfern.util.CSVReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Environment {
    private Map<String, Location> locations = new HashMap<>();
    private String trainDataFile;
    private String headerClassName;
    private CSVReader csvReader;

    public Environment(String trainDataFile, String headerClassName) {
        this.trainDataFile = trainDataFile;
        this.headerClassName = headerClassName;
        readCSVData();
        createLocations();
    }

    private void createLocations() {
        for(String location: csvReader.getLocations()) {
            System.out.println("Location: " + location);
            locations.put(location, createLocation(location));
        }
    }

    private Location createLocation(String locationName) {
        List<String> waps = csvReader.getHeaderNames();
        List<Integer> readings;
        Map<String, List<Integer>> data = new HashMap<>();
        for(String wap: waps) {
            readings = csvReader.getDataLocationWAP(locationName,wap);
            data.put(wap, readings);
        }
        Location location = new Location(locationName, data);

        return location;
    }

    private void readCSVData() {
        csvReader = new CSVReader(trainDataFile, headerClassName);
    }

    void estimateLocationProbability(Map<String, List<Integer>> measures) {
        for(String location: locations.keySet()) {
            locations.get(location).estimateLocationProbability(measures);
        }
    }
}
