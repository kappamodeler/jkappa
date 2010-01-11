package com.plectix.license.client;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SecurityUtil {
	
    private static final int RSA_SIGNATURE_SIZE = 64;
    public static final int RSA_SIGNATURE_HEX_SIZE = 2 * RSA_SIGNATURE_SIZE;

	private static final String SECURITY_ALGORITHM = "RSA";
	private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * Convert a byte array to a hex string.
     * 
     * @param bytes array of bytes
     * @return hex string
     */
    public static final String convertToHexString(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < bytes.length; i++)  {
            int n = (new Byte(bytes[i])).intValue();

            if (n < 0) {
                n += 256;
            }

            if (n < 16) {
                stringBuffer.append('0' + Integer.toHexString(n));
            } else {
                stringBuffer.append(Integer.toHexString(n));
            }
        }

        return stringBuffer.toString();
    }
    
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
     * Compute an RSA signature (formatted as a hex string) for the specified string,
     * using the supplied private key
     * 
     * @param string String to sign
     * @param privateKey key to sign with
     * @return hex string of signature
     * @throws SignatureException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static final String computeRSASignature(String string, PrivateKey privateKey)
        throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(string.getBytes());
        byte[] signatureBytes = signature.sign();

        String result = convertToHexString(signatureBytes);
        
        if (result.length() != RSA_SIGNATURE_HEX_SIZE) {
            throw new IllegalArgumentException("Unexpected length of RSA signature: " + result.length());
        }
        
        return result;
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
     * Read a private key from a hex string of the encoded key bytes.
     * 
     * @param hex hex string of encoded key bytes
     * @return private key
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey readPrivateKeyFromHexString(String hex)
        throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        byte[] privEncoded = SecurityUtil.convertFromHexString(hex);

        PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(privEncoded);
        KeyFactory keyFactory = KeyFactory.getInstance(SECURITY_ALGORITHM);
        PrivateKey key = keyFactory.generatePrivate(privKeySpec);

        return key;
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


}
