package com.example.BaiTuanTong_Frontend.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    public static String encryp(String raw){
        String md5Str = raw;
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(raw.getBytes());
            byte[] encryContext = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for(int offset=0;offset<encryContext.length;offset++){
                i=encryContext[offset];
                if(i<0){
                    i+=256;
                }
                if(i<16){
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            md5Str = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5Str;
    }
}
