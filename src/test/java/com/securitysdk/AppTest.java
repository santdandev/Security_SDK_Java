package com.securitysdk;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AppTest {


    @Test
    public void validateEncryptionAndDecryption() throws Exception {
        String plainText="Hello World";
        App app =new App();
        app.generateKeys(5,2);
        String decryptedValue=app.encryptAndDecrypt(new int[]{2,5},plainText);
        assertTrue(plainText.equals(decryptedValue));
    }
}
