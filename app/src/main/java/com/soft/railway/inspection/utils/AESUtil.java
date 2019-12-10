package com.soft.railway.inspection.utils;

import android.text.TextUtils;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AESUtil {

    public static String getKey(){
        String str="";
        try{
            KeyGenerator generator=KeyGenerator.getInstance("AES");
            SecureRandom secureRandom=SecureRandom.getInstance("SHA1PRNG");
            generator.init(256,secureRandom);
            SecretKey secretKey=generator.generateKey();
            str=Base64Utils.encodeToString(secretKey.getEncoded());
        }catch (NoSuchAlgorithmException e){
        }
        return str;
    }

    public static SecretKey getSecretKey(String keyStr){
        byte[] key=Base64Utils.decodeFromString(keyStr);
        SecretKeySpec secretKey=new SecretKeySpec(key,"AES");
        return secretKey;
    }

    public static String AESDecode(String data)throws Exception{
        if(TextUtils.isEmpty(data)){
            return "";
        }
        byte[] bytes=Base64Utils.decode(data.getBytes("utf-8"));
        SecretKey secretKey=getSecretKey(DataUtil.httpKey);
        Cipher cipher=Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,secretKey);
        return new String(cipher.doFinal(bytes),"UTF-8");
    }

    public static String  AESEncode(String data)throws Exception{
        if(TextUtils.isEmpty(data)){
            return "";
        }
        byte[] bytes=data.getBytes("utf-8");
        SecretKey secretKey=getSecretKey(DataUtil.httpKey);
        Cipher cipher=Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,secretKey);
        return Base64Utils.encodeToString(cipher.doFinal(bytes));
    }
}
