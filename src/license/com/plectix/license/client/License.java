package com.plectix.license.client;


/**
 * This class implements the infrastructure to encrypt, verify, and decrypt some license text, 
 * which has license parameters set by subclasses.
 * 
 * @author ecemis
 */
public abstract class License {

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
	 * Checks whether the given user is authorized to use the software under this license.
     * 
     * The user is authorized if the license is not expired, and the user's credentials match those in the license...
     * 
	 * @param username
	 * @param apiKey
	 * @return
	 * @throws LicenseException 
	 */
	public abstract boolean isAuthorized(String username, String apiKey) throws LicenseException;

	/**
	 * Returns the Expiration Date of this license, given in terms of milliseconds since Jan 1, 1970.
	 * 
	 * @return
	 */
	public abstract long getExpirationDate();

	/**
	 * Returns the key to decrypt JSIM.
	 * 
	 * @return
	 */
	public abstract String getJsimKey();
	
	
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
