package com.kryeit.rottenstuff.listener;

import com.kryeit.rottenstuff.MinecraftServerSupplier;
import com.kryeit.rottenstuff.PlayerApi;
import com.kryeit.rottenstuff.RottenStuff;
import com.kryeit.rottenstuff.lives.Lives;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = RottenStuff.MOD_ID)
public class PlayerSessionListener {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (new PlayerApi().check(player.getUUID(), "group.staff")) {
            return;
        }

        Lives lives = Lives.get(player.getUUID());

        if (lives.lives() < 1) {
            serverPlayer.connection.disconnect(Component.literal("You have been kicked from the server."));
        } else {
            serverPlayer.sendSystemMessage(
                Component.literal("Welcome to the server! You have " + lives.lives() + " lives remaining."),
                false
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
    }
}
