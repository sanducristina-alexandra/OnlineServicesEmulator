package com.example.onlineservicesemulator.handlers;

import com.example.onlineservicesemulator.mqtt.MqttClimatizationPublisherSingleton;
import com.example.onlineservicesemulator.mqtt.MqttPublisher;
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
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}
