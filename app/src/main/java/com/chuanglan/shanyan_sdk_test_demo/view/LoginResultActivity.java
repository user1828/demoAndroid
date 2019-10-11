package com.chuanglan.shanyan_sdk_test_demo.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuanglan.shanyan_sdk_test_demo.BuildConfig;

import com.chuanglan.shanyan_sdk_test_demo.R;
import com.chuanglan.shanyan_sdk_test_demo.http.RequestExample;
import com.chuanglan.shanyan_sdk_test_demo.utils.DES;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginResultActivity extends Activity implements View.OnClickListener {
    private Button reTry1, reTry2;
    private ScrollView successView;
    private ScrollView fialureView;
    private TextView authenticationTime;
    private long time;
    private int TYPE;
    private RequestExample example = new RequestExample();
    private String result;
    private int code;
    private TextView errorText;
    private CustomDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_result);
        initViews();
        initEvent();
        setListener();
    }

    private void initEvent() {
        Intent intent = getIntent();
        time = intent.getLongExtra("startTime", 0);
        TYPE = intent.getIntExtra("loginType", 0);
        code = intent.getIntExtra("loginCode", 0);
        result = intent.getStringExtra("loginResult");
        if (TYPE == 1) {
            if (dialog == null) {
                dialog = new CustomDialog(this);
            }
            dialog.show();
            try {
                JSONObject jsonObject = new JSONObject(result);
                //应用的appid
                String appId = jsonObject.optString("appId");
                //token
                String accessToken = jsonObject.optString("accessToken");
                //运营商
                String telecom = jsonObject.optString("telecom");
                //网络时间
                String timestamp = jsonObject.optString("timestamp");
                //随机数
                String randoms = jsonObject.optString("randoms");
                //版本号
                String version = jsonObject.optString("version");
                //签名
                String sign = jsonObject.optString("sign");
                //device
                String device = jsonObject.optString("device");
                getPhone(appId, accessToken, telecom, timestamp, randoms, sign, version, device);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (TYPE == 0) {
            errorText.setText("状态码：" + code + "\n错误日志：" + result);
            successView.setVisibility(View.GONE);
            fialureView.setVisibility(View.VISIBLE);
        }

    }

    private void setListener() {
        reTry1.setOnClickListener(this);
        reTry2.setOnClickListener(this);
    }

    private void initViews() {
        if (dialog == null) {
            dialog = new CustomDialog(this);
        }
        reTry1 = findViewById(R.id.authentication_restart01);
        reTry2 = findViewById(R.id.authentication_restart02);
        errorText = findViewById(R.id.authentication_errorText);
        successView = findViewById(R.id.authentication_scrollview);
        fialureView = findViewById(R.id.authenticationresult_failured);
        authenticationTime = findViewById(R.id.authentication_time);
    }

    @Override
    public void onClick(View v) {
        if (null != StartActivity.startActivityWeakReference && null != StartActivity.startActivityWeakReference.get()) {
            StartActivity.startActivityWeakReference.get().finish();
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (null != StartActivity.startActivityWeakReference && null != StartActivity.startActivityWeakReference.get()) {
                StartActivity.startActivityWeakReference.get().finish();
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 开发者调用自己的后台接口
     *
     * @param appId
     * @param accessToken
     * @param telecom
     * @param timestamp
     * @param randoms
     * @param sign
     * @param version
     */
    private void getPhone(String appId, String accessToken, String telecom, String timestamp, String randoms, String sign, String version, String device) {
        Call<ResponseBody> call = example.getPhoneCode(appId, accessToken, telecom, timestamp, randoms, sign, version, device);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (dialog != null) {
                        dialog.cancel();
                    }
                    String response_data = new String(response.body().bytes());
                    JSONObject json = new JSONObject(response_data);
                    int code = json.optInt("code");
                    if (code == 200000) {
                        JSONObject data = json.getJSONObject("data");
                        String mobileName = data.optString("mobileName");
                        //获取到的手机号
                        String phone = DES.decryptDES(mobileName, BuildConfig.APP_KEY);
                        Toast.makeText(LoginResultActivity.this, phone, Toast.LENGTH_SHORT).show();
                        float timespan = (System.currentTimeMillis() - time) / 1000f;
                        authenticationTime.setText(Float.toString(timespan));
                        successView.setVisibility(View.VISIBLE);
                        fialureView.setVisibility(View.GONE);
                    } else {
                        String msg = json.optString("message");
                        errorText.setText("状态码：" + code + "\n错误日志：" + msg);
                        successView.setVisibility(View.GONE);
                        fialureView.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (dialog != null) {
                    dialog.cancel();
                }
                errorText.setText("错误日志：" + t.getMessage());
                successView.setVisibility(View.GONE);
                fialureView.setVisibility(View.VISIBLE);
            }
        });
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
