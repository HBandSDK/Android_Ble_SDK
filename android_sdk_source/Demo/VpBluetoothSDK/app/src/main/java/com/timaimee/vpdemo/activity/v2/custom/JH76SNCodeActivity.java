package com.timaimee.vpdemo.activity.v2.custom;

import android.text.TextUtils;
import android.widget.EditText;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.v2.BaseActivity;
import com.veepoo.protocol.listener.data.IJH76SNCodeOptListener;
import com.veepoo.protocol.listener.data.IJH76SNCodeReadListener;
import com.veepoo.protocol.model.datas.JH76SNCodeData;
import com.veepoo.protocol.model.enums.EJH76SNCodeError;

/**
 * JH76定制功能-SN码设置测试页
 * (定制功能，需要设备支持)
 */
public class JH76SNCodeActivity extends BaseActivity {

    EditText etSNCode;

    @Override
    public int getLayoutID() {
        return R.layout.activity_jh76_sn_code;
    }

    @Override
    public String pageTitle() {
        return "JH76 SN码设置";
    }

    @Override
    public void initView() {
        etSNCode = findViewById(R.id.etSNCode);
    }

    @Override
    public void initData() {
    }

    @Override
    public void initEvent() {
        findViewById(R.id.btnRead).setOnClickListener(v -> vpBleManager.readJH76SNCode(mSNCodeReadListener, code -> {
        }));
        findViewById(R.id.btnSet).setOnClickListener(v ->
                vpBleManager.setJH76SNCode(getInputSNCode(), mSNCodeOptListener, code -> {
                }));
        findViewById(R.id.btnModify).setOnClickListener(v ->
                vpBleManager.modifyJH76SNCode(getInputSNCode(), mSNCodeOptListener, code -> {
                }));
        findViewById(R.id.btnDelete).setOnClickListener(v ->
                vpBleManager.deleteJH76SNCode(mSNCodeOptListener, code -> {
                }));
    }

    private String getInputSNCode() {
        return etSNCode == null ? "" : etSNCode.getText().toString().trim();
    }

    /**
     * 读取SN码监听
     */
    private final IJH76SNCodeReadListener mSNCodeReadListener = this::onJH76SNCodeRead;

    private void onJH76SNCodeRead(JH76SNCodeData data) {
        if (data != null && data.isSet() && !TextUtils.isEmpty(data.getSnCode())) {
            appendResult("✅️SN码读取成功：已设置，SN码=" + data.getSnCode());
        } else {
            appendBlueMiddleText("✔️SN码读取成功：未设置SN码");
        }
    }

    /**
     * 设置/修改/删除SN码监听
     */
    private final IJH76SNCodeOptListener mSNCodeOptListener = this::onJH76SNCodeOptResult;

    private void onJH76SNCodeOptResult(EJH76SNCodeError error) {
        if (error == EJH76SNCodeError.SUCCESS) {
            appendResult("✅️SN码操作成功");
        } else {
            appendRedLargeText("❌️SN码操作失败：" + error.getDes() + "(错误码:" + error.getCode() + ")");
        }
    }

    @Override
    public void onClick(android.view.View view) {
    }

    @Override
    public boolean hasCommonMsgUI() {
        return true;
    }
}
