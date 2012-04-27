/*
 * Copyright (C) 2012 Vex Software LLC
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

/**
 * The main Votifier plugin class.
 * 
 * @author Blake Beaupain
 * @author Kramer Campbell
 */
public class MineSlots extends JavaPlugin {

	/** The current Votifier version. */
	public static final String VERSION = "0.1";

	/** The logger instance. */
	private static final Logger log = Logger.getLogger("Votifier");

	/** The Votifier instance. */
	private static MineSlots instance;

	/** The vote listeners. */
	private final List<VoteListener> listeners = new ArrayList<VoteListener>();

	/** The vote receiver. */
	private VoteReceiver voteReceiver;

	/** The RSA key pair. */
	private KeyPair keyPair;

	@Override
	public void onEnable() {
		try {
			MineSlots.instance = this;

			// Handle configuration.
			if (!getDataFolder().exists()) {
				getDataFolder().mkdir();
			}
			File config = new File(getDataFolder() + "/config.yml");
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(config);
			File rsaDirectory = new File(getDataFolder() + "/rsa");
			String listenerDirectory = getDataFolder() + "/listeners";
			if (!config.exists()) {
				// First time run - do some initialization.
				log.info("Configuring Votifier for the first time...");

				// Initialize the configuration file.
				config.createNewFile();
				cfg.set("host", "0.0.0.0");
				cfg.set("port", 8992);
				cfg.set("listener_folder", listenerDirectory);
				cfg.save(config);

				// Generate the RSA key pair.
				rsaDirectory.mkdir();
				new File(listenerDirectory).mkdir();
				keyPair = RSAKeygen.generate(2048);
				RSAIO.save(rsaDirectory, keyPair);
			} else {
				// Load configuration.
				keyPair = RSAIO.load(rsaDirectory);
				cfg = YamlConfiguration.loadConfiguration(config);
			}

			// Load the vote listeners.
			listenerDirectory = cfg.getString("listener_folder");
			listeners.addAll(ListenerLoader.load(listenerDirectory));

			// Initialize the receiver.
			String host = cfg.getString("host", "0.0.0.0");
			int port = cfg.getInt("port", 8992);
			voteReceiver = new VoteReceiver(host, port);
			voteReceiver.start();

			log.info("Votifier enabled.");
		} catch (Exception ex) {
			log.log(Level.SEVERE, "Unable to enable Votifier.", ex);
		}
	}

	@Override
	public void onDisable() {
		// Interrupt the vote receiver.
		if (voteReceiver != null) {
			voteReceiver.shutdown();
		}
		log.info("Votifier disabled.");
	}

	/**
	 * Gets the instance.
	 * 
	 * @return The instance
	 */
	public static MineSlots getInstance() {
		return instance;
	}

	/**
	 * Gets the listeners.
	 * 
	 * @return The listeners
	 */
	public List<VoteListener> getListeners() {
		return listeners;
	}

	/**
	 * Gets the vote receiver.
	 * 
	 * @return The vote receiver
	 */
	public VoteReceiver getVoteReceiver() {
		return voteReceiver;
	}

	/**
	 * Gets the keyPair.
	 * 
	 * @return The keyPair
	 */
	public KeyPair getKeyPair() {
		return keyPair;
	}

}
