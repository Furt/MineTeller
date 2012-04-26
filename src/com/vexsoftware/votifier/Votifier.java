package com.vexsoftware.votifier;

import java.io.File;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.vexsoftware.votifier.crypto.RSAIO;
import com.vexsoftware.votifier.crypto.RSAKeygen;
import com.vexsoftware.votifier.model.ListenerLoader;
import com.vexsoftware.votifier.model.VoteListener;
import com.vexsoftware.votifier.net.VoteReceiver;

public class Votifier extends JavaPlugin {

	public static final String VERSION = "0.0";
	private static final Logger log = Logger.getLogger("Votifier");
	private static Votifier instance;
	private final List<VoteListener> listeners = new ArrayList<VoteListener>();
	private VoteReceiver voteReceiver;
	private KeyPair keyPair;

	public void onEnable() {
		try {
			Votifier.instance = this;

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
				cfg.set("port", 8192);
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

			listenerDirectory = cfg.getString("listener_folder");
			listeners.addAll(ListenerLoader.load(listenerDirectory));
			

			// Initialize the receiver.
			String host = cfg.getString("host", "0.0.0.0");
			int port = cfg.getInt("port", 8192);
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
	public static Votifier getInstance() {
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
