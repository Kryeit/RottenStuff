package com.kryeit.rottenstuff;

import com.kryeit.rottenstuff.command.LivesCommand;
import com.kryeit.rottenstuff.lives.Lives;
import com.kryeit.rottenstuff.storage.Database;
import com.kryeit.rottenstuff.storage.DatabaseUtils;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.io.IOException;
import java.nio.file.Path;

@Mod(RottenStuff.MOD_ID)
public class RottenStuff {
    public static final String MOD_ID = "rottenstuff";

    public RottenStuff() {
        try {
            ConfigReader.readFile(Path.of("config/" + MOD_ID));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        DatabaseUtils.createTables();

        CommandDispatcher<CommandSourceStack> dispatcher = event.getServer().getCommands().getDispatcher();

        LivesCommand.register(dispatcher);
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppedEvent event) {
        Database.closeDataSource();
    }

}
