package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inuker.bluetooth.library.Code;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.IMiniCheckupOptListener;
import com.veepoo.protocol.model.datas.MiniCheckupBPAirPump;
import com.veepoo.protocol.model.datas.MiniCheckupBPPhotoelectric;
import com.veepoo.protocol.model.datas.MiniCheckupBasePersonalInfo;
import com.veepoo.protocol.model.datas.MiniCheckupBloodComponent;
import com.veepoo.protocol.model.datas.MiniCheckupBodyComponent;
import com.veepoo.protocol.model.datas.MiniCheckupDetailData;
import com.veepoo.protocol.model.datas.MiniCheckupResultData;
import com.veepoo.protocol.model.datas.MiniCheckupSkinElectricity;
import com.veepoo.protocol.model.enums.EMiniCheckupTestErrorCode;

import java.util.Locale;

public class MiniCheckupActivity extends Activity implements IMiniCheckupOptListener, View.OnClickListener {
    Button btnStartMiniCheckup;
    Button btnStopMiniCheckup;
    TextView tvMiniCheckupInfo;

    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_checkup);
        btnStopMiniCheckup = findViewById(R.id.btnStopRealTimePPGRawDataTransfer);
        btnStartMiniCheckup = findViewById(R.id.btnReadPPGRawData);
        tvMiniCheckupInfo = findViewById(R.id.tvMiniCheckupInfo);
        btnStopMiniCheckup.setOnClickListener(this);
        btnStartMiniCheckup.setOnClickListener(this);
    }

    @Override
    public void onMiniCheckupTestProgress(int progress) {
        tvMiniCheckupInfo.setText(String.format(Locale.CHINA, "【开始】微体检指令写入成功！\n 测量中... %d%%", progress));
    }

    @Override
    public void onMiniCheckupStopSuccess() {
        appendMsg("微体检测量已【停止】");
    }

    @Override
    public void onMiniCheckupTestFailed(@NonNull EMiniCheckupTestErrorCode errorCode) {
        appendMsg("微体检测量【失败】:" + errorCode);
    }

    @Override
    public void onMiniCheckupSuccess(@NonNull MiniCheckupResultData testResultData) {
        appendMsg("微体检测量【成功】-->");
        appendMsg("【心率】:" + testResultData.getHeartRate() + "bmp");
        appendMsg("【血氧】:" + testResultData.getBloodOxygen() + "%");
        appendMsg("【压力】:" + testResultData.getStress());
        appendMsg("【情绪】:" + testResultData.getEmotion());
        appendMsg("【疲劳度】:" + testResultData.getFatigue());
        appendMsg("【血糖】:" + testResultData.getBloodGlucose() + "mmol/L");
        appendMsg("【体温】:" + testResultData.getBodyTemperature() + "℃");
        appendMsg("【血压（收缩压）】:" + testResultData.getSystolicBloodPressure());
        appendMsg("【血压（舒张压）】:" + testResultData.getDiastolicBloodPressure());
        appendMsg("【HRV】:" + testResultData.getHrv());
    }

    @Override
    public void onMiniCheckupDetailTestSuccess(@NonNull MiniCheckupDetailData data) {
        appendMsg("微体检详情测量【成功】-->");
        appendMsg("【个人信息】:" + getBasePersonalInfo(data.getBasePersonalInfo()));
        appendMsg("【心率】:" + data.getHeartRate() + " bmp");
        appendMsg("【血氧】:" + data.getBloodOxygen() + "%");
        appendMsg("【压力】:" + data.getStress());
        appendMsg("【情绪】:" + data.getEmotion());
        appendMsg("【疲劳度】:" + data.getFatigue());
        appendMsg("【血糖类型】:" + getBloodGlucoseTypeDes(data.getBloodGlucoseType()));
        appendMsg("【血糖】:" + data.getBloodGlucose() + " mmol/L");
        appendMsg("【体温】:" + ((data.getBodyTemperature() == -273.15f) ? "不支持" : (data.getBodyTemperature() + "℃")));
        appendMsg("【原始温度】:" + ((data.getOriginalTemperature() == -273.15f) ? "不支持" : (data.getBodyTemperature() + "℃")));
        appendMsg("【HRV】:" + data.getHrv());
        appendMsg("【气泵血压】:" + getBpDS(data.getBpAirPump()));
        appendMsg("【光电血压】:" + getBpDS(data.getBpPhotoelectric()));
        appendMsg("【血液成分】:" + getBloodComponent(data.getBloodComponent()));
        appendMsg("【身体成分】:" + getBodyComponent(data.getBodyComponent()));
        appendMsg("【皮电】:" + getSkinElectricity(data.getSkinElectricity()));
    }

    private String getBasePersonalInfo(MiniCheckupBasePersonalInfo personalInfo) {
        if (personalInfo == null) return "不支持";
        return "性别:" + personalInfo.getGender()
                + " 年龄:" + personalInfo.getAge()
                + " 身高:" + personalInfo.getHeight()
                + " 体重:" + personalInfo.getWeight();
    }

    private String getBpDS(MiniCheckupBPAirPump bpAirPump) {
        if (bpAirPump == null) return "不支持";
        return bpAirPump.getDiastolicBloodPressure() + " - " + bpAirPump.getSystolicBloodPressure();
    }

    private String getBpDS(MiniCheckupBPPhotoelectric bpPhotoelectric) {
        if (bpPhotoelectric == null) return "不支持";
        return bpPhotoelectric.getDiastolicBloodPressure() + " - " + bpPhotoelectric.getSystolicBloodPressure();
    }

    public String getBloodComponent(MiniCheckupBloodComponent bloodComponent) {
        if (bloodComponent == null) return "不支持";
        return "尿酸:" + bloodComponent.getUricAcid()
                + " 总胆固醇:" + bloodComponent.gettCHO()
                + " 甘油三酸酯:" + bloodComponent.gettAG()
                + " 高密度脂蛋白:" + bloodComponent.gethDL()
                + " 低密度脂蛋白:" + bloodComponent.getlDL();
    }

    public String getSkinElectricity(MiniCheckupSkinElectricity skinElectricity) {
        if (skinElectricity == null) return "不支持";
        return "情绪:" + skinElectricity.getEmotion()
                + " 皮肤含水量:" + skinElectricity.getSkinMoistureContent()
                + " 抑郁症风险:" + skinElectricity.getDepressionRisk()
                + " 交感神经活跃度:" + skinElectricity.getSympatheticActivity()
                + " 皮质醇浓度:" + skinElectricity.getCortisolConcentration();
    }

    public String getBodyComponent(MiniCheckupBodyComponent bodyComponent) {
        if (bodyComponent == null) return "不支持";
        return "性别:" + bodyComponent.getGender()
                + " 年龄:" + bodyComponent.getAge()
                + " 身高:" + bodyComponent.getHeight()
                + " 体重:" + bodyComponent.getWeight()
                + " BMI:" + bodyComponent.getBMI()
                + " 体脂率:" + bodyComponent.getBodyFatRate()
                + " 脂肪量:" + bodyComponent.getFatRate()
                + " 去脂体重:" + bodyComponent.getFFM()
                + " 肌肉率:" + bodyComponent.getMuscleRate()
                + " 肌肉量:" + bodyComponent.getMuscleMass()
                + " 皮下脂肪:" + bodyComponent.getSubcutaneousFat()
                + " 体内水分:" + bodyComponent.getBodyWater()
                + " 含水量:" + bodyComponent.getWaterContent()
                + " 骨骼肌率:" + bodyComponent.getSkeletalMuscleRate()
                + " 骨量:" + bodyComponent.getBoneMass()
                + " 蛋白质占比:" + bodyComponent.getProteinProportion()
                + " 蛋白质量:" + bodyComponent.getProteinMass()
                + " 基础代谢率:" + bodyComponent.getBasalMetabolicRate()
                ;
    }

    private String getBloodGlucoseTypeDes(int type) {
        switch (type) {
            case 0:
                return "不支持";
            case 1:
                return "显示血糖值";
            case 2:
                return "显示血糖风险等级";
        }
        return "未知";
    }

    @Override
    public void onClick(View v) {
        if (v == btnStartMiniCheckup) {
            sb.setLength(0);
            VPOperateManager.getInstance().startMiniCheckup(code -> {
                if (code == Code.REQUEST_SUCCESS) {
                    appendMsg("【开始】微体检指令写入成功！");
                } else {
                    appendMsg("【开始】微体检指令写入失败！");
                }
            }, this);
        }
        if (v == btnStopMiniCheckup) {
            VPOperateManager.getInstance().stopMiniCheckup(code -> {
                if (code == Code.REQUEST_SUCCESS) {
                    appendMsg("【停止】微体检指令写入成功！");
                } else {
                    appendMsg("【停止】微体检指令写入失败！");
                }
            }, this);
        }
    }

    private void appendMsg(String msg) {
        sb.append(msg).append("\n");
        tvMiniCheckupInfo.setText(sb.toString());
    }

}
