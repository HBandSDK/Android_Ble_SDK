package com.timaimee.vpdemo.activity;

import static com.veepoo.protocol.model.enums.EFunctionStatus.SUPPORT;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.inuker.bluetooth.library.Code;
import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.listener.data.IDeviceFuctionDataListener;
import com.veepoo.protocol.listener.data.ISocialMsgDataListener;
import com.veepoo.protocol.model.datas.DeviceFunctionPackage1;
import com.veepoo.protocol.model.datas.DeviceFunctionPackage2;
import com.veepoo.protocol.model.datas.DeviceFunctionPackage3;
import com.veepoo.protocol.model.datas.DeviceFunctionPackage4;
import com.veepoo.protocol.model.datas.DeviceFunctionPackage5;
import com.veepoo.protocol.model.datas.FunctionDeviceSupportData;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.datas.PwdData;
import com.veepoo.protocol.listener.data.IPwdDataListener;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.settings.CustomSettingData;

public class PwdConfirmActivity extends AppCompatActivity {

    private static final String TAG = "-密码校验-";

    private EditText etPassword;
    private ScrollView svInfo;
    private Button btnConfirm, btn2Function;
    private RadioGroup rgConnectConfirm;
    private TextView tvPwdInfo, tvDeviceInfo;
    private int deviceNumber = 0;
    private String deviceVersion = "";
    private String deviceTestVersion = "";

