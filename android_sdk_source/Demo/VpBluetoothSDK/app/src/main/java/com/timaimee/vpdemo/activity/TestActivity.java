package com.timaimee.vpdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.timaimee.vpdemo.R;
import com.veepoo.protocol.model.datas.InsomniaTimeData;
import com.veepoo.protocol.model.datas.SleepCrcData;
import com.veepoo.protocol.model.datas.SleepPrecisionData;
import com.veepoo.protocol.model.datas.TimeData;
//import com.veepoo.protocol.operate.parse.sleep.PrecisionSleepParse;
//import com.veepoo.protocol.util.ConvertHelper;
import com.veepoo.protocol.util.VPLogger;
import com.veepoo.protocol.util.VpBleByteUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: YWX
 * Date: 2022/2/21 15:22
 * Description:
 */
public class TestActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    getBytes();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getBytes() {
        String str = "" +
                "[e0, 32, 01, 01, a1, 28, 03, a2, 08, 57, 84, 15, f1, b0, d6, 5e, f1, a3, 24, 02]\n" +
                "[e0, 31, 01, 01, 12, 02, 19, 02, 12, 08, 11, 03, 04, 02, 04, 03, 00, 00, 03, 00]\n" +
                "[e0, 30, 01, 01, 00, 00, 6c, 00, b2, 00, 42, 00, 60, 01, 14, 00, 00, 00, 00, 00]\n" +
                "[e0, 2f, 01, 01, 3c, 00, 01, a4, 2c, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00]\n" +
                "[e0, 2e, 01, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00]\n" +
                "[e0, 2d, 01, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00]\n" +
                "[e0, 2c, 01, 01, 00, a5, c0, 02, 20, 00, 20, 00, 20, 00, 20, 00, 20, 00, 40, 00]\n" +
                "[e0, 2b, 01, 01, 00, 00, 00, 00, 20, 00, 20, 00, 00, 00, 00, 00, 40, 00, 40, 00]\n" +
                "[e0, 2a, 01, 01, 40, 00, 00, 00, 20, 00, 20, 00, 20, 00, 00, 00, 20, 00, 20, 00]\n" +
                "[e0, 29, 01, 01, 20, 00, 20, 00, 00, 00, 00, 00, 00, 00, 40, 00, 00, 00, 20, 00]\n" +
                "[e0, 28, 01, 01, 00, 00, 20, 00, 20, 00, 20, 00, 00, 00, 20, 00, 20, 00, 00, 00]\n" +
                "[e0, 27, 01, 01, 20, 00, 40, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 20, 00]\n" +
                "[e0, 26, 01, 01, 20, 00, 20, 00, 00, 00, 00, 00, 40, 00, 20, 00, 40, 00, 40, 00]\n" +
                "[e0, 25, 01, 01, 20, 00, 20, 00, 40, 00, 00, 00, 40, 00, 20, 00, 00, 00, 20, 00]\n" +
                "[e0, 24, 01, 01, 00, 00, 20, 00, 20, 00, 20, 00, 00, 00, 20, 00, 40, 00, 20, 00]\n" +
                "[e0, 23, 01, 01, 40, 00, 20, 00, 40, 00, 00, 00, 20, 00, 20, 00, 20, 00, 20, 00]\n" +
                "[e0, 22, 01, 01, 20, 00, 00, 00, 20, 00, 20, 00, 00, 00, 20, 00, 20, 00, 20, 00]\n" +
                "[e0, 21, 01, 01, 00, 00, 20, 00, 40, 00, 20, 00, 00, 00, 20, 00, 40, 00, 20, 00]\n" +
                "[e0, 20, 01, 01, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 40, 00, 20, 00, 20, 00]\n" +
                "[e0, 1f, 01, 01, 20, 00, 20, 00, 20, 00, 20, 00, 00, 00, 20, 00, 00, 00, 00, 00]\n" +
                "[e0, 1e, 01, 01, 20, 00, 00, 00, 20, 00, 20, 00, 20, 00, 20, 00, 20, 00, 40, 00]\n" +
                "[e0, 1d, 01, 01, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 20, 00, 40, 00, 00, 00]\n" +
                "[e0, 1c, 01, 01, 00, 00, 20, 00, 20, 00, 20, 00, 40, 00, 00, 00, 40, 00, 00, 00]\n" +
                "[e0, 1b, 01, 01, 20, 00, 40, 00, 20, 00, 20, 00, 00, 00, 20, 00, 00, 00, 00, 00]\n" +
                "[e0, 1a, 01, 01, 40, 00, 00, 00, 20, 00, 40, 00, 40, 00, 20, 00, 20, 00, 20, 00]\n" +
                "[e0, 19, 01, 01, 20, 00, 20, 00, 40, 00, 00, 00, 40, 00, 40, 00, 20, 00, 40, 00]\n" +
                "[e0, 18, 01, 01, 00, 00, 40, 00, 20, 00, 00, 00, 40, 00, 20, 00, 20, 00, 40, 00]\n" +
                "[e0, 17, 01, 01, 20, 00, 00, 00, 20, 00, 20, 00, 20, 00, 20, 00, 40, 00, 00, 00]\n" +
                "[e0, 16, 01, 01, 40, 00, 00, 00, 20, 00, 20, 00, 00, 00, 20, 00, 40, 00, 20, 00]\n" +
                "[e0, 15, 01, 01, 00, 00, 00, 00, 40, 00, 00, 00, 20, 00, 00, 00, 20, 00, 00, 00]\n" +
                "[e0, 14, 01, 01, 00, 00, 20, 00, 20, 00, 20, 00, 00, 00, 00, 00, 20, 00, 00, 00]\n" +
                "[e0, 13, 01, 01, 00, 00, 00, 00, 20, 00, 00, 00, 20, 00, 20, 00, 20, 00, 00, 00]\n" +
                "[e0, 12, 01, 01, 20, 00, 00, 00, 00, 00, 00, 00, 20, 00, 20, 00, 00, 00, 20, 00]\n" +
                "[e0, 11, 01, 01, 20, 00, 20, 00, 00, 00, 20, 00, 00, 00, 20, 00, 00, 00, 00, 00]\n" +
                "[e0, 10, 01, 01, 20, 00, 40, 00, 00, 00, 45, 00, 20, 00, 20, 00, 20, 00, 45, 00]\n" +
                "[e0, 0f, 01, 01, 20, 00, 00, 00, 20, 00, 00, 00, 20, 00, 20, 00, 00, 00, 00, 00]\n" +
                "[e0, 0e, 01, 01, 20, 00, 20, 00, 20, 00, 20, 00, 00, 00, 00, 00, 40, 00, 40, 00]\n" +
                "[e0, 0d, 01, 01, 40, 00, 20, 00, 20, 00, 40, 00, 20, 00, 20, 00, 40, 00, 20, 00]\n" +
                "[e0, 0c, 01, 01, 20, 00, 00, 00, 40, 00, 20, 00, 00, 00, 00, 00, 40, 00, 20, 00]\n" +
                "[e0, 0b, 01, 01, 00, 00, 00, 00, 20, 00, 00, 00, 20, 00, 00, 00, 00, 00, 20, 00]\n" +
                "[e0, 0a, 01, 01, 20, 00, 00, 00, 20, 00, 20, 00, 20, 00, 20, 00, 20, 00, 20, 00]\n" +
                "[e0, 09, 01, 01, 00, 00, 20, 00, 40, 00, 40, 00, 40, 00, 00, 00, 20, 00, 20, 00]\n" +
                "[e0, 08, 01, 01, 20, 00, 20, 00, 20, 00, 20, 00, 00, 00, 25, 00, 00, 00, 20, 00]\n" +
                "[e0, 07, 01, 01, 20, 00, 40, 00, 20, 00, 00, 00, 20, 00, 00, 00, 20, 00, 20, 00]\n" +
                "[e0, 06, 01, 01, 40, 00, 20, 00, 40, 00, 00, 00, 20, 00, 20, 00, 40, 00, 00, 00]\n" +
                "[e0, 05, 01, 01, 00, 00, 20, 00, 20, 00, 00, 00, 40, 00, 20, 00, 20, 00, 40, 00]\n" +
                "[e0, 04, 01, 01, 20, 00, 20, 00, 20, 00, 20, 00, 00, 00, 20, 00, 40, 00, 40, 00]\n" +
                "[e0, 03, 01, 01, 20, 00, 20, 00, 20, 00, 00, 00, 00, 00, 20, 00, 00, 00, 00, 00]\n" +
                "[e0, 02, 01, 01, 00, 00, 20, 00, 20, 00, 40, 00, 20, 00, 40, 00, 40, 00, 40, 00]\n" +
                "[e0, 01, 01, 01, 20, 00, 20, 00, 40, 00, 20, 00, 40, 00, 40, 00, 20, 00, 20, 00]\n" +
                "[e0, 00, 01, 01, 40, 00, 40, 00, a6, 02, 00, ff, 00, 00, 00, 00, 00, 00, 00, 00]";

