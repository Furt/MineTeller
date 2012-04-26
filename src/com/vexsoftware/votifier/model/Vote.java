

package com.vexsoftware.votifier.model;

public class Vote {

	/** The name of the vote service. */
	private String serviceName;

	/** The username of the voter. */
	private String username;

	/** The address of the voter. */
	private String address;

	/** The date and time of the vote. */
	private String timeStamp;

	/** Adding OpCode Variable for Item Codes */
	private String itemCode;

	/** Added CVar for any inject */
	private String cVar;

	@Override
	public String toString() {
		return "Vote (from:" + serviceName + " username:" + username + " address:" + address + " timeStamp:" + timeStamp + " itemCode:" + itemCode +" cVar:" + cVar +")";
	}

	/**
	 * Sets the serviceName.
	 * 
	 * @param serviceName
	 *            The new serviceName
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * Gets the serviceName.
	 * 
	 * @return The serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Sets the username.
	 * 
	 * @param username
	 *            The new username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the username.
	 * 
	 * @return The username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the address.
	 * 
	 * @param address
	 *            The new address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * Gets the address.
	 * 
	 * @return The address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the time stamp.
	 * 
	 * @param timeStamp
	 *            The new time stamp
	 */
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	/**
	 * Not Writing the JavDoc for this
	 * 
	 * @param itemCode
	 * 				Learn JAVA Crazies
	 */
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	

	/**
	 * GNU Baby Got to love it
	 * 
	 * @param cVar
	 * 				Learn JAVA Crazies
	 */
	public void setcVar(String cVar) {
		this.cVar = cVar;
		
	}
	
	/**
	 * Gets the time stamp.
	 * 
	 * @return The time stamp
	 */
	public String getTimeStamp() {
		return timeStamp;
	}

	public String getcVar() {
		return cVar;
	}

	public String getitemCode() {
		return itemCode;
	}


	

}
