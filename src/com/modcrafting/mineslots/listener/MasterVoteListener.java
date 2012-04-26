package com.modcrafting.mineslots.listener;

import java.util.logging.Logger;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VoteListener;

public class MasterVoteListener implements VoteListener {

	/** The logger instance. */
	private Logger log = Logger.getLogger("BasicVoteListener");

	@Override
	public void voteMade(Vote vote) {
		log.info("Received: " + vote);
	}

}
