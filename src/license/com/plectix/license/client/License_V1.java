package com.plectix.license.client;

import java.util.Random;
import java.util.StringTokenizer;

public class License_V1 extends License {
    private static final int FIELD_DELIMITER_LENGTH = 4;

	protected long creationDate = 0;
	protected long expirationDate = 0;
	protected String username = null;
	protected String apiKey = null;
	protected String jsimKey = null;
	protected String pluginsVersion = null;

	public License_V1() {
		super(1);
		creationDate = System.currentTimeMillis();
	}

	@Override
	public String getLicenseDataPlain() {
		if (licenseDataPlain == null) {
			final String fieldDelimiter = createFieldDelimiter(creationDate, expirationDate, username, apiKey, jsimKey, pluginsVersion);
				
			StringBuffer stringBuffer = new StringBuffer(fieldDelimiter);
			stringBuffer.append(creationDate + fieldDelimiter);
			stringBuffer.append(expirationDate + fieldDelimiter);
			stringBuffer.append(username + fieldDelimiter);
			stringBuffer.append(apiKey + fieldDelimiter);
			stringBuffer.append(jsimKey + fieldDelimiter);
			stringBuffer.append(pluginsVersion + fieldDelimiter);

	        licenseDataPlain = stringBuffer.toString();
		}
		return licenseDataPlain;
	}

	
	@Override
	protected void setLicenseDataPlain(String licenseText) throws IllegalArgumentException {
		this.licenseDataPlain = licenseText;
		final String fieldDelimiter = licenseText.substring(0, FIELD_DELIMITER_LENGTH);

        StringTokenizer fieldTokenizer = new StringTokenizer(licenseText.substring(FIELD_DELIMITER_LENGTH), fieldDelimiter);

        creationDate = getLong(fieldTokenizer, "Creation Date");
        expirationDate = getLong(fieldTokenizer, "Expiration Date");
        username = getString(fieldTokenizer, "Username");
        apiKey = getString(fieldTokenizer, "API Key");
        jsimKey = getString(fieldTokenizer, "JSIM Key");
        pluginsVersion = getString(fieldTokenizer, "Plugins Version");
	}

	
    //***************************************************************************************
	/**
	 * 
	 * @param args
	 * @return
	 */
	private static final String createFieldDelimiter(Object... args) {
		Random random = new Random();
		String fieldDelimiter = null;
		
		boolean done = false;
		while (!done) {
			// let's create a random field delimiter:
			char[] charArray = new char[FIELD_DELIMITER_LENGTH];
			for (int i= 0; i< charArray.length; i++) {
				charArray[i] = (char) (33 + random.nextInt(11)); // generates a random char between '!' and '+'
 			}
			fieldDelimiter = new String(charArray);
			
			done = true;
			for (Object arg : args) {
				if (arg.toString().indexOf(fieldDelimiter) != -1) {
					// generate another delimiter
					done = false;
					break;
				} 
			}
		}
		
		return fieldDelimiter;
	}
	
	
    //***************************************************************************************
    /**
     * Checks whether the given user is authorized to use the software under this license.
     * 
     * The user is authorized if the license is not expired, and the user's credentials match those in the license...
     * 
     * @param String username
     * @param String apiKey
     * @return true if authorized
     * @throws LicenseException 
     */
	@Override
    public boolean isAuthorized(String username, String apiKey) throws LicenseException {
        if (validateExpiry() == false) {
            // return false;
        	// we should never be here cause validateExpiry() never returns false!
        	throw new RuntimeException("validateExpiry() returned false instead of throwing an exception");
        }
        if (!username.equals(this.username)) {
        	// return false;
        	throw new LicenseException.NotLicensedException("Username is not valid", getLicenseDataEncrypted());
        }
        if (!apiKey.equals(this.apiKey)) {
        	// return false;
        	throw new LicenseException.NotLicensedException("API key is not valid", getLicenseDataEncrypted());
        }

        return true;
    }

    
    //***************************************************************************************
    /**
     * Checks whether this license has expired or not.
     * 
     * @return
     */
    protected boolean validateExpiry() throws LicenseException {
        if (creationDate <= 0) {
            // the license is not initialized properly
            // return false;
        	throw new LicenseException.InvalidLicenseException("License creation date is invalid", getLicenseDataEncrypted());
        }
        if (expirationDate <= 0) {
            // we don't let perpetual licenses in this version!!!
            // return false;
        	throw new LicenseException.InvalidLicenseException("License expiration date is invalid", getLicenseDataEncrypted());
        }
        if (System.currentTimeMillis() > expirationDate) {
            // license has expired
            // return false;
        	throw new LicenseException.NotLicensedException("License has expired", getLicenseDataEncrypted());
        }
        return true;
    }

    //***************************************************************************************
    /**
     * 
     * @param fieldTokenizer
     * @param fieldName
     * @return
     * @throws IllegalArgumentException
     */
	private final String getString(StringTokenizer fieldTokenizer, String fieldName) throws IllegalArgumentException {
        if (!fieldTokenizer.hasMoreTokens()){
            throw new IllegalArgumentException("Corrupted license file: " + fieldName + " not found");
        }
        return fieldTokenizer.nextToken();
	}

    //***************************************************************************************
	/**
	 * 
	 * @param fieldTokenizer
	 * @param fieldName
	 * @return
	 * @throws IllegalArgumentException
	 */
	private final long getLong(StringTokenizer fieldTokenizer, String fieldName) throws IllegalArgumentException {
        return Long.parseLong(getString(fieldTokenizer, fieldName));
	}

    //***************************************************************************************
	//
	//  Getters and Setters:
	//
    //***************************************************************************************
	
	public final void setExpirationDate(long expirationDate) {
		this.expirationDate = expirationDate;
	}

	public final void setUsername(String username) {
		this.username = username;
	}

	public final void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public final void setJsimKey(String jsimKey) {
		this.jsimKey = jsimKey;
	}

	public final void setPluginsVersion(String pluginsVersion) {
		this.pluginsVersion = pluginsVersion;
	}
	
	@Override
	public long getExpirationDate() {
		return expirationDate;
	}
	
	@Override
	public String getJsimKey() {
		return jsimKey;
	}

	public final String getPluginsVersion() {
		return pluginsVersion;
	}
	
}
