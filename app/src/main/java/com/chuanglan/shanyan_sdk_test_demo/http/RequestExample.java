package com.chuanglan.shanyan_sdk_test_demo.http;




import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class RequestExample {

    public AuthenticationRequestApi getService() {
        //创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.HTTP_BASE_URL)
                .build();

        //创建网络请求接口实例
        AuthenticationRequestApi service = retrofit.create(AuthenticationRequestApi.class);
        return service;
    }

    /**
     * 获取手机号
     *
     * @param appId
     * @param accessToken
     * @param telecom
     * @param timestamp
     * @param randoms
     * @param sign
     * @return
     */
    public Call<ResponseBody> getPhoneCode(String appId, String accessToken, String telecom, String timestamp, String randoms, String sign, String version, String device) {
        Call<ResponseBody> service = null;
        Map<String, String> params = NetParams.getInstance().oneKeyLogin(appId, accessToken, telecom, timestamp, randoms, sign, version, device);
        if (telecom.equals("CMCC")) {//移动
            service = getService().getMobile01(params);
        } else if (telecom.equals("CUCC")) {//联通
            service = getService().getMobile02(params);
        } else if (telecom.equals("CTCC")) {//电信
            service = getService().getMobile03(params);
        }
        return service;
    }

    /**
     * 本机校验
     *
     * @param appId
     * @param accessCode
     * @param mobile
     * @param telecom
     * @param timestamp
     * @param randoms
     * @param sign
     * @return
     */
    public Call<ResponseBody> phoneCodeVerify(String appId, String accessCode, String mobile, String telecom,
                                              String timestamp, String randoms,String version, String sign, String device) {
        Call<ResponseBody> service = null;
        Map<String, String> params = NetParams.getInstance().mobileVerify(appId, accessCode, mobile, telecom, timestamp,
                randoms, version, sign, device);
        if (telecom.equals("CMCC")) {//移动
            service = getService().authenticationCMCC(params);
        } else if (telecom.equals("CUCC")) {//联通
            service = getService().authenticationCUCC(params);
        } else if (telecom.equals("CTCC")) {//电信
            service = getService().authenticationCTCC(params);
        }
        return service;
    }
}
