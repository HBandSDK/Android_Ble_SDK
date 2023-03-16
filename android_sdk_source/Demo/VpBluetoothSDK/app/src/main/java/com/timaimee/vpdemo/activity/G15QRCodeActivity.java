package com.timaimee.vpdemo.activity;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IG15QRCodeSendListener;
import com.veepoo.protocol.model.G15QRCode;
import com.veepoo.protocol.util.VPLogger;

/**
 * Author: YWX
 * Date: 2022/2/15 10:06
 * Description:
 */
public class G15QRCodeActivity extends AppCompatActivity {

    public static final String TAG = G15QRCodeActivity.class.getSimpleName();

    EditText etContent;
    EditText etName;
    EditText etNumber;
    EditText etGroup;
    RadioGroup rgType;

    private int checkType = 0x00;

    @Override
    protected void onCreate(@Nullable android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g15_qrcode);
        {
            etContent = findViewById(R.id.etContent);
            etName = findViewById(R.id.etName);
            etNumber = findViewById(R.id.etNumber);
            etGroup = findViewById(R.id.etGroup);
            rgType = findViewById(R.id.rgQRCode);
        }
        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbHz) {
                    checkType = 0x00;
                } else if (checkedId == R.id.rbSk) {
                    checkType = 0x01;
                }
            }
        });
    }

    public void sendQRCode(View view) {
        G15QRCode qrCode = getG15QRCode();
        if (qrCode == null) return;
        try {
            VPOperateManager.getMangerInstance(this).sendG15QRCode(new IBleWriteResponse() {
                @Override
                public void onResponse(int code) {
                    Logger.t(TAG).e("sendG15QRCode-------------->" + code);
                }
            }, qrCode, new IG15QRCodeSendListener() {
                @Override
                public void onG15QRCodeSendSuccess() {
                    Toast.makeText(G15QRCodeActivity.this, "二维码发送成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onG15QRCodeSendFailed() {
                    Toast.makeText(G15QRCodeActivity.this, "二维码发送失败", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private G15QRCode getG15QRCode() {
        String qrContent = etContent.getText().toString().trim();
        String qrName = etName.getText().toString().trim();
        String qrNumber = etNumber.getText().toString().trim();
        String qrGroup = etGroup.getText().toString().trim();
        try {
            G15QRCode qrCode = new G15QRCode(checkType, qrContent, qrName, qrGroup, qrNumber);
            VPLogger.e("二维码：" + qrCode.toString());
            return qrCode;
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
