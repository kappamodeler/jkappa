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

	public static final String LICENSE_FIELD_SEPARATOR = "-";
	
    // This key was generated with com.plectix.license.server.KeyGenerator, and corresponds
    // to the private key in com.plectix.license.server.LicenseGenerator.
	private static final String PUBLIC_KEY_HEX = "305c300d06092a864886f70d0101010500034b003048024100aff3c80597c966cff656e204837c9a4dbc9e8e9c0c78330ff6445cb5c7456b73937536247890f12a189bf113c035ae70f94059bd2832b25d1c5071f04fb335d90203010001";
	private static PublicKey publicKey = null;
	
    private static final int RSA_SIGNATURE_SIZE = 64;
    public static final int RSA_SIGNATURE_HEX_SIZE = 2 * RSA_SIGNATURE_SIZE;

    private static final int KEY_BYTE_LENGTH = 16;
    
    public static final String SECURITY_ALGORITHM = "RSA";
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
	
	public static final String SECRET_KEY_SPEC_ALGORITHM = "AES";
	public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

	public static final String DEFAULT_CHARSET = "ISO-8859-1";

    /**
     * Convert a hex string to a byte array.
     * 
     * @param hexString hex string
     * @return array of bytes
     */
    public static byte[] convertFromHexStringToBytes(String hexString) {
        byte[] bytes = new byte[hexString.length() / 2];

        for (int i = 0; i < hexString.length(); i += 2) {
            int value = Integer.parseInt(hexString.substring(i, i + 2), 16);
            bytes[i / 2] = (byte) value;
        }

        return bytes;
    }
    
    /**
     * 
     * @param string
     * @return
     */
    public static final byte[] getBytes(String string) {
    	try {
			return string.getBytes(DEFAULT_CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("Charset " + DEFAULT_CHARSET + " is not supported!");
		}
    }
    
    /**
     * 
     * @param bytes
     * @return
     */
    private static final String getString(byte[] bytes) {
    	try {
			return new String(bytes, DEFAULT_CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("Charset " + DEFAULT_CHARSET + " is not supported!");
		}
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
        byte[] pubEncoded = SecurityUtil.convertFromHexStringToBytes(hex);

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
        byte[] signatureBytes = convertFromHexStringToBytes(hexSignature);

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(SecurityUtil.getBytes(string));
        
        return signature.verify(signatureBytes);
    }

	public static final byte[] getKeyBytes(String password) {
		byte[] keyBytes = new byte[KEY_BYTE_LENGTH];
		byte[] apiBytes = SecurityUtil.getBytes(password);
		
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
		
		byte[] cipherText = convertFromHexStringToBytes(encryptedText);
	    byte[] plainTextBytes = new byte[cipherText.length]; 
	    cipher.doFinal(cipherText, 0, cipherText.length, plainTextBytes, 0); 
	    
	    // The returned string can be longer if it is padded! So let's strip the rest:
	    String plainTextString = SecurityUtil.getString(plainTextBytes);
	    int index = plainTextString.indexOf('\0');
	    if (index != -1) {
	    	plainTextString =  plainTextString.substring(0, index);
	    }
	    return plainTextString;
	}

	/**
	 * Returns a license for which previously-created data has been supplied.
	 * 
	 * We break the data into the signature and the data itself and make sure it's all valid.  
	 * If it is, the other member variables of this object are initialized from the data, 
	 * and the object can be used.
	 * @param apiKey 
	 * 
	 * @throws IllegalArgumentException
	 */
	public static final License getLicense(String licenseDataEncrypted, String apiKey) throws LicenseException {
	    boolean valid = false;
	
	    try {
	    	String somewhatPlainText = decryptWithPassword(licenseDataEncrypted, apiKey);
	    	
	        // If this is first time here, initialize the key
	        if (publicKey == null) {
	            publicKey = readPublicKeyFromHexString(PUBLIC_KEY_HEX);
	        }
	
	        int indexOfSeparator = somewhatPlainText.indexOf(LICENSE_FIELD_SEPARATOR);
	        if (indexOfSeparator > 0) {
	        	int versionNumber = Integer.parseInt(somewhatPlainText.substring(0, indexOfSeparator++));
	            String signatureString = somewhatPlainText.substring(indexOfSeparator, indexOfSeparator + RSA_SIGNATURE_HEX_SIZE);
	            String obfuscatedString = somewhatPlainText.substring(indexOfSeparator + RSA_SIGNATURE_HEX_SIZE);
	            
	            String licenseDataPlain = SecurityUtil.getString(convertFromHexStringToBytes(obfuscatedString));
	            valid = validateRSASignature(licenseDataPlain, signatureString, publicKey);
	            
	            if (valid) {
	            	if (versionNumber == 1) {
	                	License license = new License_V1();
	                	license.setLicenseDataPlain(licenseDataPlain); // this method can throw an Exception
	                	return license;
	            	} else {
	            		throw new LicenseException.InvalidLicenseException("Unknown version number" + versionNumber, somewhatPlainText);
	            	}
	            } 
	        }
	    } catch (Exception exception) {
	        throw new LicenseException.InvalidLicenseException("Failed to parse and validate license", licenseDataEncrypted, exception);
	    }
	
	    // we didn't get an Exception above... and we couldn't validate and return the license... so let's throw an Exception...
	    throw new LicenseException.InvalidLicenseException("Invalid signature for license data", licenseDataEncrypted);
	}

}
