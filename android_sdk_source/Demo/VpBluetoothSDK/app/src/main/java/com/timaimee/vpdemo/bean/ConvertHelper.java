package com.timaimee.vpdemo.bean;

import com.orhanobut.logger.Logger;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author DIM
 * @version V1.0
 * @date 2015/06/03
 */
public class ConvertHelper {
    static final char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private final static String TAG = ConvertHelper.class.getSimpleName();

    /**
     * 将两个byte 合并转化为一个 hex 数据
     *
     * @param high 高位数据
     * @param low  低位数据
     * @return 返回的数据 高位在前，低位在后。
     * 02b2
     * b2 02
     */
    public static int mergeByte2Hex(byte high, byte low) {
        return (int) ((high & 0xff) << 8 | (low & 0xff));
    }

    /**
     * 将一个 int 型数据转化为两个byte 数据
     *
     * @param value int 数值
     * @return 两个字节的byte 数组 小端在前
     * <p>
     * 98 = 0x63 = 0b0110 0011 ->8 =0b0000 0000
     * 98 = 0x63 = 0b0110 0011 & 11111111 = 0b0110 0011
     * 0x63 0x00
     */
    public static byte[] intToByteArray(int value) {
        byte[] mValue = new byte[2];
        mValue[1] = (byte) ((value >> 8) & 0xFF); // 右移8位
        mValue[0] = (byte) (value & 0xFF);
        return mValue;
    }

