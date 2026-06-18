package com.timaimee.vpdemo.activity.v2.custom;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.FileProvider;

import com.inuker.bluetooth.library.Code;
import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.data.IPPGRawDataReadListener;
import com.veepoo.protocol.listener.data.IPPGRealTimeTransmissionListener;
import com.veepoo.protocol.listener.data.IPPGSwitchOperaterListener;
import com.veepoo.protocol.model.datas.AccelerationData;
import com.veepoo.protocol.model.datas.PPGRawData;
import com.veepoo.protocol.model.datas.PPGReadData;
import com.veepoo.protocol.model.datas.PPGSecondData;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.model.enums.PPGSwitchStatus;
import com.veepoo.protocol.model.enums.PPGTestMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Locale;

public class JH58PPGOptTestActivity extends BaseVPBLETestActivity implements View.OnClickListener {

    Button btnReadPPGTestStatus;
    Button btnStartRealTimePPGRawDataTransfer;
    Button btnStopRealTimePPGRawDataTransfer;
    Button btnReadPPGRawData;
    Button btnDatePicker;
    Button btnTimePicker;
    Button btnShareData;
    TextView tvPPGTestStatus;
    TextView tvPPGOptInfo;

    RadioGroup rgPPGTestMode;
    RadioGroup rgReadPPGTestMode;
    ScrollView svInfo;

    TimeData timeData = null;

    PPGSwitchStatus ppgSwitchStatus = PPGSwitchStatus.MODE1_ON;
    PPGTestMode ppgTestMode = PPGTestMode.MODE1;

    StringBuilder sb = new StringBuilder();

    @Override
    public int getLayoutID() {
        return R.layout.activity_jh58ppg_test;
    }

    @Override
    public String pageTitle() {
        return "JH58PPG测试";
    }

    @Override
    public void initView() {
        btnStartRealTimePPGRawDataTransfer = findViewById(R.id.btnStartRealTimePPGRawDataTransfer);
        btnStopRealTimePPGRawDataTransfer = findViewById(R.id.btnStopRealTimePPGRawDataTransfer);
        btnReadPPGTestStatus = findViewById(R.id.tvAutoMeasureType);
        btnReadPPGRawData = findViewById(R.id.btnReadPPGRawData);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnTimePicker = findViewById(R.id.btnTimePicker);
        btnShareData = findViewById(R.id.btnShareData);
        tvPPGTestStatus = findViewById(R.id.tvAutoMeasureStatusTitle);
        tvPPGOptInfo = findViewById(R.id.tvPPGOptInfo);
        rgPPGTestMode = findViewById(R.id.rgPPGTestMode);
        rgReadPPGTestMode = findViewById(R.id.rgReadPPGTestMode);
        svInfo = findViewById(R.id.svInfo);
    }

