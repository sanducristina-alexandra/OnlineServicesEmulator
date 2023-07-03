package com.example.onlineservicesemulator;


import com.example.onlineservicesemulator.classes.JSONReader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    private ListView<String> filesList;
    @FXML
    private VBox checkboxList;
    @FXML
    private Button addFileButton;
    private List<String> servicesNames;
    private Map<String, List<String>> servicesAndUploadedFilesMap;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        servicesNames = JSONReader.getServices();
        createAndSetCheckboxList();
        setFilesMap();
        addFileButton.setVisible(false);
    }

    public void createAndSetCheckboxList() {
        double lengthSectionA = calculateLongestServiceString(servicesNames)*8;
        checkboxList.setPrefWidth(lengthSectionA);
        for (String serviceName : servicesNames) {
            CheckBox checkBox = new CheckBox();
            checkBox.setId("checkbox_" + serviceName);
            Label checkBoxLabel = new Label(serviceName);
            checkBox.setGraphic(checkBoxLabel);
            checkBoxLabel.setOnMouseClicked(event -> onCheckboxLabelClicked(event, checkBox, checkBoxLabel, serviceName));
            checkboxList.getChildren().add(checkBox);
            checkboxList.setSpacing(5);
        }
    }

    private void onCheckboxLabelClicked(MouseEvent event, CheckBox checkBox, Label checkBoxLabel,String serviceName) {
        checkBox.setSelected(false);
        filesList.getItems().clear();
        String clickedService = checkBoxLabel.getText();
        if (servicesAndUploadedFilesMap.containsKey(clickedService) && servicesAndUploadedFilesMap.get(clickedService) != null) {
            filesList.getItems().addAll(servicesAndUploadedFilesMap.get(clickedService));
            addFileButton.setVisible(true);
        }
        event.consume();
        addFiles(serviceName);
    }

    public void setFilesMap() {
        servicesAndUploadedFilesMap = new HashMap<>();
        servicesNames.forEach(service -> servicesAndUploadedFilesMap.put(service, null));

        List<String> arraylist1 = new ArrayList<>();
        arraylist1.add("windowFile1.dat");
        arraylist1.add("windowFile2.dat");
        arraylist1.add("windowFile3.dat");
        servicesAndUploadedFilesMap.put("WindowControlService", arraylist1);
    }
    public double calculateLongestServiceString(List<String> servicesNames) {
        double max = 0;
        for (String serviceName : servicesNames) {
            if (serviceName.length() > max) {
                max = serviceName.length();
            }
        }
        return max;
    }

    public void addFiles(String serviceName){
        addFileButton.setOnMouseClicked(mouseEvent -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("uploadFilesWindow.fxml"));
                Parent root = loader.load();

                UploadFilesWindowController uploadFilesController = loader.getController();
                uploadFilesController.setServiceName(serviceName);
                uploadFilesController.setMainController(this);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));

                stage.show();
            }catch (Exception exception){
                exception.printStackTrace();
            }
        });
    }

    public void handleFileSelection(String serviceName, List<String> selectedFiles){
        List<String> files = servicesAndUploadedFilesMap.get(serviceName);
        files.addAll(selectedFiles);
        for(String s: selectedFiles){
            System.out.println(s);
        }
    }
}
