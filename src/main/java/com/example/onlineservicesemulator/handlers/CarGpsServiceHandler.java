package com.example.onlineservicesemulator.handlers;

import com.example.onlineservicesemulator.mqtt.MqttGpsPublisherSingleton;
import com.example.onlineservicesemulator.mqtt.MqttPublisher;
import com.example.onlineservicesemulator.utils.ConsoleLogger;
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
    private List<Button> greyedButtons;

    public CarGpsServiceHandler(List<String> fileNames, List<Button> greyedButtons) {
        try {
            this.fileNames = fileNames;
            this.greyedButtons = greyedButtons;
            this.mqttPublisher = MqttGpsPublisherSingleton.getInstance().getMqttPublisher();
            mqttPublisher.connect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendData() {
            String[] pairs = Arrays.stream(TextFileReader.getData(fileNames, "CarGpsService")
                            .substring(1, TextFileReader.getData(fileNames, "CarGpsService").length() - 1)
                            .split("\\},\\s?\\{"))
                    .map(s -> s.endsWith("}") ? s.substring(0, s.length() - 1) : s)
                    .toArray(String[]::new);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                int currentIndex = 0;

                @Override
                public void run() {
                    if (currentIndex < pairs.length) {
                        disableButtons();
                        String dataToSend = pairs[currentIndex];
                        try {
                            mqttPublisher.sendData(dataToSend);
                            ConsoleLogger.log("Sent trip coordinates: " + dataToSend);
                        } catch (MqttException e) {
                            ConsoleLogger.log("Failed to sennd GPS coordinates. Can't connect to the server");
                            e.printStackTrace();
                            enableButtons();
                            timer.cancel();
                            timer.purge();
                            return;
                        }
                        currentIndex++;
                    } else {
                        enableButtons();
                        timer.cancel();
                        timer.purge();
                    }
                }
            }, 0, 3000);
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
