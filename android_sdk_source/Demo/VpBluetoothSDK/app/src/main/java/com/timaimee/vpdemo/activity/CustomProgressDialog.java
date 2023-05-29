package com.timaimee.vpdemo.activity;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.timaimee.vpdemo.R;


public class CustomProgressDialog extends Dialog {

    public CustomProgressDialog(Context context) {
        super(context, R.style.loading_dialog);
        this.setContentView(R.layout.dialog_connect_progress);
    }

    public void show(String str) {
        if (this.isShowing()) {
            this.dismiss();
        }
        this.show();
    }

    public void showNoTips() {
        if (this.isShowing()) {
            this.dismiss();
        }
        this.setCancelable(false);
        this.show();
    }

    public void disMissDialog() {
        if (this.isShowing()) {
            this.dismiss();
        }
    }

}
