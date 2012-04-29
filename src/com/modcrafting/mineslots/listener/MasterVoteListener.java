/* Non-Copyright 2012 Deathmarine Under
 * The Federation of Lost Lawn Chairs License
 * 
 */
package com.modcrafting.mineslots.listener;

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
	private Logger log = Logger.getLogger("Minecraft");
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
	}
	
	public void voteMade(Vote vote) {
		log.info("Received: " + vote);
		
		String username = vote.getUsername();
		
		String cVar = vote.getcVar();
		
		if (username == null) return;
		
		//Examples of cVars to use on the site Timed Flying (itemCode=Amount of time)
		//Kind of incompatible with NoCheat
		if (cVar.equalsIgnoreCase("fly") && Bukkit.getServer().getPlayer(username).isOnline()){
			Player name = Bukkit.getServer().getPlayer(username);
			name.setFlying(true);
			name.sendMessage(ChatColor.GREEN + "You can now fly.");
			/*Hashmap.put(name.getName(),Integer.parse(vote.getitemCode()));
			 *(new Thread(FlyTimer)).start();
			 * This will enable the player Flying if vote cVar is "fly"
			 */
			return;
		}
		
		//This will deposit an amount from the itemCode
		if (cVar.equalsIgnoreCase("deposit")){
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
		//This will take money form the users account and ping back if they no longer have funds
		if (cVar.equalsIgnoreCase("pay")){
			amount = Double.parseDouble(vote.getitemCode());
			if(econ.hasAccount(username)){
				if(econ.getBalance(username) < amount){
					if(Bukkit.getServer().getPlayer(username).isOnline()){
						Bukkit.getServer().getPlayer(username).sendMessage(
								ChatColor.GREEN + "[" + 
								ChatColor.GOLD + "Money" + 
								ChatColor.GREEN + "] Attempt to withdraw " + 
								Double.toString(amount) + "from " + username + " failed!");
					}
					/*
					 *(new Thread(PingBack)).start();
					 * send ping back notification
					 */
					return;
				}else{
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
				
			}
			return;
		}
		
		//This will kill the player on receipt if cVar="die"
		if (cVar.equalsIgnoreCase("die") && Bukkit.getServer().getPlayer(username).isOnline()){
			Player name = Bukkit.getServer().getPlayer(username);
			name.setHealth(0);
			name.sendMessage(
					ChatColor.RED + "Betterluck Next Time!");
		}
		
		/*
		 * This will give a player an item/itemstack dependant on itemCode and cVar
		 * Example itemCode="278" cVar="1" will give one diamond pickaxe
		 * or itemCode="46" cVar="64" will give a full stack of tnt
		 */
		if (Integer.parseInt(vote.getitemCode()) > 0){
			Player name = Bukkit.getServer().getPlayer(username);
			//Local Storage is available to store player winnings
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
