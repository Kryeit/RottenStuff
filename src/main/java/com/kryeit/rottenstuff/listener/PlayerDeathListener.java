package com.kryeit.rottenstuff.listener;

import com.kryeit.rottenstuff.RottenStuff;
import com.kryeit.rottenstuff.Utils;
import com.kryeit.rottenstuff.lives.Lives;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = RottenStuff.MOD_ID)
public class PlayerDeathListener {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Utils.broadcast(
                "Player " + player.getName().getString() + " died!"
            );

            Lives.remove(player.getUUID(), 1);

            if (event.getSource().getEntity() == null) {
                return;
            }

            if (event.getSource().getEntity() instanceof ServerPlayer killer) {
                Utils.broadcast(
                    "Player " + killer.getName().getString() + " killed " + player.getName().getString() + "!"
                );

                Lives.add(killer.getUUID(), 1);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Utils.broadcast(
                "Player " + player.getName().getString() + " respawned!"
            );

            if (Lives.get(player.getUUID()).lives() <= 0) {
                player.connection.disconnect(
                        Component.literal("You have no lives left! Come back in 7 days from now.")
                );
            }
        }
    }
}
