package com.example.onlineservicesemulator.mqtt;

import com.example.onlineservicesemulator.models.Topic;
import org.eclipse.paho.client.mqttv3.*;

public class MqttClimatizationPublisherSingleton {

    private static MqttClimatizationPublisherSingleton instance;
    private MqttPublisher mqttPublisher;
    private static final String MQTT_CLIENT_ID = "OnlineServicesEmulator";

    private MqttClimatizationPublisherSingleton() throws MqttException {
        mqttPublisher = new MqttPublisher("tcp://broker.emqx.io:1883", Topic.INSIDE_TEMPERATURE_SENSOR.toString());
    }

    public static synchronized MqttClimatizationPublisherSingleton getInstance() throws MqttException {
        if (instance == null) {
            instance = new MqttClimatizationPublisherSingleton();
        }
        return instance;
    }

    public MqttPublisher getMqttPublisher() {
        return mqttPublisher;
    }
}