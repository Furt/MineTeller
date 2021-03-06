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
package com.modcrafting.mineteller.model;

import java.util.ArrayList;
import java.util.List;

public class ListenerLoader {

	public static List<VoteListener> load(String directory) throws Exception {
		List<VoteListener> listeners = new ArrayList<VoteListener>();
		Class<?> clasz = com.modcrafting.mineteller.listener.MasterVoteListener.class;
		Object deflisten = clasz.newInstance();
		VoteListener listen = (VoteListener) deflisten;
		listeners.add(listen);
		return listeners;
	}

}
