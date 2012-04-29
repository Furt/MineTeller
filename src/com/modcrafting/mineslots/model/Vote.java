/*
 * Copyright (C) 2011 Vex Software LLC
 * This file is part of Votifier.
 * 
 * Votifier is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Votifier is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Votifier.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * 
 * Modified By Deathmarine
 * 
 */

package com.modcrafting.mineslots.model;

public class Vote {
	private String serviceName;
	private String username;
	private String address;
	private String timeStamp;
	private String itemCode;
	private String cVar;
	public String toString() {
		return "Vote (from:" + serviceName + " username:" + username + " address:" + address + " timeStamp:" + timeStamp + " itemCode:" + itemCode +" cVar:" + cVar + ")";
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddress() {
		return address;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getitemCode() {
		return itemCode;
	}
	public void setcVar(String cVar) {
		this.cVar = cVar;
	}
	public String getcVar() {
		return cVar;
	}
}
