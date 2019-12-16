package com.tqhy.client.controllers;

import com.tqhy.client.config.Constants;
import com.tqhy.client.network.Network;
import com.tqhy.client.network.app.JavaAppBase;
import com.tqhy.client.service.HeartBeatService;
import com.tqhy.client.utils.NetworkUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Worker;
import javafx.scene.CacheHint;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * @author Yiheng
 * @create 3/19/2019
 * @since 1.0.0
 */
@Component
@Getter
@Setter
public class BaseWebviewController {


    public static BooleanProperty jumpToConnectionFlag = new SimpleBooleanProperty(false);
    Logger logger = LoggerFactory.getLogger(BaseWebviewController.class);
    /**
     * 判断页面是否需要跳转到登录页,与{@link com.tqhy.client.service.HeartBeatService HeartBeatService} 中
     * {@code jumpToLandingFlag} 进行双向绑定
     */
    BooleanProperty jumpToLandingFlag = new SimpleBooleanProperty(false);
    @Autowired
    HeartBeatService heartBeatService;
    @Value("${network.url.landing:''}")
    private String landingUrl;
    @Value("${network.url.connection:''}")
    private String connectionUrl;

    void initialize(WebView webView) {
        initWebView(webView);
        initWebAlert(webView);
        initJumpToLanding(webView);
        initJumpToConnection(webView);
    }

    /**
     * 初始化webView设置与webEngine对象
     */
    public void initWebView(WebView webView) {
        logger.info("into init webEngine..");
        webView.setCache(true);
        webView.setCacheHint(CacheHint.SPEED);
        logger.info("init webView complete..");
    }

    /**
     * 初始化web页面alert事件监听
     */
    private void initWebAlert(WebView webView) {
        webView.getEngine()
               .setOnAlert(event -> {
                   String data = event.getData() + Constants.MSG_SPLITTER;
                   logger.info("alert data is: " + data);

                   String[] split = data.split(Constants.MSG_SPLITTER);
                   switch (split[0]) {
                       case Constants.CMD_MSG_LOGOUT:
                           heartBeatService.stopBeat();
                           logout();
                           break;
                       default:
                           showAlert(data);
                   }
               });
    }


    /**
     * 初始化跳转登录页面逻辑
     */
    private void initJumpToLanding(WebView webView) {
        jumpToLandingFlag.bindBidirectional(heartBeatService.jumpToLandingFlagProperty());
        jumpToLandingFlag.addListener((observable, oldValue, newValue) -> {
            logger.info("jumpToLandingFlag changed,oldValue is: " + oldValue + ", newValue is: " + newValue);
            if (newValue) {
                Platform.runLater(() -> {
                    logger.info("jump to landing");
                    webView.getEngine().load(Network.LOCAL_BASE_URL + landingUrl);
                });
                jumpToLandingFlag.set(false);
            }
        });
    }

    private void initJumpToConnection(WebView webView) {
        heartBeatService.stopBeat();
        jumpToConnectionFlag.addListener((observable, oldValue, newValue) -> {
            logger.info("jumpToLandingFlag changed,oldValue is: " + oldValue + ", newValue is: " + newValue);
            if (newValue) {
                Platform.runLater(() -> {
                    logger.info("jump to landing");
                    webView.getEngine().load(Network.LOCAL_BASE_URL + connectionUrl);
                });
                jumpToConnectionFlag.setValue(false);
            }
        });
    }

    /**
     * alert
     *
     * @param message
     */
    public void showAlert(String message) {
        Platform.runLater(() -> {
            Dialog<ButtonType> alert = new Dialog<>();
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(NetworkUtils.toExternalForm("/static/img/logo_title_light.png")));
            alert.getDialogPane().setContentText(message);
            alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
            alert.showAndWait();
        });
    }


    void loadPage(WebView webView, String url) {
        //url为空则加载默认页面:测试连接页面
        String defaultUrl = Network.LOCAL_BASE_URL + connectionUrl;
        webView.getEngine()
               .load(StringUtils.isEmpty(url) ? defaultUrl : url);
    }

    void engineBindApp(WebView webView, JavaAppBase javaApp) {
        WebEngine engine = webView.getEngine();
        engine.getLoadWorker()
              .stateProperty()
              .addListener((ov, oldState, newState) -> {
                  // logger.info("old state: " + oldState + " ,new state: " + newState);
                  if (Worker.State.FAILED == newState) {
                      JSObject window = (JSObject) engine.executeScript("window");
                      window.setMember("tqClient", javaApp);
                      engine.reload();
                  } else if (Worker.State.SUCCEEDED == newState) {
                      JSObject window = (JSObject) engine.executeScript("window");
                      window.setMember("tqClient", javaApp);
                  }
              });
    }

    void logout() {
        jumpToLandingFlag.set(true);
    }


    /**
     * 向js传值
     *
     * @param msgs
     */
    void sendMsgToJs(WebView webView, String funcName, String... msgs) {
        logger.info("send msg to js func {} with msg {}", funcName, msgs);
        String paramsStr = Arrays.stream(msgs)
                                 .collect(StringBuilder::new,
                                          (builder, msg) -> builder.append("'")
                                                                   .append(msg)
                                                                   .append("'")
                                                                   .append(","),
                                          StringBuilder::append)
                                 .toString();
        paramsStr = paramsStr.substring(0, paramsStr.length() - 1);
        logger.info("paramstr is {}", paramsStr);
        String jsFunStr = funcName + "(" + paramsStr + ")";
        logger.info("jsFunStr is {}", jsFunStr);

        Object response = webView.getEngine()
                                 .executeScript(jsFunStr);
        String s = (String) response;
        logger.info("send msg to js get response: {}", s);
    }
}
