package com.kryeit.rottenstuff.listener;

import com.kryeit.rottenstuff.RottenStuff;
import com.kryeit.rottenstuff.Utils;
import com.kryeit.votifier.model.Vote;
import com.kryeit.votifier.model.VotifierEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = RottenStuff.MOD_ID)
public class VoteListener {

    @SubscribeEvent
    public static void onVote(VotifierEvent event) {
        Vote vote = event.getVote();

        Utils.broadcast("§a" + vote.getUsername() + " §7voted for the server on §a" + vote.getServiceName() + "§7!");
    }
}
