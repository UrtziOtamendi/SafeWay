package otamendi.urtzi.com.safeway.Utils;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt {

    private String iv = "32198eh21ej09u1j";            // Dummy iv (CHANGE IT!)
    private IvParameterSpec ivspec;
    private SecretKeySpec keyspec;
    private Cipher cipher;
    private String SecretKey = FirebaseAuth.getInstance().getCurrentUser().getUid();     // Dummy secretKey (CHANGE IT!)

    public Encrypt() {
        ivspec = new IvParameterSpec(iv.getBytes());
        keyspec = new SecretKeySpec(SecretKey.getBytes(), "AES");


        try {

            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            Log.e("EncryptModule","------->"+e.toString());
        } catch (NoSuchPaddingException e) {
            Log.e("EncryptModule","------->"+e.toString());
        }
    }

    public byte[] encrypt(String text, String salt)  {

        byte[] encrypted = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

            encrypted = cipher.doFinal(padString(text+salt).getBytes());
        } catch (Exception e) {
            Log.e("EncryptModule","------->"+e.toString());
        }
        return encrypted;
    }

    public byte[] decrypt(String text,String salt) {

        byte[] decryptedSalty = null;
        byte[] decrypted = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            decryptedSalty = cipher.doFinal(hexToBytes(text));
            int totalLength = decryptedSalty.length;
            decrypted= new byte[(totalLength -salt.getBytes().length)];
            for(int i=0; i<(totalLength -salt.getBytes().length); i++){
                decrypted[i]=decryptedSalty[i];
            }


        } catch (Exception e) {
           Log.e("EncryptModule","------->"+e.toString());
        }
        return decrypted;
    }

    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {

            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(
                        str.substring(i * 2, i * 2 + 2), 16);

            }
            return buffer;
        }
    }

    private static String padString(String source) {
        char paddingChar = 0;
        int size = 16;
        int x = source.length() % size;
        int padLength = size - x;
        for (int i = 0; i < padLength; i++) {
            source += paddingChar;
        }
        return source;
    }


}
