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

package com.modcrafting.mineslots.net;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.modcrafting.mineslots.MineSlots;
import com.modcrafting.mineslots.crypto.RSA;
import com.modcrafting.mineslots.model.Vote;
import com.modcrafting.mineslots.model.VoteListener;

public class VoteReceiver extends Thread {
	private static final Logger log = Logger.getLogger("VoteReceiver");
	private final String host;
	private final int port;
	private ServerSocket server;
	private boolean running = true;
	public VoteReceiver(String host, int port) {
		this.host = host;
		this.port = port;
	}
	public void shutdown() {
		running = false;
		if (server == null)
			return;
		try {
			server.close();
		} catch (Exception ex) {
			log.log(Level.WARNING, "Unable to shut down vote receiver cleanly.");
		}
	}
	public void run() {
		try {
			server = new ServerSocket();
			server.bind(new InetSocketAddress(host, port));
		} catch (Exception ex) {
			log.log(Level.SEVERE, "Error initializing vote receiver");
			return;
		}
		while (running) {
			try {
				Socket socket = server.accept();
				socket.setSoTimeout(5000); // Don't hang on slow connections.
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				InputStream in = socket.getInputStream();
				writer.write("MINESLOTS " + MineSlots.VERSION);
				writer.newLine();
				writer.flush();
				byte[] block = new byte[256];
				in.read(block, 0, block.length);
				block = RSA.decrypt(block, MineSlots.getInstance().getKeyPair().getPrivate());
				int position = 0;
				String opcode = readString(block, position);
				position += opcode.length() + 1;
				if (!opcode.equals("VOTE")) {
					// Something went wrong in RSA.
					throw new Exception("Unable to decode RSA");
				}

				// Parse the block.
				String serviceName = readString(block, position);
				position += serviceName.length() + 1;
				String username = readString(block, position);
				position += username.length() + 1;
				String address = readString(block, position);
				position += address.length() + 1;
				String timeStamp = readString(block, position);
				position += timeStamp.length() + 1;
				String itemCode = readString(block, position);
				position += itemCode.length() + 1;
				String cVar = readString(block, position);
				position += cVar.length() + 1;
				// Create the vote.
				Vote vote = new Vote();
				vote.setServiceName(serviceName);
				vote.setUsername(username);
				vote.setAddress(address);
				vote.setTimeStamp(timeStamp);
				vote.setItemCode(itemCode);
				vote.setcVar(cVar);

				// Dispatch the vote to all listeners.
				for (VoteListener listener : MineSlots.getInstance().getListeners()) {
					try {
						listener.voteMade(vote);
					} catch (Exception ex) {
						log.log(Level.WARNING, "Exception caught while sending the vote notification to a listener", ex);
					}
				}

				// Clean up.
				writer.close();
				in.close();
				socket.close();
			} catch (SocketException ignored) {
				// Ignore SocketException
			} catch (Exception ex) {
				log.log(Level.WARNING, "Exception caught while receiving a vote notification", ex);
			}
		}
	}

	/**
	 * Reads a string from a block of data.
	 * 
	 * @param data
	 *            The data to read from
	 * @return The string
	 */
	private String readString(byte[] data, int offset) {
		StringBuilder builder = new StringBuilder();
		for (int i = offset; i < data.length; i++) {
			if (data[i] == '\n')
				break; // Delimiter reached.
			builder.append((char) data[i]);
		}
		return builder.toString();
	}

}
