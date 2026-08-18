package com.timaimee.vpdemo.activity.v2.other;

import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.jieli.RcspAuthManager;
import com.inuker.bluetooth.library.jieli.dial.JLWatchFaceManager;
import com.inuker.bluetooth.library.jieli.response.RcspAuthResponse;
import com.jieli.jl_fatfs.model.FatFile;
import com.jieli.jl_rcsp.model.base.BaseError;
import com.orhanobut.logger.Logger;
import com.timaimee.vpdemo.R;
import com.timaimee.vpdemo.activity.CustomProgressDialog;
import com.timaimee.vpdemo.activity.v2.BaseVPBLETestActivity;
import com.veepoo.protocol.AiDialPreviewManager;
import com.veepoo.protocol.customui.WatchUIType;
import com.veepoo.protocol.listener.data.IAiRecordListener;
import com.veepoo.protocol.listener.data.IUIBaseInfoListener;
import com.veepoo.protocol.listener.data.IUiUpdateListener;
import com.veepoo.protocol.listener.data.OnAIConfigOptListener;
import com.veepoo.protocol.listener.data.OnAIDialOptListener;
import com.veepoo.protocol.listener.data.OnAIQAOptListener;
import com.veepoo.protocol.listener.data.OnAiDialPreviewSendListener;
import com.veepoo.protocol.listener.data.OnOpusDecode2PcmListener;
import com.veepoo.protocol.model.datas.AIDeviceConfigBean;
import com.veepoo.protocol.model.datas.UICustomSetData;
import com.veepoo.protocol.model.datas.UIDataCustom;
import com.veepoo.protocol.model.enums.EUIFromType;
import com.veepoo.protocol.model.enums.EUiUpdateError;
import com.veepoo.protocol.model.enums.EWatchUIType;
import com.veepoo.protocol.operate.AIFunctionOpt;
import com.veepoo.protocol.shareprence.VpSpGetUtil;
import com.veepoo.protocol.util.UiUpdateUtil;
import com.veepoo.protocol.util.ai.AiCropWatchFaceBitmap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Author: SDK Demo
 * Description: AI 功能示例（AI问答 / AI表盘）
 * <p>
 * 说明：
 * 1. AI 问答/表盘的交互流程由【设备端发起】（开始录音→结束录音→结果/开始生成等），App 侧在回调中应答；
 * 2. 设备端录音时会将录音数据（opus 编码）分包返回，通过 IAiRecordListener 接收并保存为 opus 文件，
 *    再调用 aiOpus2Pcm 解码为 pcm；
 * 3. 语音转文字、AI 问答、AI 绘图等云端能力需客户自行接入第三方 SDK，
 *    本示例使用固定文本（"这是ai问答中的问题" / "这是ai问答中的答案" / "这是ai表盘的描述"）
 *    与固定本地图片（assets/custom_rect_320_380_bg.png）演示完整链路；
 * 4. AI 表盘预览图传输（裁剪/转换/CRC/分包/设备请求应答）由 SDK 的 sendAiDialPreview 自动完成。
 */
public class AIFunActivity extends BaseVPBLETestActivity implements IAiRecordListener {

    private static final String TAG = "AIFunActivity";

    /** AI 问答固定问题文本（客户需替换为语音识别结果） */
    private static final String AI_QA_QUESTION = "这是ai问答中的问题";
    /** AI 问答固定答案文本（客户需替换为云端 AI 返回结果） */
    private static final String AI_QA_ANSWER = "这是ai问答中的答案这是ai问答中的答案这是ai问答中的答案";
    /** AI 表盘固定描述文本（客户需替换为语音识别结果） */
    private static final String AI_DIAL_DESCRIPTION = "这是ai表盘的描述";
    /** AI 表盘固定生成图片（assets 下） */
    private static final String AI_DIAL_IMAGE_ASSET = "img_push2.jpg";

    private TextView tvAiQaSupport;
    private TextView tvAiDialSupport;
    private TextView tvLog;
    private ScrollView svLog;

