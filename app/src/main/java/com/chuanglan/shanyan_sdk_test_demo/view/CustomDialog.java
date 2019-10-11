package com.chuanglan.shanyan_sdk_test_demo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;

import com.chuanglan.shanyan_sdk_test_demo.R;

/**
 * Created by ChuangLan on 2019/9/3.
 */

public class CustomDialog extends Dialog {

    public CustomDialog(Context context) {
        super(context, R.style.LoadDialog);
        initView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (CustomDialog.this.isShowing()) {
                CustomDialog.this.dismiss();
            }
        }
        return true;
    }

    private void initView() {
        setContentView(R.layout.dialog_layout);
        setCanceledOnTouchOutside(true);
        setCancelable(false);
    }
}
