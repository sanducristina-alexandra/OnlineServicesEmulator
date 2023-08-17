package com.example.onlineservicesemulator.handlers;

import com.example.onlineservicesemulator.mqtt.MqttPublisher;
import com.example.onlineservicesemulator.mqtt.MqttClimatizationPublisherSingleton;
import com.example.onlineservicesemulator.utils.ConsoleLogger;
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
    private List<Button> greyedButtons;

    public CarClimatizationFileHandler(List<String> fileNames, List<Button> greyedButtons) {
        try {
            this.fileNames = fileNames;
            this.greyedButtons = greyedButtons;
            this.mqttPublisher = MqttClimatizationPublisherSingleton.getInstance().getMqttPublisher();
            mqttPublisher.connect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendData() {
        String[] temperatures = TextFileReader.getData(fileNames, "CarClimatizationService").split(",");
        Timer timer = new Timer();
        System.out.println(Arrays.toString(temperatures));
        timer.schedule(new TimerTask() {
            int currentIndex = 0;

            @Override
            public void run() {
                if (currentIndex < temperatures.length) {
                    disableButtons();
                    String dataToSend = temperatures[currentIndex];
                    try {
                        mqttPublisher.sendData(dataToSend);
                        ConsoleLogger.log("Sent car climatization value: " + dataToSend);
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                    currentIndex++;
                } else {
                    enableButtons();
                    timer.cancel();
                    timer.purge();
                }
            }
        }, 0, 5000);
    }

    public void enableButtons() {
        this.greyedButtons.forEach(button -> {
            button.setDisable(false);
            button.setOpacity(1.0f);
        });
    }

    public void disableButtons() {
        this.greyedButtons.forEach(button -> {
            button.setDisable(true);
            button.setOpacity(0.5f);
        });
    }

}
