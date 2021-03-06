package com.tqhy.client.jna;

import com.tqhy.client.utils.FXMLUtils;
import javafx.application.Platform;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yiheng
 * @create 9/6/2019
 * @since 1.0.0
 */
public class GlobalKeyListener implements NativeKeyListener {

    boolean altPressed = false;
    boolean sPressed = false;
    boolean qPressed = false;

    Logger logger = LoggerFactory.getLogger(GlobalKeyListener.class);

    @Override
    public void nativeKeyTyped(NativeKeyEvent event) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent event) {
        if (event.getKeyCode() == NativeKeyEvent.VC_ALT) {
            altPressed = true;
            //logger.info("ctrl Pressed: {}", ctrlPressed);
        }
        if (event.getKeyCode() == NativeKeyEvent.VC_Q) {
            qPressed = true;
            //logger.info("q Pressed1: {}", qPressed);
        }

        if (event.getKeyCode() == NativeKeyEvent.VC_S) {
            sPressed = true;
            //logger.info("s Pressed: {}", sPressed);
        }

        if (altPressed && sPressed) {
            logger.info("start snapshot...");
            Platform.runLater(() -> FXMLUtils.loadSnapshot("/static/fxml/snapshot.fxml"));
            altPressed = false;
            sPressed = false;
        } else if (altPressed && qPressed) {
            logger.info("shot key fired...");
            Platform.runLater(() -> FXMLUtils.loadPopWindow("/static/fxml/warning_onclose.fxml"));
            altPressed = false;
            qPressed = false;
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent event) {
        //logger.info("Key Released: " + NativeKeyEvent.getKeyText(event.getKeyCode()));
        if (event.getKeyCode() == NativeKeyEvent.VC_ALT) {
            altPressed = false;
        }

        if (event.getKeyCode() == NativeKeyEvent.VC_Q) {
            qPressed = false;
        }

        if (event.getKeyCode() == NativeKeyEvent.VC_S) {
            sPressed = false;
        }
    }
}
