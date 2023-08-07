package com.example.onlineservicesemulator;

import com.example.onlineservicesemulator.handlers.CarClimatizationFileHandler;
import com.example.onlineservicesemulator.handlers.CarClimatizationSetTemperatureHandler;
import com.example.onlineservicesemulator.handlers.CarGpsServiceHandler;
import com.example.onlineservicesemulator.models.Topic;
import com.example.onlineservicesemulator.mqtt.MqttPublisher;
import com.example.onlineservicesemulator.utils.ClimatizationReportParser;
import com.example.onlineservicesemulator.utils.JSONReader;
import com.example.onlineservicesemulator.utils.TripReportParser;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
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
import java.awt.Desktop;

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
    private Button buttonGetTripReport;
    @FXML
    private Button buttonGetClimatizationReport;
    @FXML
    private Label selectedService;
    @FXML
    private VBox vboxTemperature;
    @FXML
    private TextField textFieldTemperature;
    @FXML
    private Button buttonTemperature;
    @FXML
    private VBox vboxTripMap;
    @FXML
    private Button buttonGenerateTripMap;
    private List<String> servicesNames;
    private Map<String, List<String>> servicesAndUploadedFilesMap;
    private MqttPublisher mqttPublisher;
    private final Alert popup = new Alert(Alert.AlertType.INFORMATION);
    private static final String ROOT_DIRECTORY = System.getProperty("user.dir");

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
        setButtonGenerateTripMap();
        setButtonGetClimatizationReport();
        setButtonGetTripReport();
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
        vboxTripMap.setVisible(serviceName.equals("CarGpsService"));
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
                        CarGpsServiceHandler carGpsServiceHandler =
                                new CarGpsServiceHandler(servicesAndUploadedFilesMap.get(selectedService.getText()));
                        carGpsServiceHandler.sendData();
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

    private void setButtonGenerateTripMap() {
        buttonGenerateTripMap.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    String serverUrl = "http://localhost:8080/get_last_trip";

                    URL url = new URL(serverUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        try {
                            String responseData = response.toString();
                            responseData = responseData.replace("{", "%7B").replace("}", "%7D")
                                    .replace("[", "%5B").replace("]", "%5D")
                                    .replace(" ", "%20").replace("\"", "%22")
                                    .replace("'", "%27").replace("<", "%3C")
                                    .replace(">", "%3E").replace("|", "%7C")
                                    .replace("\\", "%5C").replace("^", "%5E")
                                    .replace("`", "%60");

                            if (Desktop.isDesktopSupported()) {
                                Desktop desktop = Desktop.getDesktop();
                                desktop.browse(new URI(responseData));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setButtonGetClimatizationReport() {
        buttonGetClimatizationReport.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        String serverUrl = "http://localhost:8080/get_last_climatization_report";
                        URL url = new URL(serverUrl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");

                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String inputLine;
                            StringBuilder response = new StringBuilder();

                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();

                            String responseData = response.toString();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    showClimatizationReport(responseData);
                                }
                            });
                        }
                        conn.disconnect();

                        return null;
                    }
                };

                new Thread(task).start();
            }
        });
    }

    private void setButtonGetTripReport() {
        buttonGetTripReport.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        String serverUrl = "http://localhost:8080/get_last_trip_report";
                        URL url = new URL(serverUrl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");

                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String inputLine;
                            StringBuilder response = new StringBuilder();

                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();

                            String responseData = response.toString();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    showTripReport(responseData);
                                }
                            });
                        }
                        conn.disconnect();
                        return null;
                    }
                };
                new Thread(task).start();
            }
        });
    }

    private void showTripReport(String responseData) {
        String htmlTemplate = "<html><head><link rel=\"stylesheet" +
                "\" type=\"text/css\" href=\"TripReport.css\"></head>" +
                "<body><h1>Trip report</h1><p><strong>ReportId: </strong>{{ID}}" +
                "</p><p><strong>Status: </strong>{{STATUS}}</p><p><strong>Start date: " +
                "</strong>{{START_DATE}}</p><p><strong>End date: </strong>{{END_DATE}}" +
                "</p><p><strong>Coordinates: </strong><ul>" +
                "{{COORDINATES}}</ul></p><p><strong>SOS email: </strong>{{SOS_EMAIL}}</p>" +
                "<img src=\"sebi_srl.png\" alt=\"Sebi SRL\"></body></html>";

        String id = TripReportParser.getId(responseData);
        String status = TripReportParser.getStatus(responseData);
        String startDate = TripReportParser.getStartTripDate(responseData);
        String endDate = TripReportParser.getEndTripDate(responseData);
        String coordinates = TripReportParser.getCoordinates(responseData);
        String sosEmail = TripReportParser.getSosEmail(responseData);

        String coordinatesList = "<ul>";

        String[] coordinateValues = coordinates.split(",");

        for (int i = 0; i < coordinateValues.length; i += 2) {
            String latitude = coordinateValues[i];
            String longitude = coordinateValues[i + 1];
            coordinatesList += "<li>" + latitude + ", " + longitude + "</li>";
        }

        coordinatesList += "</ul>";

        String htmlContent = htmlTemplate.replace("{{ID}}", id)
                .replace("{{STATUS}}", status)
                .replace("{{START_DATE}}", startDate)
                .replace("{{END_DATE}}", endDate)
                .replace("{{COORDINATES}}", coordinatesList)
                .replace("{{SOS_EMAIL}}", sosEmail);


        String filePath = ROOT_DIRECTORY + "/TripReport.html";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(htmlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File htmlFile = new File(filePath);
            Desktop.getDesktop().browse(htmlFile.toURI());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showClimatizationReport(String responseData) {

        String htmlTemplate = "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"ClimatizationReport.css\"></head>" +
                "<body><h1>Climatization report</h1><p><strong>ReportId: </strong>{{ID}}" +
                "</p><p><strong>Date: </strong>{{DATE}}</p><p><strong>Air Conditioning Power: </strong>{{AC_POWER}}" +
                "</p><p><strong>Action Code: </strong>{{ACTION_CODE}}</p><img src=\"sebi_srl.png\" alt=\"Sebi SRL\"></body></html>";

        System.out.println(responseData);
        String id = ClimatizationReportParser.getId(responseData);
        String date = ClimatizationReportParser.getDate(responseData);
        String acPower = ClimatizationReportParser.getAcPower(responseData);
        String actionCode = ClimatizationReportParser.getActionCode(responseData);

        String htmlContent = htmlTemplate.replace("{{ID}}", id)
                .replace("{{DATE}}", date)
                .replace("{{AC_POWER}}", acPower)
                .replace("{{ACTION_CODE}}", actionCode);


        String filePath = ROOT_DIRECTORY + "/climatizationReport.html";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(htmlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File htmlFile = new File(filePath);
            Desktop.getDesktop().browse(htmlFile.toURI());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
