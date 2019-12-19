package com.tqhy.client.controllers;

import com.tqhy.client.ClientApplication;
import com.tqhy.client.utils.FXMLUtils;
import javafx.fxml.FXML;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

/**
 * @author Yiheng
 * @create 12/18/2019
 * @since 1.0.0
 */
@Controller
public class WarningOnServerIPController extends BasePopWindowController {
    @FXML
    public VBox base_pane;
    Logger logger = LoggerFactory.getLogger(WarningOnCloseController.class);

    @FXML
    public void initialize() {
        FXMLUtils.center2Display(base_pane);
    }

    @FXML
    public void closeConfirm(MouseEvent mouseEvent) {
        MouseButton button = mouseEvent.getButton();
        if (MouseButton.PRIMARY.equals(button)) {
            logger.info("close confirm ...");
            Stage stage = getOwnerStageFromEvent(mouseEvent);
            stage.close();
            ClientApplication.stage.close();
            System.exit(0);
        }
    }

}
