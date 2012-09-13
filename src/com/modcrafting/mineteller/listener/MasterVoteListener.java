/* Non-Copyright 2012 Deathmarine Under
 * The Federation of Lost Lawn Chairs License
 * 
 */
package com.modcrafting.mineteller.listener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.modcrafting.mineteller.MineTeller;
import com.modcrafting.mineteller.model.Vote;
import com.modcrafting.mineteller.model.VoteListener;

public class MasterVoteListener implements VoteListener {
	private MineTeller plugin;

	public MasterVoteListener(MineTeller instance) {
		this.plugin = instance;
	}

	public void voteMade(Vote vote) {
		plugin.logger(Level.INFO, "Received: " + vote);
		String username = vote.getUsername();
		String cVar = vote.getcVar();

		if (username == null)
			return;

		Player name = Bukkit.getServer().getPlayer(username);
		if (name == null) {
			name = Bukkit.getServer().getOfflinePlayer(username).getPlayer();
			if (name != null) {
				MineTeller.getInstance().cache.put(
						name.getName().toLowerCase(), vote.getcVar() + " "
								+ vote.getitemCode());
				return;
			} else {
				MineTeller.getInstance().cache.put(username.toLowerCase(),
						vote.getcVar() + " " + vote.getitemCode());
				return;
			}
		}

		// Examples of cVars to use on the site Timed Flying (itemCode=Amount of
		// time)
		// Kind of incompatible with NoCheat
		if (cVar.equalsIgnoreCase("fly")
				&& Bukkit.getServer().getOfflinePlayer(username).isOnline()) {
			name.setFlying(true);
			name.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD
					+ vote.getServiceName() + ChatColor.GREEN + "] "
					+ ChatColor.YELLOW + "You can now fly.");
			/*
			 * Hashmap.put(name.getName(),Integer.parse(vote.getitemCode()));
			 * (new Thread(FlyTimer)).start(); This will enable the player
			 * Flying if vote cVar is "fly"
			 */
			return;
		}

		// This will deposit an amount from the itemCode
		if (cVar.equalsIgnoreCase("deposit")) {
			if (!plugin.econEnabled) {

			}
			double amount = Double.parseDouble(vote.getitemCode());
			if (plugin.econ.hasAccount(username)) {
				EconomyResponse r = plugin.econ.depositPlayer(username, amount);
				if (r.transactionSuccess()) {
					plugin.logger(Level.INFO, "Successfully credited "
							+ username);
				} else {
					plugin.logger(Level.WARNING, "Error" + r.errorMessage);
				}
				// Sent to Specific Player Displayed as [Money] Deposited {Qty}
				// in {Player}'s account
				if (plugin.getServer().getOfflinePlayer(username).isOnline()) {
					plugin.getServer()
							.getPlayer(username)
							.sendMessage(
									ChatColor.GREEN + "[" + ChatColor.GOLD
											+ vote.getServiceName()
											+ ChatColor.GREEN + "] "
											+ ChatColor.GREEN + "Deposited "
											+ Double.toString(amount) + " in "
											+ username + "'s account.");
				}
			}
			return;
		}

		if (cVar.equalsIgnoreCase("pay")) {
			double amount = Double.parseDouble(vote.getitemCode());
			if (plugin.econ.hasAccount(username)) {
				if (plugin.econ.getBalance(username) < amount) {
					if (Bukkit.getServer().getOfflinePlayer(username)
							.isOnline()) {
						// Sent to Specific Player Displayed as [Money] Attempt
						// to withdraw {Qty} from {Player} failed
						Bukkit.getServer()
								.getPlayer(username)
								.sendMessage(
										ChatColor.GREEN + "[" + ChatColor.GOLD
												+ vote.getServiceName()
												+ ChatColor.GREEN + "] "
												+ ChatColor.GREEN
												+ "Attempt to withdraw "
												+ Double.toString(amount)
												+ "from " + username
												+ " failed!");
					}
					return;
				} else {
					EconomyResponse r = plugin.econ.withdrawPlayer(username,
							amount);
					if (r.transactionSuccess()) {
						plugin.logger(Level.INFO, "Successfully withdrew "
								+ username);
					} else {
						plugin.logger(Level.WARNING, "Error" + r.errorMessage);
					}
					// Sent to Specific Player Displayed as [Money] Withdrawn
					// {Qty} from {Player}'s account
					if (Bukkit.getServer().getOfflinePlayer(username)
							.isOnline()) {
						Bukkit.getServer()
								.getPlayer(username)
								.sendMessage(
										ChatColor.GREEN + "[" + ChatColor.GOLD
												+ vote.getServiceName()
												+ ChatColor.GREEN + "] "
												+ ChatColor.GREEN
												+ "Withdrawn "
												+ Double.toString(amount)
												+ " from " + username
												+ "'s account.");
					}
				}

			}
			return;
		}

