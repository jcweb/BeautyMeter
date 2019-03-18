package cn.yaman.utils;

/**
 * @FileName: StringUtil
 * @Dscription: 字符串工具类
 */
public class StringUtil {

    /**
     * int 转为两字节的16进制字符串
     */
    public static String IntTo2bHexString(int num) {
        String str = "";
        str = Integer.toHexString(num);
        switch (str.length()) {
            case 1:
                str = "000" + str;
                break;
            case 2:
                str = "00" + str;
                break;
            case 3:
                str = "0" + str;
                break;
            default:
                break;
        }
        return str.toUpperCase();
    }

    /**
     * int 转为1字节的16进制字符串
     */
    public static String IntToHexString(int num) {
        String str = "";
        str = Integer.toString(num, 16);
        switch (str.length()) {
            case 1:
                str = "0" + str;
                break;
            default:
                break;
        }
        return str.toUpperCase();
    }

    public static byte xor(byte[] bytes) {
        byte csXor = 0;
        if(null!=bytes&&bytes.length>0){

            int len = bytes.length;
            for (int i = 0; i < len; i++) {
                csXor ^= bytes[i];
            }
        }else{
            return 0;
        }


        return csXor;
    }

    /**
     * 16进制字符串转成byte数组
     *
     * @param data
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String data) {
        String hexString = data.toUpperCase().trim();
        if (hexString.length() % 2 != 0) {
            return null;
        }
        byte[] retData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); ++i) {
            char hex_char1 = hexString.charAt(i);
            int int_ch1;
            if ((hex_char1 >= '0') && (hex_char1 <= '9')) {
                int_ch1 = (hex_char1 - '0') * 16;
            } else {
                if ((hex_char1 >= 'A') && (hex_char1 <= 'F'))
                    int_ch1 = (hex_char1 - '7') * 16;
                else
                    return null;
            }
            ++i;
            char hex_char2 = hexString.charAt(i);
            int int_ch2;
            if ((hex_char2 >= '0') && (hex_char2 <= '9')) {
                int_ch2 = hex_char2 - '0';
            } else {
                if ((hex_char2 >= 'A') && (hex_char2 <= 'F'))
                    int_ch2 = hex_char2 - '7';
                else
                    return null;
            }
            int int_ch = int_ch1 + int_ch2;
            retData[(i / 2)] = (byte) int_ch;
        }
        return retData;
    }

    /**
     * bytes转换成十六进制字符串
     *
     * @param b byte数组
     */
    public static String byteToHexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        if (null != b) {
            if (null != b && b.length > 0) {
                for (int n = 0; n < b.length; n++) {
                    stmp = Integer.toHexString(b[n] & 0xFF);
                    sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
                    sb.append("");
                }
            }
        }
        return sb.toString().toUpperCase().trim();
    }

    /**
     * 两个字节转int
     */
    public static int TwoByteToInteger(byte b1, byte b2) {
        int humidity = 0;
        humidity = (int)(((b2&0xFF) << 8) | (b1&0xFF));
        return humidity;
    }

    /**
     * 1个字节转int
     */
    public static int byteToInteger(byte b) {
        int value;
        value = b & 0xff;
        return value;
    }

    /**
     * int转4字节数组(高字节在前，低字节在后)
     */
    public static byte[] IntToByteArray(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     * 4字节数组(高字节在前，低字节在后)转int
     */
    public static int ByteArrayToInt(byte[] bArr) {
        if (bArr.length != 4) {
            return 0;
        }
        return (int) ((((bArr[1] & 0xff) << 24)
                | ((bArr[1] & 0xff) << 16)
                | ((bArr[2] & 0xff) << 8)
                | ((bArr[3] & 0xff) << 0)));

    }

    /**
     * 浮点转换为字节
     *
     * @param f
     * @return
     */
    public static byte[] float2byte(float f) {
        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }
        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }
        return dest;
    }
    /**
     * 字节转换为浮点
     *
     * @param b 字节（至少4个字节）
     * @param index 开始位置
     * @return
     */
    public static float byte2float(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }
}
