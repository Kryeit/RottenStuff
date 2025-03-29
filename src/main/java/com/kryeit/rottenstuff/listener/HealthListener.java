package com.kryeit.rottenstuff.listener;

import com.kryeit.rottenstuff.RottenStuff;
import com.kryeit.rottenstuff.Utils;
import com.kryeit.votifier.model.Vote;
import com.kryeit.votifier.model.VotifierEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = RottenStuff.MOD_ID)
public class HealthListener {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (!(player instanceof ServerPlayer)) return;

        if (!player.isDeadOrDying() && player.getHealth() > 1.0f) {
            player.setHealth(1.0f);
        }
    }

    @SubscribeEvent
    public static void onPlayerDamage(LivingDamageEvent.Pre event) {
        if (event.getEntity() instanceof Player player) {
            // If player is attacked (takes any damage)
            if (event.getNewDamage() > 0) {

                if (event.getSource().getEntity() == null) return;
                // Kill player if they're at half heart and get attacked
                if (player.getHealth() <= 1.0f) {
                    Utils.broadcast("Player " + player.getName().getString() + " has been killed by " + event.getSource().getEntity().getName().getString());
                }
            }
        }
    }
}
