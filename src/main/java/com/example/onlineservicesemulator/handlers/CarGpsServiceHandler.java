package com.example.onlineservicesemulator.handlers;

import com.example.onlineservicesemulator.Controller;
import com.example.onlineservicesemulator.mqtt.MqttGpsPublisherSingleton;
import com.example.onlineservicesemulator.mqtt.MqttPublisher;
import com.example.onlineservicesemulator.utils.TextFileReader;
import javafx.scene.control.Button;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CarGpsServiceHandler {
    private MqttPublisher mqttPublisher;
    private List<String> fileNames;
    private Button connectButton;

    public CarGpsServiceHandler(List<String> fileNames, Button connectButton) {
        try {
            this.fileNames = fileNames;
            this.connectButton = connectButton;
            this.mqttPublisher = MqttGpsPublisherSingleton.getInstance().getMqttPublisher();
            mqttPublisher.connect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendData() {
        String[] pairs = Arrays.stream(TextFileReader.getData(fileNames)
                        .substring(1, TextFileReader.getData(fileNames).length() - 1)
                        .split("\\},\\s?\\{"))
                .map(s -> s.endsWith("}") ? s.substring(0, s.length() - 1) : s)
                .toArray(String[]::new);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int currentIndex = 0;

            @Override
            public void run() {
                if (currentIndex < pairs.length) {
                    disableConnectButton();
                    String dataToSend = pairs[currentIndex];
                    try {
                        mqttPublisher.sendData(dataToSend);
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                    currentIndex++;
                } else {
                    enableConnectButton();
                    timer.cancel();
                    timer.purge();
                }
            }
        }, 0, 10000);
    }

    public void enableConnectButton() {
        connectButton.setDisable(false);
        connectButton.setOpacity(1.0f);
    }

    public void disableConnectButton() {
        connectButton.setDisable(true);
        connectButton.setOpacity(0.5f);
    }
}
