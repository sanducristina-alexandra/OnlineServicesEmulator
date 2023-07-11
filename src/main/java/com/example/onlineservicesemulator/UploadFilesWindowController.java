package com.example.onlineservicesemulator;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.nio.file.Files;
import java.nio.file.Path;

public class UploadFilesWindowController implements Initializable {
    @FXML
    private Button selectFilesButton;
    private String serviceName;
    private Controller mainController;
    private List<String> selectedFilesList = new ArrayList<>();
    private Alert popup = new Alert(Alert.AlertType.INFORMATION);

    @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            selectFiles();
    }

    public void selectFiles(){
        selectFilesButton.setOnMouseClicked(mouseEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(selectFilesButton.getScene().getWindow());

            if (selectedFiles != null) {
                for (File file : selectedFiles) {
                    try {
                        Path uploadedFilesDir = Paths.get(".\\uploadedfiles");
                        if (!Files.exists(uploadedFilesDir)) {
                            Files.createDirectory(uploadedFilesDir);
                        }

                        Path destination = uploadedFilesDir.resolve(file.getName());
                        Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                        selectedFilesList.add(file.getName());
                        handleFileSelection();
                        popup.setContentText("File uploaded: " + destination);
                        popup.showAndWait();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    private void handleFileSelection() {
        mainController.handleFileSelection(serviceName, selectedFilesList);
    }

    public void setMainController(Controller controller) {
        this.mainController = controller;
    }
}