    /**
     * new byte[]{(byte)0x01,(byte)0x01,(byte)0x05,(byte)0x06,(byte)0x01,(byte)0x01}->010105060101
     * byte 数组转换为十六进制的字符串
     *
     * @param b 输入需要转换的byte数组
     * @return 返回十六进制 字符串
     */
    public static String byte2Hex(byte[] b) {
        char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] newChar = new char[b.length * 2];
        for (int i = 0; i < b.length; i++) {
            newChar[2 * i] = hex[(b[i] & 0xf0) >> 4];
            newChar[2 * i + 1] = hex[b[i] & 0xf];

        }
        return new String(newChar);
    }

    /**
     * byte 数组转换为十六进制的字符串
     *
     * @param b 输入需要转换的byte数组 输入需要转换的byte数组
     * @return 返回十六进制 字符串 101020203030--->10,10,20,20,30,30
     */
    public static String byte2HexForShow(byte[] b) {
        if (b == null)
            return "null";
        int iMax = b.length - 1;
        if (iMax == -1)
            return "[]";
        char[] newChar = new char[b.length * 2];
        for (int i = 0; i < b.length; i++) {
            newChar[2 * i] = hex[(b[i] & 0xf0) >> 4];
            newChar[2 * i + 1] = hex[b[i] & 0xf];

        }
        return spilt2wordstr(new String(newChar), ",");
    }

    /**
     * byte 数组转换为十六进制的字符串
     *
     * @param b 输入需要转换的byte数组 输入需要转换的byte数组
     * @return 返回十六进制 字符串 101020203030--->10:10:20:20:30:30:
     */
    public static String byte2HexForMac(byte[] b) {
        char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] newChar = new char[b.length * 2];
        for (int i = 0; i < b.length; i++) {
            newChar[2 * i] = hex[(b[i] & 0xf0) >> 4];
            newChar[2 * i + 1] = hex[b[i] & 0xf];

        }
        return spilt2wordstr(new String(newChar), ":");
    }

    /**
     * @param b 输入需要转换的byte数组
     * @return 101020203030[全部20个字节]--->10 10 20 20 30 30
     */
    public static String byte2HexForHardware(byte[] b) {
        char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] newChar = new char[b.length * 2];
        for (int i = 0; i < b.length; i++) {
            newChar[2 * i] = hex[(b[i] & 0xf0) >> 4];
            newChar[2 * i + 1] = hex[b[i] & 0xf];

        }
        return spilt2wordstr(new String(newChar), " ");
    }

    /**
     * @param b 输入需要转换的byte数组
     * @return 101020203030[全部20个字节]--->101020203030
     */
    public static String byte2HexForIOS(byte[] b) {
        char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] newChar = new char[b.length * 2];
        for (int i = 0; i < b.length; i++) {
            newChar[2 * i] = hex[(b[i] & 0xf0) >> 4];
            newChar[2 * i + 1] = hex[b[i] & 0xf];

        }
        return new String(newChar);
    }

    /**
     * 用于显示16位的ADC的实时波形
     *
     * @param b 输入需要转换的byte数组
     * @return 101020203030-->int[4112,8224,12336]
     */
    public static int[] byte2HexForAdcTranslateIntArr(byte[] b) {
        int[] rateViewArr = new int[8];
        String[] strArr = ConvertHelper.byte2HexToStrArr(b);
        for (int i = 0; i < rateViewArr.length; i++) {
            // 低位在前，高位在后
            String s = strArr[i * 2 + 1] + strArr[i * 2];
            rateViewArr[i] = Integer.valueOf(s, 16);
        }
        return rateViewArr;
    }

    /**
     * 用于显示16位的ADC的实时波形
     *
     * @param b 输入需要转换的byte数组
     * @return 101020203030-->int[4112,8224,12336]
     */
    public static int[] byte2HexForAdcTranslateShortArr(byte[] b) {
        int[] rateViewArr = new int[8];
        String[] strArr = ConvertHelper.byte2HexToStrArr(b);
        for (int i = 0; i < rateViewArr.length; i++) {
            // 低位在前，高位在后
            String s = strArr[i * 2 + 1] + strArr[i * 2];
            rateViewArr[i] = Integer.valueOf(s, 16).shortValue();
        }
        return rateViewArr;
    }

    /**
     * @param b 输入需要转换的byte数组
     * @return 101020203030--->0x10,0x10,0x20,0x20,0x30,0x30
     */
    public static String byte2HexForHardwareDebug(byte[] b) {
        char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] newChar = new char[b.length * 2];
        for (int i = 0; i < b.length; i++) {
            newChar[2 * i] = hex[(b[i] & 0xf0) >> 4];
            newChar[2 * i + 1] = hex[b[i] & 0xf];

        }
        return spilt2wordstr(new String(newChar), ",", "0x");
    }

    public static String byte2HexForHardwareDebugDaily(byte[] b) {
        char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] newChar = new char[b.length * 2];
        for (int i = 0; i < b.length; i++) {
            newChar[2 * i] = hex[(b[i] & 0xf0) >> 4];
            newChar[2 * i + 1] = hex[b[i] & 0xf];
        }
        return spilt2wordDaily(new String(newChar), ",", "0x");
    }

    public static int hexStrToInt(String str) {
        return Integer.valueOf(str, 16);
    }

    /**
     * byte 数组转换为十进制的字符串
     *
     * @param value 输入需要转换的byte数组
     * @return 返回十进制 字符串
     */
    public static String byte2HexToIntForShow(byte[] value) {
        String[] str = byte2HexToStrArr(value);
        int[] data = new int[str.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = Integer.valueOf(str[i], 16);
        }
        StringBuilder sb = new StringBuilder();
        for (int datum : data) {
            sb.append(datum).append(",");
        }
        return sb.toString();
    }

    /**
     * byte 数组转换为int的数组
     *
     * @param value 输入需要转换的byte数组
     * @return 返回十六进制 字符串
     */
    public static int[] byte2HexToIntArr(byte[] value) {
        if (value == null) {
            return null;
        }
        String[] str = byte2HexToStrArr(value);
        int[] data = new int[str.length];

        for (int i = 0; i < data.length; i++) {
            data[i] = Integer.valueOf(str[i], 16);
        }
        return data;
    }

    /**
     * 16进制 数组转换为int的数组
     *
     * @param str 输入需要转换的byte数组
     * @return 返回十六进制 字符串
     */
    public static int[] HexStrArrToIntArr(String[] str) {
        int[] data = new int[str.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = Integer.valueOf(str[i], 16);
        }
        return data;
    }

    /**
     * byte 数组转换为十六进制的字符串数组
     *
     * @param b 输入需要转换的byte数组 输入需要转换的byte数组
     * @return 返回十六进制 字符串
     */
    public static String[] byte2HexToStrArr(byte[] b) {
        char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] newChar = new char[b.length * 2];
        for (int i = 0; i < b.length; i++) {
            newChar[2 * i] = hex[(b[i] & 0xf0) >> 4];
            newChar[2 * i + 1] = hex[b[i] & 0xf];
        }
        return spilt2word(new String(newChar));
    }

