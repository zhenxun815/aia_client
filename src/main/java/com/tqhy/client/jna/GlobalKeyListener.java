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

    boolean ctrlPressed = false;
    boolean sPressed = false;

    Logger logger = LoggerFactory.getLogger(GlobalKeyListener.class);

    @Override
    public void nativeKeyTyped(NativeKeyEvent event) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent event) {
        if (event.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
            //logger.info("ctrl Pressed1: {}", ctrlPressed);
            ctrlPressed = true;
            //logger.info("ctrl Pressed2: {}", ctrlPressed);
        }

        if (event.getKeyCode() == NativeKeyEvent.VC_S) {
            sPressed = true;
        }

        if (ctrlPressed && sPressed) {
            logger.info("start snapshot...");
            Platform.runLater(() -> FXMLUtils.loadSnapshot("/static/fxml/snapshot.fxml"));
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent event) {
        //logger.info("Key Released: " + NativeKeyEvent.getKeyText(event.getKeyCode()));
        if (event.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
            ctrlPressed = false;
        }

        if (event.getKeyCode() == NativeKeyEvent.VC_S) {
            sPressed = false;
        }
    }
}
