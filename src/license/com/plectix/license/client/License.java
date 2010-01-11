package com.plectix.license.client;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * This class implements the infrastructure to encrypt, verify, and decrypt some license text, 
 * which has license parameters set by subclasses.
 * 
 * @author ecemis
 */
public abstract class License {
	private static final String LICENSE_FIELD_SEPARATOR = "-";
	
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
	protected abstract String getLicenseDataPlain();
	
	/** 
	 * This method should set the internal variables of a license.
	 * By design, it will called only from this class.
	 * 
	 */
	protected abstract void setLicenseDataPlain(String licenseDataPlain) throws IllegalArgumentException;
	
	
    /**
     * Completes the creation of a brand-new license and prepare it to be returned to the user.
     * 
     * Called exclusively from LicenseGenerator. 
     * This method assumes that license parameters stored in licenseText are already set.
     *  
     * @param privateKey key to sign license
     * @param apiKey 
     * @throws IllegalArgumentException
     */
    public final void createData(PrivateKey privateKey, String apiKey) {
        try {
        	licenseDataPlain = getLicenseDataPlain();
            String signatureString = SecurityUtil.computeRSASignature(licenseDataPlain, privateKey);
            String obfuscatedString = SecurityUtil.convertToHexString(licenseDataPlain.getBytes());
            String apiKeySignatureString = SecurityUtil.computeRSASignature(apiKey, privateKey);
            String obfuscatedAPIKeyString = SecurityUtil.convertToHexString(apiKey.getBytes());
            licenseDataEncrypted = versionNumber + LICENSE_FIELD_SEPARATOR 
                                 + signatureString + LICENSE_FIELD_SEPARATOR
                                 + obfuscatedString + LICENSE_FIELD_SEPARATOR
                                 + apiKeySignatureString + LICENSE_FIELD_SEPARATOR
                                 + obfuscatedAPIKeyString + LICENSE_FIELD_SEPARATOR;
        } catch (Exception exception) {
            throw new IllegalArgumentException("Failed to sign license data with supplied key", exception);
        }
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
        int versionNumber = -1;

        try {
            // If this is first time here, initialize the key
            if (publicKey == null) {
                publicKey = SecurityUtil.readPublicKeyFromHexString(PUBLIC_KEY_HEX);
            }

            int separatorIndex = licenseDataEncrypted.indexOf(LICENSE_FIELD_SEPARATOR);
            if (separatorIndex > 0) {
            	String[] fields = licenseDataEncrypted.split(LICENSE_FIELD_SEPARATOR);
            	
            	if (fields.length < 5) {
            		throw new LicenseException.InvalidLicenseException("Not Enough Field In the License: " + fields.length, licenseDataEncrypted);
            	}
            	
                String signatureString = fields[1];
                String obfuscatedString = fields[2];
                String apiKeySignatureString = fields[3];
                String obfuscatedAPIKeyString  = fields[4];
                
                String licenseDataPlain = new String(SecurityUtil.convertFromHexString(obfuscatedString));
                valid = SecurityUtil.validateRSASignature(licenseDataPlain, signatureString, publicKey);

                // TODO: Use apiKey here!
                String key2 = new String(SecurityUtil.convertFromHexString(obfuscatedAPIKeyString));
                valid = SecurityUtil.validateRSASignature(key2, apiKeySignatureString, publicKey);
                
                if (valid) {
                	versionNumber = Integer.parseInt(fields[0]);
                	if (versionNumber == 1) {
                    	License license = new License_V1();
                    	license.setLicenseDataPlain(licenseDataPlain); // this method can throw an Exception
                    	return license;
                	} else {
                		throw new LicenseException.NotLicensedException("Unknown version number" + versionNumber, licenseDataEncrypted);
                	}
                } 
            }
        } catch (Exception exception) {
            throw new LicenseException.InvalidLicenseException("Failed to parse and validate license", licenseDataEncrypted, exception);
        }

        // we didn't get an Exception above... and we couldn't validate and return the license...
        throw new LicenseException.InvalidLicenseException("Invalid signature for license data", licenseDataEncrypted);
    }



	public final int getVersionNumber() {
		return versionNumber;
	}


	public final String getLicenseDataEncrypted() {
		return licenseDataEncrypted;
	}

}
