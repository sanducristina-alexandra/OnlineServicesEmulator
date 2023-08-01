package com.example.onlineservicesemulator;

import com.example.onlineservicesemulator.handlers.CarClimatizationFileHandler;
import com.example.onlineservicesemulator.handlers.CarClimatizationSetTemperatureHandler;
import com.example.onlineservicesemulator.models.Topic;
import com.example.onlineservicesemulator.mqtt.MqttPublisher;
import com.example.onlineservicesemulator.utils.JSONReader;
import com.example.onlineservicesemulator.utils.TextFileReader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class Controller implements Initializable {

    @FXML
    private ListView<String> filesList;
    @FXML
    private VBox checkboxList;
    @FXML
    private Button addFileButton;
    @FXML
    private Button removeFileButton;
    @FXML
    private Button connectButton;
    @FXML
    private Button getReportButton;
    @FXML
    private Label selectedService;
    @FXML
    private VBox vboxTemperature;
    @FXML
    private TextField textFieldTemperature;
    @FXML
    private Button buttonTemperature;
    private List<String> servicesNames;
    private Map<String, List<String>> servicesAndUploadedFilesMap;
    private MqttPublisher mqttPublisher;
    private final Alert popup = new Alert(Alert.AlertType.INFORMATION);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        servicesNames = JSONReader.getServices();
        createAndSetCheckboxList();
        setFilesMap();
        disableFileButtons();
        setFilesListListener();
        setRemoveFileButtonListener();
        setConnectButton();
        setTextFieldTemperatureFilter();
        setButtonTemperature();
    }

    public void createAndSetCheckboxList() {
        short lengthSectionA = (short) (calculateLongestServiceString(servicesNames) * 9);
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
        vboxTemperature.setVisible(serviceName.equals("CarClimatizationService"));
        enableAddFileButton();
        disableRemoveFileButton();
        addFiles(serviceName);
        selectedService.setText(serviceName);
        event.consume();
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

    public void setRemoveFileButtonListener() {
        removeFileButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String fileToBeRemoved = filesList.getSelectionModel().getSelectedItem();
                if (!fileToBeRemoved.isEmpty()) {
                    filesList.getItems().remove(fileToBeRemoved);
                    servicesAndUploadedFilesMap.get(selectedService.getText()).remove(fileToBeRemoved);
                    removeFileFromDisk(fileToBeRemoved);
                    refreshFilesList(selectedService.getText());
                }
            }
        });
    }

    private void removeFileFromDisk(String fileToBeRemoved) {
        Path uploadedFilesDir = Paths.get(".\\uploadedfiles");
        Path filePath = uploadedFilesDir.resolve(fileToBeRemoved);
        if (Files.exists(filePath)) {
            try {
                Files.delete(filePath);
                popup.setContentText("File deleted successfully: " + filePath);
                popup.showAndWait();
            } catch (IOException e) {
                popup.setContentText("Error deleting file: " + filePath);
                popup.showAndWait();
            }
        } else {
            popup.setContentText("File doesn't exist on disk: " + fileToBeRemoved);
            popup.showAndWait();
        }
        disableRemoveFileButton();
    }

    private void setFilesListListener() {
        filesList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (filesList.getSelectionModel().getSelectedItem() != null) {
                    enableRemoveFileButton();
                }
            }
        });
    }

    private void setConnectButton() {
        connectButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Topic topic = null;
                switch (selectedService.getText()) {
                    case "CarClimatizationService": {
                        CarClimatizationFileHandler carClimatizationFileHandler =
                                new CarClimatizationFileHandler(servicesAndUploadedFilesMap.get(selectedService.getText()));
                        carClimatizationFileHandler.sendData();
                        break;
                    }
                    case "CarGpsService": {
                        break;
                    }
                }
            }
        });
    }

    private void disableFileButtons() {
        addFileButton.setDisable(true);
        addFileButton.setOpacity(0.5f);
        removeFileButton.setDisable(true);
        removeFileButton.setOpacity(0.5f);
    }

    private void disableRemoveFileButton() {
        removeFileButton.setDisable(true);
        removeFileButton.setOpacity(0.5f);
    }

    private void enableRemoveFileButton() {
        removeFileButton.setDisable(false);
        removeFileButton.setOpacity(1.0f);
    }

    private void enableAddFileButton() {
        addFileButton.setDisable(false);
        addFileButton.setOpacity(1.0f);
    }

    private void setTextFieldTemperatureFilter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }
            if (Pattern.matches("\\d*", newText)) {
                try {
                    int temperatureValue = Integer.parseInt(newText);
                    if (temperatureValue >= 0 && temperatureValue <= 30) {
                        return change;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textFieldTemperature.setTextFormatter(textFormatter);
    }

    private void setButtonTemperature() {
        buttonTemperature.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                CarClimatizationSetTemperatureHandler carClimatizationSetTemperatureHandler = new CarClimatizationSetTemperatureHandler();
                carClimatizationSetTemperatureHandler.sendData("set " + textFieldTemperature.getText());
            }
        });
    }
}
