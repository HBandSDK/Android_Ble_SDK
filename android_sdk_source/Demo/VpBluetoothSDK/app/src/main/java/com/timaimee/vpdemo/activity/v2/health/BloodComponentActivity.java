package com.timaimee.vpdemo.activity.v2.health;

import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.v2.BaseActivity;
import com.veepoo.protocol.listener.data.IBloodComponentOptListener;
import com.veepoo.protocol.model.datas.BloodComponent;

import org.jetbrains.annotations.NotNull;

public class BloodComponentActivity extends BaseActivity implements View.OnClickListener , IBloodComponentOptListener{

    // 输入框
    private EditText etUricAcid, etTCHO, etTAG, ethDL, etLDL;

    // 按钮
    private Button btnReadBloodCompositionCalibration, btnSettingBloodCompositionCalibration, btnDetect;
    private RadioGroup rgIsOpen;
    TextView tvProcessInfo;
    @Override
    public int getLayoutID() {
        return R.layout.activity_blood_component;
    }

    @Override
    public String pageTitle() {
        return "血液成分";
    }

    @Override
    public void initView() {
        // 初始化输入框
        etUricAcid = findViewById(R.id.etUricAcid);
        etTCHO = findViewById(R.id.etTCHO);
        etTAG = findViewById(R.id.etTAG);
        ethDL = findViewById(R.id.ethDL);
        etLDL = findViewById(R.id.etLDL);
        rgIsOpen = findViewById(R.id.rgIsOpen);

        tvProcessInfo = findViewById(R.id.tvDetectProcessStatus);
        // 初始化按钮
        btnReadBloodCompositionCalibration = findViewById(R.id.btnReadBloodCompositionCalibration);
        btnSettingBloodCompositionCalibration = findViewById(R.id.btnSettingBloodCompositionCalibration);
        btnDetect = findViewById(R.id.btnDetect);

        //==================== 限制输入类型：只允许数字 + 小数点 ====================
        etUricAcid.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etTCHO.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etTAG.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        ethDL.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etLDL.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    @Override
    public boolean hasCommonMsgUI() {
        return true;
    }

    @Override
    public void initData() {
    }

    @Override
    public void initEvent() {
        btnReadBloodCompositionCalibration.setOnClickListener(this);
        btnSettingBloodCompositionCalibration.setOnClickListener(this);
        btnDetect.setOnClickListener(this);
    }

    private BloodComponent getSettingBloodComponent() {
        BloodComponent bloodComponent = null;
        // 1. 尿酸 90.0 ~ 1000.0
        String uricStr = etUricAcid.getText().toString().trim();
        if (uricStr.isEmpty()) {
            showToast("请输入尿酸值");
            return bloodComponent;
        }
        float uricAcid = Float.parseFloat(uricStr);
        if (uricAcid < 90.0f || uricAcid > 1000.0f) {
            showToast("尿酸值必须在 90.0 ~ 1000.0 之间");
            return bloodComponent;
        }

        // 2. 总胆固醇 0.01 ~ 100.00
        String tchoStr = etTCHO.getText().toString().trim();
        if (tchoStr.isEmpty()) {
            showToast("请输入总胆固醇");
            return bloodComponent;
        }
        float tCHO = Float.parseFloat(tchoStr);
        if (tCHO < 0.01f || tCHO > 100.00f) {
            showToast("总胆固醇必须在 0.01 ~ 100.00 之间");
            return bloodComponent;
        }

        // 3. 甘油三酯 0.01 ~ 100.00
        String tagStr = etTAG.getText().toString().trim();
        if (tagStr.isEmpty()) {
            showToast("请输入甘油三酯");
            return bloodComponent;
        }
        float tAG = Float.parseFloat(tagStr);
        if (tAG < 0.01f || tAG > 100.00f) {
            showToast("甘油三酯必须在 0.01 ~ 100.00 之间");
            return bloodComponent;
        }

        // 4. 高密度脂蛋白 0.01 ~ 100.00
        String hdlStr = ethDL.getText().toString().trim();
        if (hdlStr.isEmpty()) {
            showToast("请输入高密度脂蛋白");
            return bloodComponent;
        }
        float hDL = Float.parseFloat(hdlStr);
        if (hDL < 0.01f || hDL > 100.00f) {
            showToast("高密度脂蛋白必须在 0.01 ~ 100.00 之间");
            return bloodComponent;
        }

        // 5. 低密度脂蛋白 0.01 ~ 100.00
        String ldlStr = etLDL.getText().toString().trim();
        if (ldlStr.isEmpty()) {
            showToast("请输入低密度脂蛋白");
            return bloodComponent;
        }
        float lDL = Float.parseFloat(ldlStr);
        if (lDL < 0.01f || lDL > 100.00f) {
            showToast("低密度脂蛋白必须在 0.01 ~ 100.00 之间");
            return bloodComponent;
        }

        bloodComponent = new BloodComponent();
        bloodComponent.setUricAcid(uricAcid);
        bloodComponent.setTAG(tAG);
        bloodComponent.setTCHO(tCHO);
        bloodComponent.setHDL(hDL);
        bloodComponent.setLDL(lDL);
        return bloodComponent;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnReadBloodCompositionCalibration) {
            // 读取血液成分校准值
            readBloodComponentCalibration();
        } else if (id == R.id.btnSettingBloodCompositionCalibration) {
            // 设置血液成分校准值 → 先校验
            settingBloodComponentCalibration();
        } else if (id == R.id.btnDetect) {
            // 血液成分测量
            startActivity(new Intent(this, BloodComponentDetectActivity.class));
        }
    }

    private void readBloodComponentCalibration(){
        clearTestInfo();
        vpBleManager.readBloodComponentCalibration(defaultResponse,this);
    }

    private void settingBloodComponentCalibration(){
        BloodComponent bloodComponent = getSettingBloodComponent();
        if(bloodComponent != null) {
            clearTestInfo();
            boolean isOpen = rgIsOpen.getCheckedRadioButtonId() == R.id.rbOpen;
            vpBleManager.settingBloodComponentCalibration(defaultResponse, isOpen, bloodComponent, this);
        }
    }

    @Override
    public void onBloodCompositionReadSuccess(boolean isOpen, @NotNull BloodComponent bloodComposition) {
        appendBlueMiddleText("读取成功>>> 是否开启："+isOpen + " " + bloodComposition.toString());
    }

    @Override
    public void onBloodCompositionReadFailed() {
        appendRedLargeText("读取失败");
    }

    @Override
    public void onBloodCompositionSettingSuccess(boolean isOpen, @NotNull BloodComponent bloodComposition) {
        appendBlueMiddleText("设置成功>>> 是否开启："+isOpen + " " + bloodComposition.toString());
    }

    @Override
    public void onBloodCompositionSettingFailed() {
        appendRedLargeText("设置失败");
    }
}