        List<byte[]> lByte = new ArrayList<>();

        String[] strs = str.split("]");
        List<String> hesStrArray = new ArrayList<>();
        for (String s : strs) {
            VPLogger.e(s);
            if (s.length() > 30) {
                String newStr = s.replace("[", "");
                newStr = newStr.trim();
                hesStrArray.add(newStr);
            }
        }
        for (String hex16 : hesStrArray) {
            String[] hexStrs = hex16.split(",");
            byte[] bytes = new byte[hexStrs.length];
            for (int i = 0; i < bytes.length; i++) {
                int b = Integer.parseInt(hexStrs[i].trim(), 16);
//                byte b =  Byte.parseByte(hexStrs[i],16);
                bytes[i] = (byte) (b & 0x00FF);
            }
            lByte.add(bytes);
        }

        for (byte[] bytes : lByte) {
//            VPLogger.e("----------------------->" + ConvertHelper.byte2HexForHardware(bytes));
        }
        parseSleep(1, lByte);

    }

    private byte[] getBytesByList(List<byte[]> sleepByteList) {
        if (sleepByteList.isEmpty()) {
            return new byte[0];
        }
        byte[] bytes = new byte[sleepByteList.size() * 16];
        for (int i = 0; i < sleepByteList.size(); i++) {
            byte[] byteItem = sleepByteList.get(i);
            for (int j = 4; j < byteItem.length; j++) {
                bytes[i * 16 + j - 4] = byteItem[j];
            }
        }
        return bytes;
    }

    private List<byte[]> paseA1SleepV1Data(byte[] byteData) {
        List<byte[]> byteList = new ArrayList<>();
        int byteLength = byteData.length;
        int A1Info_lenth = 3;
        ByteBuffer buffer = ByteBuffer.wrap(byteData).order(ByteOrder.LITTLE_ENDIAN);
        VPLogger.e("paseA1SleepV1Data-----------^^^^^^^^^^^^^^^---> position = " + buffer.position() + " byteLength = " + byteLength);
        while (buffer.position() < byteLength) {
            byte type = buffer.get();
            VPLogger.e("-----------^^^^^^^^^^^^^^^---> position = " + buffer.position());
            switch (type) {
                case (byte) 0xA1:
                    byte lenght_A1_low = buffer.get();
                    byte lenght_A1_high = buffer.get();
                    int lenght_A1 = VpBleByteUtil.twoByteToUnsignedInt(lenght_A1_high, lenght_A1_low);
                    int startPosition = buffer.position();
                    int endPosition = startPosition + lenght_A1 - A1Info_lenth;
                    VPLogger.e("-----------^^^^^^^^^^^^^^^---> endPosition = " + endPosition);
                    buffer.position(endPosition);
                    byte[] bufferData = copyBlockContent(byteData, lenght_A1 - A1Info_lenth, startPosition);
                    try {
                        byteList.add(bufferData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        return byteList;
    }

    /**
     * 拷贝块内容
     *
     * @param byteData
     * @param length
     * @param startPosition
     * @return
     */
    private byte[] copyBlockContent(byte[] byteData, int length, int startPosition) throws ArrayIndexOutOfBoundsException {
        byte[] bytes = new byte[length];
        System.arraycopy(byteData, startPosition, bytes, 0, length);
        return bytes;
    }


    public void parseSleep(int day, List<byte[]> bytedata) {
//        byte[] bytesContent = getBytesByList(bytedata);
//        List<byte[]> byteA1List = paseA1SleepV1Data(bytesContent);//808-3 805
//        VPLogger.e("数组长度 = " + byteA1List.get(0).length);
//        for (int i = 0; i < byteA1List.size(); i++) {
//            byte[] data = byteA1List.get(i);
//            PrecisionSleepParse.SleepItemContent sleepItemContent = null;
//            try {
//                sleepItemContent = parseItemSleepV1Data(data);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            VPLogger.e("parseSleep = " + sleepItemContent.toString());
//            byte[] baseSleepBlockBytes = sleepItemContent.getBaseSleepBlockBytes();
//            byte[] insomniaBlockBytes = sleepItemContent.getInsominaBlockBytes();
//            byte[] insomniaBlockBytesV2 = sleepItemContent.getInsominaBlockBytesV2();
//            byte[] sleepCurveBlockBytes = sleepItemContent.getSleepCurveBlockBytes();
//            SleepCrcData SleepCrcData = pasrseCrcBlock(sleepItemContent.getCrcBlockBytes());
//            SleepPrecisionData sleepPrecisionData = pasrseSleepInfoBlock(baseSleepBlockBytes, insomniaBlockBytes, insomniaBlockBytesV2, sleepCurveBlockBytes);
//            sleepPrecisionData.setLaster(sleepItemContent.getLastLength());
//            sleepPrecisionData.setNext(sleepItemContent.getNextLength());
//        }

    }


    /**
     * CRC的具体内容byte块，不包含头部,目前长度定值为8
     *
     * @param crcBlockBytes
     * @return
     */
    private SleepCrcData pasrseCrcBlock(byte[] crcBlockBytes) {
        SleepCrcData SleepCrcData = new SleepCrcData();
        if (crcBlockBytes.length < 8) {
            return SleepCrcData;
        }
        String crcStrArr[] = VpBleByteUtil.byte2HexToStrArr(crcBlockBytes);
        SleepCrcData.setBaseSleepCrc(VpBleByteUtil.hexStrToInt(crcStrArr[1] + crcStrArr[0]));
        SleepCrcData.setInsomniaCrc(VpBleByteUtil.hexStrToInt(crcStrArr[3] + crcStrArr[2]));
        SleepCrcData.setSleepCurveCrc(VpBleByteUtil.hexStrToInt(crcStrArr[5] + crcStrArr[4]));
        SleepCrcData.setAllSleepCrc(VpBleByteUtil.hexStrToInt(crcStrArr[7] + crcStrArr[6]));
        return SleepCrcData;
    }

    /**
     * 解析睡眠
     *
     * @param baseSleepBlockBytes  睡眠的基本信息的具体内容byte块
     * @param insomniaBlockBytes   失眠的基本信息的具体内容byte块
     * @param sleepCurveBlockBytes 睡眠曲线的基本信息的具体内容byte块
     * @return
     */
    private SleepPrecisionData pasrseSleepInfoBlock(byte[] baseSleepBlockBytes, byte[] insomniaBlockBytes, byte[] insomniaBlockBytesV2, byte[] sleepCurveBlockBytes) {
        SleepPrecisionData baseSleepBean = new SleepPrecisionData();
        pasrseBaseSleepBlock(baseSleepBean, baseSleepBlockBytes);
        if (insomniaBlockBytes != null) {
            pasrseInsomniaBlock(baseSleepBean, insomniaBlockBytes);
        } else if (insomniaBlockBytesV2 != null) {
            pasrseInsomniaBlockV2(baseSleepBean, insomniaBlockBytesV2);
        }
        pasrseSleepCurveBlock(baseSleepBean, sleepCurveBlockBytes);
        mixInsomniaAndSleepLine(baseSleepBean);
        return baseSleepBean;
    }

    /**
     * 要将失眠信息插入到睡眠曲线当中
     *
     * @param baseSleepBean
     */
    private void mixInsomniaAndSleepLine(SleepPrecisionData baseSleepBean) {
        List<InsomniaTimeData> insomniaBeanList = baseSleepBean.getInsomniaBeanList();
        String sleepLine = baseSleepBean.getSleepLine();
        //TODO 插入算法
        baseSleepBean.setSleepLine(sleepLine);
    }


    /**
     * 解析失眠的基本信息
     *
     * @param baseSleepBean
     * @param insomniaBlockBytes 失眠的基本信息的具体内容byte块，不包含头部,目前长度定值为44
     */
    private void pasrseInsomniaBlock(SleepPrecisionData baseSleepBean, byte[] insomniaBlockBytes) {
        if (insomniaBlockBytes.length < 44) {
            return;
        }
        int[] insomniaIntArr = VpBleByteUtil.byte2HexToIntArr(insomniaBlockBytes);
        String insomniaStrArr[] = VpBleByteUtil.byte2HexToStrArr(insomniaBlockBytes);
        baseSleepBean.setInsomniaTag(insomniaIntArr[0]);
        baseSleepBean.setInsomniaScore(insomniaIntArr[1]);
        baseSleepBean.setInsomniaTimes(insomniaIntArr[2]);
        baseSleepBean.setInsomniaLength(insomniaIntArr[3]);
        List<InsomniaTimeData> insomniaBeanList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int start = VpBleByteUtil.hexStrToInt(insomniaStrArr[5 + i * 2 - 1] + insomniaStrArr[4 + i * 2]);
            int stop = VpBleByteUtil.hexStrToInt(insomniaStrArr[25 + i * 2 - 1] + insomniaStrArr[24 + i * 2]);
            TimeData startTimeData = new TimeData(start / 60, start % 60);
            TimeData stopTimeData = new TimeData(stop / 60, stop % 60);
            InsomniaTimeData insomniaTimeData = new InsomniaTimeData(startTimeData, stopTimeData);
            insomniaBeanList.add(insomniaTimeData);
        }
        baseSleepBean.setInsomniaBeanList(insomniaBeanList);

    }

    /**
     * 解析失眠的基本信息V2,长度45
     *
     * @param baseSleepBean
     * @param insomniaBlockBytes 失眠的基本信息的具体内容byte块，不包含头部,目前长度定值为45
     */
    public void pasrseInsomniaBlockV2(SleepPrecisionData baseSleepBean, byte[] insomniaBlockBytes) {
        if (insomniaBlockBytes.length < 45) {
            return;
        }
        int[] insomniaIntArr = VpBleByteUtil.byte2HexToIntArr(insomniaBlockBytes);
        String insomniaStrArr[] = VpBleByteUtil.byte2HexToStrArr(insomniaBlockBytes);
        baseSleepBean.setInsomniaTag(insomniaIntArr[0]);
        baseSleepBean.setInsomniaScore(insomniaIntArr[1]);
        baseSleepBean.setInsomniaTimes(insomniaIntArr[2]);

        int insomniaLength = VpBleByteUtil.hexStrToInt(insomniaStrArr[4] + insomniaStrArr[3]);
        baseSleepBean.setInsomniaLength(insomniaLength);

        List<InsomniaTimeData> insomniaBeanList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int start = VpBleByteUtil.hexStrToInt(insomniaStrArr[6 + i * 2 - 1] + insomniaStrArr[5 + i * 2]);
            int stop = VpBleByteUtil.hexStrToInt(insomniaStrArr[26 + i * 2 - 1] + insomniaStrArr[25 + i * 2]);
            TimeData startTimeData = new TimeData(start / 60, start % 60);
            TimeData stopTimeData = new TimeData(stop / 60, stop % 60);
            InsomniaTimeData insomniaTimeData = new InsomniaTimeData(startTimeData, stopTimeData);
            insomniaBeanList.add(insomniaTimeData);
        }
        baseSleepBean.setInsomniaBeanList(insomniaBeanList);

    }


    /**
     * 解析睡眠曲线的基本信息
     *
     * @param baseSleepBean
     * @param sleepCurveBlockBytes 睡眠曲线的基本信息的具体内容byte块，不包含头部
     */
    private void pasrseSleepCurveBlock(SleepPrecisionData baseSleepBean, byte[] sleepCurveBlockBytes) {
//        Logger.t(TAG).i("pasrseSleepCurveBlock: sleepCurveBlockBytes=" + sleepCurveBlockBytes.length);
        int length = sleepCurveBlockBytes.length;
        if (length % 2 != 0) {
            return;
        }
        String sleepCurveStrArr[] = VpBleByteUtil.byte2HexToStrArr(sleepCurveBlockBytes);
        int insomniaDuration = 0;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length / 2; i++) {
            //大端模式,其他为小端模式
            String strHex = sleepCurveStrArr[i * 2] + sleepCurveStrArr[i * 2 + 1];
            int vlaue = VpBleByteUtil.hexStrToInt(strHex);
            //取前三位表示状态，一共16位
            int first3Bit = (vlaue & 0b1110000000000000) >> 13;
            if (first3Bit > 4) {
                first3Bit = 4;
            }
            if (first3Bit == 3) {
                insomniaDuration++;
            }
            sb.append(first3Bit);
        }
        StringBuffer sbSource = new StringBuffer();
        for (int i = 0; i < sleepCurveStrArr.length; i++) {
            sbSource.append(sleepCurveStrArr[i]);
        }
        baseSleepBean.setSleepSourceStr(sbSource.toString());
        baseSleepBean.setSleepLine(sb.toString());
        int onePointDuration = baseSleepBean.getOnePointDuration();
        baseSleepBean.setInsomniaDuration(insomniaDuration * onePointDuration / 60);
//        Logger.t(TAG).i("pasrseSleepCurveBlock: sb.toString()=" + sb.toString());
    }

    /**
     * 解析睡眠的基本信息
     *
     * @param sleepPrecisionData
     * @param baseSleepBlockBytes 睡眠的基本信息的具体内容byte块，不包含头部,目前长度定值为35
     */
    private void pasrseBaseSleepBlock(SleepPrecisionData sleepPrecisionData, byte[] baseSleepBlockBytes) {
        if (baseSleepBlockBytes.length < 35) {
            return;
        }

        int[] baseSleepIntArr = VpBleByteUtil.byte2HexToIntArr(baseSleepBlockBytes);
        String baseSleepStrArr[] = VpBleByteUtil.byte2HexToStrArr(baseSleepBlockBytes);
        TimeData sleepTime = new TimeData(baseSleepIntArr[0], baseSleepIntArr[1], baseSleepIntArr[2], baseSleepIntArr[3]);
        TimeData wakeTime = new TimeData(baseSleepIntArr[4], baseSleepIntArr[5], baseSleepIntArr[6], baseSleepIntArr[7]);


        sleepPrecisionData.setPresicionDate(sleepTime, wakeTime);
        sleepPrecisionData.setSleepTag(baseSleepIntArr[8]);
        sleepPrecisionData.setGetUpScore(baseSleepIntArr[9]);
        sleepPrecisionData.setDeepScore(baseSleepIntArr[10]);
        sleepPrecisionData.setSleepEfficiencyScore(baseSleepIntArr[11]);
        sleepPrecisionData.setFallAsleepScore(baseSleepIntArr[12]);
        sleepPrecisionData.setSleepTimeScore(baseSleepIntArr[13]);
        sleepPrecisionData.setExitSleepMode(baseSleepIntArr[14]);
        sleepPrecisionData.setSleepQulity(baseSleepIntArr[15]);
        sleepPrecisionData.setWakeCount(baseSleepIntArr[16]);
        sleepPrecisionData.setDeepAndLightMode(VpBleByteUtil.hexStrToInt(baseSleepStrArr[18] + baseSleepStrArr[17]));
        sleepPrecisionData.setDeepSleepTime(VpBleByteUtil.hexStrToInt(baseSleepStrArr[20] + baseSleepStrArr[19]));
        sleepPrecisionData.setLowSleepTime(VpBleByteUtil.hexStrToInt(baseSleepStrArr[22] + baseSleepStrArr[21]));
        sleepPrecisionData.setOtherDuration(VpBleByteUtil.hexStrToInt(baseSleepStrArr[24] + baseSleepStrArr[23]));
        sleepPrecisionData.setAllSleepTime(VpBleByteUtil.hexStrToInt(baseSleepStrArr[26] + baseSleepStrArr[25]));
        sleepPrecisionData.setFirstDeepDuration(VpBleByteUtil.hexStrToInt(baseSleepStrArr[28] + baseSleepStrArr[27]));
        sleepPrecisionData.setGetUpDuration(VpBleByteUtil.hexStrToInt(baseSleepStrArr[30] + baseSleepStrArr[29]));
        sleepPrecisionData.setGetUpToDeepAve(VpBleByteUtil.hexStrToInt(baseSleepStrArr[32] + baseSleepStrArr[31]));
        sleepPrecisionData.setOnePointDuration(VpBleByteUtil.hexStrToInt(baseSleepStrArr[34] + baseSleepStrArr[33]));
        sleepPrecisionData.setAccurateType(baseSleepIntArr[35]);


    }


//    private PrecisionSleepParse.SleepItemContent parseItemSleepV1Data(byte[] byteData) throws Exception {
//        VPLogger.e("byteData length = " + byteData.length);
//        PrecisionSleepParse.SleepItemContent sleepItemContent = new PrecisionSleepParse.SleepItemContent();
//        int byteLength = byteData.length;
//        ByteBuffer buffer = ByteBuffer.wrap(byteData).order(ByteOrder.LITTLE_ENDIAN);
//        while (buffer.position() < byteLength) {
//            byte type = buffer.get();
//            switch (type) {
//                case (byte) 0xA2:
//                    byte[] crcBlockBytes = getBlockContent(buffer, "0xA2", byteData);
//                    sleepItemContent.setCrcBlockBytes(crcBlockBytes);
//                    break;
//                case (byte) 0xA3:
//                    byte[] baseSleepBlockBytes = getBlockContent(buffer, "0xA3", byteData);
//                    sleepItemContent.setBaseSleepBlockBytes(baseSleepBlockBytes);
//                    break;
//                case (byte) 0xA4:
//                    byte[] insominaBlockBytes = getBlockContent(buffer, "0xA4", byteData);
//                    sleepItemContent.setInsominaBlockBytes(insominaBlockBytes);
//                    break;
//                case (byte) 0xA7:
//                    byte[] insominaBlockBytesV2 = getBlockContent(buffer, "0xA7", byteData);
//                    sleepItemContent.setInsominaBlockBytesV2(insominaBlockBytesV2);
//                    break;
//                case (byte) 0xA5:
//                    byte[] sleepCurveBlockBytes = getBlockContent(buffer, "0xA5", byteData);
//                    sleepItemContent.setSleepCurveBlockBytes(sleepCurveBlockBytes);
//                    break;
//                case (byte) 0xA6:
//                    byte[] sleepLastNextBytes = getBlockContent(buffer, "0xA6", byteData);
////                    Logger.t(TAG).i("parseItemSleepV1Data: " + VpBleByteUtil.byte2HexForShow(sleepLastNextBytes));
//                    int[] data = VpBleByteUtil.byte2HexToIntArr(sleepLastNextBytes);
//                    if (data != null && data.length >= 1) {
//                        sleepItemContent.setLastLength(data[0]);
//                    }
//                    if (data != null && data.length >= 2) {
//                        sleepItemContent.setNextLength(data[1]);
//                    }
//                    break;
//                default:
//                    break;
//            }
//
//        }
//        return sleepItemContent;
//    }


    /**
     * 获取块内容
     *
     * @param buffer
     * @param type
     * @param byteData
     * @return Buffer Block
     */
//    private byte[] getBlockContent(ByteBuffer buffer, String type, byte[] byteData) throws ArrayIndexOutOfBoundsException {
//        VPLogger.i("getBlockContent[" + type + "]" + ConvertHelper.byte2HexForHardware(byteData));
//        byte length_low = buffer.get();
//        int length = length_low;
//        if (type.equals("0xA5")) {
//            byte length_high = buffer.get();
//            length = VpBleByteUtil.twoByteToUnsignedInt(length_high, length_low);
//        }
//        int startPosition = buffer.position();
//        int endPosition = startPosition + length;
//        VPLogger.e("getBlockContent[" + type + "]  startPosition = " + startPosition + ",endPosition = " + endPosition + ",length = " + length);
//
//        buffer.position(endPosition);
//        byte[] bufferData = copyBlockContent(byteData, length, startPosition);
//        return bufferData;
//    }


}
