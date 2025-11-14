package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.timaimee.vpdemo.R;
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
import com.veepoo.protocol.util.thread.HBThreadPools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Locale;

public class JH58PPGOptTestActivity extends Activity implements View.OnClickListener {

    TimePickerDialog timePickerDialog = null;
    DatePickerDialog datePickerDialog = null;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jh58ppg_test);
        initView();
        initData();
    }

    private void initView() {
        btnStartRealTimePPGRawDataTransfer = findViewById(R.id.btnStartRealTimePPGRawDataTransfer);
        btnStopRealTimePPGRawDataTransfer = findViewById(R.id.btnStopRealTimePPGRawDataTransfer);
        btnReadPPGTestStatus = findViewById(R.id.btnReadPPGTestStatus);
        btnReadPPGRawData = findViewById(R.id.btnReadPPGRawData);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnTimePicker = findViewById(R.id.btnTimePicker);
        btnShareData = findViewById(R.id.btnShareData);
        tvPPGTestStatus = findViewById(R.id.tvPPGTestStatus);
        tvPPGOptInfo = findViewById(R.id.tvPPGOptInfo);
        rgPPGTestMode = findViewById(R.id.rgPPGTestMode);
        rgReadPPGTestMode = findViewById(R.id.rgReadPPGTestMode);
        svInfo = findViewById(R.id.svInfo);
    }

    private void initData() {
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
        //读取PPG原生数据时的模式
        rgReadPPGTestMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbReadMode1On) {
                ppgTestMode = PPGTestMode.MODE1;
            }
            if (checkedId == R.id.rbReadMode2On) {
                ppgTestMode = PPGTestMode.MODE2;
            }
        });
        //设置测量模式
        rgPPGTestMode.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rbAllOff:
                    ppgSwitchStatus = PPGSwitchStatus.ALL_OFF;
                    break;
                case R.id.rbMode1On:
                    ppgSwitchStatus = PPGSwitchStatus.MODE1_ON;
                    break;
                case R.id.rbMode2On:
                    ppgSwitchStatus = PPGSwitchStatus.MODE2_ON;
                    break;
            }
            /*设置PPG开关状态*/
            VPOperateManager.getInstance().setPPGSwitchStatus(ppgSwitchStatus, code -> tvPPGOptInfo.setText("设置PPG测量开关状态指令发送" + (code == Code.REQUEST_SUCCESS ? "成功" : "失败")));
        });
        readPPGTestStatus();
        /*添加PPG测量状态监听*/
        VPOperateManager.getInstance().addPPGTestSwitchStatusListener(new IPPGSwitchOperaterListener() {
            @Override
            public void onPPGSwitchStatusRead(@NonNull PPGSwitchStatus switchStatus) {
                tvPPGTestStatus.setText(switchStatus.getDes());
                if (switchStatus != PPGSwitchStatus.ALL_OFF) {
                    ppgSwitchStatus = switchStatus;
                } /*else if(switchStatus == PPGSwitchStatus.MODE1_ON) {
                    rgReadPPGTestMode.check(R.id.rbReadMode1On);
                } else if(switchStatus == PPGSwitchStatus.MODE2_ON) {
                    rgReadPPGTestMode.check(R.id.rbReadMode2On);
                }*/
            }

            @Override
            public void onPPGSwitchStatusSetting(@NonNull PPGSwitchStatus switchStatus) {
                tvPPGOptInfo.setText("PPG测量模式开关设置成功：" + switchStatus);
            }

            @Override
            public void onPPGSwitchStatusReport(@NonNull PPGSwitchStatus switchStatus) {
                tvPPGOptInfo.setText("PPG测量模式开关上报：" + switchStatus);
            }
        });
        /*添加设备PPG高频实时传输监听*/
        VPOperateManager.getInstance().addDevicePPGRealTimeTransferListener(new IPPGRealTimeTransmissionListener() {

            @Override
            public void onDeviceRequestPPGRealTimeTransfer(boolean isRequestOpen) {
                appendMsg("设备请求PPG实时传输：" + (isRequestOpen ? "【开启】" : "【关闭】"));
            }

            @Override
            public void onAppRequestPPGRealTimeTransfer(boolean isSuccess) {
                appendMsg("App请求PPG实时传输：" + (isSuccess ? "成功" : "失败"));
            }

            @Override
            public void onGreenLightDataReport(@NonNull List<Integer> greenLightDataList) {
                appendMsg("收到绿光信号>>> " + greenLightDataList);
            }

            @Override
            public void onAccelerationDataReport(@NonNull List<AccelerationData> accDataList) {
                appendMsg("收到加速度信号>>> " + accDataList);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDatePicker: {
                if (datePickerDialog != null) {
                    datePickerDialog.dismiss();
                    datePickerDialog = null;
                }
                datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> btnDatePicker.setText(String.format(Locale.CHINA, "%04d-%02d-%02d", year, month + 1, dayOfMonth)), TimeData.getSystemYear(), TimeData.getSystemMonth(), TimeData.getSystemDay());
                datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        btnDatePicker.setText(String.format(Locale.CHINA, "%04d-%02d-%02d", year, month + 1, dayOfMonth));
                        timeData.setYear(year);
                        timeData.setMonth(month + 1);
                        timeData.setDay(dayOfMonth);
                    }
                }, TimeData.getSystemYear(), TimeData.getSystemMonth(), TimeData.getSystemDay());
                datePickerDialog.show();
                break;
            }
            case R.id.btnTimePicker: {
                if (timePickerDialog != null) {
                    timePickerDialog.dismiss();
                    timePickerDialog = null;
                }
                timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> btnTimePicker.setText(String.format(Locale.CHINA, "%02d:%02d:00", hourOfDay, minute)),
                        timeData.hour, timeData.minute, true);
                timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        btnTimePicker.setText(String.format(Locale.CHINA, "%02d:%02d:00", hourOfDay, minute));
                        timeData.setHour(hourOfDay);
                        timeData.setMinute(hourOfDay);
                        timeData.setSecond(0);
                    }
                }, TimeData.getSystemHour(), TimeData.getSystemMinute(), true);
                timePickerDialog.show();
                break;
            }
            case R.id.btnReadPPGRawData: {
                readPPGRawData();
                break;
            }
            case R.id.btnShareData: {
                shareData();
                break;
            }
            case R.id.btnReadPPGTestStatus: {
                readPPGTestStatus();
                break;
            }
            case R.id.btnStartRealTimePPGRawDataTransfer: {
                startPPGRawDataRealTimeTransfer();
                break;
            }
            case R.id.btnStopRealTimePPGRawDataTransfer: {
                stopPPGRawDataRealTimeTransfer();
                break;
            }
        }
    }

    private void readPPGTestStatus() {
        VPOperateManager.getInstance().readPPGSwitchStatus(code -> tvPPGOptInfo.setText("读取PPG测量开关状态指令发送" + (code == Code.REQUEST_SUCCESS ? "成功" : "失败")));
    }

    private PPGReadData ppgReadData = null;

    private void readPPGRawData() {
        sb.setLength(0);
        appendMsg("【读取】PPG原始数据~");
        appendMsg("时间:" + timeData.toFullDateTimeString() + " , 模式:" + ppgTestMode);
        VPOperateManager.getInstance().readPPGRawData(timeData, ppgTestMode, new BleWriteResponse() {
            @Override
            public void onResponse(int code) {
                appendMsg("~【读取PPG测量开关状态指令发送" + (code == Code.REQUEST_SUCCESS ? "成功】" : "失败】"));
            }
        }, new IPPGRawDataReadListener() {
            @Override
            public void onPPGReadStart(int count) {
                appendMsg("开始读取PPG原始数据。\n一共" + count + "组数据");
            }

            @Override
            public void onPPGRawDataRead(int index, int count, @NonNull PPGRawData ppgRawData) {
                appendMsg("PPG原始数据读取中。\n>>>>>>>>>【" + index + "/" + count + "】 --> " + ppgRawData.toString());
            }

            @Override
            public void onPPGRawDataReadComplete(@NonNull PPGReadData ppgReadData) {
                appendMsg("PPG原始数据读取完成。\n" + ppgReadData);
                JH58PPGOptTestActivity.this.ppgReadData = ppgReadData;
            }

            @Override
            public void onPPGRawDataReadStop() {
                String content = tvPPGOptInfo.getText().toString();
                appendMsg(content + "\nPPG原始数据读取停止。");
            }
        });
    }

    private void startPPGRawDataRealTimeTransfer() {
        sb.setLength(0);
        appendMsg("APP请求【开始】PPG实时传输");
        VPOperateManager.getInstance().startPPGRealTimeTransmission(code -> appendMsg("【开始】PPG实时传输指令发送" + (code == Code.REQUEST_SUCCESS ? "成功" : "失败")), new IPPGRealTimeTransmissionListener() {
            @Override
            public void onDeviceRequestPPGRealTimeTransfer(boolean isRequestOpen) {
                appendMsg("设备请求PPG实时传输：" + (isRequestOpen ? "【开启】" : "【关闭】"));
            }

            @Override
            public void onAppRequestPPGRealTimeTransfer(boolean isSuccess) {
                appendMsg("App请求PPG实时传输：" + (isSuccess ? "成功" : "失败"));
            }

            @Override
            public void onGreenLightDataReport(@NonNull List<Integer> greenLightDataList) {
                appendMsg("收到绿光信号>>> " + greenLightDataList);
            }

            @Override
            public void onAccelerationDataReport(@NonNull List<AccelerationData> accDataList) {
                appendMsg("收到加速度信号>>> " + accDataList);
            }
        }/*, new IPPGRealTimeTransferOptListener() {
            @Override
            public void onDeviceRequestPPGRealTimeTransfer(boolean isRequestOpen) {
                tvPPGOptInfo.setText("start，设备请求PPG实时传输：" + (isRequestOpen ? "【开启】" : "【关闭】"));
            }

            @Override
            public void onAppRequestPPGRealTimeTransfer(boolean isSuccess) {
                tvPPGOptInfo.setText("start，App请求PPG实时传输：" + (isSuccess ? "成功" : "失败"));
            }
        }*/);
    }

    private void stopPPGRawDataRealTimeTransfer() {
        sb.setLength(0);
        appendMsg("APP请求【停止】PPG实时传输");
        VPOperateManager.getInstance().stopPPGRealTimeTransmission(code -> appendMsg("【停止】PPG实时传输指令发送" + (code == Code.REQUEST_SUCCESS ? "成功" : "失败"))/*, new IPPGRealTimeTransferOptListener() {
            @Override
            public void onDeviceRequestPPGRealTimeTransfer(boolean isRequestOpen) {
                tvPPGOptInfo.setText("stop，设备请求PPG实时传输：" + (isRequestOpen ? "【开启】" : "【关闭】"));
            }

            @Override
            public void onAppRequestPPGRealTimeTransfer(boolean isSuccess) {
                tvPPGOptInfo.setText("stop，App请求PPG实时传输：" + (isSuccess ? "成功" : "失败"));
            }
        }*/);
    }

    private void shareData(){
        if (ppgReadData == null) {
            Toast.makeText(this, "暂无读取数据可以分享", Toast.LENGTH_SHORT).show();
            return;
        }
        String fileName = "JH58PPG原生数据读取.txt";
        HBThreadPools.getInstance().execute(() -> {
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
                    Log.e("FileSave", "无法获取外部存储目录，请检查设备状态或存储是否可用。");
                    return;
                }

                File targetFile = new File(externalFilesDir, fileName);

                shareTxtContent(JH58PPGOptTestActivity.this, targetFile.getAbsolutePath(), "分享PPG读取的原始数据");
            });
        });

    }


    /**
     * 读取 TXT 文件内容并直接分享文本（不分享文件本身）
     * @param context 上下文
     * @param filePath 文件路径
     * @param chooserTitle 分享选择器标题
     */
    public void shareTxtContent(Context context, String filePath, String chooserTitle) {
        File file = new File(filePath);

        if (!file.exists()) {
            Toast.makeText(context, "文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = getUri(context, filePath);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/txt");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(shareIntent, chooserTitle));
    }

    private Uri getUri(Context context, String filePath) {
        Uri dfuFileUri;
        if (Build.VERSION.SDK_INT >= 24) {
            dfuFileUri = FileProvider.getUriForFile(
                    context,
                    "com.timaimee.vpdemo.fileProvider",
                    new File(filePath));
        } else {
            dfuFileUri = Uri.fromFile(new File(filePath));
        }
        return dfuFileUri;
    }

    // 追加写入版本
    public static void appendTextToExternalFilesDir(Context context, String filename, String textContent) {
        File externalFilesDir = context.getExternalFilesDir(null);

        if (externalFilesDir == null) {
            Log.e("FileSave", "无法获取外部存储目录，请检查设备状态或存储是否可用。");
            return;
        }

        File targetFile = new File(externalFilesDir, filename);
//        if (targetFile.exists()) {
//            targetFile.delete();
//        }
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;

        try {
            // 使用 FileOutputStream 的追加模式
            fos = new FileOutputStream(targetFile, false);
            osw = new OutputStreamWriter(fos);

            osw.write(textContent);
            osw.flush();

            Log.i("FileSave", "文本已成功追加到: " + targetFile.getAbsolutePath());

        } catch (IOException e) {
            Log.e("FileSave", "追加文件失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (osw != null) {
                    osw.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                Log.e("FileSave", "关闭流时发生错误: " + e.getMessage());
                e.printStackTrace();
            }
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
