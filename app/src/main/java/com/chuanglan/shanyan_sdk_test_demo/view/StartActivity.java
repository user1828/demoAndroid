package com.chuanglan.shanyan_sdk_test_demo.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.chuanglan.shanyan_sdk.listener.OnClickPrivacyListener;
import com.chuanglan.shanyan_sdk.listener.OneKeyLoginListener;
import com.chuanglan.shanyan_sdk.listener.OpenLoginAuthListener;
import com.chuanglan.shanyan_sdk_test_demo.R;
import com.chuanglan.shanyan_sdk_test_demo.utils.AbScreenUtils;
import com.chuanglan.shanyan_sdk_test_demo.utils.ConfigUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.lang.ref.WeakReference;
import java.util.List;

public class StartActivity extends Activity {
    private ImageView iv_flash;
    private CustomDialog dialog;
    private int TYPE;//失败0，成功1
    private boolean isScreenLandscape;
    private long startTime;
    private String type;
    public static WeakReference<Activity> startActivityWeakReference = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_start_land);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_start);

        }
        initViews();
        startActivityWeakReference = new WeakReference<Activity>(this);
    }

    private void initViews() {
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        isScreenLandscape = intent.getBooleanExtra("isScreenLandscape", false);
        if (isScreenLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        iv_flash = findViewById(R.id.iv_flash);
        int width = AbScreenUtils.getScreenWidth(getApplicationContext());
        int height = 1010 * width / 1242;
        AbScreenUtils.setViewWH(iv_flash, width, height);
        Glide.with(this).load(R.mipmap.gif).into(iv_flash);
        OneKeyLoginManager.getInstance().setOnClickPrivacyListener(new OnClickPrivacyListener() {
            @Override
            public void getOnClickPrivacyStatus(int code, String result, String operator) {
                AbScreenUtils.showToast(getApplicationContext(), "result=" + result + "operator=" + operator);
            }
        });

    }

    public void doClick(View view) {
        Log.e("VVV", "doClick");
        if (dialog == null) {
            dialog = new CustomDialog(this);
        }
        dialog.show();
        //权限判断：调用拉起授权页前需要获取READ_PHONE_STATE权限，否则会返回失败状态码
        requestPermission(Permission.READ_PHONE_STATE);
    }


    private void requestPermission(String... permissions) {
        AndPermission.with(this)
                .permission(permissions)
                .onGranted(new Action() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onAction(List<String> permissions) {
                        //自定义运营商授权页界面
                        Log.e("VVV", "requestPermission");
                        if (type.equals("normal")) {
                            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                OneKeyLoginManager.getInstance().setAuthThemeConfig(ConfigUtils.getLandscapeUiConfig(getApplicationContext()));
                            } else {
                                OneKeyLoginManager.getInstance().setAuthThemeConfig(ConfigUtils.getDialogUiConfig(getApplicationContext()));
                            }
                        } else {
                            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                OneKeyLoginManager.getInstance().setAuthThemeConfig(ConfigUtils.getCJSLandscapeUiConfig(getApplicationContext()));
                            } else {
                                OneKeyLoginManager.getInstance().setAuthThemeConfig(ConfigUtils.getCJSConfig(getApplicationContext()));
                            }
                        }
                        //开始拉取授权页
                        OneKeyLoginManager.getInstance().openLoginAuth(false, new OpenLoginAuthListener() {
                            @Override
                            public void getOpenLoginAuthStatus(int code, String result) {
                                if (dialog != null) {
                                    dialog.cancel();
                                }
                                if (code != 1000) {
                                    AbScreenUtils.showToast(getApplicationContext(), result);
                                }
                                Log.e("VVV", "getAuthCode=" + code + "result=" + result);
                            }
                        }, new OneKeyLoginListener() {
                            @Override
                            public void getOneKeyLoginStatus(int code, String result) {
                                if (dialog != null) {
                                    dialog.cancel();
                                }
                                dataProcessing(code, result);
                            }
                        });
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        if (type.equals("normal")) {
                            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                OneKeyLoginManager.getInstance().setAuthThemeConfig(ConfigUtils.getLandscapeUiConfig(getApplicationContext()));
                            } else {
                                OneKeyLoginManager.getInstance().setAuthThemeConfig(ConfigUtils.getDialogUiConfig(getApplicationContext()));
                            }
                        } else {
                            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                OneKeyLoginManager.getInstance().setAuthThemeConfig(ConfigUtils.getCJSLandscapeUiConfig(getApplicationContext()));
                            } else {
                                OneKeyLoginManager.getInstance().setAuthThemeConfig(ConfigUtils.getCJSConfig(getApplicationContext()));
                            }
                        }
                        Log.e("VVV", "onDenied");
                        //开始拉取授权页
                        OneKeyLoginManager.getInstance().openLoginAuth(false, new OpenLoginAuthListener() {
                            @Override
                            public void getOpenLoginAuthStatus(int code, String result) {
                                if (dialog != null) {
                                    dialog.cancel();
                                }
                                if (code != 1000) {
                                    AbScreenUtils.showToast(getApplicationContext(), result);
                                }
                                Log.e("VVV", "getAuthCode=" + code + "result=" + result);
                            }
                        }, new OneKeyLoginListener() {
                            @Override
                            public void getOneKeyLoginStatus(int code, String result) {
                                if (dialog != null) {
                                    dialog.cancel();
                                }
                                dataProcessing(code, result);
                            }
                        });
                    }
                })
                .start();
    }


    private void dataProcessing(int code, String result) {
        Log.e("VVV", "点击一键登录code=" + code + "result==" + result);
        if (code == 1000) {
            startTime = System.currentTimeMillis();
            TYPE = 1;
        } else {
            TYPE = 0;
        }
        startResultActivity(code, result);
    }

    private void startResultActivity(int code, String result) {
        Intent intent = new Intent(StartActivity.this, LoginResultActivity.class);
        intent.putExtra("startTime", startTime);
        intent.putExtra("loginType", TYPE);
        intent.putExtra("loginResult", result);
        intent.putExtra("loginCode", code);
        startActivity(intent);
        //销毁授权页
        OneKeyLoginManager.getInstance().finishAuthActivity();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("fff", "onConfigurationChanged");
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_start_land);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_start);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
        Log.e("tttt","onDestroy");
        OneKeyLoginManager.getInstance().unregisterOnClickPrivacyListener();
        //OneKeyLoginManager.getInstance().removeAllListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
