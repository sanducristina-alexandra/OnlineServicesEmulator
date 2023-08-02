package com.example.onlineservicesemulator.mqtt;

import com.example.onlineservicesemulator.models.Topic;
import org.eclipse.paho.client.mqttv3.*;

public class MqttPublisherSingleton {

    private static MqttPublisherSingleton instance;
    private MqttPublisher mqttPublisher;
    private static final String MQTT_CLIENT_ID = "OnlineServicesEmulator";

    private MqttPublisherSingleton() throws MqttException {
        mqttPublisher = new MqttPublisher("tcp://broker.emqx.io:1883", Topic.INSIDE_TEMPERATURE_SENSOR.toString());
    }

    public static synchronized MqttPublisherSingleton getInstance() throws MqttException {
        if (instance == null) {
            instance = new MqttPublisherSingleton();
        }
        return instance;
    }
    public MqttPublisher getMqttPublisher() {
        return mqttPublisher;
    }
}