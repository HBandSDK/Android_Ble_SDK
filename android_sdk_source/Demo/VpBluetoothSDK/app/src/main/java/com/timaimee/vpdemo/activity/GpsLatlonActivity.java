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
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IAppReportGpsDataListener;
import com.veepoo.protocol.listener.data.IGpsLatLonDataListener;
import com.veepoo.protocol.listener.data.IKaaBaDataListener;
import com.veepoo.protocol.model.datas.GpsLatlonData;
import com.veepoo.protocol.model.datas.KaaBaData;
import com.veepoo.protocol.model.datas.ReportGpsData;
import com.veepoo.protocol.model.enums.EGpsStatus;
import com.veepoo.protocol.model.settings.GpsLatLongSetting;
import com.veepoo.protocol.model.settings.GpsLatLongSettingFromApp;
import com.veepoo.protocol.model.settings.KaabaSetting;
import com.veepoo.protocol.util.VpBleByteUtil;

/**
 * Created by timaimee on 2017/4/24.
 */
public class GpsLatlonActivity extends Activity implements View.OnClickListener {
    private final static String TAG = GpsLatlonActivity.class.getSimpleName();
    EditText mLonvTv;
    EditText mLatvTv;
    EditText mTimezoneTv;
    EditText mTimestampTv;
    EditText mAltitudeTv;

