package com.chuanglan.shanyan_sdk_test_demo.http;

import java.util.HashMap;
import java.util.Map;

public class NetParams {
    public static NetParams instance = null;

    public static NetParams getInstance() {
        if (instance == null) {
            synchronized (NetParams.class) {
                if (instance == null) {
                    instance = new NetParams();
                }

            }
        }
        return instance;
    }


    /**
     * 免密登录获取手机号
     *
     * @param appId
     * @param accessToken
     * @param randoms
     * @param timestamp
     * @param sign
     * @return
     */
    public Map<String, String> oneKeyLogin(String appId, String accessToken, String telecom, String timestamp, String randoms, String sign, String version, String device) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        params.put("accessToken", accessToken);
        params.put("randoms", randoms);
        params.put("timestamp", timestamp);
        params.put("sign", sign);
        params.put("telecom", telecom);
        params.put("version", version);
        params.put("device", device);
        return params;
    }

    /**
     * 本机校验
     *
     * @param appId
     * @param randoms
     * @param timestamp
     * @param accessCode
     * @param mobile
     * @param sign
     * @return
     */
    public Map<String, String> mobileVerify(String appId, String accessCode, String mobile, String telecom,
                                            String timestamp, String randoms, String version, String sign, String device) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        params.put("randoms", randoms);
        params.put("timestamp", timestamp);
        params.put("accessCode", accessCode);
        params.put("mobile", mobile);
        params.put("sign", sign);
        params.put("telecom", telecom);
        params.put("version", version);
        params.put("device", device);
        return params;
    }
}
