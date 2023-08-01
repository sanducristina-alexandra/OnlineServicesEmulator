package com.example.onlineservicesemulator.handlers;

import com.example.onlineservicesemulator.models.Topic;
import com.example.onlineservicesemulator.mqtt.MqttPublisher;
import com.example.onlineservicesemulator.utils.TextFileReader;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.*;

public class CarClimatizationFileHandler {
    private MqttPublisher mqttPublisher;
    private List<String> fileNames;

    public CarClimatizationFileHandler(List<String> fileNames) {
        try {
            this.fileNames = fileNames;
            this.mqttPublisher = new MqttPublisher("tcp://broker.emqx.io:1883",
                    Topic.INSIDE_TEMPERATURE_SENSOR.toString());
            mqttPublisher.connect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendData() {
        String[] temperatures = TextFileReader.getData(fileNames).split(",");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int currentIndex = 0;

            @Override
            public void run() {
                if (currentIndex < temperatures.length) {
                    String dataToSend = temperatures[currentIndex];
                    try {
                        mqttPublisher.sendData(dataToSend);
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                    currentIndex++;
                } else {
                    timer.cancel();
                    timer.purge();
                }
            }
        }, 0, 15000);
    }
}
