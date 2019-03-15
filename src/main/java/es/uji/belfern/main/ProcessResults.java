package es.uji.belfern.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProcessResults {
    public static void main(String[] args) {
        try {
            Path path = Paths.get("coso.json");
            Reader reader = Files.newBufferedReader(path);
            Gson gson = new GsonBuilder().create();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
