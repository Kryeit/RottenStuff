package com.kryeit.rottenstuff.listener;

import com.kryeit.rottenstuff.ConfigReader;
import com.kryeit.rottenstuff.RottenStuff;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;

@EventBusSubscriber(modid = RottenStuff.MOD_ID)
public class DeskBellHintListener {

    public static final List<BlockPos> bellPositions = List.of(
            new BlockPos(8, 67, 12),
            new BlockPos(8, 67, 4)
    );

    @SubscribeEvent
    public static void onBellInteract(PlayerInteractEvent.RightClickBlock event) {
        for (BlockPos pos : bellPositions) {
            if (event.getPos().equals(pos)) {
                List<String> hints = ConfigReader.HINTS;

                if (hints.isEmpty()) {
                    return;
                }

                String hint = hints.get(event.getEntity().getRandom().nextInt(hints.size()));

                event.getEntity().sendSystemMessage(
                        Component.literal(hint).withStyle(ChatFormatting.AQUA)
                );

            }
        }
    }
}
