package com.chuanglan.shanyan_sdk_test_demo.http;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthenticationRequestApi {

    /**
     * 移动一键登录
     * @param params
     * @return
     */
    @POST("/open/flashsdk/mobile-query-m")
    @FormUrlEncoded
    Call<ResponseBody> getMobile01(@FieldMap Map<String, String> params);

    /**
     * 联通一键登录
     * @param params
     * @return
     */
    @POST("/open/flashsdk/mobile-query-u")
    @FormUrlEncoded
    Call<ResponseBody> getMobile02(@FieldMap Map<String, String> params);



    /**
     * 电信一键登录
     * @param params
     * @return
     */
    @POST("/open/flashsdk/mobile-query-t")
    @FormUrlEncoded
    Call<ResponseBody> getMobile03(@FieldMap Map<String, String> params);


    /**
     * 移动本机校验
     *
     * @param params
     * @return
     */
    @POST("/open/flashsdk/mobile-validate-m")
    @FormUrlEncoded
    Call<ResponseBody> authenticationCMCC(@FieldMap Map<String, String> params);


    /**
     * 联通本机校验
     *
     * @param params
     * @return
     */
    @POST("/open/flashsdk/mobile-validate-u")
    @FormUrlEncoded
    Call<ResponseBody> authenticationCUCC(@FieldMap Map<String, String> params);


    /**
     * 电信本机校验
     *
     * @param params
     * @return
     */
    @POST("/open/flashsdk/mobile-validate-t")
    @FormUrlEncoded
    Call<ResponseBody> authenticationCTCC(@FieldMap Map<String, String> params);


}