    @Override
    public void initData() {
        rgReadPPGTestMode.check(R.id.rbReadMode1On);
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        btnReadPPGTestStatus.setOnClickListener(this);
        btnReadPPGRawData.setOnClickListener(this);
        btnShareData.setOnClickListener(this);
        btnStartRealTimePPGRawDataTransfer.setOnClickListener(this);
        btnStopRealTimePPGRawDataTransfer.setOnClickListener(this);

        timeData = new TimeData();
        timeData.setCurrentTime();
        timeData.setHour(0);
        timeData.setMinute(0);
        timeData.setSecond(0);

        btnDatePicker.setText(timeData.toDatabaseDateString());
        btnTimePicker.setText(timeData.getClock() + ":00");

        rgReadPPGTestMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbReadMode1On) {
                ppgTestMode = PPGTestMode.MODE1;
            } else if (checkedId == R.id.rbReadMode2On) {
                ppgTestMode = PPGTestMode.MODE2;
            }
        });

        rgPPGTestMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbAllOff) {
                ppgSwitchStatus = PPGSwitchStatus.ALL_OFF;
            } else if (checkedId == R.id.rbMode1On) {
                ppgSwitchStatus = PPGSwitchStatus.MODE1_ON;
            } else if (checkedId == R.id.rbMode2On) {
                ppgSwitchStatus = PPGSwitchStatus.MODE2_ON;
            }
            vpBleManager.setPPGSwitchStatus(ppgSwitchStatus,
                    code -> tvPPGOptInfo.setText("设置PPG测量开关状态指令发送" + (code == Code.REQUEST_SUCCESS ? "成功" : "失败")));
        });

        readPPGTestStatus();

        vpBleManager.addPPGTestSwitchStatusListener(new IPPGSwitchOperaterListener() {
            @Override
            public void onPPGSwitchStatusRead(PPGSwitchStatus switchStatus) {
                tvPPGTestStatus.setText(switchStatus.getDes());
                if (switchStatus != PPGSwitchStatus.ALL_OFF) {
                    ppgSwitchStatus = switchStatus;
                }
            }

            @Override
            public void onPPGSwitchStatusSetting(PPGSwitchStatus switchStatus) {
                tvPPGOptInfo.setText("PPG测量模式开关设置成功：" + switchStatus);
            }

            @Override
            public void onPPGSwitchStatusReport(PPGSwitchStatus switchStatus) {
                tvPPGOptInfo.setText("PPG测量模式开关上报：" + switchStatus);
            }
        });

        vpBleManager.addDevicePPGRealTimeTransferListener(new IPPGRealTimeTransmissionListener() {
            @Override
            public void onDeviceRequestPPGRealTimeTransfer(boolean isRequestOpen) {
                appendMsg("设备请求PPG实时传输：" + (isRequestOpen ? "【开启】" : "【关闭】"));
            }

            @Override
            public void onAppRequestPPGRealTimeTransfer(boolean isSuccess) {
                appendMsg("App请求PPG实时传输：" + (isSuccess ? "成功" : "失败"));
            }

            @Override
            public void onGreenLightDataReport(List<Integer> greenLightDataList) {
                appendMsg("收到绿光信号>>> " + greenLightDataList);
            }

            @Override
            public void onAccelerationDataReport(List<AccelerationData> accDataList) {
                appendMsg("收到加速度信号>>> " + accDataList);
            }
        });
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnDatePicker) {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                timeData.setYear(year);
                timeData.setMonth(month + 1);
                timeData.setDay(dayOfMonth);
                btnDatePicker.setText(String.format(Locale.CHINA, "%04d-%02d-%02d", year, month + 1, dayOfMonth));
            }, TimeData.getSystemYear(), TimeData.getSystemMonth(), TimeData.getSystemDay()).show();
        } else if (id == R.id.btnTimePicker) {
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                timeData.setHour(hourOfDay);
                timeData.setMinute(minute);
                timeData.setSecond(0);
                btnTimePicker.setText(String.format(Locale.CHINA, "%02d:%02d:00", hourOfDay, minute));
            }, timeData.getHour(), timeData.getMinute(), true).show();
        } else if (id == R.id.btnReadPPGRawData) {
            readPPGRawData();
        } else if (id == R.id.btnShareData) {
            shareData();
        } else if (id == R.id.tvAutoMeasureType) {
            readPPGTestStatus();
        } else if (id == R.id.btnStartRealTimePPGRawDataTransfer) {
            startPPGRawDataRealTimeTransfer();
        } else if (id == R.id.btnStopRealTimePPGRawDataTransfer) {
            stopPPGRawDataRealTimeTransfer();
        }
    }

    private void readPPGTestStatus() {
        vpBleManager.readPPGSwitchStatus(
                code -> tvPPGOptInfo.setText("读取PPG测量开关状态指令发送" + (code == Code.REQUEST_SUCCESS ? "成功" : "失败")));
    }

    private PPGReadData ppgReadData = null;

    private void readPPGRawData() {
        sb.setLength(0);
        appendMsg("【读取】PPG原始数据~");
        appendMsg("时间:" + timeData.toFullDateTimeString() + " , 模式:" + ppgTestMode);

        vpBleManager.readPPGRawData(timeData, ppgTestMode,
                code -> appendMsg("~【读取PPG测量开关状态指令发送" + (code == Code.REQUEST_SUCCESS ? "成功】" : "失败】")),
                new IPPGRawDataReadListener() {
                    @Override
                    public void onPPGReadStart(int count) {
                        appendMsg("开始读取PPG原始数据。\n一共" + count + "组数据");
                    }

                    @Override
                    public void onPPGRawDataRead(int index, int count, PPGRawData ppgRawData) {
                        appendMsg("PPG原始数据读取中。\n>>>>>>>>>【" + index + "/" + count + "】 --> " + ppgRawData);
                    }

                    @Override
                    public void onPPGRawDataReadComplete(PPGReadData readData) {
                        appendMsg("PPG原始数据读取完成。\n" + readData);
                        ppgReadData = readData;
                    }

                    @Override
                    public void onPPGRawDataReadStop() {
                        appendMsg("PPG原始数据读取停止。");
                    }
                });
    }

    private void startPPGRawDataRealTimeTransfer() {
        sb.setLength(0);
        appendMsg("APP请求【开始】PPG实时传输");
        vpBleManager.startPPGRealTimeTransmission(
                code -> appendMsg("【开始】PPG实时传输指令发送" + (code == Code.REQUEST_SUCCESS ? "成功" : "失败")),
                new IPPGRealTimeTransmissionListener() {
                    @Override
                    public void onDeviceRequestPPGRealTimeTransfer(boolean isRequestOpen) {
                        appendMsg("设备请求PPG实时传输：" + (isRequestOpen ? "【开启】" : "【关闭】"));
                    }

                    @Override
                    public void onAppRequestPPGRealTimeTransfer(boolean isSuccess) {
                        appendMsg("App请求PPG实时传输：" + (isSuccess ? "成功" : "失败"));
                    }

                    @Override
                    public void onGreenLightDataReport(List<Integer> greenLightDataList) {
                        appendMsg("收到绿光信号>>> " + greenLightDataList);
                    }

                    @Override
                    public void onAccelerationDataReport(List<AccelerationData> accDataList) {
                        appendMsg("收到加速度信号>>> " + accDataList);
                    }
                }
        );
    }

    private void stopPPGRawDataRealTimeTransfer() {
        sb.setLength(0);
        appendMsg("APP请求【停止】PPG实时传输");
        VPOperateManager.getInstance().stopPPGRealTimeTransmission(
                code -> appendMsg("【停止】PPG实时传输指令发送" + (code == Code.REQUEST_SUCCESS ? "成功" : "失败"))
        );
    }

    private void shareData() {
        if (ppgReadData == null) {
            Toast.makeText(this, "暂无读取数据可以分享", Toast.LENGTH_SHORT).show();
            return;
        }
        String fileName = "JH58PPG原生数据读取.txt";
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();
                sb.append("数据总数:").append(ppgReadData.getDataCount()).append("组\n");
                for (PPGRawData ppgRawData : ppgReadData.getPpgRawDataList()) {
                    sb.append("第【").append(ppgRawData.getIndex()).append("/").append(ppgReadData.getDataCount()).append("】组:")
                            .append(TimeData.getTimeBeanByTimestampSecond((int) ppgRawData.getTimestamp()).toFullDateTimeString())
                            .append(", 数据量=").append(ppgRawData.getCount()).append(",一共").append(ppgRawData.getPpgSecondDataList().size()).append("秒数据。\n");
                    for (PPGSecondData ppgSecondData : ppgRawData.getPpgSecondDataList()) {
                        sb.append(ppgSecondData.toDataStr()).append("\n");
                    }
                }
                appendTextToExternalFilesDir(JH58PPGOptTestActivity.this, fileName, sb.toString());
                runOnUiThread(() -> {
                    File externalFilesDir = getExternalFilesDir(null);
                    if (externalFilesDir == null) {
                        Log.e("FileSave", "无法获取外部存储目录");
                        return;
                    }
                    File targetFile = new File(externalFilesDir, fileName);
                    shareTxtContent(JH58PPGOptTestActivity.this, targetFile.getAbsolutePath(), "分享PPG读取的原始数据");
                });
            }
        }).start();
    }

    public void shareTxtContent(Context context, String filePath, String chooserTitle) {
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(context, "文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = getUri(context, filePath);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, chooserTitle));
    }

    private Uri getUri(Context context, String filePath) {
        if (Build.VERSION.SDK_INT >= 24) {
            return FileProvider.getUriForFile(context, "com.timaimee.vpdemo.fileProvider", new File(filePath));
        } else {
            return Uri.fromFile(new File(filePath));
        }
    }

    public static void appendTextToExternalFilesDir(Context context, String filename, String textContent) {
        File externalFilesDir = context.getExternalFilesDir(null);
        if (externalFilesDir == null) {
            Log.e("FileSave", "无法获取外部存储目录");
            return;
        }
        File targetFile = new File(externalFilesDir, filename);

        try (FileOutputStream fos = new FileOutputStream(targetFile, false);
             OutputStreamWriter osw = new OutputStreamWriter(fos)) {

            osw.write(textContent);
            osw.flush();
            Log.i("FileSave", "保存成功: " + targetFile.getAbsolutePath());

        } catch (IOException e) {
            Log.e("FileSave", "保存失败: " + e.getMessage());
        }
    }

    private void appendMsg(String msg) {
        runOnUiThread(() -> {
            sb.append(msg).append("\n");
            tvPPGOptInfo.setText(sb.toString());
            svInfo.fullScroll(View.FOCUS_DOWN);
        });
    }
}