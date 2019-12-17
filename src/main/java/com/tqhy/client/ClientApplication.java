package com.tqhy.client;

import com.tqhy.client.jna.GlobalKeyListener;
import com.tqhy.client.unique.AlreadyLockedException;
import com.tqhy.client.unique.JUnique;
import com.tqhy.client.utils.FXMLUtils;
import com.tqhy.client.utils.ViewsUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

/**
 * @author Yiheng
 * @create 1/29/2019
 * @since 1.0.0
 */
@SpringBootApplication
public class ClientApplication extends Application {

    public static ConfigurableApplicationContext springContext;
    public static Stage stage;
    public static Stage menuStage;
    public static Stage snapshotStage;
    static Logger logger = LoggerFactory.getLogger(ClientApplication.class);

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        String appId = "TQHY_AIA_CLIENT";
        boolean alreadyRunning;
        try {
            JUnique.acquireLock(appId, message -> {
                System.out.println("get message: " + message);
                return null;
            });
            alreadyRunning = false;
        } catch (AlreadyLockedException e) {
            alreadyRunning = true;
        }

        if (alreadyRunning) {
            for (int i = 0; i < args.length; i++) {
                JUnique.sendMessage(appId, "call_window");
            }
        } else {
            launch(args);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initPrimaryStage(primaryStage);
    }

    /**
     * 初始化主窗口
     *
     * @throws IOException
     */
    private void initPrimaryStage(Stage primaryStage) {
        logger.info("into initPrimaryStage...");
        stage = primaryStage;
        Platform.setImplicitExit(false);
        Parent root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/static/float/float.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            root = fxmlLoader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("load float complete...");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
//        primaryStage.getIcons()
//                    .add(new javafx.scene.image.Image(
//                            getClass().getResourceAsStream("/deploy/package/windows/logo_title.png")));
        javafx.scene.shape.Rectangle rect = new Rectangle(50, 50);
        rect.setArcHeight(25);
        rect.setArcWidth(25);
        root.setClip(rect);
        logger.info("rect...");
        Scene scene = new Scene(root, 50, 50);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.setX(ViewsUtils.getMaxX(50 * 2));
        stage.setY(ViewsUtils.getScreenHeight() / 2);
        stage.show();

        stage.setOnCloseRequest(event -> {
            event.consume();
            FXMLUtils.loadPopWindow("/static/fxml/warning_onclose.fxml");
        });
    }


    /**
     * 创建系统托盘图标
     */
    private void initSystemTray() {
        try {
            System.setProperty("java.awt.headless", "false");
            Toolkit.getDefaultToolkit();
            if (!java.awt.SystemTray.isSupported()) {
                logger.info("系统不支持托盘图标,程序退出..");
                Platform.exit();
            }
            //PopupMenu popupMenu = createPopMenu(stage);

            SystemTray systemTray = SystemTray.getSystemTray();
            String iconPath = ClientApplication.class.getResource("/static/img/AIAlogo.png").toExternalForm();
            URL imageLoc = new URL(iconPath);
            java.awt.Image image = ImageIO.read(imageLoc);
            //final TrayIcon trayIcon = new TrayIcon(image, "打开悬浮窗",popupMenu);
            final TrayIcon trayIcon = new TrayIcon(image);

            systemTray.add(trayIcon);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() throws Exception {
        super.init();
        logger.info("into init...");

        springContext = SpringApplication.run(ClientApplication.class);
        java.util.logging.Logger globalScreenLogger = java.util.logging.Logger.getLogger(
                GlobalScreen.class.getPackage().getName());
        globalScreenLogger.setLevel(Level.OFF);
        Platform.setImplicitExit(false);
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            logger.error("There was a problem registering the native hook.", ex);
            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
        //initSystemTray();
    }

    @Override
    public void stop() {
        springContext.stop();
        try {
            super.stop();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
