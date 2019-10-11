package com.chuanglan.shanyan_sdk_test_demo.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.chuanglan.shanyan_sdk.listener.GetPhoneInfoListener;
import com.chuanglan.shanyan_sdk.listener.InitListener;
import com.chuanglan.shanyan_sdk.listener.OneKeyLoginListener;
import com.chuanglan.shanyan_sdk.listener.OpenLoginAuthListener;
import com.chuanglan.shanyan_sdk_test_demo.BuildConfig;
import com.chuanglan.shanyan_sdk_test_demo.R;
import com.chuanglan.shanyan_sdk_test_demo.utils.ConfigUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.text.SimpleDateFormat;
import java.util.List;

public class DebugModeActivity extends Activity implements View.OnClickListener {
    private ImageView iv_flash;
    private  CustomDialog dialog;
    private int TYPE;//失败0，成功1
    private long startTime, loginTime, authTime, aunthDeltTime;
    private SharedPreferences sp;
    private TextView check_message_init, check_message;
    private Button clean;
    private Button copy;
    private String message1, message2, message3, message4,message5;
    private long time1, time2;
    private String deviceMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_mode);
        initButton();
        TextView title = (TextView) findViewById(R.id.sdktitle);
        sp = this.getSharedPreferences("auth_result", MODE_PRIVATE);
    }

    public void initButton() {
        Button init = (Button) findViewById(R.id.sdk_init);
        init.setOnClickListener(this);
        Button pre = (Button) findViewById(R.id.sdk_pre);
        pre.setOnClickListener(this);
        Button auth = (Button) findViewById(R.id.sdk_auth);
        auth.setOnClickListener(this);
        Button disp = (Button) findViewById(R.id.sdk_disp);
        disp.setOnClickListener(this);
        check_message_init = findViewById(R.id.check_message_init);
        check_message = findViewById(R.id.check_message);
        copy = findViewById(R.id.copy);
        clean = findViewById(R.id.clean);
        clean.setOnClickListener(this);
        copy.setOnClickListener(this);
        clean.setVisibility(View.GONE);
        copy.setVisibility(View.GONE);
        deviceMessage = "手机机型：" + Build.BRAND + android.os.Build.MODEL + "\n" + "android系统版本：" + android.os.Build.VERSION.RELEASE;
    }

    @Override
    public void onClick(View view) {
        if (view == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.sdk_init:
                //init shangyan sdk function
                startNativeInit();
                break;
            case R.id.sdk_pre:
                startPreInit();
                break;
            case R.id.sdk_auth:
                startAuthorityPage();
                break;
            case R.id.sdk_disp:
                //置换手机号
                displacePhoneNumber();
                break;
            case R.id.copy:
                copyMessage();
                break;
            case R.id.clean:
                cleanData();
                break;
        }

    }

    private void cleanData() {
        check_message_init.setText("");
        check_message.setText("");
        message1 = "";
        message2 = "";
        message3 = "";
        message4 = "";

    }

    private void copyMessage() {
        //获取剪贴版
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//创建ClipData对象
//第一个参数只是一个标记，随便传入。
//第二个参数是要复制到剪贴版的内容
        ClipData clip = ClipData.newPlainText("simple text", check_message_init.getText().toString() + check_message.getText().toString());
//传入clipdata对象.
        clipboard.setPrimaryClip(clip);
        Toast.makeText(DebugModeActivity.this, "已复制", Toast.LENGTH_SHORT).show();
    }

    public void startNativeInit() {
        //闪验sdk本地数据初始化
        time1 = System.currentTimeMillis();
        OneKeyLoginManager.getInstance().init(getApplicationContext(), BuildConfig.APP_ID, BuildConfig.APP_KEY, new InitListener() {
            @Override
            public void getInitStatus(int code, String result) {
                long costTime = System.currentTimeMillis() - time1;
                // Toast.makeText(DebugModeActivity.this, "code=" + code + "result=" + result + "初始化耗时=" + costTime + "ms", Toast.LENGTH_LONG).show();
                message1 = "初始化步骤：" + "\n" + "开始时间:" + getCurrentTime() + "      耗时:" + costTime + "ms" + "\n" + "日志:" + "code=" + code + "result=" + result + "\n";
                check_message_init.setText(message1);
                check_message.setText(deviceMessage);
                copy.setVisibility(View.VISIBLE);
                clean.setVisibility(View.VISIBLE);
            }
        });
    }

    public void startPreInit() {
        //设置网络初始化和预取号监听
        time2 = System.currentTimeMillis();
        //权限判断：调用网络初始化和预取号前需要获取READ_PHONE_STATE权限，否则会返回失败状态码
        requestPrePermission(Permission.READ_PHONE_STATE);
    }

    public void requestPrePermission(String... permissions) {
        AndPermission.with(this)
                .permission(permissions)
                .onGranted(new Action() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onAction(List<String> permissions) {

                        //闪验SDK预取号
                        OneKeyLoginManager.getInstance().getPhoneInfo(new GetPhoneInfoListener() {
                            @Override
                            public void getPhoneInfoStatus(int code, String result) {
                                long costTime = System.currentTimeMillis() - time2;
                                //Toast.makeText(DebugModeActivity.this, "code=" + code + "result=" + result + "预取号耗时=" + costTime + "ms", Toast.LENGTH_LONG).show();
                                message2 = "预取号步骤：" + "\n" + "开始时间:" + getCurrentTime() + "      耗时:" + costTime + "ms" + "\n" + "日志:" + "code=" + code + "result=" + result + "\n";
                                check_message_init.setText(message1 + message2);
                                check_message.setText(deviceMessage);
                                copy.setVisibility(View.VISIBLE);
                                clean.setVisibility(View.VISIBLE);
                            }
                        });

                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        //闪验SDK预取号
                        OneKeyLoginManager.getInstance().getPhoneInfo(new GetPhoneInfoListener() {
                            @Override
                            public void getPhoneInfoStatus(int code, String result) {
                                long costTime = System.currentTimeMillis() - time2;
                                //Toast.makeText(DebugModeActivity.this, "code=" + code + "result=" + result + "预取号耗时=" + costTime + "ms", Toast.LENGTH_LONG).show();
                                message2 = "预取号步骤：" + "\n" + "开始时间:" + getCurrentTime() + "      耗时:" + costTime + "ms" + "\n" + "日志:" + "code=" + code + "result=" + result + "\n";
                                check_message_init.setText(message1 + message2);
                                check_message.setText(deviceMessage);
                                copy.setVisibility(View.VISIBLE);
                                clean.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                })
                .start();
    }

    public void startAuthorityPage() {
        authTime = System.currentTimeMillis();
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
                        //OneKeyLoginManager.getInstance().setAuthThemeConfig(ConfigUtils.getCJSConfig(getApplicationContext()));
                        OneKeyLoginManager.getInstance().setAuthThemeConfig(ConfigUtils.getDialogUiConfig(getApplicationContext()));
                        //开始拉取授权页
                        startTime = System.currentTimeMillis();
                        Log.e("fff", "LoginStart");
                        OneKeyLoginManager.getInstance().openLoginAuth(false, new OpenLoginAuthListener() {
                            @Override
                            public void getOpenLoginAuthStatus(int code, String result) {
                                long costTime = System.currentTimeMillis() - startTime;
                                // Toast.makeText(DebugModeActivity.this, "code=" + code + "result=" + result + "拉授权页耗时=" + costTime + "ms", Toast.LENGTH_LONG).show();
                                loginTime = System.currentTimeMillis();
                                if (dialog != null) {
                                    dialog.cancel();
                                }
                                message3 = "拉起授权页步骤：" + "\n" + "开始时间:" + getCurrentTime() + "      耗时:" + costTime + "ms" + "\n" + "日志:" + "code=" + code + "result=" + result + "\n";
                                check_message_init.setText(message1 + message2 + message3);
                                check_message.setText(deviceMessage);
                                copy.setVisibility(View.VISIBLE);
                                clean.setVisibility(View.VISIBLE);
                            }
                        }, new OneKeyLoginListener() {
                            @Override
                            public void getOneKeyLoginStatus(int code, String result) {
                                long costTime = System.currentTimeMillis() - loginTime;
                                //Toast.makeText(DebugModeActivity.this, "code=" + code + "result=" + result + "点击一键登录耗时=" + costTime + "ms", Toast.LENGTH_LONG).show();
                                dataProcessing(code, result);
                                if (dialog != null) {
                                    dialog.cancel();
                                }
                                message4 = "点击一键登录步骤：" + "\n" + "开始时间:" + getCurrentTime() + "      耗时:" + costTime + "ms" + "\n" + "日志:" + "code=" + code + "result=" + result + "\n";
                                check_message_init.setText(message1 + message2 + message3 + message4);
                                check_message.setText(deviceMessage);
                                copy.setVisibility(View.VISIBLE);
                                clean.setVisibility(View.VISIBLE);
                            }
                        });//false：设置不自动销毁授权页（默认为true：自动销毁）
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        //自定义运营商授权页界面
                        //OneKeyLoginManager.getInstance().setAuthThemeConfig(ConfigUtils.getCJSConfig(getApplicationContext()));
                        OneKeyLoginManager.getInstance().setAuthThemeConfig(ConfigUtils.getDialogUiConfig(getApplicationContext()));
                        //开始拉取授权页
                        startTime = System.currentTimeMillis();
                        Log.e("fff", "LoginStart");
                        OneKeyLoginManager.getInstance().openLoginAuth(false, new OpenLoginAuthListener() {
                            @Override
                            public void getOpenLoginAuthStatus(int code, String result) {
                                long costTime = System.currentTimeMillis() - startTime;
                                // Toast.makeText(DebugModeActivity.this, "code=" + code + "result=" + result + "拉授权页耗时=" + costTime + "ms", Toast.LENGTH_LONG).show();
                                loginTime = System.currentTimeMillis();
                                if (dialog != null) {
                                    dialog.cancel();
                                }
                                message3 = "拉起授权页步骤：" + "\n" + "开始时间:" + getCurrentTime() + "      耗时:" + costTime + "ms" + "\n" + "日志:" + "code=" + code + "result=" + result + "\n";
                                check_message_init.setText(message1 + message2 + message3);
                                check_message.setText(deviceMessage);
                                copy.setVisibility(View.VISIBLE);
                                clean.setVisibility(View.VISIBLE);
                            }
                        }, new OneKeyLoginListener() {
                            @Override
                            public void getOneKeyLoginStatus(int code, String result) {
                                long costTime = System.currentTimeMillis() - loginTime;
                                //Toast.makeText(DebugModeActivity.this, "code=" + code + "result=" + result + "点击一键登录耗时=" + costTime + "ms", Toast.LENGTH_LONG).show();
                                dataProcessing(code, result);
                                if (dialog != null) {
                                    dialog.cancel();
                                }
                                message4 = "点击一键登录步骤：" + "\n" + "开始时间:" + getCurrentTime() + "      耗时:" + costTime + "ms" + "\n" + "日志:" + "code=" + code + "result=" + result + "\n";
                                check_message_init.setText(message1 + message2 + message3 + message4);
                                check_message.setText(deviceMessage);
                                copy.setVisibility(View.VISIBLE);
                                clean.setVisibility(View.VISIBLE);
                            }
                        });//false：设置不自动销毁授权页（默认为true：自动销毁）
                    }
                })
                .start();
    }

    private void dataProcessing(int code, String result) {
        Log.e("VVV", "拉起授权页code=" + code + "result==" + result);
        if (code == 1000) {
            TYPE = 1;
        } else {
            TYPE = 0;
        }
        //销毁授权页
        OneKeyLoginManager.getInstance().finishAuthActivity();
        //通过edit()方法创建一个SharePreferences.Editor类的实例对象
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("code", code);
        editor.putString("result", result);
        editor.commit();
    }

    private void displacePhoneNumber() {
        aunthDeltTime = System.currentTimeMillis();
        int code = sp.getInt("code", 0);
        String result = sp.getString("result", "");
        if (null!=result) {
            startResultActivity(code, result);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("code", 0);
            editor.putString("result", "");
            editor.commit();
        } else {
            message5 = "开始时间:" + getCurrentTime() + "\n" + "token为空？请先拉起授权页并点击一键登录";
            check_message_init.setText(message5);
            check_message.setText(deviceMessage);
        }
    }

    private void startResultActivity(int code, String result) {
        Intent intent = new Intent(DebugModeActivity.this, LoginResultActivity.class);
        intent.putExtra("startTime", aunthDeltTime);
        intent.putExtra("loginType", TYPE);
        intent.putExtra("loginResult", result);
        intent.putExtra("loginCode", code);
        startActivity(intent);
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
        //OneKeyLoginManager.getInstance().removeAllListener();
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new java.util.Date());
    }
}
