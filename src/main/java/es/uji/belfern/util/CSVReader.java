package es.uji.belfern.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CSVReader {
    private final static String TIME_STAMP = "timestamp";
    private String fileName;
    private String headerClassName;
    private List<CSVRecord> rawData = new ArrayList<>();
    private List<String> headerNames;
    private List<String> locations = new ArrayList<>();

    public CSVReader(String fileName, String headerClassName) {
        this.fileName = fileName;
        this.headerClassName = headerClassName;
        readData();
    }

    public List<String> getHeaderNames() {
        return headerNames;
    }

    public List<String> getLocations() {
        return locations;
    }

    private void readLocations() {
        locations = rawData.stream()
                .map(record -> record.get(headerClassName))
                .distinct()
                .collect(Collectors.toList());
    }

    private void readData() {
        try {
            Reader reader = new FileReader(fileName);
            CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

            readHeaderNames(csvParser);
            readRecords(csvParser);
            readLocations();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readRecords(CSVParser csvParser) throws IOException {
        List<CSVRecord> records = csvParser.getRecords();
        for (CSVRecord record : records) {
            rawData.add(record);
        }
    }

    private void readHeaderNames(CSVParser csvParser) {
        headerNames = new ArrayList(csvParser.getHeaderMap().keySet());
        headerNames.remove(headerClassName);
        headerNames.remove(TIME_STAMP);
    }

    public List<Integer> getDataLocationWAP(String location, String wap) {
        List<Integer> result = new ArrayList<>();

        for (CSVRecord record : rawData) {
            if (location.equals(record.get(headerClassName))) {
                result.add(Integer.parseInt(record.get(wap)));
            }
        }

        return result;
    }

    public List<Integer> getDataLocation(String location) {
        List<Integer> result = new ArrayList<>();

        for (CSVRecord record : rawData) {
            if (location.equals(record.get(headerClassName))) {
                for (String wap : headerNames) {
                    result.add(Integer.parseInt(record.get(wap)));
                }
            }
        }

        return result;
    }

    public List<List<Integer>> getDataLocationAsLists(String location) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> row;

        for (CSVRecord record : rawData) {
            if (location.equals(record.get(headerClassName))) {
                row = new ArrayList<>();
                for (String wap : headerNames) {
                    row.add(Integer.parseInt(record.get(wap)));
                }
                result.add(row);
            }
        }

        return result;
    }

//    public Map<String, List<Integer>> getAllDataAsLists() {
//        Map<String, List<Integer>> result = new HashMap<>();
//        List<Integer> row;
//        String location;
//
//        for (CSVRecord record : rawData) {
//            location = record.get(headerClassName);
//            row = new ArrayList<>();
//            for (String wap : headerNames) {
//                row.add(Integer.parseInt(record.get(wap)));
//            }
//            result.put(location, row);
//        }
//
//        return result;
//    }
}
