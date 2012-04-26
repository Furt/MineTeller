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
/* All of the following will modified in accordance with the 
 * GNU GPL v3 as previously stated and parts of this code will 
 * no longer resemble the previously mentioned Software under 
 * copyright thus rendering this software "Modified"
 * Original Software (C) 2012 Vex Software LLC 
 * If you did not receive the full unabridged code
 * it can be found on <https://github.com/vexsoftware/votifier>
 * If this was All Rights Reserved and under Copyright
 * I would be fined up to $2500.
 */
package com.vexsoftware.votifier.model;
//import java.net.URL;
//import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
public class ListenerLoader {
	private static Logger log = Logger.getLogger("ListenerLoader");
	public static List<VoteListener> load(String directory) throws Exception {
		List<VoteListener> listeners = new ArrayList<VoteListener>();		
		Class<?> clasz = com.modcrafting.mineslots.listener.MasterVoteListener.class;
		Object deflisten = clasz.newInstance();
		VoteListener listen = (VoteListener) deflisten;
		listeners.add(listen);
		log.info("Loaded: " + listen.getClass().getSimpleName());
		return listeners;
	}

}
