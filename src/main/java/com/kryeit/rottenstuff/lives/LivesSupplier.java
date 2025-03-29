package com.kryeit.rottenstuff.lives;

import com.kryeit.rottenstuff.MinecraftServerSupplier;
import com.kryeit.rottenstuff.RottenStuff;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@EventBusSubscriber(modid = RottenStuff.MOD_ID)
public class LivesSupplier {

    private static final long WEEK_IN_MILLISECONDS = TimeUnit.DAYS.toMillis(7);

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (MinecraftServerSupplier.getServer() == null) return;
        // Check once per minute (1200 ticks)
        if (MinecraftServerSupplier.getServer().getTickCount() % 1200 == 0) {
            checkAndSupplyLives();
        }
    }

    private static void checkAndSupplyLives() {
        long currentTime = System.currentTimeMillis();

        MinecraftServerSupplier.getServer().getPlayerList().getPlayers().forEach(player -> {
            UUID playerUUID = player.getUUID();
            Lives playerLives = Lives.get(playerUUID);
            Timestamp lastSupplyTime = playerLives.week();

            // Check if a week has passed since last supply
            if (isWeekPassed(lastSupplyTime)) {
                supplyLivesToPlayer(player);
            }
        });
    }

    private static boolean isWeekPassed(Timestamp lastTime) {
        if (lastTime == null) return true;

        Instant now = Instant.now();
        Instant lastInstant = lastTime.toInstant();

        // Check if at least 7 days have passed
        return lastInstant.plus(7, ChronoUnit.DAYS).isBefore(now);
    }

    private static void supplyLivesToPlayer(ServerPlayer player) {
        Lives playerLives = Lives.get(player.getUUID());
        int currentLives = playerLives.lives();
        int maxLives = playerLives.rank().getMaxLives();

        // Only add lives if player isn't at maximum
        if (currentLives < maxLives) {

            int livesToAdd = maxLives / 2;
            Lives updatedLives = Lives.add(player.getUUID(), livesToAdd);
            Lives.updateWeekTimestamp(player.getUUID());

            player.sendSystemMessage(
                    Component.literal("You received " + livesToAdd + " extra lives! You now have " + updatedLives.lives() + " lives.")
            );

            // Log the lives supply event
            MinecraftServerSupplier.getServer().sendSystemMessage(Component.literal("[RottenStuff] Weekly life supplied to " + player.getName()));
        } else {
            // Update the timestamp even if no lives were added
            Lives.updateWeekTimestamp(player.getUUID());
        }
    }
}