    /** 当前录音保存的 opus 文件路径（设备返回的录音数据分包写入） */
    private String aiRecordOpusPath;
    /** 当前录音输出流 */
    private BufferedOutputStream aiRecordFileStream;
    /** 预览图传输完成时保存的大图（设备上报设为表盘时使用，参照 GBand2 setAIDial2WatchFace） */
    private AiCropWatchFaceBitmap aiBigWatchFace;
    /** 待发送预览图路径（readAiConfig 成功回调后继续发送，避免"配置为空"校验失败） */
    private String pendingAiDialPreviewPath;

    @Override
    public int getLayoutID() {
        return R.layout.activity_aifun;
    }

    @Override
    public String pageTitle() {
        return "AI功能";
    }

    @Override
    public void initView() {
        tvAiQaSupport = findViewById(R.id.tvAiQaSupport);
        tvAiDialSupport = findViewById(R.id.tvAiDialSupport);
        tvLog = findViewById(R.id.tvLog);
        svLog = findViewById(R.id.svLog);
        //loadingDialog 必须在 onCreate 之后创建（构造时访问系统服务，字段初始化阶段会抛
        //"System services not available to Activities before onCreate()"）
        loadingDialog = new CustomProgressDialog(this);
    }

    @Override
    public void initData() {
        //1. 展示设备是否支持 AI 问答 / AI 表盘
        boolean supportAiQa = VpSpGetUtil.getVpSpVariInstance(this).isSupportAiQa();
        boolean supportAiDial = VpSpGetUtil.getVpSpVariInstance(this).isSupportAiDial();
        tvAiQaSupport.setText("AI问答支持：" + (supportAiQa ? "✅支持" : "❌不支持"));
        tvAiDialSupport.setText("AI表盘支持：" + (supportAiDial ? "✅支持" : "❌不支持"));
        appendLog("AI问答支持=" + supportAiQa + "，AI表盘支持=" + supportAiDial);

        //2. 初始化 UiUpdateUtil（读取UI信息/设置表盘依赖其 context，未初始化会导致 getMangerInstance(null) 空指针）
        UiUpdateUtil.getInstance().init(this);

        //3. 注册 AI 功能监听（配置/问答/表盘），设备上报的 AI 指令才会回调到 App
        //   （JL 设备预览图传输由 SDK 内置实现，客户无需注入）
        vpBleManager.setAiListener(aiConfigListener, aiqaListener, aiDialListener);
        appendLog("已注册AI功能监听（setAiListener），等待设备发起AI流程...");

        //4. JL（杰理）设备且支持 AI 表盘：检查杰理通知与设备认证，未就绪则打开（参照 JLDeviceOPTActivity）
        if (supportAiDial && VpSpGetUtil.getVpSpVariInstance(this).isJieLiDevice()) {
            checkJLReady();
        }
    }

    //======================================================================
    // JL（杰理）设备：打开通知 + 设备认证（参照 JLDeviceOPTActivity），
    // 进入页面时完成，AI 表盘预览图传输（SDK 内置实现）假定已就绪
    //======================================================================
    // 注意：不能在字段初始化处 new（Activity 构造阶段访问系统服务会崩溃），
    // 统一在 initView()（onCreate 之后）创建
    private CustomProgressDialog loadingDialog;

    private void checkJLReady() {
        boolean notifyOpened = vpBleManager.isJLNotifyOpened();
        boolean authPass = RcspAuthManager.getInstance().isAuthPass();
        if (notifyOpened && authPass) {
            appendLog("【JL】通知已打开、设备认证已通过 → 检查文件系统");
            initJLFileSystem();
            return;
        }
        appendLog("【JL】通知=" + (notifyOpened ? "已打开" : "未打开") + "，认证=" + (authPass ? "已通过" : "未通过") + " → 打开通知并认证");
        loadingDialog.showNoTips();
        if (notifyOpened) {
            startJLAuth();
        } else {
            vpBleManager.openJLDataNotify(new BleNotifyResponse() {
                @Override
                public void onNotify(java.util.UUID service, java.util.UUID character, byte[] value) {
                }

                @Override
                public void onResponse(int code) {
                    appendLog("【JL】通知已打开 → 开始设备认证");
                    startJLAuth();
                }
            });
        }
    }

