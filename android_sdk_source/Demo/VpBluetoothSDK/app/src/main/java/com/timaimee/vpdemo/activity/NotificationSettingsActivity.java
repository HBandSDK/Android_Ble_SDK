package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.adapter.GridAdatper;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IG15MessageListener;
import com.veepoo.protocol.listener.data.ISocialMsgDataListener;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.enums.ESocailMsg;
import com.veepoo.protocol.model.settings.ContentPhoneSetting;
import com.veepoo.protocol.model.settings.ContentSetting;
import com.veepoo.protocol.model.settings.ContentSmsSetting;
import com.veepoo.protocol.model.settings.ContentSocailSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: YWX
 * Date: 2021/12/7 15:43
 * Description: 通知消息设置
 */
public class NotificationSettingsActivity extends Activity {
    public static final String TAG = NotificationSettingsActivity.class.getSimpleName();

    GridView mGridView;
    EditText mInput;
    NotificationSettingsAdapter mAdapter;
    List<NotificationFunction> functions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        mGridView = findViewById(R.id.gvNotification);
        mInput = findViewById(R.id.etInput);
        VPOperateManager.getInstance().readSocialMsg(new IBleWriteResponse() {
            @Override
            public void onResponse(int code) {

            }
        }, new ISocialMsgDataListener() {
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData socailMsgData) {
                String message = " 社交信息提醒1-读取:\n" + socailMsgData.toString();
                Logger.t(TAG).i(message);
                functions = getNotificationFunctionList(socailMsgData);
                initGridView();
            }

            @Override
            public void onSocialMsgSupportDataChange2(FunctionSocailMsgData socailMsgData) {
                String message = " 社交信息提醒2-读取:\n" + socailMsgData.toString();
                Logger.t(TAG).i(message);
                functions = getNotificationFunctionList(socailMsgData);
                initGridView();
            }
        });
    }

    private void initGridView() {
        mAdapter = new NotificationSettingsAdapter(functions);
        mGridView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mGridView.getContext(), functions.get(position).toString(), Toast.LENGTH_SHORT).show();
                NotificationFunction function = functions.get(position);
                String msg = mInput.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    msg = "君子和而不同,小人同而不和";
                }

                if(function.type == ESocailMsg.G15MSG) {
                    testG15(msg);
                } else {
                    if (function.status == EFunctionStatus.SUPPORT_OPEN) {
                        ContentSetting contentSetting;
                        if (function.type == ESocailMsg.PHONE) {
                            contentSetting = new ContentPhoneSetting(ESocailMsg.PHONE, function.label, "010-6635214");
                        } else if (function.type == ESocailMsg.SMS) {
                            contentSetting = new ContentSmsSetting(ESocailMsg.SMS, function.label, "010-6635214", msg);
                        } else {
                            contentSetting = new ContentSocailSetting(function.type, function.label, msg);
                        }
                        VPOperateManager.getInstance().sendSocialMsgContent(new OperaterActivity.WriteResponse(), contentSetting);
                    } else {
                        Toast.makeText(mGridView.getContext(), "设备端消息开关好像没有打开！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void testG15(String msg) {

        VPOperateManager.getMangerInstance(this).sendG15MsgContent(new OperaterActivity.WriteResponse(), "G15", msg, new IG15MessageListener() {
            @Override
            public void onG15MessageSendSuccess() {
                Toast.makeText(NotificationSettingsActivity.this, "收到应答", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onG15MessageSendFailed() {
                Toast.makeText(NotificationSettingsActivity.this , "没有收到应答", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<NotificationFunction> getNotificationFunctionList(FunctionSocailMsgData socailMsgData) {
        List<NotificationFunction> functions = new ArrayList<>();
        functions.add(new NotificationFunction(ESocailMsg.WECHAT, socailMsgData.getWechat(), "微信"));
        functions.add(new NotificationFunction(ESocailMsg.QQ, socailMsgData.getQq(), "QQ"));
        functions.add(new NotificationFunction(ESocailMsg.DINGDING, socailMsgData.getDingding(), "钉钉"));
        functions.add(new NotificationFunction(ESocailMsg.SINA, socailMsgData.getSina(), "新浪"));
        functions.add(new NotificationFunction(ESocailMsg.FACEBOOK, socailMsgData.getFacebook(), "非死不可"));

        functions.add(new NotificationFunction(ESocailMsg.TWITTER, socailMsgData.getTwitter(), "X(原推特)"));
        functions.add(new NotificationFunction(ESocailMsg.TIKTOK, socailMsgData.getTikTok(), "TikTok"));
        functions.add(new NotificationFunction(ESocailMsg.FLICKR, socailMsgData.getFlickr(), "Flickr"));
        functions.add(new NotificationFunction(ESocailMsg.TELEGRAM, socailMsgData.getTelegram(), "Telegram"));
        functions.add(new NotificationFunction(ESocailMsg.GMAIL, socailMsgData.getGmail(), "Gmail"));

        functions.add(new NotificationFunction(ESocailMsg.INSTAGRAM, socailMsgData.getInstagram(), "Instagram"));
        functions.add(new NotificationFunction(ESocailMsg.PHONE, socailMsgData.getPhone(), "电话"));
        functions.add(new NotificationFunction(ESocailMsg.SMS, socailMsgData.getMsg(), "短信"));
        functions.add(new NotificationFunction(ESocailMsg.MESSENGER, socailMsgData.getMessenger(), "MESSENGER"));
        functions.add(new NotificationFunction(ESocailMsg.WXWORK, socailMsgData.getWxWork(), "企业微信"));
        functions.add(new NotificationFunction(ESocailMsg.KAKAO_TALK, socailMsgData.getKakaoTalk(), "Kakao Talk"));

        functions.add(new NotificationFunction(ESocailMsg.LINKIN, socailMsgData.getLinkin(), "Linkin"));
        functions.add(new NotificationFunction(ESocailMsg.LINE, socailMsgData.getLine(), "Line"));
        functions.add(new NotificationFunction(ESocailMsg.CONNECTED2_ME, socailMsgData.getConnected2_me(), "Connected2me"));
        functions.add(new NotificationFunction(ESocailMsg.SKYPE, socailMsgData.getSkype(), "Skype"));
        functions.add(new NotificationFunction(ESocailMsg.SNAPCHAT, socailMsgData.getSnapchat(), "Snapchat"));

        functions.add(new NotificationFunction(ESocailMsg.SHIELD_POLICE, socailMsgData.getShieldPolice(), "警右"));
        functions.add(new NotificationFunction(ESocailMsg.WHATS, socailMsgData.getWhats(), "Whats"));
        functions.add(new NotificationFunction(ESocailMsg.G15MSG, socailMsgData.getWhats(), "G-15"));
        return functions;
    }


    static class NotificationFunction {
        public EFunctionStatus status;
        public String label;
        public ESocailMsg type;

        public NotificationFunction(ESocailMsg type, EFunctionStatus status, String label) {
            this.type = type;
            this.status = status;
            this.label = label;
        }

        @Override
        public String toString() {
            return "NotificationFunction{" +
                    "status=" + status +
                    ", label='" + label + '\'' +
                    ", type=" + type +
                    '}';
        }
    }

    public class NotificationSettingsAdapter extends BaseAdapter {

        List<NotificationFunction> functions;
        LayoutInflater mLayoutInflater;

        public NotificationSettingsAdapter(List<NotificationFunction> functions) {
            this.functions = functions;
            mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return functions == null ? 0 : functions.size();
        }

        @Override
        public Object getItem(int position) {
            return functions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            NotificationSettingsAdapter.GridHold mGridHold;
            if (null == convertView) {
                convertView = mLayoutInflater.inflate(R.layout.item_notify, null);
                mGridHold = new GridHold();
                mGridHold.mButton = (TextView) convertView.findViewById(R.id.gridbutton);
                convertView.setTag(mGridHold);
            } else {
                mGridHold = (GridHold) convertView.getTag();
            }
            NotificationFunction function = functions.get(i);
            mGridHold.mButton.setEnabled(function.status == EFunctionStatus.SUPPORT_OPEN || function.status == EFunctionStatus.SUPPORT);
            mGridHold.mButton.setText(function.label);
//            mGridHold.mButton.setEnabled(function.status.);
            return convertView;
        }


        class GridHold {
            TextView mButton;
        }
    }
}
