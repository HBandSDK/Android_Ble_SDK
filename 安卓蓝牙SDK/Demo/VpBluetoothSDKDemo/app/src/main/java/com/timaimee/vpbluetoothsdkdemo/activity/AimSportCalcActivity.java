package com.timaimee.vpbluetoothsdkdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.timaimee.vpbluetoothsdkdemo.R;
import com.veepoo.protocol.model.enums.ESex;
import com.veepoo.protocol.util.SportUtil;

/**
 * Created by timaimee on 2017/4/24.
 */
public class AimSportCalcActivity extends Activity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private final static String TAG = AimSportCalcActivity.class.getSimpleName();
    TextView tv;
    RadioGroup radioButtonSex;
    EditText heightEd, weightEd, stepsEd;
    Button update;
    boolean isnewsportcalc = false;
    ESex sex = ESex.MAN;
    int height = 175, weight = 50, steps = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sportcalc);
        tv = (TextView) findViewById(R.id.aimsportcalc_tv);
        isnewsportcalc = getIntent().getBooleanExtra("isnewsportcalc", false);

        radioButtonSex = (RadioGroup) findViewById(R.id.aimsportcalc_person_sex);
        heightEd = (EditText) findViewById(R.id.aimsportcalc_person_height);
        weightEd = (EditText) findViewById(R.id.aimsportcalc_person_weight);
        stepsEd = (EditText) findViewById(R.id.aimsportcalc_steps);
        update = (Button) findViewById(R.id.aimsportcalc_update);
        radioButtonSex.check(R.id.aimsportcalc_person_sexm);
        radioButtonSex.setOnCheckedChangeListener(this);
        update.setOnClickListener(this);

        String personInfo = setPersonInfo(sex, height, weight, steps);
        tv.setText(personInfo);
        Logger.t(TAG).i(personInfo);
    }

    private String setPersonInfo(ESex sex, float height, float weight, int stepcount) {

        StringBuffer sb = new StringBuffer();
        String perSonInfo = "个人信息,性别=" + sex + ",身高=" + height + ",体重=" + weight;
        String bmi = "BMI=" + SportUtil.getBMI(height, weight);
        String stepLength = "步长=" + SportUtil.getStepLength(height);
        String aimTip = "根据性别、身高、体重算出来的目标值,是否采用新的计算方法=" + isnewsportcalc;
        String aimSportCount = "AimSportCount=" + SportUtil.getAimSportCount(sex, weight, height);
        String aimDistance = "AimDistance=" + SportUtil.getAimDistance(sex, weight, height);
        String aimKcalNew = "AimKcal=" + SportUtil.getAimKcal(sex, weight, height, isnewsportcalc);
        String sportCalc = "根据性别、身高、体重,步数算出来的步行距离以及消耗卡路里，步数=" + stepcount;
        String distance_1 = "getDistance1=" + SportUtil.getDistance1(stepcount, height);
        String distance_2 = "getDistance2=" + SportUtil.getDistance2(stepcount, height);
        String distance_3 = "getDistance3=" + SportUtil.getDistance3(stepcount, height);
        String kcal_0 = "getKcal0=" + SportUtil.getKcal0(stepcount, height, isnewsportcalc);
        String kca1_1 = "getKcal1=" + SportUtil.getKcal1(stepcount, height, isnewsportcalc);

        sb.append(perSonInfo);
        sb.append("\n");
        sb.append("\n");
        sb.append(bmi);
        sb.append("\n");
        sb.append(stepLength);
        sb.append("\n");
        sb.append("\n");
        sb.append("\n");
        sb.append(aimTip);
        sb.append("\n");
        sb.append("\n");
        sb.append(aimSportCount);
        sb.append("\n");
        sb.append(aimDistance);
        sb.append("\n");
        sb.append(aimKcalNew);
        sb.append("\n");
        sb.append("\n");
        sb.append(sportCalc);
        sb.append("\n");
        sb.append("\n");
        sb.append(distance_1);
        sb.append("\n");
        sb.append(distance_2);
        sb.append("\n");
        sb.append(distance_3);
        sb.append("\n");
        sb.append(kcal_0);
        sb.append("\n");
        sb.append(kca1_1);
        sb.append("\n");
        sb.append("\n");
        sb.append("\n");


        return sb.toString();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.aimsportcalc_person_sexm:
                sex = ESex.MAN;
                break;
            case R.id.aimsportcalc_person_sexw:
                sex = ESex.WOMEN;
                break;
        }
    }

    @Override
    public void onClick(View v) {
        String heightStr = heightEd.getText().toString();
        if (!TextUtils.isEmpty(heightStr)) {
            height = Integer.valueOf(heightStr);
        }

        String weightStr = weightEd.getText().toString();
        if (!TextUtils.isEmpty(weightStr)) {
            weight = Integer.valueOf(weightStr);
        }

        String stepStr = stepsEd.getText().toString();
        if (!TextUtils.isEmpty(stepStr)) {
            steps = Integer.valueOf(stepStr);
        }
        String personInfo = setPersonInfo(sex, height, weight, steps);
        tv.setText(personInfo);
    }
}
