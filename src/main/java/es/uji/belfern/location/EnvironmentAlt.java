package es.uji.belfern.location;

import es.uji.belfern.util.CSVReader;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvironmentAlt implements Serializable {
    private final Map<String, LocationAlt> locations = new HashMap<>();
    private final transient String trainDataFile;
    private final transient String headerClassName;
    private final transient CSVReader csvReader;

    public EnvironmentAlt(String trainDataFile, String headerClassName, int iterations) {
        this.trainDataFile = trainDataFile;
        this.headerClassName = headerClassName;
        csvReader = new CSVReader(trainDataFile, headerClassName);
        createLocations(iterations);
    }

    private void createLocations(int iterations) {
        for(String location: csvReader.getLocations()) {
            System.out.println("Location: " + location);
            locations.put(location, createLocation(location, iterations));
        }

    }

    private LocationAlt createLocation(String location, int iterations) {
        List<String> waps = csvReader.getHeaderNames();
        List<Integer> readings = csvReader.getDataLocation(location);

        return new LocationAlt(location, readings, iterations);
    }

    public void storeEnvironment(String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static EnvironmentAlt readEnvironmentFromFile(String fileName) throws IOException {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            EnvironmentAlt environment = (EnvironmentAlt) ois.readObject();
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

    String estimateLocationProbability(List<Integer> measures) {
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
}