//    public static int fourBytes2Int(byte[] b, int startIndex) {
//        if (b.length > startIndex + 4) {
//
//        }
//    }

    /**
     * @param hexString
     * @return 将十六进制转换为字节数组
     */
    public static byte[] HexStringToBinary(String hexString) {
        if (hexString != null) {
            byte[] result = new byte[hexString.length() / 2];
            for (int i = 0; i < hexString.length() / 2; ++i)
                result[i] = (byte) (Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16) & 0xff);
            return result;
        } else
            return null;
    }

    /**
     * 将字符串两两分割
     *
     * @param str
     * @return
     */
    public static String[] spilt2word(String str) {
        int m = str.length() / 2;
        if (m * 2 < str.length()) {
            m++;
        }
        String[] strs = new String[m];
        int j = 0;
        for (int i = 0; i < str.length(); i++) {
            if (i % 2 == 0) {
                strs[j] = str.charAt(i) + "";
            } else {
                strs[j] = strs[j] + str.charAt(i);
                j++;
            }
        }
        return strs;
    }

    /**
     * 将字符串两两分割
     *
     * @param str
     * @return
     */
    public static String spilt2wordstr(String str, String rex) {
        int m = str.length() / 2;
        if (m * 2 < str.length()) {
            m++;
        }
        String[] strs = new String[m];
        int j = 0;
        for (int i = 0; i < str.length(); i++) {
            if (i % 2 == 0) {
                strs[j] = str.charAt(i) + "";
            } else {
                strs[j] = strs[j] + str.charAt(i);
                j++;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String s : strs) {
            sb.append(s).append(rex);
        }
        return (sb.toString());
    }

    /**
     * 将字符串两两分割
     *
     * @param str
     * @return
     */
    public static String spilt2wordDaily(String str, String rex, String head) {
        int m = str.length() / 2;
        if (m * 2 < str.length()) {
            m++;
        }
        String[] strs = new String[m];
        int j = 0;
        for (int i = 0; i < str.length(); i++) {
            if (i % 2 == 0) {
                strs[j] = str.charAt(i) + "";
            } else {
                strs[j] = strs[j] + str.charAt(i);
                j++;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 3; i < strs.length - 1; i++) {
            sb.append(head).append(strs[i]).append(rex);
        }
        return (sb.toString());
    }

    /**
     * 将字符串两两分割
     *
     * @param str
     * @return
     */
    public static String spilt2wordstr(String str, String rex, String head) {
        int m = str.length() / 2;
        if (m * 2 < str.length()) {
            m++;
        }
        String[] strs = new String[m];
        int j = 0;
        for (int i = 0; i < str.length(); i++) {
            if (i % 2 == 0) {
                strs[j] = str.charAt(i) + "";
            } else {
                strs[j] = strs[j] + str.charAt(i);
                j++;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (String s : strs) {
            sb.append(head).append(s).append(rex);
        }
        return (sb.toString());
    }

    public static byte[] intToBytes(int res) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (res >>> (24 - i * 8));
        }
        return b;
    }

//    public static byte[] intTo4Bytes(int value) {
//        byte[] bytes = new byte[4];
//        bytes[0] = (byte) (value >> 24);
//        bytes[1] = (byte) (value >> 16);
//        bytes[2] = (byte) (value >> 8);
//        bytes[3] = (byte) value;
//        return bytes;
//    }

    public static byte loUint16(short v) {
        return (byte) (v & 0xFF);
    }

    public static byte hiUint16(short v) {
        return (byte) (v >> 8);
    }

    public static String getNickString(String title, int length) {
        String str = "";
        try {
            byte[] value = getNickByte(title, length);
            str = new String(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static byte[] getNickByte(String title, int length) {
        try {
            byte[] aArray = title.getBytes("UTF-8");
            Logger.t(TAG).i("nickName=" + title);
            Logger.t(TAG).i("byte=" + ConvertHelper.byte2HexForShow(aArray));
            if (aArray.length > length) {
                Logger.t(TAG).i("---");
                byte[] aimByteUtf = new byte[length];
                System.arraycopy(aArray, 0, aimByteUtf, 0, length);
                Logger.t(TAG).i("aiby:" + ConvertHelper.byte2HexForShow(aimByteUtf));
                title = new String(aimByteUtf, "UTF-8");
                Logger.t(TAG).i("astr:" + title);

                //转以后，可能会有乱码
                byte[] bArray = title.getBytes();
                Logger.t(TAG).i("---");
                Logger.t(TAG).i("biby:" + ConvertHelper.byte2HexForShow(bArray));
                final int lengthNew = bArray.length;
                if (bArray.length > 6
                        && (bArray[lengthNew - 6] == (byte) 0xEF && bArray[lengthNew - 5] == (byte) 0xBF && bArray[lengthNew - 4] == (byte) 0xBD)
                        && (bArray[lengthNew - 3] == (byte) 0xEF && bArray[lengthNew - 2] == (byte) 0xBF && bArray[lengthNew - 1] == (byte) 0xBD)) {
                    //astr:测试长备注婚�� 华为手机NB
                    byte[] dArray = new byte[lengthNew - 6];
                    System.arraycopy(bArray, 0, dArray, 0, lengthNew - 6);
                    return dArray;
                } else if (bArray.length > 3 && bArray[lengthNew - 3] == (byte) 0xEF && bArray[lengthNew - 2] == (byte) 0xBF && bArray[lengthNew - 1] == (byte) 0xBD) {
                    //astr:测试长备注婚�
                    byte[] cArray = new byte[lengthNew - 3];
                    System.arraycopy(bArray, 0, cArray, 0, lengthNew - 3);
                    Logger.t(TAG).i("beq3:" + ConvertHelper.byte2HexForShow(cArray));
                    Logger.t(TAG).i("---");
                    return cArray;
                } else if (bArray.length > 2 && bArray[lengthNew - 2] == (byte) 0xEF && bArray[lengthNew - 1] == (byte) 0xBF) {
                    byte[] cArray = new byte[lengthNew - 2];
                    System.arraycopy(bArray, 0, cArray, 0, lengthNew - 2);
                    Logger.t(TAG).i("beq2:" + ConvertHelper.byte2HexForShow(cArray));
                    Logger.t(TAG).i("---");
                    return cArray;
                } else {
                    Logger.t(TAG).i("---");
                    return bArray;
                }
            } else {
                return aArray;
            }
        } catch (UnsupportedEncodingException e) {
            Logger.t(TAG).i("cmdlist->UnsupportedEncodingException");
            e.printStackTrace();
        }
        return new byte[]{};
    }


    /**
     * 取前9位 getFirstBit16(18894,9)=147
     *
     * @param value
     * @param position
     * @return
     */
    public static int getFirstBit16(int value, int position) {
//        return value / (int) Math.pow(2, (16 - position));
        return value >> (16 - position);
    }

    public static int getFirstBit24(int value, int position) {
//        return value / (int) Math.pow(2, (16 - position));
        return value >> (24 - position);
    }


    /**
     * 取后4位 getLastBit16(18894,4)=14
     *
     * @param value
     * @param position
     * @return
     */
    public static int getLastBit16(int value, int position) {
        return value % (int) Math.pow(2, position);
    }


    /**
     * 取中间3位,此后辍为后4位 getMiddleBit16(18894,4，3)=4
     *
     * @param value
     * @param lastPosition
     * @param middlePosition
     * @return
     */
    public static int getMiddleBit16(int value, int lastPosition, int middlePosition) {
        int values = getFirstBit16(value, lastPosition);
        return getLastBit16(values, middlePosition);
    }

    /**
     * 将两个byte数据转化为有符号int
     *
     * @param high : 高八位
     * @param low  : 低八位
     * @return
     */
    public static int twoByteToSignedInt(byte high, byte low) {
        return (high << 8) | low;
    }

    /**
     * 将两个byte数据转化为无符号int
     *
     * @param high : 高八位
     * @param low  : 低八位
     * @return
     */
    public static int twoByteToUnsignedInt(byte high, byte low) {
        return ((high << 8) & 0xffff) | (low & 0x00ff);
    }

    /**
     * int 转换为四字节小端序 (Little-Endian) 字节数组
     * @param value
     * @return
     */
    public static byte[] intTo4BytesLittleEndian(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);  // 设置为小端序
        buffer.putInt(value);
        return buffer.array();
    }

    public static byte[] intTo2BytesLittleEndian(int value) {
        byte[] result = new byte[2]; // 2字节
        result[0] = (byte) (value & 0xFF); // 低位字节
        result[1] = (byte) ((value >> 8) & 0xFF); // 高位字节
        return result;
    }

    /**
     * 将int转换为两个byte
     * 62589 - 65535
     *
     * @param numInt : 实际只取其中的低16位二进制数
     * @return 长度为2的byte数组 ，byte[0]为高8位，byte[1]为低八位
     */
    public static byte[] intToTwoByte(int numInt) {
        byte[] rest = new byte[2];
//        if (numInt < -32768 || numInt > 32767) {
//            return null;
//        }
        if (numInt > 65535) return null;
        rest[0] = (byte) (numInt >> 8);//高8位
        rest[1] = (byte) (numInt & 0x00ff);//低8位

        return rest;
    }

    /**
     * 将int转换为两个byte
     * 62589 - 65535
     *
     * @param numInt : 实际只取其中的低16位二进制数
     * @return 长度为2的byte数组 ，byte[0]为高8位，byte[1]为低八位
     */
    public static byte[] intTo2Bytes(int numInt) {
        byte[] rest = new byte[2];
        if (numInt > 65535) return null;
        rest[0] = (byte) (numInt >> 8);//高8位
        rest[1] = (byte) (numInt & 0x00ff);//低8位

        return rest;
    }

    public static byte[] byteMerger(byte[] bt1, byte[] bt2) {
        if (bt1 == null && bt2 == null) {
            return new byte[0];
        }
        if (bt1 == null) {
            return bt2;
        }
        if (bt2 == null) {
            return bt1;
        }
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    /**
     * 合并高低位，2位
     *
     * @param high 02
     * @param low  32
     * @return 0x0232 = 562
     */
    public static int twoByteToInt(byte high, byte low) {
        int high_id = (high & 0x00ff);
        int low_id = (low & 0x00ff);
        return (high_id << 8) | low_id;
    }

    /**
     * 合并高低位，2位
     *
     * @return 0x0232 = 562
     */
    public static int twoByteToInt(byte[] data) {
        if (data == null || data.length != 2) {
            throw new IllegalArgumentException("The byte array must be exactly 2 bytes long");
        }
        int high_id = (data[1] & 0x00ff);
        int low_id = (data[0] & 0x00ff);
        return (high_id << 8) | low_id;
    }

    /**
     * 把字符串去空格后转换成byte数组(e.g: "37 5a"-->{0x37, 0x5A})
     * 16
     * b87e755657d11c9b
     * ae9a6565e799ee00
     * @param str 转换的字符串
     * @return
     */
    public static byte[] string2ByteArray(String str) {
        String s = str.replace(" ", "");
        int stringLen = s.length();
        int len = stringLen / 2;
        if (stringLen % 2 == 1) {
            s = "0" + s;
            stringLen++;
            len++;
        }
        byte[] data = new byte[len];
        for (int i = 0; i < len; i++) {
            data[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
        }
        return data;
    }

    public static int mmolL2mgdL(float value) {
        return (int) (value * 18f + 0.5);
    }

    public static float mgdL2mmolL(int value) {
        return value / 18f;
    }

    public static float mgdL2μmolL(float mgdL) {
        return mgdL * 59.5f;
    }

    public static float μmolL2mgdL(float μmolL) {
        return μmolL * 0.0168067F;
    }

    //    public static final float CONVERSION_RATIO_1 = 38.67F;
//    public static final float CONVERSION_RATIO_2 = 88.545F;
//    public static final float CONVERSION_RATIO_3 = 18F;
//
//    public static String float2String_2(float value) {
//        return String.format(Locale.US, "%.2f", value);
//    }
//
//    /**
//     * 尿酸 URIC_ACID      ：1mg/dL = 59.5μmol/L
//     * 总胆固醇 TCHO        ：1mg/dL = 38.67mmol/L
//     * 甘油三酯 TAG         ：1mg/dL = 88.545mmol/L
//     * 高密度脂蛋白 HDL     ：1mg/dL = 38.67mmol/L
//     * 低密度脂蛋白 LDL     ：1mg/dL = 38.67mmol/L
//     *
//     * @param type
//     * @param mmolLValue
//     * @return
//     */
//    public static float mmolL2mgdL(DataType type, float mmolLValue) {
//        switch (type) {
//            case BLOOD_GLUCOSE:
//                return mmolLValue / CONVERSION_RATIO_3;
//            case TAG:
//                return mmolLValue / CONVERSION_RATIO_2;
//            case TCHO:
//            case HDL:
//            case LDL:
//                return mmolLValue / CONVERSION_RATIO_1;
//        }
//        return mmolLValue;
//    }
//
//    public static String mmolL2mgdLStr(DataType type, float mmolLValue) {
//        float mgdLValue = mmolL2mgdL(type, mmolLValue);
//        return String.format(Locale.US, "%.2f", mgdLValue);
//    }
//
//    public static float mgdL2mmolL(DataType type, float mgdLValue) {
//        switch (type) {
//            case BLOOD_GLUCOSE:
//                return mgdLValue * CONVERSION_RATIO_3;
//            case TAG:
//                return mgdLValue * CONVERSION_RATIO_2;
//            case TCHO:
//            case HDL:
//            case LDL:
//                return mgdLValue * CONVERSION_RATIO_1;
//        }
//        return mgdLValue;
//    }
//
//    public static enum DataType {
//        BLOOD_GLUCOSE("血糖"),
//        URIC_ACID("尿酸"),
//        TCHO("总胆固醇"),
//        TAG("甘油三酯"),
//        HDL("高密度脂蛋白"),
//        LDL("低密度脂蛋白");
//        String name;
//
//        DataType(String name) {
//            this.name = name;
//        }
//    }
    //大图长度：198224 data = 00,03,06,50,
    public static byte[] intTo4Bytes(int value) {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) (value >> 24);
        bytes[2] = (byte) (value >> 16);
        bytes[1] = (byte) (value >> 8);
        bytes[0] = (byte) value;
        return bytes;
    }

    //10 - 7 4
    public static byte[] subByteArray(byte[] bytes, int startIndex, int length) {
        if (bytes.length >= startIndex + length) {
            byte[] data = new byte[length];
            System.arraycopy(bytes, startIndex, data, 0, length);
            return data;
        } else {
            return null;
        }
    }

    /**
     * byte数组转int
     *
     * @param _4bytes byte数组
     * @return 高位字节在前（也就是大端序，Big Endian）
     */
    public static int _4Bytes2Int(byte[] _4bytes) {
        if (_4bytes.length != 4) {
            throw new IllegalArgumentException("The byte array must be exactly 4 bytes long");
        }
        return (_4bytes[0] & 0x000000ff) | ((_4bytes[1] << 8) & 0x0000ff00) | ((_4bytes[2] << 16) & 0x00ff0000) | ((_4bytes[3] << 24) & 0xff000000);
    }

    //E8,01,01,B5,04,00,00,08,00,00,
    public static int _4Bytes2Int(byte[] bytes, int startIndex) {
        byte[] _4bytes = subByteArray(bytes, startIndex, 4);
        if (_4bytes != null && _4bytes.length == 4) {
            return _4Bytes2Int(_4bytes);
        } else {
            throw new IllegalArgumentException("The byte array must be exactly 4 bytes long");
        }
    }

    public static int unsignedByteToInt(byte bt) {
        return bt & 0xFF;
    }
}

