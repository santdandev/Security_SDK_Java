package com.securitysdk;

import com.codahale.shamir.Scheme;
import com.securitysdk.io.InputReader;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class App {

    private static Scheme scheme;
    private final static Logger LOGGER = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws Exception {
        App app =new App();
        LOGGER.info("***** Welcome To Input Encryptor/Decryptor using Key Sharding Scheme *****");
        LOGGER.info("***** Please Enter your Input *****");
        String input = InputReader.readAndReturnInput();
        LOGGER.info("***** Processing Started *****");
        app.generateKeys(5,2);
        String decryptedString = app.encryptAndDecrypt(new int[] { 2, 4 }, input);
        LOGGER.info("***** Your input has been decrypted successfully *****");
    }

    public void generateKeys(int shards,int numberOfShardsRequired) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(4096);
        scheme = new Scheme(new SecureRandom(), shards, numberOfShardsRequired);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        writeKeysInFile(publicKey,privateKey);
    }

    public String encryptAndDecrypt(int[] keyNumbers,String plainText) throws Exception{
        byte[] publicKeyBytes = Files.readAllBytes(Paths.get("Public.TXT"));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        byte[] cipherTextArray = encrypt(plainText, kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes)));
        LOGGER.info("***** Recreating Keys from shards *****");
        byte[] privateKeyBytes = recreateKey(keyNumbers);
        PrivateKey recreatedPrivateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        return decrypt(cipherTextArray, recreatedPrivateKey);
    }

    private void writeKeysInFile(PublicKey publicKey,PrivateKey privateKey){
        try {
            File file = new File("Public.TXT");
            OutputStream stream = new FileOutputStream(file);
            stream.write(publicKey.getEncoded());
            stream.close();

            final byte[] secret = privateKey.getEncoded();
            final Map<Integer, byte[]> parts = scheme.split(secret);
            int k=1;
            for(Map.Entry<Integer,byte[]>  entry : parts.entrySet()){
                File privateFile = new File("Shard"+k+".TXT");
                OutputStream pos = new FileOutputStream(privateFile);
                pos.write(entry.getValue());
                pos.close();
                k++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] recreateKey(int... selectKeys) throws IOException {
        Map<Integer,byte[]> parts=new HashMap<>();
        for(int i=0;i<selectKeys.length;i++){
            parts.put(selectKeys[i], Files.readAllBytes(Paths.get("Shard" + selectKeys[i] + ".txt")));
        }
        return scheme.join(parts);
    }

    public byte[] encrypt(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING");

        //Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        //Perform Encryption
        byte[] cipherText = cipher.doFinal(plainText.getBytes());

        return cipherText;
    }

    public String decrypt(byte[] cipherTextArray, PrivateKey privateKey) throws Exception {
        //Get Cipher Instance RSA With ECB Mode and OAEPWITHSHA-512ANDMGF1PADDING Padding
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-512ANDMGF1PADDING");

        //Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        //Perform Decryption
        byte[] decryptedTextArray = cipher.doFinal(cipherTextArray);

        return new String(decryptedTextArray);
    }


}

