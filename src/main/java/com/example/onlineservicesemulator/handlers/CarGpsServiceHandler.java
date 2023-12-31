package com.example.onlineservicesemulator.handlers;

import com.example.onlineservicesemulator.mqtt.MqttGpsPublisherSingleton;
import com.example.onlineservicesemulator.mqtt.MqttPublisher;
import com.example.onlineservicesemulator.utils.TextFileReader;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CarGpsServiceHandler {
    private MqttPublisher mqttPublisher;
    private List<String> fileNames;

    public CarGpsServiceHandler(List<String> fileNames) {
        try {
            this.fileNames = fileNames;
            this.mqttPublisher = MqttGpsPublisherSingleton.getInstance().getMqttPublisher();
            mqttPublisher.connect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendData() {
        String[] pairs = Arrays.stream(TextFileReader.getData(fileNames).substring(1, TextFileReader.getData(fileNames).length() - 1).split("\\},\\s?\\{")).map(s -> s.endsWith("}") ? s.substring(0, s.length() - 1) : s).toArray(String[]::new);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int currentIndex = 0;

            @Override
            public void run() {
                if (currentIndex < pairs.length) {
                    String dataToSend = pairs[currentIndex];
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
        }, 0, 10000);
    }
}
