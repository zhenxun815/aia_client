package com.tqhy.client.controllers;

import com.tqhy.client.service.HeartBeatService;
import com.tqhy.client.utils.FXMLUtils;
import com.tqhy.client.utils.NetworkUtils;
import com.tqhy.client.utils.ViewsUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class FloatController {

    @FXML
    AnchorPane anchorPane;

    private double xOffset = 0;
    private double yOffset = 0;
    private Logger logger = LoggerFactory.getLogger(FloatController.class);

    @Autowired
    HeartBeatService heartBeatService;

    @FXML
    void initialize() {
        if (NetworkUtils.initServerIP()) {
            heartBeatService.startBeat();
        }
    }

    @FXML
    public void onPress(MouseEvent event) {
        //anchorPane.setStyle("-fx-background-color: green;");
        event.consume();
        MouseButton button = event.getButton();
        if (MouseButton.SECONDARY == button) {
            logger.info(button.name() + "....");

            double x = event.getScreenX();
            double y = event.getScreenY();
        } else if (MouseButton.PRIMARY.equals(button)) {
            if (MouseEvent.MOUSE_PRESSED == event.getEventType()) {
                // logger.info("left press...");
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        }

    }

    @FXML
    public void onDrag(MouseEvent event) {
        event.consume();
        //anchorPane.setStyle("-fx-background-color: lightseagreen;");
        MouseButton button = event.getButton();
        if (MouseButton.SECONDARY == button) {
            //logger.info(button.name() + "....");
        } else if (MouseButton.PRIMARY.equals(button)) {
            if (MouseEvent.MOUSE_DRAGGED == event.getEventType()) {
                //logger.info("left drag..");
                Stage stage = (Stage) anchorPane.getScene().getWindow();
                double width = anchorPane.getWidth();
                double height = anchorPane.getHeight();
                double x = event.getScreenX() - xOffset;
                double y = event.getScreenY() - yOffset;
                double maxX = ViewsUtils.getMaxX(width);
                double maxY = ViewsUtils.getMaxY(height);
                stage.setX(x < maxX * 2 / 3 ? maxX * 2 / 3 : (x > maxX ? maxX : x));
                stage.setY(y < maxY * 1 / 3 ? maxY * 1 / 3 : (y > maxY ? maxY : y));
            }
        }
    }

    @FXML
    public void onClick(MouseEvent event) {
        event.consume();
        MouseButton button = event.getButton();
        if (MouseButton.SECONDARY == button) {
            logger.info("onclick...");
            Platform.runLater(() -> FXMLUtils.loadSnapshot("/static/fxml/snapshot.fxml"));
        }
    }
}