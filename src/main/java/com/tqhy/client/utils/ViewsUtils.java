package com.tqhy.client.utils;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 界面相关工具类
 *
 * @author Yiheng
 * @create 2018/6/14
 * @since 1.0.0
 */
public class ViewsUtils {

    public static final String SHOW_VIEW_TAG = "show_view_tag";
    public static final String CLOSE_VIEW_TAG = "close_view_tag";
    private static Logger logger = LoggerFactory.getLogger(ViewsUtils.class);


    /**
     * 获取屏幕最大可用宽度
     *
     * @return 屏幕最大可用宽度
     */
    public static double getScreenWidth() {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        //return visualBounds.getWidth();
        return visualBounds.getMaxX();
    }

    /**
     * 获取窗口左上角x坐标最大值
     *
     * @param width 窗口宽度
     * @return 窗口左上角横坐标位置
     */
    public static double getMaxX(double width) {
        return getScreenWidth() - width;
    }

    /**
     * 获取屏幕最大可用高度
     *
     * @return 屏幕最大可用高度
     */
    public static double getScreenHeight() {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        //return visualBounds.getHeight();
        return visualBounds.getMaxY();
    }

    /**
     * 获取窗口左上角Y坐标最大值
     *
     * @param height
     * @return
     */
    public static double getMaxY(double height) {
        return getScreenHeight() - height;
    }


    /**
     * 按照与屏幕比率,设定窗口宽高
     *
     * @param stage       待设定宽高的窗口对象
     * @param widthRatio  窗口宽度与屏幕宽度比率
     * @param heightRatio 窗口高度与屏幕高度比率
     */
    public static void setStageScaleAccordingScreen(Stage stage, double widthRatio, double heightRatio) {
        double screenWidth = ViewsUtils.getScreenWidth();
        double screenHeight = ViewsUtils.getScreenHeight();
        double stageWidth = screenWidth * widthRatio;
        double stageHeight = screenHeight * heightRatio;
        stage.setWidth(stageWidth);
        stage.setHeight(stageHeight);
    }

}
