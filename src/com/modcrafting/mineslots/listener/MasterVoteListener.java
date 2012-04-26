package com.modcrafting.mineslots.listener;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.vexsoftware.votifier.Votifier;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VoteListener;

public class MasterVoteListener implements VoteListener {
	private Logger log = Logger.getLogger("MasterVoteListener");
	private double amount = 0;
	private static Votifier v = null;
	private static Economy econ = null;
	public MasterVoteListener() {
		Properties props = new Properties();
		v = Votifier.getInstance();
		if (v.getServer().getPluginManager().getPlugin("Vault") != null) {
			try {
			RegisteredServiceProvider<Economy> economyProvider = v.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			econ = economyProvider.getProvider();
			}
			catch(Exception e) {
				log.severe("(MineSlots) Error hooking to Vault! MineSlots Listener will not work!");
				log.severe("(MineSlots) Error is: "+e.getMessage()+" from "+e.getCause()+".");
			}
		} else {
			log.severe("(MineSlots) Could not find Vault! Vote Listener will not work!");
		}
		try {
			// Create the file if it doesn't exist.
			File configFile = new File("./plugins/Votifier/MineSlots.ini");
			if (!configFile.exists()) {
				configFile.createNewFile();

				// Load the configuration.
				props.load(new FileReader(configFile));

				// Write the default configuration.
				props.setProperty("reward_amount", Double.toString(amount));
				props.store(new FileWriter(configFile), "iConomy Listener Configuration");
			} else {
				// Load the configuration.
				props.load(new FileReader(configFile));
			}

		} catch (Exception ex) {
			log.log(Level.WARNING, "Unable to load MineSlots.ini, using default reward value of: " + amount);
		}
	}
	
	public void voteMade(Vote vote) {
		log.info("Received: " + vote);
		String username = vote.getUsername();
		String cVar = vote.getcVar();
		if (username == null) return;
		if (cVar.equalsIgnoreCase("fly") && Bukkit.getServer().getPlayer(username).isOnline()){
			Player name = Bukkit.getServer().getPlayer(username);
			name.setFlying(true);
			/*Hashmap.put(name.getName(),"fly");
			 *(new Thread(FlyTimer).start())
			 * This will enable the player Flying if vote cVar is "fly"
			 */
			return;
		}
		if (cVar.equalsIgnoreCase("money")){
			amount = Double.parseDouble(vote.getitemCode());
			if(econ.hasAccount(username)){
				EconomyResponse r = econ.depositPlayer(username, amount);
				if (r.transactionSuccess()) {
					log.log(Level.INFO, "(MineSlots) Successfully credited " + username); 
				}else{
					log.log(Level.WARNING, "(MineSlots) Error" + r.errorMessage); 
				}
				if(Bukkit.getServer().getPlayer(username).isOnline()){
					Bukkit.getServer().getPlayer(username).sendMessage(
						ChatColor.GREEN + "[" + 
						ChatColor.GOLD + "Money" + 
						ChatColor.GREEN + "] Deposited " + 
						Double.toString(amount) + " in " + username + "'s account.");
				}
			}
			return;
		}
		return;
	}

}
