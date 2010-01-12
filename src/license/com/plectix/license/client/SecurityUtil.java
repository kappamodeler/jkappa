package com.plectix.license.client;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtil {
	
    private static final int RSA_SIGNATURE_SIZE = 64;
    public static final int RSA_SIGNATURE_HEX_SIZE = 2 * RSA_SIGNATURE_SIZE;

    private static final int KEY_BYTE_LENGTH = 24;
    
    public static final String SECURITY_ALGORITHM = "RSA";
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
	
	public static final String SECRET_KEY_SPEC_ALGORITHM = "AES";
	public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * Convert a hex string to a byte array.
     * 
     * @param hex hex string
     * @return array of bytes
     */
    public static byte[] convertFromHexString(String hex) {
        byte[] bytes = new byte[hex.length() / 2];

        for (int i = 0; i < hex.length(); i += 2) {
            int value = Integer.parseInt(hex.substring(i, i + 2), 16);
            bytes[i / 2] = (byte) value;
        }

        return bytes;
    }

    /**
     * Read a public key from a hex string of the encoded key bytes.
     * 
     * @param hex hex string of encoded key bytes
     * 
     * @return public key
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static final PublicKey readPublicKeyFromHexString(String hex)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] pubEncoded = SecurityUtil.convertFromHexString(hex);

        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubEncoded);
        KeyFactory keyFactory = KeyFactory.getInstance(SECURITY_ALGORITHM);
        return keyFactory.generatePublic(pubKeySpec);
    }
    
    /**
     * Compute an RSA signature (formatted as a hex string) for the specified string,
     * using the supplied private key
     * 
     * @param string String to verify
     * @param signature of string, as computed by computeRSASignature()
     * @param publicKey public key for private key used to sign
     * @return true if signature is valid, false otherwise
     * @throws SignatureException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static final boolean validateRSASignature(String string, String hexSignature, PublicKey publicKey)
        throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] signatureBytes = convertFromHexString(hexSignature);

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(string.getBytes());
        
        return signature.verify(signatureBytes);
    }


	public static final byte[] getKeyBytes(String password) {
		byte[] keyBytes = new byte[KEY_BYTE_LENGTH];
		byte[] apiBytes = password.getBytes();
		
		if (apiBytes.length >= keyBytes.length) {
			System.arraycopy(apiBytes, 0, keyBytes, 0, keyBytes.length);
		} else {
			for (int i= 0; i< keyBytes.length; i++) {
				keyBytes[i] = apiBytes[i%apiBytes.length];
			}
		}
		
		return keyBytes;
	}

	public static final String decryptWithPassword(String encryptedText, String password) throws Exception {
		byte[] keyBytes = getKeyBytes(password);
		SecretKeySpec key = new SecretKeySpec(keyBytes, SECRET_KEY_SPEC_ALGORITHM);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);
		
		byte[] cipherText = convertFromHexString(encryptedText);
	    byte[] plainText = new byte[cipherText.length]; 
	    cipher.doFinal(cipherText, 0, cipherText.length, plainText, 0); 
	    
	    // The returned string can be longer if it is padded! So let's strip the rest:
	    String plainTextString = new String(plainText);
	    int index = plainTextString.indexOf('\0');
	    if (index != -1) {
	    	plainTextString =  plainTextString.substring(0, index);
	    }
	    return plainTextString;
	}
}
