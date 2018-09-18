package belfern.uji.es.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    private String fileName;
//    private String headerName;
    private String headerClassName;
//    private String className;
//    private List<Integer> data = new ArrayList<>();
    private List<CSVRecord> rawData = new ArrayList<>();
//    private int min;
//    private int max;

//    public CSVReader(String fileName, String headerName, String headerClassName, String className) {
    public CSVReader(String fileName, String headerClassName) {
        this.fileName = fileName;
//        this.headerName = headerName;
        this.headerClassName = headerClassName;
//        this.className = className;
//        readData(fileName, headerName, headerClassName, className);
        readData();
//        processData();
    }

//    public List<Integer> getData() {
//        return data;
//    }

    private void readData() {//String fileName, String headerName, String headerClassName, String className) {
        try {
            Reader reader = new FileReader(fileName);
            List<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader).getRecords();
            for(CSVRecord record: records) {
                rawData.add(record);
//                if(className.equals(record.get(headerClassName))) {
//                    data.add(Integer.parseInt(record.get(headerName)));
//                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getDataWAPLocation(String wap, String location) {
        List<Integer> result = new ArrayList<>();

        for(CSVRecord record: rawData) {
            if(location.equals(record.get(headerClassName))) {
                result.add(Integer.parseInt(record.get(wap)));
            }
        }

        return result;
    }

//    private void processData() {
//        min = data.stream()
//                .mapToInt(i -> i)
//                .min()
//                .orElse(-100);
//
//        max = data.stream()
//                .mapToInt(i -> i)
//                .max()
//                .orElse(-20);
//
//    }
}
