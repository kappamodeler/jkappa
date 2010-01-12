package com.plectix.license.client;

import java.util.StringTokenizer;

public class License_V1 extends License {
    private static final String FIELD_DELIMITER = ":::::";

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
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(creationDate + FIELD_DELIMITER);
			stringBuffer.append(expirationDate + FIELD_DELIMITER);
			stringBuffer.append(username + FIELD_DELIMITER);
			stringBuffer.append(apiKey + FIELD_DELIMITER);
			stringBuffer.append(jsimKey + FIELD_DELIMITER);
			stringBuffer.append(pluginsVersion + FIELD_DELIMITER);

	        licenseDataPlain = stringBuffer.toString();
		}
		return licenseDataPlain;
	}

	
	@Override
	protected void setLicenseDataPlain(String licenseText) throws IllegalArgumentException {
		this.licenseDataPlain = licenseText;
		
        StringTokenizer fieldTokenizer = new StringTokenizer(licenseText, FIELD_DELIMITER);

        creationDate = getLong(fieldTokenizer, "Creation Date");
        expirationDate = getLong(fieldTokenizer, "Expiration Date");
        username = getString(fieldTokenizer, "Username");
        apiKey = getString(fieldTokenizer, "API Key");
        jsimKey = getString(fieldTokenizer, "JSIM Key");
        pluginsVersion = getString(fieldTokenizer, "Plugins Version");
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
     */
    public boolean isAuthorized(String username, String apiKey) {
        if (validateExpiry() == false) {
            return false;
        }
        if (!username.equals(this.username)) {
        	return false;
        }
        if (!apiKey.equals(this.apiKey)) {
        	return false;
        }

        return true;
    }

    
    //***************************************************************************************
    /**
     * Checks whether this license has expired or not.
     * 
     * @return
     */
    protected boolean validateExpiry() {
        if (creationDate <= 0) {
            // the license is not initialized properly
            return false;
        }
        if (expirationDate <= 0) {
            // we don't let perpetual licenses in this version!!!
            return false;
        }
        if (System.currentTimeMillis() > expirationDate) {
            // license has expired
            return false;
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

	public final long getExpirationDate() {
		return expirationDate;
	}

	public final String getJsimKey() {
		return jsimKey;
	}

	public final String getPluginsVersion() {
		return pluginsVersion;
	}
	
}
