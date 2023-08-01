package com.example.onlineservicesemulator.handlers;

import com.example.onlineservicesemulator.mqtt.MqttPublisherSingleton;
import com.example.onlineservicesemulator.mqtt.MqttPublisher;
import org.eclipse.paho.client.mqttv3.MqttException;

public class CarClimatizationSetTemperatureHandler {

    private MqttPublisher mqttPublisher;
    public CarClimatizationSetTemperatureHandler() {
        try {
            this.mqttPublisher = MqttPublisherSingleton.getInstance().getMqttPublisher();
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
