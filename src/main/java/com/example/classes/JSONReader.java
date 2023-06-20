package com.example.classes;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONReader {
    public static ArrayList<String> getServices() {
        try {
            String jsonData = new String(Files.readAllBytes(Paths.get(".\\src\\main\\resources\\online-services.json")));
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray services = jsonObject.getJSONArray("services");
            ArrayList<String> servicesNames = new ArrayList<>();

            for (int i = 0; i < services.length(); i++) {
                JSONObject service = services.getJSONObject(i);
                servicesNames.add(service.getString("service"));
            }
            return servicesNames;
        } catch (IOException exception) {
            exception.printStackTrace();
            return new ArrayList<>();
        }
    }
}
