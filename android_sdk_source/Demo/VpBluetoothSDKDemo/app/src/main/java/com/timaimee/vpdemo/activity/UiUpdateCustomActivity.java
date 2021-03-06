package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.veepoo.protocol.customui.Rect240240WatchUIType;
import com.veepoo.protocol.customui.Rect240280WatchUIType;
import com.veepoo.protocol.customui.Round240240WatchUIType;
import com.veepoo.protocol.customui.WatchUIType;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IUIBaseInfoFormCustomListener;
import com.veepoo.protocol.listener.data.IUiUpdateListener;
import com.veepoo.protocol.model.datas.UICustomSetData;
import com.veepoo.protocol.model.datas.UIDataCustom;
import com.veepoo.protocol.model.enums.EUIFromType;
import com.veepoo.protocol.model.enums.EUiUpdateError;
import com.veepoo.protocol.model.enums.EWatchUIElementPosition;
import com.veepoo.protocol.model.enums.EWatchUIElementType;
import com.veepoo.protocol.model.enums.EWatchUIType;
import com.veepoo.protocol.util.ColorUtil;
import com.veepoo.protocol.util.UiUpdateUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UiUpdateCustomActivity extends Activity {
    private final static String TAG = UiUpdateCustomActivity.class.getSimpleName();
    Context mContext;
    IBleWriteResponse bleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int code) {
            Logger.t(TAG).i("onResponse code:" + code);
        }
    };

    @BindView(R.id.ui_issupport_custom)
    public TextView mUiCustomSupportTV;

    @BindView(R.id.ui_baseinfo_custom)
    public TextView mUiCustomBaseInfoTV;

    @BindView(R.id.custom_use_us)
    public ImageView mImgUseUs;

    @BindView(R.id.defalut_img_bg)
    public ImageView mImgViewBg;

    @BindView(R.id.ui_custom_set_callback)
    public TextView mUiCustomSetCallbackTV;
    @BindView(R.id.ui_custom_progress)
    public TextView mSendProgressTv;

    WatchUIType mWatchUIType;
    UiUpdateUtil mUiUpdateUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uiupdate_custom);
        ButterKnife.bind(this);
        mContext = UiUpdateCustomActivity.this;
        mUiUpdateUtil = new UiUpdateUtil(mContext);

    }

    /**
     * 是否支持
     */
    @OnClick(R.id.is_support_custom_ui)
    public void isSupport() {
        if (mUiUpdateUtil.isSupportChangeCustomUi()) {
            mUiCustomSupportTV.setText("1.支持自定义表盘");
            mUiUpdateUtil.init();
        } else {
            mUiCustomSupportTV.setText("1.不支持自定义表盘");
            Toast.makeText(mContext, "不支持自定义表盘", Toast.LENGTH_LONG).show();
        }
    }

    UIDataCustom mUIDataCustom;

    /**
     * 读取基本的信息(自定义表盘)
     */

    @OnClick(R.id.read_base_info_custom)
    public void readBaseInfo() {
        mUiUpdateUtil.getCustomWacthUiInfo(new IUIBaseInfoFormCustomListener() {
            @Override
            public void onBaseUiInfoFormCustom(UIDataCustom uiDataCustom) {
                Logger.t(TAG).i("2.自定义表盘的基本信息 uiDataCustom:" + uiDataCustom.toString());
                mUIDataCustom = uiDataCustom;
                mUiCustomBaseInfoTV.setText(uiDataCustom.toString());
                EWatchUIType customUIType = mUIDataCustom.getCustomUIType();
                EWatchUIElementPosition timePosition = mUIDataCustom.getTimePosition();
                EWatchUIElementType downTimeType = mUIDataCustom.getDownTimeType();
                EWatchUIElementType upTimeType = mUIDataCustom.getUpTimeType();
                int color888 = mUIDataCustom.getColor888();

                mWatchUIType = getCustomWatchUI(customUIType);
                int defaultImg = mWatchUIType.getDefaultImg();
                int imgTime = mWatchUIType.getElementImg(EWatchUIElementType.TIME, timePosition);
                int imgDownTime = mWatchUIType.getElementImg(downTimeType, timePosition);
                int imgUpTime = mWatchUIType.getElementImg(upTimeType, timePosition);
                mImgViewBg.setImageResource(defaultImg);
            }
        });

    }

    /**
     * 设置自定义表盘（自选位置以及自选元素），使用的是默认背景，可参考H Band的自定义ui界面去理解
     */
    @OnClick(R.id.ui_custom_set)
    public void setCostomUi() {
        if (mWatchUIType == null) {
            return;
        }
        boolean isDefalutUI = true;

        EWatchUIElementPosition timePosition = getRandomPosition(mWatchUIType);
        EWatchUIElementType upTimeType = getRandomElementType();
        EWatchUIElementType downTimeType = getRandomElementType();
        final int fontColor = getRandomColor();
        final UICustomSetData uiCustomSetData = new UICustomSetData(isDefalutUI, timePosition, upTimeType, downTimeType, fontColor);
        Logger.t(TAG).i("uiCustomSetData:" + uiCustomSetData.toString());
        mUiUpdateUtil.setCustomWacthUi(uiCustomSetData, new IUIBaseInfoFormCustomListener() {
            @Override
            public void onBaseUiInfoFormCustom(UIDataCustom uiDataCustom) {
                Logger.t(TAG).i("3.设置元素及其对应的位置 uiDataCustom:" + uiDataCustom.toString());
                int watchColor = uiDataCustom.getColor888();
                String hexColor = ColorUtil.intColorToHexStr(watchColor);
                /**
                 * app上颜色跟设备的颜色虽然是同一个颜色值，但是显示上会有差别
                 * 因为App上可以显示RCG_888,设备只能显示RGB_565
                 * 所以为了显示效果，最好是自己做一个映射表，
                 * 比如app上的A1和设备上的A2颜色相近,那么在app上显示A1颜色，下发给设备的是A2颜色。
                 */
                Logger.t(TAG).i(watchColor + "->" + hexColor);
                mUiCustomSetCallbackTV.setTextColor(Color.parseColor(hexColor));
                mUiCustomSetCallbackTV.setText(uiDataCustom.toString());
            }
        });
    }


    private WatchUIType getCustomWatchUI(EWatchUIType customUIType) {

        WatchUIType customWatchUI = null;
        switch (customUIType) {
            case ROUND_240_240:
                customWatchUI = new Round240240WatchUIType();
                break;
            case RECT_240_240:
                customWatchUI = new Rect240240WatchUIType();
                break;
            case RECT_240_280:
                customWatchUI = new Rect240280WatchUIType();
                break;
            default:
                customWatchUI = new Round240240WatchUIType();
        }
        return customWatchUI;
    }

    /**
     * 获取随机的位置（时间元素放的位置）
     *
     * @return
     */
    private EWatchUIElementPosition getRandomPosition(WatchUIType customWatchUI) {
        //获取表盘支持设置的位置列表
        List<EWatchUIElementPosition> supportPosition = customWatchUI.getSupportPosition();
        for (EWatchUIElementPosition position : supportPosition) {
            Logger.t(TAG).i("supportPosition" + position);
        }

        //所有的表盘可设置的位置一共有7种
        int random = new Random().nextInt(7) + 1;//(1-7)
        EWatchUIElementPosition watchUIElementPosition = EWatchUIElementPosition.getWatchUIElementPosition(random);

        if (customWatchUI.isSupportPosition(watchUIElementPosition)) {
            //当前的表盘刚好支持随机生成的位置
            return watchUIElementPosition;
        } else {
            //当前的表盘不支持随机生成的位置，为了demo方便就取支持列表的第一个
            return supportPosition.get(0);
        }

    }

    /**
     * 获取随机的元素
     *
     * @return
     */
    private EWatchUIElementType getRandomElementType() {
        int random = new Random().nextInt(9);//(0-8)
        return EWatchUIElementType.getEWatchUIElementType(random);
    }

    /**
     * 获取随机的颜色
     *
     * @return
     */
    private int getRandomColor() {
        int maxColor = 0xffffff;
        int color = new Random().nextInt(maxColor + 1);//(0x000000-0xffffff)
        return color;
    }


    /**
     * 使用的是自选图片
     */
    @OnClick(R.id.ui_custom_change_img_set)
    public void changeImgToSet() {
        Logger.t(TAG).i("changeImgToSet");

        //20210128143910.png是个240*240的bitmap
        String filePath = getExternalFilesDir(null) + File.separator + "20210128143910.png";
        File file = new File(filePath);
        if (file.exists()) {
            Uri mUritempFile = Uri.fromFile(file);
            InputStream inputStream = null;
            try {
                inputStream = mContext.getContentResolver().openInputStream(mUritempFile);
                Bitmap bmp = BitmapFactory.decodeStream(inputStream);//原图
                Logger.t(TAG).i("get BitMap");
                InputStream sendInputStream = mWatchUIType.getSendInputStream(mContext, bmp);
                mUiUpdateUtil.startSetUiStream(EUIFromType.CUSTOM,sendInputStream, new IUiUpdateListener() {

                    @Override
                    public void onUiUpdateStart() {
                        Logger.t(TAG).i("onUiUpdateStart");
                    }

                    @Override
                    public void onStartClearCache(int sumCount) {
                        Logger.t(TAG).i("onStartClearCache:" + sumCount);
                    }

                    @Override
                    public void onClearCacheProgress(int currentCount, int sumCount, int progress) {
                        Logger.t(TAG).i("onClearCacheProgress:" + currentCount + "," + sumCount + "," + progress + "%");
                    }

                    @Override
                    public void onFinishClearCache() {
                        Logger.t(TAG).i("onFinishClearCache");
                    }

                    @Override
                    public void onUiUpdateProgress(int currentBlock, int sumBlock, int progress) {
                        Logger.t(TAG).i("onUiUpdateProgress:" + currentBlock + "," + sumBlock + "," + progress + "%");
                        mSendProgressTv.setText("发送中：" + progress + "%");
                    }


                    @Override
                    public void onUiUpdateSuccess() {
                        Logger.t(TAG).i("onUiUpdateSuccess");
                        mSendProgressTv.setText("设置成功");
                    }

                    @Override
                    public void onUiUpdateFail(EUiUpdateError eUiUpdateError) {
                        Logger.t(TAG).i("onUiUpdateFail:" + eUiUpdateError);
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Logger.t(TAG).i("can find bitmap");
        }
    }
}
