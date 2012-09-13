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

import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
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

	// Create a setter to pull from
	// PluginDescriptionFile pdfFile = this.getDescription();

	public static final String VERSION = "0.1";

	/** The Votifier instance. */
	private static MineTeller instance;

	/** The vote listeners. */
	private final List<VoteListener> listeners = new ArrayList<VoteListener>();

	/** The vote receiver. */
	private VoteReceiver voteReceiver;

	/** The RSA key pair. */
	private KeyPair keyPair;

	/** Economy instance. */
	public Economy econ = null;

	public HashMap<String, String> cache = new HashMap<String, String>();

	public final PlayerListener player = new PlayerListener(this);

	public boolean econEnabled = false;

	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(player, this);

		File rsaDirectory = new File(getDataFolder() + "/rsa");
		File listenerDirectory = new File(getDataFolder(), "/listeners");

		// Check Directories
		if (!getDataFolder().exists())
			getDataFolder().mkdir();

		if (!rsaDirectory.exists())
			rsaDirectory.mkdir();

		if (!listenerDirectory.exists())
			listenerDirectory.mkdir();

		// Generate/load config
		this.getConfig().addDefault("host", "0.0.0.0");
		this.getConfig().addDefault("port", 8992);
		this.getConfig().addDefault("listener_folder",
				getDataFolder().getPath() + "/listeners");
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();

		try {
			// Generate the RSA key pair.
			keyPair = RSAKeygen.generate(2048);
			RSAIO.save(rsaDirectory, keyPair);
		} catch (Exception ex) {
			logger(Level.SEVERE, "RSAKeygen", ex);
		}

		// Load the vote listeners.
		try {
			listeners.addAll(ListenerLoader.load(getConfig().getString(
					"listener_folder")));
		} catch (Exception ex) {

		}

		// Initialize the receiver.
		voteReceiver = new VoteReceiver(getConfig().getString("host"),
				getConfig().getInt("port"));
		voteReceiver.start();
		/*
		 * Configure for
		 * getServer().getScheduler().scheduleAsyncRepeatingTask(this,
		 * voteReceiver, 20L, 20L); Let bukkit recirculate the Thread
		 */

		logger(Level.INFO, pdfFile.getVersion() + " enabled.");
	}

	@Override
	public void onDisable() {
		// Interrupt the vote receiver.
		if (voteReceiver != null) {
			voteReceiver.shutdown();
		}
		logger(Level.INFO, "disabled.");
	}

	public void loadVault() {
		if (getServer().getPluginManager().getPlugin("Vault") != null) {
			try {
				RegisteredServiceProvider<Economy> economyProvider = getServer()
						.getServicesManager().getRegistration(
								net.milkbowl.vault.economy.Economy.class);
				econ = economyProvider.getProvider();
				econEnabled = true;
			} catch (Exception e) {
				logger(Level.SEVERE,
						"Error hooking to Vault! MineTeller Listener will not work!",
						e);
			}
		} else {
			logger(Level.SEVERE,
					"Could not find Vault! Vote Listener will not work!");
		}
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

	public void logger(Level l, String message) {
		this.getLogger().log(l, message);
	}

	public void logger(Level l, String cause, String message) {
		this.getLogger().log(l, "(" + cause + ") " + message);
	}

	public void logger(Level l, String message, Exception ex) {
		this.getLogger().log(l, message + " " + ex.getMessage());
	}

	public void logger(Level l, String cause, String message, Exception ex) {
		this.getLogger().log(l,
				"(" + cause + ") " + message + " " + ex.getMessage());
	}
}
