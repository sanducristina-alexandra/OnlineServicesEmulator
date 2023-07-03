module com.example.onlineservicesemulator {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;

    opens com.example.onlineservicesemulator to javafx.fxml;
    exports com.example.onlineservicesemulator;
}