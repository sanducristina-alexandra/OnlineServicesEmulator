package com.example.onlineservicesemulator.mqtt;

import com.example.onlineservicesemulator.models.Topic;

public class MqttGpsPublisherSingleton {
    private static MqttGpsPublisherSingleton instance;
    private final MqttPublisher mqttPublisher;

    private MqttGpsPublisherSingleton() {
        mqttPublisher = new MqttPublisher("tcp://broker.emqx.io:1883", Topic.CAR_GPS_SENSOR.toString());
    }

    public static synchronized MqttGpsPublisherSingleton getInstance() {
        if (instance == null) {
            instance = new MqttGpsPublisherSingleton();
        }
        return instance;
    }

    public MqttPublisher getMqttPublisher() {
        return mqttPublisher;
    }
}
