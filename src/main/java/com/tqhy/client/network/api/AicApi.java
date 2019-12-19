package com.tqhy.client.network.api;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

/**
 * @author Yiheng
 * @create 2018/6/13
 * @since 1.0.0
 */
public interface AicApi {


    /**
     * 文件下载
     *
     * @param imgUrlString
     * @return
     */
    @Streaming
    @Headers("Authorization: admin")
    @GET("util/getDownload")
    Observable<Response<ResponseBody>> download(@Query("imgUrlString") String imgUrlString);

    /**
     * 单个参数,单个文件上传
     *
     * @param param 参数名
     * @return
     */
    @Multipart
    @POST("upload/case/single")
    Observable<ResponseBody> uploadFile(@Part("param") RequestBody param, @Part() MultipartBody.Part filePart);

    /**
     * 多个参数,单个文件上传
     *
     * @param params
     * @return
     */
    @Multipart
    @POST("aia/upload")
    Observable<ResponseBody> uploadFile(@PartMap Map<String, RequestBody> params, @Part() MultipartBody.Part filePart);

    /**
     * 单个参数,多文件上传
     *
     * @param param
     * @return
     */
    @Multipart
    @POST("upload/case/multi")
    Observable<ResponseBody> uploadFiles(@Part("param") RequestBody param, @Part List<MultipartBody.Part> fileParts);

    /**
     * 多个参数,多文件上传
     *
     * @param params
     * @return
     */
    @Multipart
    @POST("upload/")
    Observable<ResponseBody> uploadFiles(@PartMap Map<String, RequestBody> params, @Part MultipartBody.Part fileParts);


    /**
     * 多个参数,多文件上传
     *
     * @param params
     * @return
     */
    @Multipart
    @POST("uploadTest/")
    Observable<ResponseBody> uploadTestFiles(@PartMap Map<String, RequestBody> params,
                                             @Part MultipartBody.Part fileParts);


    /**
     * 获取所有模型名称与id
     *
     * @return
     */
    @GET("modellist")
    Observable<ResponseBody> getAllModels();

    /**
     * 获取病例信息
     *
     * @param caseId
     * @return
     */
    @POST("searchCase")
    Observable<ResponseBody> searchCase(@Query("caseNo") String caseId);



    /**
     * 与后台保持心跳请求
     *
     * @param token
     * @return
     */
    @POST("/monitoraia")
    Observable<ResponseBody> heartbeat(@Query("token") String token);

}
