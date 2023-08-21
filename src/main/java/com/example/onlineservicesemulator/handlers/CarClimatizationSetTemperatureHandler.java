package com.example.onlineservicesemulator.handlers;

import com.example.onlineservicesemulator.mqtt.MqttClimatizationPublisherSingleton;
import com.example.onlineservicesemulator.mqtt.MqttPublisher;
import com.example.onlineservicesemulator.utils.ConsoleLogger;
import javafx.scene.control.Button;
import org.eclipse.paho.client.mqttv3.MqttException;

public class CarClimatizationSetTemperatureHandler {

    private MqttPublisher mqttPublisher;

    public CarClimatizationSetTemperatureHandler() {
        try {
            this.mqttPublisher = MqttClimatizationPublisherSingleton.getInstance().getMqttPublisher();
            mqttPublisher.connect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendData(String setTemperature) {
        try {
            mqttPublisher.sendData(setTemperature);
            ConsoleLogger.log("Target temperature successfully set to " + setTemperature.substring(4,6) + " °C.");
        } catch (MqttException e) {
            ConsoleLogger.log("Failed to set target temperature. Can't connect to the server");
            e.printStackTrace();
        }
    }
}
