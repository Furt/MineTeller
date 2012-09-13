package com.modcrafting.mineteller;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
	MineTeller plugin;

	public PlayerListener(MineTeller instance) {
		plugin = instance;
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		final Player p = event.getPlayer();
		final String name = p.getName();
		if (plugin.cache.containsKey(name.toLowerCase())) {
			plugin.loadVault();
			plugin.getServer().getScheduler()
					.scheduleAsyncDelayedTask(plugin, new Runnable() {

						@Override
						public void run() {
							Collection<String> cValue = plugin.cache.values();
							Collection<String> cKey = plugin.cache.keySet();
							Iterator<String> itrValue = cValue.iterator();
							Iterator<String> itrKey = cKey.iterator();
							while (itrValue.hasNext() && itrKey.hasNext()) {
								if (itrKey.next().equalsIgnoreCase(
										name.toLowerCase())) {
									String[] code = plugin.cache.remove(
											name.toLowerCase()).split(" ");
									String cVar = code[0].trim();
									String itemCode = code[1].trim();
									// This will give cVar="xp" based on the
									// value given for itemCode added to the
									// original players amount
									if (cVar.equalsIgnoreCase("xp")) {
										p.giveExp(Integer.parseInt(itemCode));
										p.sendMessage(ChatColor.GREEN + "["
												+ ChatColor.GOLD + "MineTeller"
												+ ChatColor.GREEN + "] "
												+ ChatColor.DARK_PURPLE
												+ "You've Received " + itemCode
												+ " Xp");
									}

									// This will remove cVar="xp" based on the
									// value given for itemCode added to the
									// original players amount
									if (cVar.equalsIgnoreCase("losexp")) {
										if (p.getExp() > Float
												.parseFloat(itemCode)) {
											p.setExp(p.getExp()
													- Float.parseFloat(itemCode));
											p.sendMessage(ChatColor.GREEN + "["
													+ ChatColor.GOLD
													+ "MineTeller"
													+ ChatColor.GREEN + "] "
													+ ChatColor.RED
													+ "You've lost " + itemCode
													+ " Xp");
										}
										return;
										/*
										 * Similar to the ping back system for
										 * Money xp will not remove if under the
										 * player amount unless you just want to
										 * punish them with death Or establish a
										 * ping for this as well
										 * name.setHealth(0);
										 */

									}
									/*
									 * Example String. $V->Vote("deathmarine",
									 * "127.0.0.1", 8992, "100",
									 * "code:AAAA-BBBB-1111-2222-xxxx",
									 * "RSAPublicKey"); cVar "code:" add the
									 * code to that string. itemCode use for the
									 * amount to withdraw and test.
									 */
									if (cVar.contains("code:")) {
										double amount = Double
												.parseDouble(itemCode);
										String code1 = cVar
												.replace("code:", "");

										if (plugin.econ.hasAccount(name)) {
											if (plugin.econ.getBalance(name) < amount) {
												if (plugin.getServer()
														.getOfflinePlayer(name)
														.isOnline()) {
													// Sent to Specific Player
													// Displayed as [] Attempt
													// to withdraw {Qty} from
													// {Player} failed
													Bukkit.getServer()
															.getPlayer(name)
															.sendMessage(
																	ChatColor.GREEN
																			+ "["
																			+ ChatColor.GOLD
																			+ "MineTeller"
																			+ ChatColor.GREEN
																			+ "] "
																			+ ChatColor.GREEN
																			+ "Attempt to withdraw "
																			+ Double.toString(amount)
																			+ " from "
																			+ name
																			+ " failed!");
												}
												return;
											} else {
												EconomyResponse r = plugin.econ
														.withdrawPlayer(name,
																amount);
												if (r.transactionSuccess()) {
													plugin.logger(Level.INFO,
															"Successfully withdrew "
																	+ name);
												} else {
													plugin.logger(
															Level.WARNING,
															r.errorMessage);
												}
												if (Bukkit.getServer()
														.getOfflinePlayer(name)
														.isOnline()) {
													Bukkit.getServer()
															.getPlayer(name)
															.sendMessage(
																	ChatColor.GREEN
																			+ "["
																			+ ChatColor.GOLD
																			+ "MineTeller"
																			+ ChatColor.GREEN
																			+ "] "
																			+ ChatColor.GREEN
																			+ "Use Code: "
																			+ code1
																			+ " to recieve your "
																			+ ChatColor.GOLD
																			+ Double.toString(amount)
																			+ ChatColor.GREEN
																			+ " Tokens!");
												}
												return;
											}
										}
									}
									// This will kill the player on receipt if
									// cVar="die"
									if (cVar.equalsIgnoreCase("die")
											&& Bukkit.getServer()
													.getOfflinePlayer(name)
													.isOnline()) {
										p.setHealth(0);
										p.sendMessage(ChatColor.GREEN + "["
												+ ChatColor.GOLD + "MineTeller"
												+ ChatColor.GREEN + "] "
												+ ChatColor.RED
												+ "Betterluck Next Time!");
										return;
									}
									// Logic check for integer in cVar
									boolean working = true;

									for (int i = 0; i < cVar.length(); i++) {
										if (!Character.isDigit(cVar.charAt(i))) {
											working = false;
											break;
										}
									}

									/*
									 * This will give a player an item/itemstack
									 * dependant on itemCode and cVar Example
									 * itemCode="278" cVar="1" will give one
									 * diamond pickaxe or itemCode="46"
									 * cVar="64" will give a full stack of tnt
									 * Displays You Recieved {QTY} {ITEMNAME}!
									 */
									if (working
											&& Integer.parseInt(itemCode) > 0) {
										if (p != null && p.isOnline()) {
											ItemStack item = new ItemStack(
													Integer.parseInt(itemCode),
													Integer.parseInt(cVar));
											p.getInventory().addItem(item);
											p.sendMessage(ChatColor.GREEN + "["
													+ ChatColor.GOLD
													+ "MineTeller"
													+ ChatColor.GREEN + "] "
													+ ChatColor.AQUA
													+ "You Received "
													+ ChatColor.DARK_AQUA
													+ cVar + " "
													+ ChatColor.DARK_AQUA
													+ item.getType().name()
													+ "!");
										}
										return;
									}

								}
							}
						}
					});

		}
	}

}
