package com.example.onlineservicesemulator.handlers;

import com.example.onlineservicesemulator.mqtt.MqttPublisher;
import com.example.onlineservicesemulator.mqtt.MqttClimatizationPublisherSingleton;
import com.example.onlineservicesemulator.utils.TextFileReader;
import javafx.scene.control.Button;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CarClimatizationFileHandler {
    private MqttPublisher mqttPublisher;
    private List<String> fileNames;
    private Button connectButton;

    public CarClimatizationFileHandler(List<String> fileNames, Button connectButton) {
        try {
            this.fileNames = fileNames;
            this.connectButton = connectButton;
            this.mqttPublisher = MqttClimatizationPublisherSingleton.getInstance().getMqttPublisher();
            mqttPublisher.connect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendData() {
        String[] temperatures = TextFileReader.getData(fileNames).split(",");
        Timer timer = new Timer();
        System.out.println(Arrays.toString(temperatures));
        timer.schedule(new TimerTask() {
            int currentIndex = 0;

            @Override
            public void run() {
                if (currentIndex < temperatures.length) {
                    disableConnectButton();
                    String dataToSend = temperatures[currentIndex];
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
        }, 0, 15000);
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
