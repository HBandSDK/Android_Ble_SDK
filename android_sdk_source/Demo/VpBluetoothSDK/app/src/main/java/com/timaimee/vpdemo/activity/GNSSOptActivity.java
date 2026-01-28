package com.timaimee.vpdemo.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.data.IGNSSOptListener;
import com.veepoo.protocol.listener.data.ISafetyProtectionOptListener;
import com.veepoo.protocol.model.datas.GnssLocationData;
import com.veepoo.protocol.model.enums.EGNSSState;
import com.veepoo.protocol.model.enums.EGnssOptType;
import com.veepoo.protocol.model.enums.ESafetyProtectionState;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GNSSOptActivity extends AppCompatActivity implements View.OnClickListener, IGNSSOptListener, ISafetyProtectionOptListener {
    private Button btnStartSP, btnStopSP, btnPushGnssData;
    private EditText etRssi, etAltitude, etLng, etLat;
    private Spinner spFunction;
    private TextView tvInfo, tvTitle;
    private ScrollView svInfo;

    private int function = 1;
    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_gnss_opt);
        btnStartSP = findViewById(R.id.btnStartSafetyProtection);
        btnStopSP = findViewById(R.id.btnStopSafetyProtection);
        btnPushGnssData = findViewById(R.id.btnPushGnssLocationData);
        etRssi = findViewById(R.id.etRSSI);
        etAltitude = findViewById(R.id.etAltitude);
        etLng = findViewById(R.id.etLng);
        etLat = findViewById(R.id.etLat);
        spFunction = findViewById(R.id.spFunction);
        tvInfo = findViewById(R.id.tvInfo);
        svInfo = findViewById(R.id.svInfo);
        tvTitle = findViewById(R.id.tvTitle);

        btnStartSP.setOnClickListener(this);
        btnStopSP.setOnClickListener(this);
        btnPushGnssData.setOnClickListener(this);

        String[] languages = {"GNSS运动", "亲情安全守护", "SOS紧急求助"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, languages);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFunction.setAdapter(adapter);

        spFunction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                function = position + 1;
                String selected = languages[position];
                Toast.makeText(GNSSOptActivity.this, "Selected: " + selected, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //mgLat=22.549002719389936,mgLon=113.94113370819578
        etLat.setText("22.54900");
        etLng.setText("113.94113");
        etAltitude.setText("1000");
        etRssi.setText("-65");
        VPOperateManager.getInstance().addGnssLocationOptListener(this);
        VPOperateManager.getInstance().addSafetyProtectionOptListener(this);

        tvTitle.setOnClickListener(v -> {
            tvInfo.setText("");
            sb.setLength(0);
        });
    }

    private GnssLocationData getGnssLocationData(){
        GnssLocationData data = new GnssLocationData();
        data.setState(EGNSSState.VALID_POSITION);
        String rssiStr = etRssi.getText().toString();
        int rssi = -65;
        if(!TextUtils.isEmpty(rssiStr)) {
            rssi = Integer.parseInt(rssiStr);
        } else {
            showToast("蓝牙信号值不能为空");
            return null;
        }

        String altitudeStr = etAltitude.getText().toString();
        int altitude = 1000;
        if(!TextUtils.isEmpty(altitudeStr)) {
            altitude = Integer.parseInt(altitudeStr);
        } else {
            altitude = 0;
        }
        //mgLat=22.549002719389936,mgLon=113.94113370819578

        String latStr = etLat.getText().toString();
        double lat = 22.54900;
        if(!TextUtils.isEmpty(latStr)) {
            lat = Double.parseDouble(latStr);
        } else {
            showToast("纬度不能为空");
            return null;
        }

        String lngStr = etLng.getText().toString();
        double lng = 113.94113;
        if(!TextUtils.isEmpty(lngStr)) {
            lng = Double.parseDouble(lngStr);
        } else {
            showToast("经度不能为空");
            return null;
        }
        data.setRssi(rssi);
        data.setAltitude(altitude);
        data.setLongitude(lng);
        data.setLatitude(lat);
        return data;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartSafetyProtection: {
                VPOperateManager.getInstance().startSafetyProtection(code -> {

                });
                break;
            }
            case R.id.btnStopSafetyProtection: {
                VPOperateManager.getInstance().stopSafetyProtection(code -> {

                });
                break;
            }
            case R.id.btnPushGnssLocationData: {
                GnssLocationData data = getGnssLocationData();
                EGnssOptType optType = EGnssOptType.Companion.getTypeWithCMD(function);
                if (data != null) {
                    VPOperateManager.getInstance().pushGnssLocationData(EGnssOptType.GNSS_SPORT, data, code -> {
                        appendMsg("+--------------------------------------+");
                        appendMsg("| 【" + optType.getDes() + "】位置信息推送成功：");
                        appendMsg("| ---> " + data);
                        appendMsg("+--------------------------------------+");
                    });
                }
                break;
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void appendMsg(String msg) {
        runOnUiThread(() -> {
            sb.append(msg).append("\n");
            tvInfo.setText(sb.toString());
            svInfo.fullScroll(View.FOCUS_DOWN);
        });
    }

    @Override
    public void onPhoneSafetyProtectionOpt(boolean isStart) {
        appendMsg("+--------------------------------------+");
        appendMsg(isStart ? "| 【手机】开启设备安全守护成功" : "| 【手机】结束设备安全守护成功");
        appendMsg("+--------------------------------------+");
    }

    @Override
    public void onWatchSafetyProtectionOpt(boolean isStart, long timestamp) {
        appendMsg("+--------------------------------------+");
        appendMsg((isStart ? "| 【设备】开启安全守护成功:" : "| 【设备】结束安全守护成功:") + sdf.format(new Date(timestamp * 1000)));
        appendMsg("+--------------------------------------+");

    }

    @Override
    public void onPhoneSafetyProtectionStartError(@NonNull ESafetyProtectionState status) {
        appendMsg("+--------------------------------------+");
        appendMsg("| 【手机】开启手表安全守护异常:" + status);
        appendMsg("+--------------------------------------+");

    }

    @Override
    public void onDeviceGNSSOptRequest(@NonNull EGnssOptType gnssOptType, double longitude, double latitude) {
        appendMsg("+--------------------------------------+");
        appendMsg("| 设备【" + gnssOptType.getDes() + "】请求获取实时位置数据。（附带位置信息："+longitude + " , " + latitude +")");
        appendMsg("+--------------------------------------+");
    }

    @Override
    public void onDeviceGNSSOptReport(@NonNull EGnssOptType gnssOptType, @NonNull GnssLocationData data) {
        appendMsg("+--------------------------------------+");
        appendMsg("| 设备【" + gnssOptType.getDes() + "】实时位置数据上报：");
        appendMsg("| ---> " + data.toString());
        appendMsg("+--------------------------------------+");
    }
}