    TextView gps_call_text;
    TextView gps_send_text;
    Button mGpsUpdate, mKaabaUpdate, mListenGps, mSetGpsFromApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpslatlon);

        mLonvTv = (EditText) findViewById(R.id.gps_lonv);
        mLatvTv = (EditText) findViewById(R.id.gps_latv);
        mTimezoneTv = (EditText) findViewById(R.id.gps_timezone);
        mTimestampTv = (EditText) findViewById(R.id.gps_timestamp);
        mAltitudeTv = (EditText) findViewById(R.id.gps_altitude);

        gps_call_text = (TextView) findViewById(R.id.gps_call_text);
        gps_send_text = (TextView) findViewById(R.id.gps_send_text);

        mGpsUpdate = (Button) findViewById(R.id.gps_update);
        mKaabaUpdate = (Button) findViewById(R.id.kaaba_update);
        mListenGps = (Button) findViewById(R.id.listen_gps);
        mSetGpsFromApp = (Button) findViewById(R.id.set_gps_from_app);

        mSetGpsFromApp.setOnClickListener(this);
        mListenGps.setOnClickListener(this);
        mGpsUpdate.setOnClickListener(this);
        mKaabaUpdate.setOnClickListener(this);
    }


    double lon = 0;
    double lat = 0f;
    short timeZon = 0;
    int timestam = 1617934421;
    short altitud = 0;

    @Override
    public void onClick(View v) {

        String lonvStr = mLonvTv.getText().toString();
        if (!TextUtils.isEmpty(lonvStr)) {
            try {

                lon = Double.valueOf(lonvStr);
            } catch (Exception E) {

            }
        }

        String latStr = mLatvTv.getText().toString();
        if (!TextUtils.isEmpty(latStr)) {

            try {

                lat = Double.valueOf(latStr);
            } catch (Exception E) {

            }
        }

        String timeZonStr = mTimezoneTv.getText().toString();
        if (!TextUtils.isEmpty(timeZonStr)) {
            try {

                timeZon = Short.valueOf(timeZonStr);
            } catch (Exception E) {

            }
        }

        String timestamStr = mTimestampTv.getText().toString();
        if (!TextUtils.isEmpty(timestamStr)) {

            try {

                timestam = Integer.valueOf(timestamStr);
            } catch (Exception E) {

            }
        }
        String altitudStr = mAltitudeTv.getText().toString();
        if (!TextUtils.isEmpty(altitudStr)) {


            try {
                altitud = Short.valueOf(altitudStr);
            } catch (Exception E) {

            }
        }

        int id = v.getId();
        switch (id) {
            case R.id.listen_gps:
                VPOperateManager.getMangerInstance(getApplicationContext()).settingRequestAppReportGpsDataListener(new IAppReportGpsDataListener() {

                    @Override
                    public void onReportGpsDataDataChange(ReportGpsData reportGpsData) {
                        boolean isReport = reportGpsData.isReport();
                        if (isReport) {
                            Logger.t(TAG).i("请 app 开始上传 gps数据");
                        } else {
                            Logger.t(TAG).i("请 app 结束上传 gps数据");
                        }
                    }
                });
                break;
            case R.id.set_gps_from_app:
                boolean isReqeustRepo=true;
                EGpsStatus gpsStatus= EGpsStatus.SIGNAL_NORML;
                GpsLatLongSettingFromApp gpsLatLongSettingFromApp = new GpsLatLongSettingFromApp(isReqeustRepo,gpsStatus,lon, lat, timeZon, timestam);
                VPOperateManager.getMangerInstance(getApplicationContext()).settingGpsLatLonfromApp(new IBleWriteResponse() {
                    @Override
                    public void onResponse(int code) {

                    }
                }, gpsLatLongSettingFromApp);
                break;


            case R.id.gps_update:
                GpsLatLongSetting gpsLatLongSetting = new GpsLatLongSetting(lon, lat, timeZon, timestam, altitud);

                byte[] gpsLatlongCmd = getGpsLatlongCmd(gpsLatLongSetting, (byte) 0x01);

                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(gpsLatLongSetting.toString());
                stringBuffer.append("\n");
                stringBuffer.append(VpBleByteUtil.byte2HexForShow(gpsLatlongCmd));
                gps_send_text.setText("请求值:" + stringBuffer);

                VPOperateManager.getMangerInstance(getApplicationContext()).settingGpsLatLon(new IBleWriteResponse() {
                    @Override
                    public void onResponse(int code) {

                    }
                }, new IGpsLatLonDataListener() {
                    @Override
                    public void onGpsLatLonDataChange(final GpsLatlonData gpsLatlonData) {
                        Logger.t(TAG).i(gpsLatlonData.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                gps_call_text.setText("响应值:" + gpsLatlonData.toString());
                            }
                        });
                    }
                }, gpsLatLongSetting);
                break;

            case R.id.kaaba_update:
                KaabaSetting kaabaSetting = new KaabaSetting(lon, lat, altitud);
                byte[] kaabaSettingCmd = getKaabaCmd(kaabaSetting, (byte) 0x03);

                StringBuffer kaaStringBuffer = new StringBuffer();
                kaaStringBuffer.append(kaabaSetting.toString());
                kaaStringBuffer.append("\n");
                kaaStringBuffer.append(VpBleByteUtil.byte2HexForShow(kaabaSettingCmd));
                gps_send_text.setText("请求值:" + kaaStringBuffer);

                VPOperateManager.getMangerInstance(getApplicationContext()).settingKaaba(new IBleWriteResponse() {
                    @Override
                    public void onResponse(int code) {

                    }
                }, kaabaSetting, new IKaaBaDataListener() {
                    @Override
                    public void onKaaBaDataChange(final KaaBaData kaaBaData) {
                        Logger.t(TAG).i(kaaBaData.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                gps_call_text.setText("响应值:" + kaaBaData.toString());
                            }
                        });
                    }
                });

                break;
        }

    }

    private byte[] getGpsLatlongCmd(GpsLatLongSetting gpslatlongsetting, byte setting) {
        byte[] cmd = new byte[20];
        cmd[0] = (byte) 0x8F;
        cmd[1] = setting;

        double lonv = gpslatlongsetting.getLonv();
        int lonvInt = (int) (lonv * 100000);
        byte[] lonvIntbyte = getByteInt(lonvInt);
        cmd[2] = lonvIntbyte[0];
        cmd[3] = lonvIntbyte[1];
        cmd[4] = lonvIntbyte[2];
        cmd[5] = lonvIntbyte[3];

        double latv = gpslatlongsetting.getLatv();
        int latvInt = (int) (latv * 100000);
        byte[] latvIntbyte = getByteInt(latvInt);
        cmd[6] = latvIntbyte[0];
        cmd[7] = latvIntbyte[1];
        cmd[8] = latvIntbyte[2];
        cmd[9] = latvIntbyte[3];

        short timeZone = gpslatlongsetting.getTimeZone();
        byte[] timeZoneByteShort = getByteShort(timeZone);
        cmd[10] = timeZoneByteShort[0];
        cmd[11] = timeZoneByteShort[1];

        int timestamp = gpslatlongsetting.getTimestamp();
        byte[] timeStampByte = getByteInt(timestamp);
        cmd[12] = timeStampByte[0];
        cmd[13] = timeStampByte[1];
        cmd[14] = timeStampByte[2];
        cmd[15] = timeStampByte[3];

        short altitude = gpslatlongsetting.getAltitude();
        byte[] altitudeByteShort = getByteShort(altitude);
        cmd[16] = altitudeByteShort[0];
        cmd[17] = altitudeByteShort[1];

        return cmd;
    }


    public byte[] getByteShort(short n) {
        byte loUint16 = VpBleByteUtil.loUint16(n);
        byte hiUint16 = VpBleByteUtil.hiUint16(n);
        byte[] cmd = {loUint16, hiUint16};
        return cmd;
    }

    public byte[] getByteInt(int data) {
        byte[] cmd = new byte[4];
        cmd[0] = (byte) (data & 0x000000ff);
        cmd[1] = (byte) (data >> 8 & 0x000000ff);
        cmd[2] = (byte) (data >> 16 & 0x000000ff);
        cmd[3] = (byte) (data >> 24 & 0x000000ff);
        return cmd;
    }

    private byte[] getKaabaCmd(KaabaSetting kaabaSetting, byte setting) {
        byte[] cmd = new byte[20];
        cmd[0] = (byte) 0x8F;
        cmd[1] = setting;

        double lat = kaabaSetting.getLat();
        int latInt = (int) (lat * 100000);
        byte[] latIntbyte = getByteInt(latInt);
        cmd[2] = latIntbyte[0];
        cmd[3] = latIntbyte[1];
        cmd[4] = latIntbyte[2];
        cmd[5] = latIntbyte[3];

        double lon = kaabaSetting.getLon();
        int lonInt = (int) (lon * 100000);
        byte[] lonIntbyte = getByteInt(lonInt);
        cmd[6] = lonIntbyte[0];
        cmd[7] = lonIntbyte[1];
        cmd[8] = lonIntbyte[2];
        cmd[9] = lonIntbyte[3];


        short altitude = kaabaSetting.getAltitude();
        byte[] altitudeByteShort = getByteShort(altitude);
        cmd[10] = altitudeByteShort[0];
        cmd[11] = altitudeByteShort[1];

        return cmd;
    }

}
