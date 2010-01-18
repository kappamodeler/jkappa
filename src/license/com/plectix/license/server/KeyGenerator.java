package com.plectix.license.server;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;


/**
 *   Generate a new public/private key pair, encode them to bytes, 
 *   convert them to hex, and print them to stdout. These keys are 
 *   used in the LicenseGenerator and LicenseController classes.
 *   
 * @author ecemis
 *
 */
public class KeyGenerator {
	private static final int KEY_SIZE = 1024;
	private static final String SECURITY_ALGORITHM = "RSA";
	
	private String privateKeyEncodedHex;
	private String publicKeyEncodedHex;
	
	public KeyGenerator() {
		super();
	}
	
	public final void generateKeys() throws NoSuchAlgorithmException {
        // Generate keys
        KeyPairGenerator generator =  KeyPairGenerator.getInstance(SECURITY_ALGORITHM);
        generator.initialize(KEY_SIZE);
        KeyPair keyPair = generator.generateKeyPair();
        
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        // Encode them
        byte[] privateKeyEncoded = privateKey.getEncoded();
        byte[] publicKeyEncoded = publicKey.getEncoded();

        // Convert to hex
        privateKeyEncodedHex = ServerSecurityUtil.convertFromBytesToHexString(privateKeyEncoded);
        publicKeyEncodedHex = ServerSecurityUtil.convertFromBytesToHexString(publicKeyEncoded);
	}


	public final String getPrivateKeyEncodedHex() {
		return privateKeyEncodedHex;
	}

	public final String getPublicKeyEncodedHex() {
		return publicKeyEncodedHex;
	}
	
    public static void main(String[] args) throws Exception {
    	KeyGenerator keyGenerator = new KeyGenerator();
    	keyGenerator.generateKeys();

        // Output
        System.out.println("private static final String PRIVATE_KEY_HEX = \"" + keyGenerator.getPrivateKeyEncodedHex() + "\";");
        System.out.println("private static final String  PUBLIC_KEY_HEX = \"" + keyGenerator.getPublicKeyEncodedHex() + "\";");
    }

}
