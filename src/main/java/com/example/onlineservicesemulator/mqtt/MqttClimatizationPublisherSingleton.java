package com.example.onlineservicesemulator.mqtt;

import com.example.onlineservicesemulator.models.Topic;

public class MqttClimatizationPublisherSingleton {

    private static MqttClimatizationPublisherSingleton instance;
    private final MqttPublisher mqttPublisher;

    private MqttClimatizationPublisherSingleton() {
        mqttPublisher = new MqttPublisher("tcp://broker.emqx.io:1883", Topic.INSIDE_TEMPERATURE_SENSOR.toString());
    }

    public static synchronized MqttClimatizationPublisherSingleton getInstance() {
        if (instance == null) {
            instance = new MqttClimatizationPublisherSingleton();
        }
        return instance;
    }

    public MqttPublisher getMqttPublisher() {
        return mqttPublisher;
    }
}