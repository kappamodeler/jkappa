package com.plectix.license.client;

import java.security.PublicKey;

/**
 * This class implements the infrastructure to encrypt, verify, and decrypt some license text, 
 * which has license parameters set by subclasses.
 * 
 * @author ecemis
 */
public abstract class License {
	public static final String LICENSE_FIELD_SEPARATOR = "-";
	
    // This key was generated with com.plectix.license.server.KeyGenerator, and corresponds
    // to the private key in com.plectix.license.server.LicenseGenerator.
    private static final String PUBLIC_KEY_HEX = "305c300d06092a864886f70d0101010500034b003048024100aff3c80597c966cff656e204837c9a4dbc9e8e9c0c78330ff6445cb5c7456b73937536247890f12a189bf113c035ae70f94059bd2832b25d1c5071f04fb335d90203010001";
    private static PublicKey publicKey = null;

	private final int versionNumber;

	/** This is plain license data that contains license information */
    protected String licenseDataPlain = null;
    
    /** Encrypted license data to be saved in a license file*/
    private String licenseDataEncrypted = null;
    

	public License (int versionNumber) {
		this.versionNumber = versionNumber;
	}
	
	/**
	 * This method needs to return a String that includes all information we need to authenticate a user.
	 * By design, it will called only from this class.
	 * 
	 * @return
	 */
	public abstract String getLicenseDataPlain();
	
	/** 
	 * This method should set the internal variables of a license.
	 * By design, it will called only from this class.
	 * 
	 */
	protected abstract void setLicenseDataPlain(String licenseDataPlain) throws IllegalArgumentException;
	
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
        	String somewhatPlainText = SecurityUtil.decryptWithPassword(licenseDataEncrypted, apiKey);
        	
            // If this is first time here, initialize the key
            if (publicKey == null) {
                publicKey = SecurityUtil.readPublicKeyFromHexString(PUBLIC_KEY_HEX);
            }

            int indexOfSeparator = somewhatPlainText.indexOf(LICENSE_FIELD_SEPARATOR);
            if (indexOfSeparator > 0) {
            	int versionNumber = Integer.parseInt(somewhatPlainText.substring(0, indexOfSeparator++));
                String signatureString = somewhatPlainText.substring(indexOfSeparator, indexOfSeparator + SecurityUtil.RSA_SIGNATURE_HEX_SIZE);
                String obfuscatedString = somewhatPlainText.substring(indexOfSeparator + SecurityUtil.RSA_SIGNATURE_HEX_SIZE);
                
                String licenseDataPlain = new String(SecurityUtil.convertFromHexString(obfuscatedString));
                valid = SecurityUtil.validateRSASignature(licenseDataPlain, signatureString, publicKey);
                
                if (valid) {
                	if (versionNumber == 1) {
                    	License license = new License_V1();
                    	license.setLicenseDataPlain(licenseDataPlain); // this method can throw an Exception
                    	return license;
                	} else {
                		throw new LicenseException.NotLicensedException("Unknown version number" + versionNumber, somewhatPlainText);
                	}
                } 
            }
        } catch (Exception exception) {
            throw new LicenseException.InvalidLicenseException("Failed to parse and validate license", licenseDataEncrypted, exception);
        }

        // we didn't get an Exception above... and we couldn't validate and return the license... so let's throw an Exception...
        throw new LicenseException.InvalidLicenseException("Invalid signature for license data", licenseDataEncrypted);
    }



	public final int getVersionNumber() {
		return versionNumber;
	}


	public final String getLicenseDataEncrypted() {
		return licenseDataEncrypted;
	}

	public final void setLicenseDataEncrypted(String licenseDataEncrypted) {
		this.licenseDataEncrypted = licenseDataEncrypted;
	}

}