    private void startJLAuth() {
        if (RcspAuthManager.getInstance().isAuthPass()) {
            appendLog("【JL】设备认证已通过 → 检查文件系统");
            initJLFileSystem();
            return;
        }
        vpBleManager.startJLDeviceAuth(new RcspAuthResponse() {
            @Override
            public void onRcspAuthStart() {
            }

            @Override
            public void onRcspAuthSuccess() {
                appendLog("【JL】设备认证成功 → 检查文件系统");
                initJLFileSystem();
            }

            @Override
            public void onRcspAuthFailed() {
                loadingDialog.disMissDialog();
                appendLog("【JL】设备认证失败");
            }
        });
    }

    /**
     * 初始化 JL 文件系统（WatchManager），AI 表盘预览图传输（startTransferAIPreview）依赖其已就绪；
     * 参照 JLDeviceOPTActivity.getJLFileSystem / GBand2 openJlFileSystem。
     * 未初始化时日志会报 [checkWatchManagerIsInit] Watch system has not been initialized → 传输失败。
     */
    private void initJLFileSystem() {
        JLWatchFaceManager jlManager = JLWatchFaceManager.getInstance();
        if (jlManager.isJLFatFileSystemInitSuccess()) {
            appendLog("【JL】文件系统已就绪，可直接使用 AI 表盘");
            loadingDialog.disMissDialog();
            return;
        }
        appendLog("【JL】文件系统未初始化 → checkJLSDKAndInit（初始化 WatchManager 并获取表盘列表）");
        loadingDialog.showNoTips();
        jlManager.checkJLSDKAndInit(new JLWatchFaceManager.OnWatchDialInfoGetListener() {
            @Override
            public void onGettingWatchDialInfo() {
            }

            @Override
            public void onWatchDialInfoGetStart() {
            }

            @Override
            public void onWatchDialInfoGetComplete() {
                loadingDialog.disMissDialog();
                appendLog("【JL】文件系统初始化完成（表盘列表获取流程结束）");
            }

            @Override
            public void onWatchDialInfoGetSuccess(List<FatFile> systemFatFiles, List<FatFile> serverFatFiles, FatFile picFatFile) {
                appendLog("【JL】文件系统初始化成功（表盘列表获取成功）");
            }

            @Override
            public void onWatchDialInfoGetFailed(BaseError error) {
                loadingDialog.disMissDialog();
                appendLog("【JL】文件系统初始化失败: " + error);
            }
        });
    }

    @Override
    public void initEvent() {
        Button btnReadConfig = findViewById(R.id.btnReadConfig);
        btnReadConfig.setOnClickListener(v -> {
            appendLog("读取AI配置 readAiConfig...");
            vpBleManager.readAiConfig(defaultResponse);
        });
    }

    //======================================================================
    // AI 配置监听
    //======================================================================
    private final OnAIConfigOptListener aiConfigListener = new OnAIConfigOptListener() {
        @Override
        public void onAIDeviceConfigReport(AIDeviceConfigBean config) {
            appendLog("设备上报AI配置: " + config);
        }

        @Override
        public void onAIDeviceConfigSettingResult(boolean isSuccess) {
            appendLog("AI配置设置结果: " + isSuccess);
        }

        @Override
        public void onAIDeviceConfigRead(AIDeviceConfigBean config) {
            appendLog("读取AI配置成功: " + config);
            //AI 表盘预览图在等待配置（startSendAiDialPreview 中 readAiConfig 之后）→ 配置就绪，继续发送
            if (pendingAiDialPreviewPath != null) {
                String path = pendingAiDialPreviewPath;
                pendingAiDialPreviewPath = null;
                doSendAiDialPreview(path);
            }
        }
    };

