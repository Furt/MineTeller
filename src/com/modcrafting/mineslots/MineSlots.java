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
package com.modcrafting.mineslots;

import java.io.File;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.modcrafting.mineslots.crypto.RSAIO;
import com.modcrafting.mineslots.crypto.RSAKeygen;
import com.modcrafting.mineslots.model.ListenerLoader;
import com.modcrafting.mineslots.model.VoteListener;
import com.modcrafting.mineslots.net.VoteReceiver;

public class MineSlots extends JavaPlugin {
	public static final String VERSION = "0.0";
	private static final Logger log = Logger.getLogger("Votifier");
	private static MineSlots instance;
	private final List<VoteListener> listeners = new ArrayList<VoteListener>();
	private VoteReceiver voteReceiver;
	private KeyPair keyPair;
	public void onDisable() {
		if (voteReceiver != null) voteReceiver.shutdown();
		log.info("MineSlots disabled.");
	}
	public void onEnable() {
		try {
			MineSlots.instance = this;
			if (!getDataFolder().exists()) getDataFolder().mkdir();
			File config = new File(getDataFolder() + "/config.yml");
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(config);
			File rsaDirectory = new File(getDataFolder() + "/rsa");
			String listenerDirectory = getDataFolder() + "/listeners";
			if (!config.exists()) {				
				log.info("Reverse Enginneering The World...");
				config.createNewFile();
				cfg.set("host", "0.0.0.0");
				cfg.set("port", 8192);
				cfg.set("listener_folder", listenerDirectory);
				cfg.save(config);

				// Brute Force FTW RSA
				// Ammend Previous Comment
				rsaDirectory.mkdir();
				new File(listenerDirectory).mkdir();
				keyPair = RSAKeygen.generate(2048);
				RSAIO.save(rsaDirectory, keyPair);
			} else {
				keyPair = RSAIO.load(rsaDirectory);
				cfg = YamlConfiguration.loadConfiguration(config);
			}
			listenerDirectory = cfg.getString("listener_folder");
			listeners.addAll(ListenerLoader.load(listenerDirectory));
			String host = cfg.getString("host", "0.0.0.0");
			int port = cfg.getInt("port", 8992);
			voteReceiver = new VoteReceiver(host, port);
			voteReceiver.start();
		} catch (Exception ex) {
			log.log(Level.SEVERE, "Votifier Screwd Up.", ex);
		}
	}
	public static MineSlots getInstance() {
		return instance;
	}
	public List<VoteListener> getListeners() {
		return listeners;
	}
	public VoteReceiver getVoteReceiver() {
		return voteReceiver;
	}
	public KeyPair getKeyPair() {
		return keyPair;
	}
}
