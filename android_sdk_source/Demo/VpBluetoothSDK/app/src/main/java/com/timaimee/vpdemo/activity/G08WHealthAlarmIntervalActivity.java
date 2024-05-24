package com.timaimee.vpdemo.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IHealthAlarmIntervalListener;
import com.veepoo.protocol.model.datas.HealthAlarmInterval;
import com.veepoo.protocol.model.enums.EHealthAlarmType;

public class G08WHealthAlarmIntervalActivity extends AppCompatActivity {

    private static final String TAG = "G08WHealthAlarmInterval";

    Spinner spinner, spinner1;
    EditText etSX, etXX;
    ToggleButton tbSwitch;
    Button btnSetting, btnRead;
    TextView tvHealthAlarmIntervalInfo;
    String[] data = {"心率报警", "血压报警", "体温过高报警", "血氧过低报警"};
    HealthAlarmInterval healthAlarmInterval;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g08w_health_alarm_interval);
        spinner = findViewById(R.id.spHealthType);
        spinner1 = findViewById(R.id.spHealthType1);
        etSX = findViewById(R.id.etSX);
        etXX = findViewById(R.id.etXX);
        tbSwitch = findViewById(R.id.tgSwitch);
        btnSetting = findViewById(R.id.btnSetting);
        btnRead = findViewById(R.id.btnRead);
        tvHealthAlarmIntervalInfo = findViewById(R.id.tvHealthAlarmIntervalInfo);
        initSpinner(spinner);
        initSpinner(spinner1);
        btnSetting.setOnClickListener(v -> setHealthAlarmInterval2Device());
        btnRead.setOnClickListener(v -> readHealthAlarmIntervalFDevice());
    }

    private void initSpinner(Spinner sp) {
        //声明一个下拉列表的数组适配器
        ArrayAdapter<String> starAdapter = new ArrayAdapter<String>(this, R.layout.item_select, data);
        //设置数组适配器的布局样式
        starAdapter.setDropDownViewResource(R.layout.item_dropdown);
        //从布局文件中获取名叫sp_dialog的下拉框
        //设置下拉框的标题，不设置就没有难看的标题了
        sp.setPrompt("请选择行星");
        //设置下拉框的数组适配器
        sp.setAdapter(starAdapter);
        //设置下拉框默认的显示第一项
        sp.setSelection(0);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        sp.setOnItemSelectedListener(new MySelectedListener(sp));
    }

    private HealthAlarmInterval getHealthAlarmInterval() {
        int selectIndex = spinner.getSelectedItemPosition();
        EHealthAlarmType eHealthAlarmType = EHealthAlarmType.Companion.getEHealthAlarmTypeWithCMD((byte) selectIndex);
        float ceilingValue = 0;
        float floorValue = 0;
        String sxStr = etSX.getText().toString();
        String xxStr = etXX.getText().toString();
        if (!TextUtils.isEmpty(sxStr)) {
            ceilingValue = Float.parseFloat(sxStr);
        }
        if (!TextUtils.isEmpty(xxStr)) {
            floorValue = Float.parseFloat(xxStr);
        }
        boolean isOpen = tbSwitch.isChecked();
        if (selectIndex == 3) {
            //无上限
            ceilingValue = 0;
        }
        return new HealthAlarmInterval(eHealthAlarmType, ceilingValue, floorValue, isOpen);
    }

    public void setHealthAlarmInterval2Device() {
        HealthAlarmInterval health = getHealthAlarmInterval();
        VPOperateManager.getInstance().setHealthAlarmInterval(health, new IBleWriteResponse() {
            @Override
            public void onResponse(int code) {

            }
        }, new IHealthAlarmIntervalListener() {
            @Override
            public void functionNotSupport() {

            }

            @Override
            public void onHealthAlarmIntervalReadSuccess(@NonNull HealthAlarmInterval data, boolean isOptSuccess) {

            }

            @Override
            public void onHealthAlarmIntervalSetting(@NonNull HealthAlarmInterval data, boolean isOptSuccess) {
                if (isOptSuccess) {
                    Logger.t(TAG).i("设置成功-" + data.toString());
                    tvHealthAlarmIntervalInfo.setText("设置成功-" + data.toString());
                } else {
                    Logger.t(TAG).i("设置失败-" + data.toString());
                    tvHealthAlarmIntervalInfo.setText("设置失败-" + data.toString());
                }
            }
        });
    }

    public void readHealthAlarmIntervalFDevice() {
        int selectIndex = spinner1.getSelectedItemPosition();
        EHealthAlarmType eHealthAlarmType = EHealthAlarmType.Companion.getEHealthAlarmTypeWithCMD((byte) selectIndex);
        VPOperateManager.getInstance().readHealthAlarmInterval(eHealthAlarmType, new IBleWriteResponse() {
            @Override
            public void onResponse(int code) {

            }
        }, new IHealthAlarmIntervalListener() {
            @Override
            public void functionNotSupport() {

            }

            @Override
            public void onHealthAlarmIntervalReadSuccess(@NonNull HealthAlarmInterval data, boolean isOptSuccess) {
                if (isOptSuccess) {
                    Logger.t(TAG).i("读取成功-" + data.toString());
                    tvHealthAlarmIntervalInfo.setText("读取成功-" + data.toString());
                } else {
                    Logger.t(TAG).i("读取失败-" + data.toString());
                    tvHealthAlarmIntervalInfo.setText("读取失败-" + data.toString());
                }
            }

            @Override
            public void onHealthAlarmIntervalSetting(@NonNull HealthAlarmInterval data, boolean isOptSuccess) {

            }
        });
    }


    class MySelectedListener implements AdapterView.OnItemSelectedListener {

        Spinner sp;

        MySelectedListener(Spinner spinner) {
            this.sp = spinner;
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (sp == spinner) {
                Toast.makeText(G08WHealthAlarmIntervalActivity.this, "您选择的设置是：" + data[i], Toast.LENGTH_SHORT).show();
                if (i == 2) {
                    etSX.setText("");
                    etSX.setHint("请输入高温报警上限值");
                    etSX.setHintTextColor(Color.GRAY);
                    etSX.setEnabled(true);
                    etSX.setFocusable(true);

                    etXX.setText("");
                    etXX.setEnabled(false);
                    etXX.setHint("高温报警下限值无需设置");
                    etXX.setHintTextColor(Color.RED);

                } else if (i == 3) {
                    etSX.setText("");
                    etSX.setHint("低氧过低报警上限值无需设置");
                    etSX.setHintTextColor(Color.RED);
                    etSX.setEnabled(false);

                    etXX.setEnabled(true);
                    etXX.setHint("请输入低氧过低报警下限值");
                    etXX.setHintTextColor(Color.GRAY);
                    etXX.setFocusable(true);
                } else {
                    etSX.setFocusable(true);
                    etSX.setEnabled(true);
                    etXX.setEnabled(true);
                    etSX.setText("");
                    etXX.setText("");
                    etSX.setHint("");
                    etXX.setHint("");
                }


            }
            if (sp == spinner1) {
                Toast.makeText(G08WHealthAlarmIntervalActivity.this, "您选择的读取是：" + data[i], Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

}
