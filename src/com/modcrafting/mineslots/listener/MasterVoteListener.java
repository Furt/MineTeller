/* Non-Copyright 2012 Deathmarine Under
 * The Federation of Lost Lawn Chairs License 
 * Hereby make it know that the law is my bitch
 * and I need to protect open source software from 
 * thieves trying to sell it when that damn money
 * could have been mine. Blah. Blah. Blah.
 * 
 * Congrats I added on to you plugin.
 * Use the extra additions to the you plugin
 * as a compliment.
 * 
 * It really is a nice plugin just not what I needed
 * when I'm help out a friend.
 * 
 */
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.modcrafting.mineslots.MineSlots;
import com.modcrafting.mineslots.model.Vote;
import com.modcrafting.mineslots.model.VoteListener;

public class MasterVoteListener implements VoteListener {
	private Logger log = Logger.getLogger("MasterVoteListener");
	private double amount = 0;
	private static MineSlots v = null;
	private static Economy econ = null;
	public MasterVoteListener() {
		//Properties props = new Properties();
		v = MineSlots.getInstance();
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
		
	/*	
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
	*/
	}
	
	public void voteMade(Vote vote) {
		log.info("Received: " + vote);
		
		String username = vote.getUsername();
		
		String cVar = vote.getcVar();
		
		if (username == null) return;
		
		//Examples
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
		if (cVar.equalsIgnoreCase("pay")){
			amount = Double.parseDouble(vote.getitemCode());
			if(econ.hasAccount(username)){
				EconomyResponse r = econ.withdrawPlayer(username, amount);
				if (r.transactionSuccess()) {
					log.log(Level.INFO, "(MineSlots) Successfully withdrew " + username); 
				}else{
					log.log(Level.WARNING, "(MineSlots) Error" + r.errorMessage); 
				}
				if(Bukkit.getServer().getPlayer(username).isOnline()){
					Bukkit.getServer().getPlayer(username).sendMessage(
						ChatColor.GREEN + "[" + 
						ChatColor.GOLD + "Money" + 
						ChatColor.GREEN + "] Withdrawn " + 
						Double.toString(amount) + " from " + username + "'s account.");
				}
			}
			return;
		}
		
		if (cVar.equalsIgnoreCase("die") && Bukkit.getServer().getPlayer(username).isOnline()){
			Player name = Bukkit.getServer().getPlayer(username);
			name.setHealth(0);
			name.sendMessage(
					ChatColor.RED + "Betterluck Next Time!");
		}
		
		if (cVar.equalsIgnoreCase("ban")){
			Player name = Bukkit.getServer().getPlayer(username);
			if(name.isOnline()){
				name.kickPlayer(
						ChatColor.BLACK + "You just got F'd in the A!!");
			}
			name.setBanned(true);
		}
		
		if (Integer.parseInt(vote.getitemCode()) > 0){
			Player name = Bukkit.getServer().getPlayer(username);
			if(name.isOnline()){
				ItemStack item = new ItemStack(
						Integer.parseInt(vote.getitemCode()), 
						Integer.parseInt(vote.getcVar()));
				name.getInventory().addItem(item);
				name.sendMessage(
						ChatColor.AQUA + "You Received " +
						ChatColor.DARK_AQUA + item.getType().name() +
						"!");
			}
		}
		return;
	}

}
