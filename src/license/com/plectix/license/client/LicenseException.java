package com.plectix.license.client;

/**
 * This exception is thrown when attempting to authenticate a user who is not licensed.
 * 
 */
public class LicenseException extends Exception {
	public String licenseDate = null;
	
	public LicenseException(String message, String licenseData) {
		super(message);
		this.licenseDate = licenseData;
	}

	public LicenseException(String message, String licenseData, Throwable ex) {
		super(message, ex);
		this.licenseDate = licenseData;
	}

	public static class NotLicensedException extends LicenseException {
		public NotLicensedException(String message, String licenseData) {
			super(message, licenseData);
		}
	}

	public static class InvalidLicenseException extends LicenseException {
		public InvalidLicenseException(String message, String licenseData) {
			super(message, licenseData);
		}
		
		public InvalidLicenseException(String message, String licenseData, Throwable ex) {
			super(message, licenseData, ex);
		}
	}
}

