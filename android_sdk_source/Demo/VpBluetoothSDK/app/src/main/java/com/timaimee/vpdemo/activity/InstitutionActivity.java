package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.util.InstitutionUtil;

/**
 * Created by timaimee on 2017/4/24.
 */
public class InstitutionActivity extends Activity implements View.OnClickListener {
    private final static String TAG = AimSportCalcActivity.class.getSimpleName();
    TextView tv;
    EditText heightEd, weightEd, distanceEd;

    int height = 175, weight = 50, distance = 50;
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institution);
        tv = (TextView) findViewById(R.id.institution_tv);

        heightEd = (EditText) findViewById(R.id.institution_person_height);
        weightEd = (EditText) findViewById(R.id.institution_person_weight);
        distanceEd = (EditText) findViewById(R.id.institution_distance);
        update = (Button) findViewById(R.id.institution_update);
        update.setOnClickListener(this);

        String institutionStr = getInstitution(height, weight, distance);
        tv.setText(institutionStr);
        Logger.t(TAG).i(institutionStr);
    }

    private String getInstitution(int height, int weight, int distance) {

        StringBuffer sb = new StringBuffer();


        int lbcm = InstitutionUtil.cmToLBCM(height);
        String cmToLbcm = "公制cm-->英制cm," + height + "cm->" + lbcm / 12 + "\'" + lbcm % 12 + "\'\'";
        String lbcmTocm = "英制cm-->公制cm," + lbcm / 12 + "\'" + lbcm % 12 + "\'\'->" + InstitutionUtil.lbcmToCM(lbcm) + "cm";

        float lbkg = InstitutionUtil.kgToLBKG(weight);
        String kgToLbkg = "公制kg-->英制kg, " + weight + "kg->" + lbkg + "Lbs";
        String lbkgTokg = "英制kg-->公制kg, " + lbkg + "Lbs->" + InstitutionUtil.lbkgToKG(lbkg) + "kg";

        double lbkm1 = InstitutionUtil.kmToLBKM1(distance);
        double lbkm2 = InstitutionUtil.kmToLBKM2(distance);
        String kmToLbkm1 = "公制km-->英制km,一位小数," + distance + "km->" + lbkm1 + "mile";
        String kmToLbkm2 = "公制km-->英制km,二位小数," + distance + "km->" + InstitutionUtil.kmToLBKM2(distance) + "mile";
        String LbkmTokm = "英制km-->公制km,2位小数," + lbkm2 + "mile->" + InstitutionUtil.LBkmTokm(lbkm2) + "km";

        sb.append(kmToLbkm1);
        sb.append("\n");
        sb.append(kmToLbkm2);
        sb.append("\n");
        sb.append(LbkmTokm);
        sb.append("\n");
        sb.append("\n");
        sb.append("\n");
        sb.append(cmToLbcm);
        sb.append("\n");
        sb.append(lbcmTocm);
        sb.append("\n");
        sb.append("\n");
        sb.append("\n");
        sb.append(kgToLbkg);
        sb.append("\n");
        sb.append(lbkgTokg);
        sb.append("\n");

        return sb.toString();
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

        String stepStr = distanceEd.getText().toString();
        if (!TextUtils.isEmpty(stepStr)) {
            distance = Integer.valueOf(stepStr);
        }
        String institutionStr = getInstitution(height, weight, distance);
        tv.setText(institutionStr);
    }
}
