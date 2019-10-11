package com.chuanglan.shanyan_sdk_test_demo.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chuanglan.shanyan_sdk_test_demo.R;
import com.chuanglan.shanyan_sdk_test_demo.http.RequestExample;
import com.chuanglan.shanyan_sdk_test_demo.utils.AbScreenUtils;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationResultActivity extends Activity implements View.OnClickListener {
    private Button reTry1, reTry2;
    private ScrollView successView;
    private ScrollView fialureView;
    private TextView authenticationTime;
    private String result;
    private RequestExample example = new RequestExample();
    private TextView errorText;
    private CustomDialog dialog;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AbScreenUtils.setWindowDecor(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication_result);
        initViews();
        setListener();
        initEvent();
    }

    private void initEvent() {
        if (dialog == null) {
            dialog = new CustomDialog(this);
        }
        dialog.show();
        Intent intent = getIntent();
        int code = intent.getIntExtra("code",-1);
        result = intent.getStringExtra("result");
        startTime = intent.getLongExtra("startTime",-1);
        if (code == 2000) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String appId = jsonObject.optString("appId");
                        String accessCode = jsonObject.optString("accessCode");
                        String mobile = jsonObject.optString("mobile");
                        String telecom = jsonObject.optString("telecom");
                        String timestamp = jsonObject.optString("timestamp");
                        String randoms = jsonObject.optString("randoms");
                        String sign = jsonObject.optString("sign");
                        String version = jsonObject.optString("version");
                        String device = jsonObject.optString("device");
                        String tradeNo = jsonObject.optString("tradeNo");
                        authenticationPhone(appId, accessCode, mobile, telecom, timestamp, randoms, version, sign, device);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else {
            displayResult(code,false,result);
        }
    }

    private void setListener() {
        reTry1.setOnClickListener(this);
        reTry2.setOnClickListener(this);
    }

    private void initViews() {
        reTry1 = (Button) findViewById(R.id.authentication_restart01);
        reTry2 = (Button) findViewById(R.id.authentication_restart02);
        successView = (ScrollView) findViewById(R.id.authentication_scrollview);
        fialureView =findViewById(R.id.authenticationresult_failured);
        errorText = (TextView) findViewById(R.id.authentication_errorText);
        authenticationTime = (TextView) findViewById(R.id.authentication_time);
    }

    @Override
    public void onClick(View v) {
        finish();
    }


    private void authenticationPhone(String appId, String accessCode, String mobile, String telecom,
                                     String timestamp, String randoms, String version, String sign, String device) {

        Call<ResponseBody> call = example.phoneCodeVerify(appId, accessCode, mobile, telecom, timestamp,
                randoms, version, sign, device);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String response_data = new String(response.body().bytes());
                    Log.e("sdffdsfsdadfsadfas", "dfsdfsdsfdfs------>" + response_data);
                    JSONObject json = new JSONObject(response_data);
                    int code = json.optInt("code");
                    if (code == 200000) {
                        String data = json.optString("data");
                        JSONObject jsonObject = new JSONObject(data);
                        if(jsonObject.getString("isVerify").equals("1")){
                            displayResult(code,true,"");
                        }else if (jsonObject.getString("isVerify").equals("0")){
                            displayResult(code,false,"手机号码与本机号不一致");
                        }else {
                            displayResult(code,false,data);
                        }

                    } else {
                       displayResult(code,false,json.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    displayResult(2007,false,e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                displayResult(2007,false,t.getMessage());
            }
        });
    }

    private void displayResult(int code,boolean isSuccess,String msg){
        if (dialog != null) {
            dialog.cancel();
        }
        if(isSuccess){
            float timespan = (System.currentTimeMillis() - startTime) / 1000f;
            Log.e("TTT", "time=" + startTime + "timespan+" + System.currentTimeMillis());
            authenticationTime.setText(Float.toString(timespan));
            successView.setVisibility(View.VISIBLE);
            fialureView.setVisibility(View.GONE);
        }else {
            successView.setVisibility(View.GONE);
            fialureView.setVisibility(View.VISIBLE);
            errorText.setText("状态码：" + code + "\n错误日志：" + msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
