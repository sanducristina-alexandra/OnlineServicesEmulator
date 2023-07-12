package com.example.onlineservicesemulator.classes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONReader {
    public static List<String> getServices() {
        try {
            String jsonData = new String(Files.readAllBytes(Paths.get(".\\src\\main\\resources\\online-services.json")));
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray services = jsonObject.getJSONArray("services");
            List<String> servicesNames = new ArrayList<>();

            for (int i = 0; i < services.length(); i++) {
                JSONObject service = services.getJSONObject(i);
                servicesNames.add(service.getString("service"));
            }
            return servicesNames;
        } catch (IOException exception) {
            exception.printStackTrace();
            return Collections.emptyList();
        }
    }
}