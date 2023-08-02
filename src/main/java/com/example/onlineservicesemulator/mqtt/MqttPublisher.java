package com.example.onlineservicesemulator.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttPublisher {
    private final String brokerUrl;
    private final String topic;
    private MqttClient mqttClient;
    private static final String MQTT_CLIENT_ID = "OnlineServicesEmulator";

    public MqttPublisher(String brokerUrl, String topic) {
        this.brokerUrl = brokerUrl;
        this.topic = topic;
    }

    public void connect() throws MqttException {
        try {
            mqttClient = new MqttClient(brokerUrl, MQTT_CLIENT_ID, new MemoryPersistence());
            mqttClient.connect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() throws MqttException {
        if (mqttClient != null && mqttClient.isConnected()) {
            mqttClient.disconnect();
        }
    }

    public void sendData(String data) throws MqttException {
        MqttMessage message = new MqttMessage(data.getBytes());
        mqttClient.publish(topic, message);
    }
}