    private StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pwd_confirm);
        isOadModel = getIntent().getBooleanExtra("isoadmodel", false);
        deviceaddress = getIntent().getStringExtra("deviceaddress");

        VPOperateManager.getInstance().init(this);
        etPassword = findViewById(R.id.et_password);
        btnConfirm = findViewById(R.id.btn_confirm);
        btn2Function = findViewById(R.id.btn2Function);
        tvPwdInfo = findViewById(R.id.tv_pwd_info);
        svInfo = findViewById(R.id.svInfo);
        tvDeviceInfo = findViewById(R.id.tvDeviceInfo);
        rgConnectConfirm = findViewById(R.id.rgConnectConfirm);
        VPOperateManager.getInstance().setDeviceShowConfirm(true);//默认弹
        rgConnectConfirm.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rbNeedCC) {
                    VPOperateManager.getInstance().setDeviceShowConfirm(true);
                } else if (i == R.id.rbUnneedCC) {
                    VPOperateManager.getInstance().setDeviceShowConfirm(false);
                }
            }
        });

        btnConfirm.setEnabled(true);
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnConfirm.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPassword();
            }
        });
        btn2Function.setEnabled(false);
        btn2Function.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toFunctionTestPager();
            }
        });
    }

    private void confirmPassword() {
        String password = etPassword.getText().toString().trim();
        if (password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        btnConfirm.setEnabled(false);
        btn2Function.setEnabled(false);
        sb.setLength(0);
        tvDeviceInfo.setText("");
        tvPwdInfo.setText("正在验证密码...");
        VPOperateManager.getInstance().confirmDevicePwd(new IBleWriteResponse() {
            @Override
            public void onResponse(int code) {
                if (code == Code.REQUEST_SUCCESS) {
                    tvPwdInfo.setText("密码校验指令写入成功");
                } else {
                    tvPwdInfo.setText("密码校验指令写入失败");
                    btn2Function.setEnabled(false);
                }
            }
        }, new IPwdDataListener() {
            @Override
            public void onPwdDataChange(PwdData pwdData) {
                String message = "PwdData:\n" + pwdData.toString();
                Logger.t(TAG).i(message);
                deviceNumber = pwdData.getDeviceNumber();
                deviceVersion = pwdData.getDeviceVersion();
                deviceTestVersion = pwdData.getDeviceTestVersion();
                sb.append("设备号：").append(deviceNumber).append(",版本号：").append(deviceVersion).append(", 测试版本号：").append(deviceTestVersion);
            }

            @Override
            public void onConnectionConfirmTimeout() {
                tvPwdInfo.setText("错误:连接确认超时");
                btn2Function.setEnabled(false);
            }
        }, new IDeviceFuctionDataListener() {
            @Override
            public void onFunctionSupportDataChange(FunctionDeviceSupportData functionSupport) {
                String message = "FunctionDeviceSupportData:\n" + functionSupport.toString();
                Logger.t(TAG).i(message);
                EFunctionStatus newCalcSport = functionSupport.getNewCalcSport();
                if (newCalcSport != null && newCalcSport.equals(SUPPORT)) {
                    isNewSportCalc = true;
                } else {
                    isNewSportCalc = false;
                }
                watchDataDay = functionSupport.getWathcDay();
                weatherStyle = functionSupport.getWeatherStyle();
                contactMsgLength = functionSupport.getContactMsgLength();
                allMsgLenght = functionSupport.getAllMsgLength();
                isSleepPrecision = functionSupport.getPrecisionSleep() == SUPPORT;
            }

            @Override
            public void onDeviceFunctionPackage1Report(DeviceFunctionPackage1 functionPackage1) {
                String message = "【功能第1包】:\n" + functionPackage1.toString();
                appendDeviceInfo(message);
                Logger.t(TAG).i(message);
            }

            @Override
            public void onDeviceFunctionPackage2Report(DeviceFunctionPackage2 functionPackage2) {
                String message = "【功能第2包】:\n" + functionPackage2.toString();
                appendDeviceInfo(message);
                Logger.t(TAG).i(message);
            }

            @Override
            public void onDeviceFunctionPackage3Report(DeviceFunctionPackage3 functionPackage3) {
                String message = "【功能第3包】:\n" + functionPackage3.toString();
                appendDeviceInfo(message);
                Logger.t(TAG).i(message);
            }

            @Override
            public void onDeviceFunctionPackage4Report(DeviceFunctionPackage4 functionPackage4) {
                String message = "【功能第4包】:\n" + functionPackage4.toString();
                appendDeviceInfo(message);
                Logger.t(TAG).i(message);
            }

            @Override
            public void onDeviceFunctionPackage5Report(DeviceFunctionPackage5 functionPackage5) {
                String message = "【功能第5包】:\n" + functionPackage5.toString();
                appendDeviceInfo(message);
                Logger.t(TAG).i(message);
            }
        }, new ISocialMsgDataListener() {
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData socailMsgData) {
                String message = "【消息开关第1包】:\n" + socailMsgData.toString();
                appendDeviceInfo(message);
                Logger.t(TAG).i(message);
            }

            @Override
            public void onSocialMsgSupportDataChange2(FunctionSocailMsgData socailMsgData) {
                String message = "【消息开关第2包】:\n" + socailMsgData.toString();
                appendDeviceInfo(message);
                Logger.t(TAG).i(message);
            }
        }, new ICustomSettingDataListener() {
            @Override
            public void OnSettingDataChange(CustomSettingData customSettingData) {
                btnConfirm.setEnabled(true);
                btn2Function.setEnabled(true);
                String message = "【开关设置】:\n" + customSettingData.toString();
                appendDeviceInfo(message);
                Logger.t(TAG).i(message);
            }
        }, "0000", true);
    }

    private void appendDeviceInfo1(String msg) {
        runOnUiThread(() -> {
            sb.append("\n").append("#############################");
            sb.append("\n").append(msg);
            tvDeviceInfo.setText(sb.toString());
            svInfo.fullScroll(View.FOCUS_DOWN);
        });
    }

    private void appendDeviceInfo(String msg) {
        runOnUiThread(() -> {
            String line = "########################";
            String coloredLine = "<font color='#0000FF' size='16'>" + line + "</font>";
            String styledMsg = msg.replaceAll("【([^】]+)】", "<font color='#FF0000'><b>【$1】</b></font>");
            sb.append("\n").append(coloredLine);
            sb.append("\n").append(styledMsg).append("\n");
            tvDeviceInfo.setText(android.text.Html.fromHtml(sb.toString()));
            svInfo.fullScroll(View.FOCUS_DOWN);
        });
    }

    private int watchDataDay = 0;
    private int weatherStyle = 0;
    private int contactMsgLength = 0;
    private int allMsgLenght = 0;
    private boolean isSleepPrecision = false;
    private boolean isNewSportCalc = false;
    private boolean isOadModel = false;
    private String deviceaddress = "";

    private void toFunctionTestPager() {
        new AlertDialog.Builder(this)
                .setTitle("是否跳转功能测试页面")
                .setMessage("【注意】：测试设备功能时，只有当前设备【支持该功能】才能测试，功能支持与否请参考【IDeviceFuctionDataListener】的功能回调接口")
                .setPositiveButton("确定", (dialog, which) -> {
                    // 点击确定后要做的事
                    Intent intent = new Intent(this, OperaterActivity.class);
                    intent.putExtra("password_confirmed", true);
                    intent.putExtra("deviceNumber", deviceNumber);
                    intent.putExtra("deviceVersion", deviceVersion);
                    intent.putExtra("deviceTestVersion", deviceTestVersion);
                    intent.putExtra("watchDataDay", watchDataDay);
                    intent.putExtra("weatherStyle", weatherStyle);
                    intent.putExtra("contactMsgLength", contactMsgLength);
                    intent.putExtra("allMsgLenght", allMsgLenght);
                    intent.putExtra("isSleepPrecision", isSleepPrecision);
                    intent.putExtra("isNewSportCalc", isNewSportCalc);
                    intent.putExtra("isOadModel", isOadModel);
                    intent.putExtra("deviceaddress", deviceaddress);
                    startActivity(intent);
                    dialog.dismiss();
                    finish();
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    // 点击取消后要做的事
                    dialog.dismiss();
                })
                .setCancelable(true) // 点击空白是否能关闭
                .show();
    }
}
