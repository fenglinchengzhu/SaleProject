package com.goldwind.app.help.util;

import com.goldwind.app.help.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

/***
 * DES文件加密&解密 可以实现android和window的文件互通
 */
public class FileDesUtil {
    /**
     * 加密解密的key
     */
    private Key mKey;
    /**
     * 解密的密码
     */
    private Cipher mDecryptCipher;
    /**
     * 加密的密码
     */
    private Cipher mEncryptCipher;

    public FileDesUtil(String key) throws Exception {
        initKey(key);
        initCipher();
    }

    public static void main(String[] args) throws Exception {
        FileDesUtil fileDES = new FileDesUtil("zi85mc8F0m34S2F%*");
        fileDES.doDecryptFile("/Users/houdp/Documents/des/jiami/des_ffplay.mp4", "/Users/houdp/Documents/des/jiemi/en2_ffplay.mp4");
    }

    /**
     * 创建一个加密解密的key
     *
     * @param keyRule
     */
    public void initKey(String keyRule) {
        byte[] keyByte = keyRule.getBytes();
        // 创建一个空的八位数组,默认情况下为0
        byte[] byteTemp = new byte[8];
        // 将用户指定的规则转换成八位数组
        for (int i = 0; i < byteTemp.length && i < keyByte.length; i++) {
            byteTemp[i] = keyByte[i];
        }
        mKey = new SecretKeySpec(byteTemp, "DES");
    }

    /***
     * 初始化加载密码
     *
     * @throws Exception
     */
    private void initCipher() throws Exception {
        mEncryptCipher = Cipher.getInstance("DES");
        mEncryptCipher.init(Cipher.ENCRYPT_MODE, mKey);

        mDecryptCipher = Cipher.getInstance("DES");
        mDecryptCipher.init(Cipher.DECRYPT_MODE, mKey);
    }

    /**
     * 加密文件
     *
     * @param in
     * @param savePath 加密后保存的位置
     */
    public void doEncryptFile(InputStream in, String savePath) {
        if (in == null) {
            System.out.println("inputstream is null");
            return;
        }
        try {
            CipherInputStream cin = new CipherInputStream(in, mEncryptCipher);
            OutputStream os = new FileOutputStream(savePath);
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = cin.read(bytes)) > 0) {
                os.write(bytes, 0, len);
                os.flush();
            }
            os.close();
            cin.close();
            in.close();
            System.out.println("加密成功");
        } catch (Exception e) {
            System.out.println("加密失败");
            e.printStackTrace();
        }
    }

    /**
     * 加密文件
     *
     * @param filePath 需要加密的文件路径
     * @param savePath 加密后保存的位置
     * @throws FileNotFoundException
     */
    public void doEncryptFile(String filePath, String savePath) throws FileNotFoundException {
        doEncryptFile(new FileInputStream(filePath), savePath);
    }

    /**
     * 解密文件
     */
    public void doDecryptFile(InputStream in, String savePath) {
        if (in == null) {
            System.out.println("inputstream is null");
            return;
        }
        try {
            CipherInputStream cin = new CipherInputStream(in, mDecryptCipher);
            OutputStream os = new FileOutputStream(savePath);
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = cin.read(bytes)) > 0) {
                os.write(bytes, 0, len);
                os.flush();
            }
            os.close();
            cin.close();
            in.close();
            System.out.println("解密成功");
        } catch (Exception e) {
            System.out.println("解密失败");
            e.printStackTrace();
        }
    }

    /**
     * 解密文件
     *
     * @throws IOException
     */
    public void doDecryptFile(InputStream in, OutputStream os) throws IOException {
        if (in == null || os == null) {
            return;
        }
        File file = new File(Constant.BASE_DECODE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        CipherInputStream cin = new CipherInputStream(in, mDecryptCipher);
        byte[] bytes = new byte[1024 * 8];
        int len = -1;
        while ((len = cin.read(bytes)) > 0) {
            os.write(bytes, 0, len);
            os.flush();
        }
        os.close();
        cin.close();
        in.close();
    }

    /**
     * 解密文件
     *
     * @param filePath 文件路径
     * @throws Exception
     */
    public void doDecryptFile(String filePath, String savePath) throws Exception {
        doDecryptFile(new FileInputStream(filePath), savePath);
    }
}