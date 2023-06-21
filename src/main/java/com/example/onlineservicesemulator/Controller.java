package com.example.onlineservicesemulator;

import com.example.onlineservicesemulator.classes.JSONReader;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    public ListView<String> filesList;
    @FXML
    private VBox checkboxList;
    ArrayList<String> servicesNames;
    Map<String, List<String>> uploadedFiles;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        servicesNames = JSONReader.getServices();
        setFilesMap();
        setCheckboxList();
    }

    public void setCheckboxList() {
        for (String servicesName : servicesNames) {
            CheckBox checkBox = new CheckBox(servicesName);
            checkBox.setId("checkbox_" + servicesName);
            checkboxList.getChildren().add(checkBox);
            checkboxList.setSpacing(5);
            checkBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    filesList.getItems().clear();

                    String clickedService = checkBox.getText();
                    if (uploadedFiles.containsKey(clickedService) && uploadedFiles.get(clickedService) != null) {
                        filesList.getItems().addAll(uploadedFiles.get(clickedService));
                    }
                }
            });
        }
    }

    public void setFilesMap() {
        uploadedFiles = new HashMap<>();
        servicesNames.forEach(service -> uploadedFiles.put(service, null));

        uploadedFiles.put("WindowControlService", List.of("windowFile1.dat", "windowFile2.dat", "windowFile3.dat"));
        uploadedFiles.put("MusicControlService", List.of("musicFile1.dat", "musicFile2.dat", "musicFile3.dat"));
    }

}
