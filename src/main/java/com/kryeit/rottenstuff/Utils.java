package com.kryeit.rottenstuff;

import net.minecraft.network.chat.Component;

public class Utils {

    public static void broadcast(String message) {
        MinecraftServerSupplier.getServer().getPlayerList().broadcastSystemMessage(
                Component.literal(message), false
        );
    }
}
