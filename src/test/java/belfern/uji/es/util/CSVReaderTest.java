package belfern.uji.es.util;

import belfern.uji.es.hmm.HMM;
import belfern.uji.es.hmm.Node;
import belfern.uji.es.hmm.TabulatedCSVProbabilityEmitter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class CSVReaderTest {
    private final static String fileNameTrain = "oscar_train.csv";
    private final static String fileNameTest = "oscar_test.csv";
    private final static String headerName = "e2:41:36:00:07:b8";
    private final static String headerClassName = "label";
    private final static String className = "Despacho";
    private static CSVReader csvTrain;
    private static CSVReader csvTest;

    @BeforeAll
    public static void setUp() {
        csvTrain = new CSVReader(fileNameTrain, headerClassName);
        csvTest = new CSVReader(fileNameTest, headerClassName);
    }

    @Test
    public void twoStatesTest() {
//        List<Integer> dataDespacho = csvTrain.getDataWAPLocation(headerName, className);
        List<Integer> dataDespachoAll = csvTrain.getDataWAPLocation(headerName, className);
        List<Integer> dataDespacho = dataDespachoAll.subList(0, 80);
        List<Integer> firstDespacho = dataDespacho.subList(0, dataDespacho.size()/2);
        List<Integer> secondDespacho = dataDespacho.subList(dataDespacho.size()/2+1, dataDespacho.size()-1);
        TabulatedCSVProbabilityEmitter emitter1Despacho = new TabulatedCSVProbabilityEmitter(firstDespacho);
        TabulatedCSVProbabilityEmitter emitter2Despacho = new TabulatedCSVProbabilityEmitter(secondDespacho);

        HMM<String, Integer> hmmDespacho = new HMM<>();

        Node<String, Integer> a = hmmDespacho.instanceNode("a", emitter1Despacho);
        Node<String, Integer> b = hmmDespacho.instanceNode("b", emitter2Despacho);

        hmmDespacho.instanceEdge(a, a, 0.7);
        hmmDespacho.instanceEdge(a, b, 0.3);
        hmmDespacho.instanceEdge(b, b, 0.5);
        hmmDespacho.instanceEdge(b, a, 0.5);

        hmmDespacho.addInitialNode(a, 0.6);
        hmmDespacho.addInitialNode(b, 0.4);

        List<Integer> emissionSet = dataDespacho.stream()
                .distinct()
                .collect(Collectors.toList());

        int iterations = 10;

        HMM<String, Integer> estimatedHMMDespacho = hmmDespacho.EM(emissionSet, dataDespacho, iterations);

//        List<Integer> dataDormitorio = csvTrain.getDataWAPLocation(headerName, "Dormitorio");
        List<Integer> dataDormitorioAll = csvTrain.getDataWAPLocation(headerName, "Dormitorio");
        List<Integer> dataDormitorio = dataDormitorioAll.subList(0, 80);
        List<Integer> firstDormitorio = dataDespacho.subList(0, dataDespacho.size()/2);
        List<Integer> secondDormitorio = dataDespacho.subList(dataDespacho.size()/2+1, dataDespacho.size()-1);
        TabulatedCSVProbabilityEmitter emitter1Dormitorio = new TabulatedCSVProbabilityEmitter(firstDormitorio);
        TabulatedCSVProbabilityEmitter emitter2Dormitorio = new TabulatedCSVProbabilityEmitter(secondDormitorio);

        HMM<String, Integer> hmmDormitorio = new HMM<>();

        a = hmmDormitorio.instanceNode("a", emitter1Dormitorio);
        b = hmmDormitorio.instanceNode("b", emitter2Dormitorio);

        hmmDormitorio.instanceEdge(a, a, 0.7);
        hmmDormitorio.instanceEdge(a, b, 0.3);
        hmmDormitorio.instanceEdge(b, b, 0.5);
        hmmDormitorio.instanceEdge(b, a, 0.5);

        hmmDormitorio.addInitialNode(a, 0.6);
        hmmDormitorio.addInitialNode(b, 0.4);

        emissionSet = dataDormitorio.stream()
                .distinct()
                .collect(Collectors.toList());

        HMM<String, Integer> estimatedHMMDormitorio = hmmDormitorio.EM(emissionSet, dataDormitorio, iterations);

        List<Integer> dataDespachoTest = dataDespachoAll.subList(80, dataDespachoAll.size());
        List<Integer> dataDormitorioTest = dataDormitorioAll.subList(80, dataDormitorioAll.size());
//        List<Integer> dataDormitorioTest = Arrays.asList(-44, -38, -38, -38, -37);
//        List<Integer> dataDespachoTest = csvTest.getDataWAPLocation(headerName, className);
//        List<Integer> dataDormitorioTest = csvTest.getDataWAPLocation(headerName, "Dormitorio");
//        List<Integer> observationsDespacho = dataDespacho.subList(dataDespacho.size()-6, dataDespacho.size()-1);
//        List<Integer> observationsDormitorio = dataDormitorio.subList(dataDormitorio.size()-6, dataDormitorio.size()-1);
//        System.out.println(dataDespachoTest);
        for(int start = 0; start < 15; start++) {
            List<Integer> observationsDespacho = dataDespachoTest.subList(start, start + 5);
            List<Integer> observationsDormitorio = dataDormitorioTest.subList(start, start + 5);
            System.out.println("HMM Despacho");
            double despachoDespacho = Math.log(estimatedHMMDespacho.forward(observationsDespacho));
            double despachoDormitorio = Math.log(estimatedHMMDespacho.forward(observationsDormitorio));
            System.out.println(despachoDespacho);
            System.out.println(despachoDormitorio);

            System.out.println("HMM Dormitorio");
            double dormitorioDormitorio = Math.log(estimatedHMMDormitorio.forward(observationsDormitorio));
            double dormitorioDespacho = Math.log(estimatedHMMDormitorio.forward(observationsDespacho));
            System.out.println(dormitorioDormitorio);
            System.out.println(dormitorioDespacho);
        }
    }

    @Test
    public void getDataWAPLocationTest() {
        List<Integer> data = csvTrain.getDataWAPLocation(headerName, className);
        System.out.println(data);
    }
}
