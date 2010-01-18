package com.plectix.license.server;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;


import com.plectix.license.client.License;
import com.plectix.license.client.LicenseException;
import com.plectix.license.client.License_V1;
import com.plectix.license.client.SecurityUtil;

public class LicenseGenerator {
    // This key was generated with com.plectix.license.server.KeyGenerator, and corresponds
    // to the public key in com.plectix.license.client.License
    private static final String PRIVATE_KEY_HEX = "30820157020100300d06092a864886f70d0101010500048201413082013d020100024100aff3c80597c966cff656e204837c9a4dbc9e8e9c0c78330ff6445cb5c7456b73937536247890f12a189bf113c035ae70f94059bd2832b25d1c5071f04fb335d902030100010241009445afca1ec5e6b0db1b0e2dd58bdc102421cd756d00a1af12cd3aff2824b4cd73928d66d412db6adfdcdb272af82d40f953a37420684a720300e1b59fa57ecd022100db49ff1254152ae6162f7d43fe0776ef5fddc688b4f13f6c86463fe040a3fb6b022100cd6883926ce63e367ef5bd4732db4899d8254e1d791e160a4143637ce0ce88cb022100b8f726566063e666630a357fd75296887c754553e443a53ab5dba55f5346bdf7022100c8b82d81e34a6656c85f87d3503df9c6e3f1285122aea4a8e6b75c3b864e2c5b022100cda4e146808ce483b83049dcddb73d2b99a7c43162b47f9c4e44e7f73cf111c9";
   
    /**
     * 
     * @return
     * @throws LicenseException
     */
    public static final PrivateKey getPrivateKey() throws LicenseException {
        try {
			return ServerSecurityUtil.readPrivateKeyFromHexString(PRIVATE_KEY_HEX);
		} catch (NoSuchAlgorithmException e) {
			throw new LicenseException.LicenseGenerationException("Caught NoSuchAlgorithmException: ", null, e);
		} catch (InvalidKeySpecException e) {
			throw new LicenseException.LicenseGenerationException("Caught InvalidKeyException: ", null, e);
		}
    }

	/**
     * This method assumes that license parameters stored in licenseText are already set.
     * 
     * @param license
     * @param privateKey
     * @param apiKey
     * @return
     * @throws IllegalArgumentException
     */
    private static final String getLicenseDataEncrypted(License license, PrivateKey privateKey, String apiKey) throws LicenseException {
    	String licenseDataPlain = license.getLicenseDataPlain();
    	String signatureString = null;
		try {
			signatureString = ServerSecurityUtil.computeRSASignature(licenseDataPlain, privateKey);
		} catch (InvalidKeyException e) {
			throw new LicenseException.LicenseGenerationException("Caught InvalidKeyException: ", licenseDataPlain, e);
		} catch (SignatureException e) {
			throw new LicenseException.LicenseGenerationException("Caught SignatureException: ", licenseDataPlain, e);
		} catch (NoSuchAlgorithmException e) {
			throw new LicenseException.LicenseGenerationException("Caught NoSuchAlgorithmException: ", licenseDataPlain, e);
		}
    	String obfuscatedString = ServerSecurityUtil.convertFromBytesToHexString(SecurityUtil.getBytes(licenseDataPlain));
    	String somewhatPlainText = license.getVersionNumber() + SecurityUtil.LICENSE_FIELD_SEPARATOR + signatureString + obfuscatedString;
    	return ServerSecurityUtil.encryptWithPassword(somewhatPlainText, apiKey);
    }

    
    /**
     * 
     * @param username
     * @param apiKey
     * @param jsimKey
     * @param pluginsVersion
     * @param expirationDate
     * @return
     * @throws LicenseException
     */
    public static final String getLicenseDataEncrypted(String username, String apiKey, String jsimKey, String pluginsVersion, long expirationDate) throws LicenseException {
    	// Currently we are generating License_V1 as license object...
       	License_V1 license = new License_V1();
    	license.setUsername(username);
    	license.setApiKey(apiKey);
    	license.setJsimKey(jsimKey);
    	license.setPluginsVersion(pluginsVersion);
    	license.setExpirationDate(expirationDate);
    	
    	return getLicenseDataEncrypted(license, getPrivateKey(), apiKey);
    }
    
    
    /**
     * Creates and returns a new license
     * 
     * @throws Exception
     */
    public static final License generateLicense(String username, String apiKey, String jsimKey, String pluginsVersion, long expirationDate) throws Exception {
    	return SecurityUtil.getLicense(getLicenseDataEncrypted(username, apiKey, jsimKey, pluginsVersion, expirationDate), apiKey);
    }


    /**
     * Tests what a client would do with encrypted license data:
     * @param encryptedLicenseData
     * @throws LicenseException 
     */
    private static final void testLicense(String encryptedLicenseData, String username, String apiKey) throws LicenseException {
    	License license = SecurityUtil.getLicense(encryptedLicenseData, apiKey);
    	
    	System.out.println("\nLicense Version Number: " + license.getVersionNumber());
    	System.out.println("isAuthorized: " + license.isAuthorized(username, apiKey));
    	System.out.println("jsimKey: " + license.getJsimKey());
    	System.out.println("expirationDate: " + license.getExpirationDate());
    }

    /**
     * Creates a new license and outputs the license data as a string.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
    	String username = args[0];
    	String apiKey = args[1];
    	String jsimKey = args[2];
    	String pluginsVersion = args[3];                           
    	long expirationDate = Long.parseLong(args[4]);
    	
        String encryptedLicenseData = LicenseGenerator.getLicenseDataEncrypted(username, apiKey, jsimKey, pluginsVersion, expirationDate);
        System.out.println(encryptedLicenseData);
        
        testLicense(encryptedLicenseData, username, apiKey);
    }
}
