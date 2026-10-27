package com.timaimee.vpdemo.activity.v2.other;

import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.v2.BaseActivity;
import com.veepoo.protocol.listener.data.IWearHandConfigListener;
import com.veepoo.protocol.model.datas.WearHandConfigData;
import com.veepoo.protocol.model.enums.EWearHandMode;
import com.veepoo.protocol.shareprence.VpSpGetUtil;

/**
 * 左右手佩戴配置测试页
 * 需设备支持：A7功能列表返回左右手佩戴支持标志
 */
public class WearHandConfigActivity extends BaseActivity implements IWearHandConfigListener {

    @Override
    public int getLayoutID() {
        return R.layout.activity_wear_hand_config;
    }

    @Override
    public String pageTitle() {
        return "左右手佩戴配置";
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        if (!VpSpGetUtil.getVpSpVariInstance(this).isSupportWearHand()) {
            appendRedLargeText("❌️当前设备不支持左右手佩戴配置功能");
        }
    }

    @Override
    public void initEvent() {
        findViewById(R.id.btnRead).setOnClickListener(v ->
                vpBleManager.readWearHandConfig(this, defaultResponse));
        findViewById(R.id.btnLeftHand).setOnClickListener(v ->
                vpBleManager.setWearHandConfig(EWearHandMode.LEFT_HAND, this, defaultResponse));
        findViewById(R.id.btnRightHand).setOnClickListener(v ->
                vpBleManager.setWearHandConfig(EWearHandMode.RIGHT_HAND, this, defaultResponse));
    }

    @Override
    public void onWearHandConfigResult(WearHandConfigData data) {
        if (data.getSuccess()) {
            appendResult("✅️操作成功：当前佩戴手位=" + data.getWearMode().name()
                    + "(数据类型:" + wearHandCmdDes(data.getCmd()) + ")");
        } else {
            appendRedLargeText("❌️操作失败");
        }
    }

    private String wearHandCmdDes(int cmd) {
        switch (cmd) {
            case 0:
                return "设置应答";
            case 1:
                return "读取应答";
            case 2:
                return "设备主动上报";
            default:
                return "未知(" + cmd + ")";
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
