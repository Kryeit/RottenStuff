package com.kryeit.rottenstuff.command;

import com.kryeit.rottenstuff.lives.Lives;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;


public class LivesCommand {
    public static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayer();

        if (player == null) {
            return -1;
        }

        Lives lives = Lives.get(player.getUUID());

        Component message = Component.literal(
            "You have " + lives.lives() + " lives and your max lives rank is " + lives.rank().name() + " with " + lives.rank().getMaxLives()
        );

        player.sendSystemMessage(message);

        return Command.SINGLE_SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("lives")
                .executes(LivesCommand::execute)
        );
    }
}
