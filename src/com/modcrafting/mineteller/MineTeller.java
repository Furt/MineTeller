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
/*
 * Modified By Deathmarine
 * 
 */
package com.modcrafting.mineteller;

import java.io.File;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.modcrafting.mineteller.crypto.RSAIO;
import com.modcrafting.mineteller.crypto.RSAKeygen;
import com.modcrafting.mineteller.model.ListenerLoader;
import com.modcrafting.mineteller.model.VoteListener;
import com.modcrafting.mineteller.net.VoteReceiver;

/**
 * The main Votifier plugin class.
 * 
 * @author Blake Beaupain
 * @author Kramer Campbell
 */
public class MineTeller extends JavaPlugin {

	//Create a setter to pull from 
	//PluginDescriptionFile pdfFile = this.getDescription();
	
	public static final String VERSION = "0.1";

	/** The logger instance. */
	private static final Logger log = Logger.getLogger("Votifier");

	/** The Votifier instance. */
	private static MineTeller instance;

	/** The vote listeners. */
	private final List<VoteListener> listeners = new ArrayList<VoteListener>();

	/** The vote receiver. */
	private VoteReceiver voteReceiver;

	/** The RSA key pair. */
	private KeyPair keyPair;

	public HashMap<String, String> cache = new HashMap<String, String>();

	public final Listener player = new PlayerListener(this);
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(player, this);
		try {
			MineTeller.instance = this;

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
				log.info("[" + pdfFile.getName() + "] Contemplating the universe...");

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
			/* Configure for
			 * getServer().getScheduler().scheduleAsyncRepeatingTask(this, voteReceiver, 20L, 20L);
			 * Let bukkit recirculate the Thread
			 */

			log.info("[" + pdfFile.getName() + "] " + pdfFile.getVersion() + " enabled.");
		} catch (Exception ex) {
			log.log(Level.SEVERE, "Unable to enable [" + pdfFile.getName() + "]", ex);
		}
	}

	@Override
	public void onDisable() {
		// Interrupt the vote receiver.
		if (voteReceiver != null) {
			voteReceiver.shutdown();
		}		
		log.info("[MineTeller] disabled.");
	}

	/**
	 * Gets the instance.
	 * 
	 * @return The instance
	 */
	public static MineTeller getInstance() {
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
