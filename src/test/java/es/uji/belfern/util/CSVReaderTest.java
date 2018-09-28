package es.uji.belfern.util;

import es.uji.belfern.hmm.HMM;
import es.uji.belfern.hmm.Node;
import es.uji.belfern.hmm.TabulatedCSVProbabilityEmitter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;

class CSVReaderTest {
//    private final static String fileNameTrain = "oscar_train.csv";
//    private final static String fileNameTest = "oscar_test.csv";
    private final static String fileNameTrain = "train_emilio.csv";
    private final static String fileNameTest = "test_emilio.csv";
//    private final static String headerName = "e2:41:36:00:07:b8";
    private final static String headerName = "b";
//    private final static String headerClassName = "label";
    private final static String headerClassName = "label";
//    private final static String className = "Despacho";
    private final static String className = "zero";
    private static CSVReader csvTrain;
    private static CSVReader csvTest;

    @BeforeAll
    static void setUp() {
        csvTrain = new CSVReader(fileNameTrain, headerClassName);
        csvTest = new CSVReader(fileNameTest, headerClassName);
    }

    @Test
    void twoStatesTest() {
//        List<Integer> dataDespacho = csvTrain.getDataLocationWAP(className, headerName);
        List<Integer> dataDespachoAll = csvTrain.getDataLocationWAP(className, headerName);
//        List<Integer> dataDespacho = dataDespachoAll.subList(0, 80);
        List<Integer> dataDespacho = dataDespachoAll;
//        List<Integer> firstDespacho = dataDespacho.subList(0, dataDespacho.size()/2);
//        List<Integer> secondDespacho = dataDespacho.subList(dataDespacho.size()/2+1, dataDespacho.size()-1);
        List<Integer> firstDespacho = dataDespacho;
        List<Integer> secondDespacho = dataDespacho;
        TabulatedCSVProbabilityEmitter emitter1Despacho = new TabulatedCSVProbabilityEmitter<Integer>(firstDespacho);
        TabulatedCSVProbabilityEmitter emitter2Despacho = new TabulatedCSVProbabilityEmitter<Integer>(secondDespacho);

        List<Integer> symbols = new ArrayList<>();
        symbols.addAll(firstDespacho);
        symbols.addAll(secondDespacho);
        symbols = symbols.stream()
                .distinct()
                .collect(Collectors.toList());
        HMM<String, Integer> hmmDespacho = new HMM<>(symbols);

        Node<String, Integer> a = hmmDespacho.instanceNode("a", emitter1Despacho);
        Node<String, Integer> b = hmmDespacho.instanceNode("b", emitter2Despacho);

        hmmDespacho.instanceEdge(a, a, 0.7);
        hmmDespacho.instanceEdge(a, b, 0.3);
        hmmDespacho.instanceEdge(b, b, 0.6);
        hmmDespacho.instanceEdge(b, a, 0.4);

        hmmDespacho.addInitialNode(a, 0.6);
        hmmDespacho.addInitialNode(b, 0.4);

        List<Integer> emissionSet = dataDespacho.stream()
                .distinct()
                .collect(Collectors.toList());

        int iterations = 10;

        HMM<String, Integer> estimatedHMMDespacho = hmmDespacho.EM(emissionSet, dataDespacho, iterations);
        System.out.println(estimatedHMMDespacho);

//        List<Integer> dataDormitorio = csvTrain.getDataLocationWAP("Dormitorio", headerName);
//        List<Integer> dataDormitorioAll = csvTrain.getDataLocationWAP("Dormitorio", headerName);
        List<Integer> dataDormitorioAll = csvTrain.getDataLocationWAP("1", headerName);
//        List<Integer> dataDormitorio = dataDormitorioAll.subList(0, 80);
        List<Integer> dataDormitorio = dataDormitorioAll;
//        List<Integer> firstDormitorio = dataDormitorio.subList(0, dataDespacho.size()/2);
//        List<Integer> secondDormitorio = dataDormitorio.subList(dataDormitorio.size()/2+1, dataDormitorio.size()-1);
        List<Integer> firstDormitorio = dataDormitorio;
        List<Integer> secondDormitorio = dataDormitorio;
        TabulatedCSVProbabilityEmitter emitter1Dormitorio = new TabulatedCSVProbabilityEmitter(firstDormitorio);
        TabulatedCSVProbabilityEmitter emitter2Dormitorio = new TabulatedCSVProbabilityEmitter(secondDormitorio);

        symbols = new ArrayList<>();
        symbols.addAll(firstDespacho);
        symbols.addAll(secondDespacho);
        symbols = symbols.stream()
                .distinct()
                .collect(Collectors.toList());
        HMM<String, Integer> hmmDormitorio = new HMM<>(symbols);

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
        System.out.println(estimatedHMMDormitorio);

//        List<Integer> dataDespachoTest = dataDespachoAll.subList(80, dataDespachoAll.size());
//        List<Integer> dataDormitorioTest = dataDormitorioAll.subList(80, dataDormitorioAll.size());
        List<Integer> dataDespachoTest = dataDespachoAll;
        List<Integer> dataDormitorioTest = dataDormitorioAll;
//        List<Integer> dataDormitorioTest = Arrays.asList(-44, -38, -38, -38, -37);
//        List<Integer> dataDespachoTest = csvTest.getDataLocationWAP(headerName, className);
//        List<Integer> dataDormitorioTest = csvTest.getDataLocationWAP(headerName, "Dormitorio");
//        List<Integer> observationsDespacho = dataDespacho.subList(dataDespacho.size()-6, dataDespacho.size()-1);
//        List<Integer> observationsDormitorio = dataDormitorio.subList(dataDormitorio.size()-6, dataDormitorio.size()-1);
//        System.out.println(dataDespachoTest);
        int exitoDespacho = 0, exitoDormitorio = 0;
        int steps = 95;
        for(int start = 0; start < steps; start++) {
            List<Integer> observationsDespacho = dataDespachoTest.subList(start, start + 5);
            List<Integer> observationsDormitorio = dataDormitorioTest.subList(start, start + 5);
            System.out.println("HMM Despacho");
            double despachoDespacho = Math.log(estimatedHMMDespacho.forward(observationsDespacho));
            double dormitorioDespacho = Math.log(estimatedHMMDormitorio.forward(observationsDespacho));
            System.out.println(observationsDespacho);
            System.out.println(despachoDespacho);
            System.out.println(dormitorioDespacho);
            if(despachoDespacho > dormitorioDespacho) exitoDespacho++;

            System.out.println("HMM Dormitorio");
            double dormitorioDormitorio = Math.log(estimatedHMMDormitorio.forward(observationsDormitorio));
            double despachoDormitorio = Math.log(estimatedHMMDespacho.forward(observationsDormitorio));
            System.out.println(observationsDormitorio);
            System.out.println(dormitorioDormitorio);
            System.out.println(despachoDormitorio);
            if(dormitorioDormitorio > despachoDormitorio) exitoDormitorio++;
        }
        System.out.println(exitoDespacho*100.0/steps);
        System.out.println(exitoDormitorio*100.0/steps);
    }

    @Test
    public void getDataWAPLocationTest() {
        List<Integer> data = csvTrain.getDataLocationWAP(headerName, className);
        System.out.println(data);
    }

    @Test
    public void stringGeneratorTest() {
        System.out.println(RandomStringUtils.randomAlphanumeric(10));
    }

    @Test
    void getDataLocationTest() {
        List<Integer> expected = Arrays.asList(0, 0, 0, -60, -88, 0, -80, 0, 0);
        List<Integer> data = csvTrain.getDataLocation("zero");
        assertThat(data.size(), is(900));
        assertThat(data.subList(9, 18), is(expected));
        System.out.println(data);
    }
}
