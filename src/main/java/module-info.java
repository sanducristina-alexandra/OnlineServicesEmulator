module com.example.onlineservicesemulator {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires org.eclipse.paho.client.mqttv3;


    opens com.example.onlineservicesemulator to javafx.fxml;
    exports com.example.onlineservicesemulator;
    exports com.example.onlineservicesemulator.mqtt;
    opens com.example.onlineservicesemulator.mqtt to javafx.fxml;
}