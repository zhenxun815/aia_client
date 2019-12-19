package com.tqhy.client.service;

import com.google.gson.Gson;
import com.tqhy.client.config.Constants;
import com.tqhy.client.models.msg.server.ClientMsg;
import com.tqhy.client.network.Network;
import com.tqhy.client.utils.FXMLUtils;
import com.tqhy.client.utils.NetworkUtils;
import com.tqhy.client.utils.PropertyUtils;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

import static com.tqhy.client.config.Constants.CMD_MSG_CONTINUE_BEAT;
import static com.tqhy.client.config.Constants.CMD_MSG_STOP_BEAT;

/**
 * @author Yiheng
 * @create 3/23/2019
 * @since 1.0.0
 */
@Service
public class HeartBeatService {

    private static String status;
    Logger logger = LoggerFactory.getLogger(HeartBeatService.class);

    public void stopBeat() {
        status = CMD_MSG_STOP_BEAT;
        Platform.runLater(() -> FXMLUtils.loadPopWindow("/static/fxml/warning_max_client.fxml"));
    }

    public void startBeat() {
        status = CMD_MSG_CONTINUE_BEAT;
        String token = NetworkUtils.getPhysicalAddress();
        logger.info("into start beat...{}", token);
        Observable.interval(5, TimeUnit.SECONDS)
                  .takeWhile(beatTimes -> CMD_MSG_CONTINUE_BEAT.equals(status))
                  .observeOn(Schedulers.trampoline())
                  .subscribeOn(Schedulers.io())
                  .subscribe(aLong -> {
                      //logger.info("start token is...{}", token);
                      String serverIP = PropertyUtils.getProperty(Constants.SERVER_IP);
                      if (StringUtils.isEmpty(serverIP)) {
                          stopBeat();
                      } else {
                          Network.getAicApi()
                                 .heartbeat(token)
                                 .observeOn(Schedulers.io())
                                 .subscribeOn(Schedulers.trampoline())
                                 .subscribe(responseBody -> {
                                     String json = responseBody.string();
                                     //logger.info("heart beat response json is: {}", json);
                                     ClientMsg clientMsg = new Gson().fromJson(json, ClientMsg.class);
                                     Integer flag = clientMsg.getFlag();
                                     if (1 == flag) {
                                         logger.info("heart beat continue...{}", token);
                                         status = CMD_MSG_CONTINUE_BEAT;
                                     } else if (Constants.CMD_STATUS_LOGOUT == flag) {
                                         logger.info("heart beat stop...");
                                         stopBeat();
                                     }
                                 });
                      }

                  });
    }
}
