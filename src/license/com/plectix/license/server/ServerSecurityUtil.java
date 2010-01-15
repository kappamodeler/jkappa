package com.plectix.license.server;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

import com.plectix.license.client.LicenseException;
import com.plectix.license.client.SecurityUtil;

/**
 * Container of some utility methods that are called only from within the com.plectix.license.server package
 * and hidden from the client...
 * 
 * @author ecemis
 */
public class ServerSecurityUtil {

	private static final int BLOCK_SIZE = 16;
	
	/**
	 * Convert a byte array to a hex string.
	 * 
	 * @param bytes array of bytes
	 * @return hex string
	 */
	protected static final String convertToHexString(byte[] bytes) {
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
	protected static final String computeRSASignature(String string, PrivateKey privateKey)
	    throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
	    Signature signature = Signature.getInstance(SecurityUtil.SIGNATURE_ALGORITHM);
	    signature.initSign(privateKey);
	    signature.update(string.getBytes());
	    byte[] signatureBytes = signature.sign();
	
	    String result = convertToHexString(signatureBytes);
	    
	    if (result.length() != SecurityUtil.RSA_SIGNATURE_HEX_SIZE) {
	        throw new IllegalArgumentException("Unexpected length of RSA signature: " + result.length());
	    }
	    
	    return result;
	}

	/**
	 * Read a private key from a hex string of the encoded key bytes.
	 * 
	 * @param hex hex string of encoded key bytes
	 * @return private key
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	protected static PrivateKey readPrivateKeyFromHexString(String hex)
	    throws NoSuchAlgorithmException, InvalidKeySpecException {
	    byte[] privEncoded = SecurityUtil.convertFromHexString(hex);
	
	    PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(privEncoded);
	    KeyFactory keyFactory = KeyFactory.getInstance(SecurityUtil.SECURITY_ALGORITHM);
	    PrivateKey key = keyFactory.generatePrivate(privKeySpec);
	
	    return key;
	}

	/**
	 * 
	 * @param plainText
	 * @param password
	 * @return
	 * @throws LicenseException 
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws GeneralSecurityException
	 */
	protected static final String encryptWithPassword(String plainText, String password) throws LicenseException {
		byte[] keyBytes = SecurityUtil.getKeyBytes(password);
		SecretKeySpec key = new SecretKeySpec(keyBytes, SecurityUtil.SECRET_KEY_SPEC_ALGORITHM);
		
		try {
			Cipher cipher = Cipher.getInstance(SecurityUtil.CIPHER_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			
			byte[] input = plainText.getBytes();
			byte[] cipherText = new byte[BLOCK_SIZE*(1+input.length/BLOCK_SIZE)];
		    cipher.doFinal(input, 0, input.length, cipherText, 0); 
		
			return convertToHexString(cipherText);
		} catch (NoSuchAlgorithmException e) {
			throw new LicenseException.LicenseGenerationException("Caught NoSuchAlgorithmException: ", plainText, e);
		} catch (NoSuchPaddingException e) {
			throw new LicenseException.LicenseGenerationException("Caught NoSuchPaddingException: ", plainText, e);
		} catch (InvalidKeyException e) {
			throw new LicenseException.LicenseGenerationException("Caught InvalidKeyException: ", plainText, e);
		} catch (ShortBufferException e) {
			throw new LicenseException.LicenseGenerationException("Caught ShortBufferException: ", plainText, e);
		} catch (IllegalBlockSizeException e) {
			throw new LicenseException.LicenseGenerationException("Caught IllegalBlockSizeException: ", plainText, e);
		} catch (BadPaddingException e) {
			throw new LicenseException.LicenseGenerationException("Caught BadPaddingException: ", plainText, e);
		}
	
	}

}
