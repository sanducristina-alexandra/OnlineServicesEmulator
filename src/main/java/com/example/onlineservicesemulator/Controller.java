package com.example.onlineservicesemulator;


import com.example.onlineservicesemulator.classes.JSONReader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private VBox checkboxList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createAndSetCheckboxList();
    }

    public void createAndSetCheckboxList() {
        List<String> servicesNames = JSONReader.getServices();
        double lenghtSectionA = calculateLongestServiceString(servicesNames);
        checkboxList.setPrefWidth(lenghtSectionA);
        for (String serviceName : servicesNames) {
            CheckBox checkBox = new CheckBox();
            checkBox.setId("checkbox_" + serviceName);
            Label checkBoxLabel = new Label(serviceName);
            checkBox.setGraphic(checkBoxLabel);
            checkBoxLabel.setOnMouseClicked(event -> {
                checkBox.setSelected(false);
                event.consume();
            });
            checkboxList.getChildren().add(checkBox);
            checkboxList.setSpacing(5);
        }
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
}
