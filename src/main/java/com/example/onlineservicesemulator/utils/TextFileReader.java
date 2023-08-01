package com.example.onlineservicesemulator.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class TextFileReader {

    public static String getData(List<String> fileNames){
        String fileData = "";
        for(String fileName:fileNames){
            try {
                byte[] dataInBytes = Files.readAllBytes(Paths.get(Utils.getFileDestination(fileName)));
                String dataInString = new String(dataInBytes, StandardCharsets.UTF_8);
                fileData += dataInString + "\n" ;
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        return fileData;
    }
}
