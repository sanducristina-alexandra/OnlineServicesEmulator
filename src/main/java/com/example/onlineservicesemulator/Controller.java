package com.example.onlineservicesemulator;


import com.example.onlineservicesemulator.classes.JSONReader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML
    private ListView<String> filesList;
    @FXML
    private VBox checkboxList;
    @FXML
    private Button addFileButton;
    @FXML
    private Label serviceSelected;
    private List<String> servicesNames;
    private Map<String, List<String>> servicesAndUploadedFilesMap;
    private Alert popup = new Alert(Alert.AlertType.INFORMATION);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        servicesNames = JSONReader.getServices();
        createAndSetCheckboxList();
        setFilesMap();
        addFileButton.setDisable(true);
        addFileButton.setOpacity(0.5);
    }

    public void createAndSetCheckboxList() {
        short lengthSectionA = (short) (calculateLongestServiceString(servicesNames) * 8);
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

    private void onCheckboxLabelClicked(MouseEvent event, CheckBox checkBox, Label checkBoxLabel, String serviceName) {
        checkBox.setSelected(false);
        filesList.getItems().clear();
        String clickedService = checkBoxLabel.getText();
        if (servicesAndUploadedFilesMap.containsKey(clickedService) && servicesAndUploadedFilesMap.get(clickedService) != null) {
            filesList.getItems().addAll(servicesAndUploadedFilesMap.get(clickedService));
        }
        addFileButton.setDisable(false);
        addFileButton.setOpacity(1.0);
        event.consume();
        addFiles(serviceName);
        serviceSelected.setText(serviceName);
    }


    public void setFilesMap() {
        servicesAndUploadedFilesMap = new HashMap<>();
        servicesNames.forEach(service -> servicesAndUploadedFilesMap.put(service, new ArrayList<>()));
    }

    public short calculateLongestServiceString(List<String> servicesNames) {
        short max = 0;
        for (String serviceName : servicesNames) {
            if (serviceName.length() > max) {
                max = (short) serviceName.length();
            }
        }
        return max;
    }

    public void addFiles(String serviceName) {
        addFileButton.setOnMouseClicked(mouseEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(addFileButton.getScene().getWindow());

            if (selectedFiles != null) {
                List<String> selectedFilesList = new ArrayList<>();
                try {
                    Path uploadedFilesDir = Paths.get(".\\uploadedfiles");
                    if (!Files.exists(uploadedFilesDir)) {
                        Files.createDirectory(uploadedFilesDir);
                    }

                    for (File file : selectedFiles) {
                        Path destination = uploadedFilesDir.resolve(file.getName());
                        if (Files.exists(destination)) {
                            popup.setContentText("File '" + file.getName() + "' is already uploaded.");
                            popup.showAndWait();
                        } else {
                            Files.copy(file.toPath(), destination);
                            selectedFilesList.add(file.getName());
                        }
                    }
                    if (!selectedFilesList.isEmpty()) {
                        popup.setContentText("Files have been uploaded.");
                        popup.showAndWait();
                    }
                    handleFileSelection(serviceName, selectedFilesList);
                    refreshFilesList(serviceName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void refreshFilesList(String serviceName) {
        filesList.getItems().clear();
        filesList.getItems().addAll(servicesAndUploadedFilesMap.get(serviceName));
    }

    public void handleFileSelection(String serviceName, List<String> selectedFiles) {
        List<String> files = servicesAndUploadedFilesMap.get(serviceName);
        files.addAll(selectedFiles);
    }
}