		// This will give cVar="xp" based on the value given for itemCode added
		// to the original players amount
		if (cVar.equalsIgnoreCase("xp")) {
			if (Bukkit.getServer().getOfflinePlayer(username).isOnline()) {
				name.giveExp(Integer.parseInt(vote.getitemCode()));
				name.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD
						+ vote.getServiceName() + ChatColor.GREEN + "] "
						+ ChatColor.DARK_PURPLE + "You've Received "
						+ vote.getitemCode() + " Xp");
				return;
			} else {
				return;
			}
		}

		// This will remove cVar="xp" based on the value given for itemCode
		// added to the original players amount
		if (cVar.equalsIgnoreCase("losexp")
				&& Bukkit.getServer().getOfflinePlayer(username).isOnline()) {
			if (name.getExp() > Float.parseFloat(vote.getitemCode())) {
				name.setExp(name.getExp()
						- Float.parseFloat(vote.getitemCode()));
				name.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD
						+ vote.getServiceName() + ChatColor.GREEN + "] "
						+ ChatColor.RED + "You've lost " + vote.getitemCode()
						+ " Xp");
			}
			return;
			/*
			 * Similar to the ping back system for Money xp will not remove if
			 * under the player amount unless you just want to punish them with
			 * death Or establish a ping for this as well name.setHealth(0);
			 */

		}

		/*
		 * Example String. $V->Vote("deathmarine", "127.0.0.1", 8992, "100",
		 * "code:AAAA-BBBB-1111-2222-xxxx", "RSAPublicKey"); cVar "code:" add
		 * the code to that string. itemCode use for the amount to withdraw and
		 * test.
		 */
		if (cVar.contains("code:")) {
			String code = cVar.replace("code:", "");
			double amount = Double.parseDouble(vote.getitemCode());
			if (plugin.econ.hasAccount(username)) {
				if (plugin.econ.getBalance(username) < amount) {
					if (Bukkit.getServer().getOfflinePlayer(username)
							.isOnline()) {
						// Sent to Specific Player Displayed as [] Attempt to
						// withdraw {Qty} from {Player} failed
						Bukkit.getServer()
								.getPlayer(username)
								.sendMessage(
										ChatColor.GREEN + "[" + ChatColor.GOLD
												+ vote.getServiceName()
												+ ChatColor.GREEN + "] "
												+ ChatColor.GREEN
												+ "Attempt to withdraw "
												+ Double.toString(amount)
												+ " from " + username
												+ " failed!");
					}
					return;
				} else {
					EconomyResponse r = plugin.econ.withdrawPlayer(username,
							amount);
					if (r.transactionSuccess()) {
						plugin.logger(Level.INFO, "Successfully withdrew "
								+ username);
					} else {
						plugin.logger(Level.WARNING, "Error" + r.errorMessage);
					}
					if (Bukkit.getServer().getOfflinePlayer(username)
							.isOnline()) {
						Bukkit.getServer()
								.getPlayer(username)
								.sendMessage(
										ChatColor.GREEN + "[" + ChatColor.GOLD
												+ vote.getServiceName()
												+ ChatColor.GREEN + "] "
												+ ChatColor.GREEN
												+ "Use Code: " + code
												+ " to recieve your "
												+ ChatColor.GOLD
												+ Double.toString(amount)
												+ ChatColor.GREEN + " Tokens!");
					}
					return;
				}
			}
		}

		if (cVar.equalsIgnoreCase("debug")) {
			this.outputHashMap(plugin.cache);
		}

		// This will kill the player on receipt if cVar="die"
		if (cVar.equalsIgnoreCase("die")
				&& Bukkit.getServer().getOfflinePlayer(username).isOnline()) {
			name.setHealth(0);
			name.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD
					+ vote.getServiceName() + ChatColor.GREEN + "] "
					+ ChatColor.RED + "Betterluck Next Time!");
			return;
		}

		/*
		 * This will give a player an item/itemstack dependant on itemCode and
		 * cVar Example itemCode="278" cVar="1" will give one diamond pickaxe or
		 * itemCode="46" cVar="64" will give a full stack of tnt Displays You
		 * Recieved {QTY} {ITEMNAME}!
		 */
		if (Integer.parseInt(vote.getitemCode()) > 0) {
			if (name != null && name.isOnline()) {
				ItemStack item = new ItemStack(Integer.parseInt(vote
						.getitemCode()), Integer.parseInt(vote.getcVar()));
				name.getInventory().addItem(item);
				name.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD
						+ vote.getServiceName() + ChatColor.GREEN + "] "
						+ ChatColor.AQUA + "You Received "
						+ ChatColor.DARK_AQUA + vote.getcVar() + " "
						+ ChatColor.DARK_AQUA + item.getType().name() + "!");
			}
			return;
		}

		return;
	}

	void outputHashMap(HashMap<String, String> hm) {
		if (hm == null) {
			return;
		}

		Collection<String> cValue = hm.values();
		Collection<String> cKey = hm.keySet();
		Iterator<String> itrValue = cValue.iterator();
		Iterator<String> itrKey = cKey.iterator();

		while (itrValue.hasNext() && itrKey.hasNext()) {
			plugin.getServer().broadcastMessage(
					itrKey.next() + ": " + itrValue.next());

		}
	}

}
