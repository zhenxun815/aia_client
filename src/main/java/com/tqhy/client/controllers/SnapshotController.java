package com.tqhy.client.controllers;

import com.tqhy.client.models.msg.server.ClientMsg;
import com.tqhy.client.network.Network;
import com.tqhy.client.utils.FileUtils;
import com.tqhy.client.utils.GsonUtils;
import com.tqhy.client.utils.ImgUtils;
import com.tqhy.client.utils.NetworkUtils;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yiheng
 * @create 11/26/2019
 * @since 1.0.0
 */
@Controller
public class SnapshotController extends BaseWebviewController {

    static Logger logger = LoggerFactory.getLogger(SnapshotController.class);
    String imgStorePath = FileUtils.getAppPath() + "/capture.jpg";
    @FXML
    private WebView webView;

    @FXML
    void initialize() {
        super.initialize(webView);

        if (ImgUtils.captureScreen(imgStorePath)) {
            uploadCaptureImg(imgStorePath);
        }

    }

    private void uploadCaptureImg(String captureImgPath) {
        Map<String, String> requestParamMap = new HashMap<>();
        String physicalAddress = NetworkUtils.getPhysicalAddress();
        requestParamMap.put("clientId", physicalAddress);
        Map<String, RequestBody> requestMap = NetworkUtils.createRequestParamMap(requestParamMap);
        MultipartBody.Part filePart = NetworkUtils.createFilePart("file", captureImgPath);
        Network.getAicApi()
               .uploadFile(requestMap, filePart)
               .observeOn(Schedulers.io())
               .subscribeOn(Schedulers.trampoline())
               .blockingSubscribe(new UploadObserver(captureImgPath));
    }

    @GetMapping(value = "/viewImg")
    public void viewImg(HttpServletResponse res) {
        try {
            res.reset();
            OutputStream out = res.getOutputStream();
            res.setHeader("Content-Type", "image/jpeg");
            //logger.info("img rel path is: " + path);
            try {
                File file = new File(imgStorePath);
                if (file != null) {
                    FileInputStream fis = new FileInputStream(file);
                    @SuppressWarnings("resource")
                    BufferedInputStream buff = new BufferedInputStream(fis);
                    byte[] b = new byte[1024];
                    long k = 0;
                    // 开始循环下载
                    while (k < file.length()) {
                        int j = buff.read(b, 0, 1024);
                        k += j;
                        // 将b中的数据写到客户端的内存
                        out.write(b, 0, j);
                    }
                }
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            logger.error("加载图片失败！");
            e.printStackTrace();
        }
    }

    private class UploadObserver implements Observer<ResponseBody> {
        Disposable d;
        String uploadImgPath;

        public UploadObserver(String uploadImgPath) {
            this.uploadImgPath = uploadImgPath;
        }

        @Override
        public void onSubscribe(Disposable d) {
            logger.info("on subscribe Disposable: " + d);
            this.d = d;
        }

        @Override
        public void onNext(ResponseBody responseBody) {
            ClientMsg clientMsg = GsonUtils.parseResponseToObj(responseBody);
            Integer flag = clientMsg.getFlag();
            List<String> msg = clientMsg.getMsg();
            String imgUrl = msg.get(0);
            logger.info("upload onNext flag is {}, msg is {}", flag, imgUrl);
            String clientId = NetworkUtils.getPhysicalAddress();
            String url = Network.SERVER_BASE_URL + "aia/init.html?clientId=" + clientId + "&imgUrl=" + imgUrl;
            logger.info("load page {}", url);
            loadPage(webView, url);
        }

        @Override
        public void onError(Throwable e) {
            logger.error("upload " + uploadImgPath + " failed", e);
        }

        @Override
        public void onComplete() {

        }
    }
}