    //======================================================================
    // AI 问答监听（设备发起 → App 应答，问题/答案使用固定文本演示）
    //======================================================================
    private final OnAIQAOptListener aiqaListener = new OnAIQAOptListener() {
        @Override
        public void onAIQAStartRecording() {
            appendLog("【AI问答】设备请求开始录音 → 注册录音监听并应答");
            //1. 准备接收设备返回的录音数据（opus 分包），保存为 opus 文件
            prepareAiRecordFile("ai_qa.opus");
            UiUpdateUtil.getInstance().setAiRecordListener(AIFunActivity.this);
            //2. 应答设备：开始录音
            vpBleManager.sendAIQAStartRecordingResult(defaultResponse, AIFunctionOpt.AIAppErrorCode.SUCCESS);
        }

        @Override
        public void onAIQAStopRecording() {
            appendLog("【AI问答】设备请求结束录音 → 取消监听，使用保存的 opus 文件调用 aiOpus2Pcm 解码");
            //1. 取消录音监听，关闭文件流（设备返回的录音数据已完整保存为 opus 文件）
            UiUpdateUtil.getInstance().setAiRecordListener(null);
            closeAiRecordFile();
            //2. 将设备返回的 opus 音频解码为 pcm，供语音识别使用
            String opusFilePath = aiRecordOpusPath;
            String pcmFilePath = new File(getExternalFilesDir(null), "ai_record/ai_qa.pcm").getAbsolutePath();
            vpBleManager.aiOpus2Pcm(opusFilePath, pcmFilePath, new OnOpusDecode2PcmListener() {
                @Override
                public void onDecodeComplete(String filePath) {
                    appendLog("【AI问答】opus转pcm完成: " + filePath + " → 语音识别后发送问题文本与AI答案");
                    //客户实际流程：此处对 pcm 做语音识别得到问题文本（第三方SDK），
                    //再调用云端 AI 问答得到答案；示例使用固定文本
                    //⚠️ 注意：问题与答案【不能连续快速发送】——SDK 分包发送不逐包等待设备 ACK，
                    //连续调用会把 问题2包+答案2包 在几毫秒内全部挤进 BLE 写通道，JL 设备处理不过来，
                    //表现为"设备显示的问题变成答案、答案为空"。
                    //正确做法：发送问题后等待设备处理/云端返回再发答案（参照 GBand2 云端异步时序）。
                    //示例用 1s 延迟模拟云端问答耗时：
                    vpBleManager.sendAIQARecordingContent2Device(defaultResponse, AIFunctionOpt.AIAppErrorCode.SUCCESS, AI_QA_QUESTION);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        appendLog("【AI问答】发送答案: [" + AI_QA_ANSWER + "]");
                        vpBleManager.sendAIQAResultCmd(defaultResponse, AIFunctionOpt.AIAppErrorCode.SUCCESS, AI_QA_ANSWER);
                    }, 1000);
                }

                @Override
                public void onDecodeFailed(String filePath) {
                    appendLog("【AI问答】opus转pcm失败 → 发送失败指令，避免设备一直等待");
                    vpBleManager.sendAIQARecordingContent2Device(defaultResponse, AIFunctionOpt.AIAppErrorCode.RECORDING_FAILED_0x04, "");
                }
            });
        }

        @Override
        public void onAIQAResult(AIFunctionOpt.AIDeviceErrorCode errorCode) {
            //设备上报问答结果（设备侧状态），仅做记录；答案已在录音结束→opus转pcm完成后发送
            appendLog("【AI问答】设备上报问答结果: " + errorCode + "（仅记录，答案已发送）");
        }

        @Override
        public void onAIQARegenerate() {
            appendLog("【AI问答】设备请求重新生成 → 重新发送答案: [" + AI_QA_ANSWER + "]");
            vpBleManager.sendAIQARegenerateResultCmd(defaultResponse, AIFunctionOpt.AIAppErrorCode.SUCCESS, AI_QA_ANSWER);
        }

        @Override
        public void onAIQATerminateREQ() {
            appendLog("【AI问答】设备请求终止AI问答，结束当前流程");
        }
    };

    //======================================================================
    // AI 表盘监听（设备发起 → App 应答，描述使用固定文本，表盘使用本地图片）
    //======================================================================
    private final OnAIDialOptListener aiDialListener = new OnAIDialOptListener() {
        @Override
        public void onAIDialStartRecording() {
            appendLog("【AI表盘】设备请求开始录音 → 注册录音监听并应答");
            //1. 准备接收设备返回的录音数据（opus 分包），保存为 opus 文件
            prepareAiRecordFile("ai_dial.opus");
            UiUpdateUtil.getInstance().setAiRecordListener(AIFunActivity.this);
            //2. 应答设备：开始录音
            vpBleManager.sendAIDialStartRecordingResult(defaultResponse, AIFunctionOpt.AIAppErrorCode.SUCCESS);
        }

        @Override
        public void onAIDialStopRecording() {
            appendLog("【AI表盘】设备请求结束录音 → 取消监听，使用保存的 opus 文件调用 aiOpus2Pcm 解码");
            //1. 取消录音监听，关闭文件流（设备返回的录音数据已完整保存为 opus 文件）
            UiUpdateUtil.getInstance().setAiRecordListener(null);
            closeAiRecordFile();
            //2. 将设备返回的 opus 音频解码为 pcm，做语音识别得到表盘描述文本
            String opusFilePath = aiRecordOpusPath;
            String pcmFilePath = new File(getExternalFilesDir(null), "ai_record/ai_dial.pcm").getAbsolutePath();
            vpBleManager.aiOpus2Pcm(opusFilePath, pcmFilePath, new OnOpusDecode2PcmListener() {
                @Override
                public void onDecodeComplete(String filePath) {
                    appendLog("【AI表盘】opus转pcm完成: " + filePath + " → 语音识别后发送表盘描述");
                    //客户实际流程：此处对 pcm 做语音识别得到表盘描述文本；示例使用固定文本
                    vpBleManager.sendAIDialRecordingResult(defaultResponse, AIFunctionOpt.AIAppErrorCode.SUCCESS, AI_DIAL_DESCRIPTION);
                }

                @Override
                public void onDecodeFailed(String filePath) {
                    appendLog("【AI表盘】opus转pcm失败 → 发送失败指令，避免设备一直等待");
                    vpBleManager.sendAIDialRecordingResult(defaultResponse, AIFunctionOpt.AIAppErrorCode.RECORDING_FAILED_0x04, "");
                }
            });
        }

        @Override
        public void onAIDialStartGenerate() {
            appendLog("【AI表盘】设备请求开始生成 → 应答 sendAIDialStartGenerateACK(SUCCESS)，使用本地图片生成表盘");
            vpBleManager.sendAIDialStartGenerateACK(defaultResponse, AIFunctionOpt.AIAppErrorCode.SUCCESS);
            //客户实际流程：此处调用云端 AI 绘图（文生图），生成完成后把表盘原图路径传给 sendAiDialPreview；
            //本示例直接使用固定的本地图片（assets/custom_rect_320_380_bg.png）
            startSendAiDialPreview();
        }

        @Override
        public void onAIDialProgressSetting() {
            appendLog("【AI表盘】生成进度设置应答（一般不用处理）");
        }

        @Override
        public void onAIDialResultSettingACK() {
            appendLog("【AI表盘】生成结果设置应答（一般不用处理）");
        }

        @Override
        public void onAIDialPreviewLengthGetACK(int needGetPreviewLength) {
            //预览图传输由 SDK 的 sendAiDialPreview 自动应答，此处仅记录
            appendLog("【AI表盘】设备请求预览图长度=" + needGetPreviewLength + "（SDK自动应答）");
        }

        @Override
        public void onAIDialContinueGetPreviewDataREQ(int needGetPreviewLength) {
            appendLog("【AI表盘】设备请求继续获取预览图=" + needGetPreviewLength + "（SDK自动应答）");
        }

        @Override
        public void onAIDialPreviewDataReceiveComplete(AIFunctionOpt.AIDeviceErrorCode errorCode) {
            appendLog("【AI表盘】设备预览图数据接收结束=" + errorCode + "（SDK自动应答）");
        }

        @Override
        public void onAIDialSetDial() {
            appendLog("【AI表盘】设备上报设为表盘 → 将大图设置为当前表盘");
            setAiDial2WatchFace();
        }

        @Override
        public void onAIDialRegenerate() {
            appendLog("【AI表盘】设备请求重新生成 → 使用本地图片重新发送预览图");
            startSendAiDialPreview();
        }

        @Override
        public void onAIDialTerminateREQ() {
            appendLog("【AI表盘】设备请求终止 → 应答 sendAIDialTerminateACK");
            vpBleManager.sendAIDialTerminateACK(defaultResponse);
        }
    };

    //======================================================================
    // IAiRecordListener：接收设备端返回的录音数据（opus 分包），按包追加保存为 opus 文件
    //======================================================================
    @Override
    public void onAiRecording(byte[] recordData, int pkgNum) {
        if (pkgNum == 1) {
            //第一包：清空旧文件，重新开始写入
            try {
                if (aiRecordFileStream != null) {
                    aiRecordFileStream.flush();
                    aiRecordFileStream.close();
                }
                File opusFile = new File(aiRecordOpusPath);
                if (opusFile.exists()) {
                    opusFile.delete();
                }
                aiRecordFileStream = new BufferedOutputStream(new FileOutputStream(opusFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (aiRecordFileStream != null && recordData != null) {
            try {
                aiRecordFileStream.write(recordData);
                aiRecordFileStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        appendLog("【AI录音】接收设备录音数据 第" + pkgNum + "包，长度=" + (recordData != null ? recordData.length : 0));
    }

    /**
     * 准备录音文件（删除旧文件并新建输出流）
     *
     * @param fileName opus 文件名（如 ai_qa.opus / ai_dial.opus）
     */
    private void prepareAiRecordFile(String fileName) {
        aiRecordOpusPath = new File(getExternalFilesDir(null), "ai_record/" + fileName).getAbsolutePath();
        File dir = new File(getExternalFilesDir(null), "ai_record");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        closeAiRecordFile();
        File opusFile = new File(aiRecordOpusPath);
        if (opusFile.exists()) {
            opusFile.delete();
        }
        try {
            aiRecordFileStream = new BufferedOutputStream(new FileOutputStream(opusFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        appendLog("【AI录音】准备录音文件: " + aiRecordOpusPath);
    }

    /**
     * 关闭录音输出流
     */
    private void closeAiRecordFile() {
        if (aiRecordFileStream != null) {
            try {
                aiRecordFileStream.flush();
                aiRecordFileStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                aiRecordFileStream = null;
            }
        }
    }

    //======================================================================
    // 设为表盘（参照 GBand2 setAIDial2WatchFace：将预览图传输完成时的大图设置为当前表盘）
    //======================================================================
    private void setAiDial2WatchFace() {
        if (aiBigWatchFace == null || aiBigWatchFace.bitmap == null) {
            appendLog("【AI表盘】设置表盘失败：无大图数据（预览图未传输完成）");
            return;
        }
        IUiUpdateListener uiUpdateListener = new IUiUpdateListener() {
            @Override
            public void onUiUpdateStart() {
                appendLog("【AI表盘】开始设置表盘");
            }

            @Override
            public void onStartClearCache(int sumCount) {
            }

            @Override
            public void onClearCacheProgress(int currentCount, int sumCount, int progress) {
            }

            @Override
            public void onFinishClearCache() {
            }

            @Override
            public void onUiUpdateProgress(int currentBlock, int sumBlock, int progress) {
                appendLog("【AI表盘】设置表盘进度: " + progress + "%");
            }

            @Override
            public void onUiUpdateSuccess() {
                appendLog("【AI表盘】设置表盘成功 → 回复设备");
                vpBleManager.sendAIDialRecordingResult(defaultResponse, AIFunctionOpt.AIAppErrorCode.SUCCESS, "");
                //⚠️ JL（杰理）照片表盘 = 背景图 + 功能元素（时间/日期/步数/颜色）：
                //setJLWatchPhotoDial 只传输了背景图，还需 setCustomWacthUi 重新设置功能元素，
                //否则设备只显示默认照片表盘（GBand2 在设置成功后延迟 150ms 调 bleSetCustomWatchUi）
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    setCustomWatchUiForAIDial();
                }, 150);
            }

            @Override
            public void onUiUpdateFail(EUiUpdateError eUiUpdateError) {
                appendLog("【AI表盘】设置表盘失败: " + eUiUpdateError + " → 回复设备失败");
                vpBleManager.sendAIDialRecordingResult(defaultResponse, AIFunctionOpt.AIAppErrorCode.AI_DRAWING_FAILED_0x07, "");

            }
        };

        if (VpSpGetUtil.getVpSpVariInstance(this).isJieLiDevice()) {
            //JL（杰理）设备：设置照片表盘（SDK 的 VPOperateManager.setJLWatchPhotoDial，
            //与 GBand2 的 VPBleCenter.setJLWatchPhotoDial 内部实现一致）
            appendLog("【AI表盘】JL设备设置表盘 setJLWatchPhotoDial，大图=" + aiBigWatchFace.filePath);
            vpBleManager.setJLWatchPhotoDial(aiBigWatchFace.filePath, new JLWatchFaceManager.JLTransferPicDialListener() {
                @Override
                public void onLowPower() {
                    uiUpdateListener.onUiUpdateFail(EUiUpdateError.LOW_BATTERY);
                }

                @Override
                public void onJLTransferPicDialStart() {
                    uiUpdateListener.onUiUpdateStart();
                }

                @Override
                public void onTransferPicDialProgress(int progress) {
                    uiUpdateListener.onUiUpdateProgress(0, 100, progress);
                }

                @Override
                public void onScaleBGPFileTransferComplete() {
                }

                @Override
                public void onAIPreviewTransferComplete() {
                }

                @Override
                public void onBigBGPFileTransferComplete() {
                }

                @Override
                public void onTransferComplete() {
                    appendLog("【AI表盘】JL设置照片表盘成功");
                    uiUpdateListener.onUiUpdateSuccess();
                }

                @Override
                public void onTransferError(int code, String errorMsg) {
                    appendLog("【AI表盘】JL设置照片表盘失败: " + errorMsg);
                    uiUpdateListener.onUiUpdateFail(EUiUpdateError.CHECK_CRC_FAIL);
                }
            });
        } else {
            //非 JL：读取 UI 信息，将大图转换为输入流后通过 UI 更新流设置表盘
            appendLog("【AI表盘】读取UI信息 getCustomWatchUiInfo...");
            UiUpdateUtil.getInstance().getCustomWatchUiInfo(uiDataCustom -> {
                EWatchUIType uiType = uiDataCustom != null ? uiDataCustom.getCustomUIType() : null;
                if (uiType == null) {
                    appendLog("【AI表盘】设置表盘失败：未获取到屏幕类型");
                    return;
                }
                InputStream sendInputStream = WatchUIType.getInstance(uiType)
                        .getSendInputStream(AIFunActivity.this, aiBigWatchFace.bitmap);
                appendLog("【AI表盘】startSetUiStream(CUSTOM) 设置表盘，屏幕类型=" + uiType);
                UiUpdateUtil.getInstance().startSetUiStream(EUIFromType.CUSTOM, sendInputStream, uiUpdateListener);
            });
        }
    }

    //======================================================================
    // 发送 AI 表盘预览图（SDK 自动完成裁剪/转换/CRC/分包传输/设备请求应答）
    // 屏幕类型通过 readWatchUiInfo(CUSTOM) 从设备读取，不写死
    //======================================================================
    private void startSendAiDialPreview() {
        String imagePath = copyAssetToCache(AI_DIAL_IMAGE_ASSET);
        if (imagePath == null) {
            appendLog("【AI表盘】本地图片复制失败: " + AI_DIAL_IMAGE_ASSET);
            return;
        }
        //sendAiDialPreview 依赖 SDK 缓存的 AI 配置（AIFunctionOpt.currentAiConfig，需先 readAiConfig），
        //未读取时会报"参数不合法 或 配置为空 或 图片不存在"（日志统一显示图片路径，实际是配置为空）
        AIDeviceConfigBean config = AiDialPreviewManager.getInstance().getAiDeviceConfig();
        if (config != null && config.isValid()) {
            doSendAiDialPreview(imagePath);
        } else {
            appendLog("【AI表盘】AI配置未读取 → 先 readAiConfig，配置回调后继续发送预览图");
            pendingAiDialPreviewPath = imagePath;
            vpBleManager.readAiConfig(defaultResponse);
        }
    }

    /** 读取设备屏幕类型并调用 sendAiDialPreview（前置：AI 配置已就绪） */
    private void doSendAiDialPreview(String imagePath) {
        //先读取设备当前屏幕类型（CUSTOM），再调用 sendAiDialPreview 发送预览图
        appendLog("【AI表盘】读取设备屏幕类型 readWatchUiInfo(CUSTOM)...");
        vpBleManager.readWatchUiInfo(defaultResponse, EUIFromType.CUSTOM, new IUIBaseInfoListener<UIDataCustom>() {
            @Override
            public void onBaseUiInfo(UIDataCustom uiDataCustom) {
                EWatchUIType uiType = uiDataCustom != null ? uiDataCustom.getCustomUIType() : null;
                appendLog("【AI表盘】读取到屏幕类型: " + uiType + " → 调用 sendAiDialPreview");
                vpBleManager.sendAiDialPreview(imagePath, uiType, previewSendListener);
            }
        });
    }

    /**
     * 设置自定义 UI 元素（时间位置/上下功能/颜色）——JL 照片表盘传输完成后的必要步骤（参照 GBand2 bleSetCustomWatchUi）。
     * 表盘背景图（bgp_w001/bgp_w000）只包含背景，时间/日期/步数等功能元素需单独下发，否则设备显示默认照片表盘。
     */
    private void setCustomWatchUiForAIDial() {
        UiUpdateUtil.getInstance().getCustomWatchUiInfo(uiDataCustom -> {
            if (uiDataCustom == null) {
                appendLog("【AI表盘】设置自定义UI失败：未获取到UI信息");
                return;
            }
            appendLog("【AI表盘】设置自定义UI元素: timePosition=" + uiDataCustom.getTimePosition()
                    + " upTimeType=" + uiDataCustom.getUpTimeType()
                    + " downTimeType=" + uiDataCustom.getDownTimeType()
                    + " color888=" + uiDataCustom.getColor888());
            UICustomSetData uiCustomSetData = new UICustomSetData(false,
                    uiDataCustom.getTimePosition(),
                    uiDataCustom.getUpTimeType(),
                    uiDataCustom.getDownTimeType(),
                    uiDataCustom.getColor888());
            vpBleManager.setCustomWacthUi(defaultResponse, uiCustomSetData, new IUIBaseInfoListener<UIDataCustom>() {
                @Override
                public void onBaseUiInfo(UIDataCustom data) {
                    appendLog("【AI表盘】自定义UI设置完成");
                }
            });
        });
    }

    private final OnAiDialPreviewSendListener previewSendListener = new OnAiDialPreviewSendListener() {
        @Override
        public void onDataSendProgress(byte[] data, int currentPackage, int haveSendDataLength, int totalDataLength, int progress) {
            appendLog("【AI表盘】预览图发送进度: " + progress + "% 第" + currentPackage + "包 [" + haveSendDataLength + "/" + totalDataLength + "]");
        }

        @Override
        public void onDataSendFailed(byte[] data, int failedTimes) {
            appendLog("【AI表盘】预览图数据发送失败: " + failedTimes + "次");
        }

        @Override
        public void onPreviewSendFailed() {
            appendLog("【AI表盘】预览图发送失败！请确认：1.已读取AI配置 2.设备支持AI表盘 3.JL设备已注入IAiDialJLPreviewSender");
        }

        @Override
        public void onPreviewSendComplete(AiCropWatchFaceBitmap bigWatchFace, AiCropWatchFaceBitmap scaleWatchFace, AiCropWatchFaceBitmap previewWatchFace) {
            //保存大图，设备上报设为表盘（onAIDialSetDial）时用于设置表盘
            AIFunActivity.this.aiBigWatchFace = bigWatchFace;
            appendLog("【AI表盘】预览图发送完成！大图=" + (bigWatchFace != null ? bigWatchFace.filePath : "null"));
        }
    };

    /**
     * 将 assets 下的表盘图片复制到应用外部缓存目录（sendAiDialPreview 需要本地文件路径）
     */
    private String copyAssetToCache(String assetName) {
        File dir = new File(getExternalFilesDir(null), "ai_dial");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File outFile = new File(dir, assetName);
        try {
            InputStream is = getAssets().open(assetName);
            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[8192];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            fos.close();
            is.close();
            return outFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 页面日志输出
     */
    private void appendLog(String msg) {
        Logger.t(TAG).i(msg);
        runOnUiThread(() -> {
            tvLog.append(msg + "\n");
            svLog.post(() -> svLog.fullScroll(ScrollView.FOCUS_DOWN));
        });
    }
}
