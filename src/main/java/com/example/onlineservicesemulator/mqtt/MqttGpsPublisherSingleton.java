package com.example.onlineservicesemulator.mqtt;

import com.example.onlineservicesemulator.models.Topic;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttGpsPublisherSingleton {
    private static MqttGpsPublisherSingleton instance;
    private MqttPublisher mqttPublisher;
    private static final String MQTT_CLIENT_ID = "OnlineServicesEmulator";

    private MqttGpsPublisherSingleton() throws MqttException {
        mqttPublisher = new MqttPublisher("tcp://broker.emqx.io:1883", Topic.CAR_GPS_SENSOR.toString());
    }

    public static synchronized MqttGpsPublisherSingleton getInstance() throws MqttException {
        if (instance == null) {
            instance = new MqttGpsPublisherSingleton();
        }
        return instance;
    }

    public MqttPublisher getMqttPublisher() {
        return mqttPublisher;
    }
}
