package com.chuanglan.shanyan_sdk_test_demo.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.chuanglan.shanyan_sdk.listener.GetPhoneInfoListener;
import com.chuanglan.shanyan_sdk.listener.InitListener;
import com.chuanglan.shanyan_sdk_test_demo.BuildConfig;
import com.chuanglan.shanyan_sdk_test_demo.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

public class FristActivity extends Activity implements View.OnClickListener {

    Button normal, chenjinshi, debug, mobile;
    private CheckBox checkbox_screen;
    private boolean isScreenLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frist);
        initViews();
    }

    private void initViews() {
        normal = findViewById(R.id.normal);
        chenjinshi = findViewById(R.id.chenjinshi);
        debug = findViewById(R.id.debug);
        checkbox_screen = findViewById(R.id.checkbox_screen);
        mobile = findViewById(R.id.mobile);
        normal.setOnClickListener(this);
        chenjinshi.setOnClickListener(this);
        debug.setOnClickListener(this);
        mobile.setOnClickListener(this);

        if (isScreenLandscape) {
            checkbox_screen.setChecked(true);
        } else {
            checkbox_screen.setChecked(false);
        }
        checkbox_screen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isScreenLandscape = true;
                } else {
                    isScreenLandscape = false;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.normal:
                startNormalModel("normal");
                requestPermission(Permission.READ_PHONE_STATE, "normal", isScreenLandscape);
                break;
            case R.id.chenjinshi:
                startNormalModel("chenjinshi");
                requestPermission(Permission.READ_PHONE_STATE, "chenjinshi", isScreenLandscape);
                break;
            case R.id.debug:
                Intent intent = new Intent(FristActivity.this, DebugModeActivity.class);
                startActivity(intent);
                break;

            case R.id.mobile:
                startAuthentication();
                break;
        }
    }

    private void startAuthentication() {
        OneKeyLoginManager.getInstance().init(getApplicationContext(), BuildConfig.APP_ID, BuildConfig.APP_KEY, new InitListener() {
            @Override
            public void getInitStatus(int code, String result) {
                Log.e("VVV", "初始化code=" + code + "result==" + result);
            }
        });
        //跳转到本机号认证页面
        Intent intent = new Intent(FristActivity.this, AuthenticationActivity.class);
        startActivity(intent);
    }

    private void startNormalModel(final String type) {
        OneKeyLoginManager.getInstance().init(getApplicationContext(), BuildConfig.APP_ID, BuildConfig.APP_KEY, new InitListener() {
            @Override
            public void getInitStatus(int code, String result) {
                Log.e("VVV", "初始化code=" + code + "result==" + result);
                if (code == 1022) {

                }
            }
        });
    }

    private void requestPermission(String permissions, final String type, final boolean isScreenLandscape) {
        AndPermission.with(this)
                .permission(permissions)
                .onGranted(new Action() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onAction(List<String> permissions) {
                        //闪验SDK预取号（可选，能加速拉起授权页）
                        OneKeyLoginManager.getInstance().getPhoneInfo(new GetPhoneInfoListener() {
                            @Override
                            public void getPhoneInfoStatus(int code, String result) {
                                Log.e("VVV", "预取号code=" + code + "result=" + result);
                            }
                        });

                        Intent intent = new Intent(FristActivity.this, StartActivity.class);
                        intent.putExtra("type", type);
                        intent.putExtra("isScreenLandscape", isScreenLandscape);
                        startActivity(intent);
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        //闪验SDK预取号（可选，能加速拉起授权页）
                        OneKeyLoginManager.getInstance().getPhoneInfo(new GetPhoneInfoListener() {
                            @Override
                            public void getPhoneInfoStatus(int code, String result) {
                                Log.e("VVV", "预取号code=" + code + "result=" + result);
                            }
                        });

                        Intent intent = new Intent(FristActivity.this, StartActivity.class);
                        intent.putExtra("type", type);
                        intent.putExtra("isScreenLandscape", isScreenLandscape);
                        startActivity(intent);
                    }
                })
                .start();
    }
}
