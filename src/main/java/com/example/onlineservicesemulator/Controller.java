package com.example.onlineservicesemulator;


import com.example.onlineservicesemulator.classes.JSONReader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private VBox checkboxList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       setCheckboxList();
    }
    public void setCheckboxList(){
        ArrayList<String> servicesNames = JSONReader.getServices();
        for (int i = 0; i < servicesNames.size(); i++) {
            CheckBox checkBox = new CheckBox(servicesNames.get(i));
            checkBox.setId("checkbox_" + servicesNames.get(i));
            checkboxList.getChildren().add(checkBox);
            checkboxList.setSpacing(5);
        }
    }
}
