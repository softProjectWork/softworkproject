package com.example.sony.myapplication.util;

public class StringToHex {
    public static String String2Hex(String str) {
        char []chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for(int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0F0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0F;
            sb.append(chars[bit]);
        }
        return sb.toString().trim();
    }
}
