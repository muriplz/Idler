package com.kryeit.idler;

import com.kryeit.idler.commands.AFK;
import com.kryeit.idler.config.ConfigReader;
import com.kryeit.idler.storage.LastTimePlayed;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.io.IOException;
import java.nio.file.Path;

public final class Idler {
    public static final String MOD_ID = "idler";
    public static LastTimePlayed lastTimePlayed;

    public static void init() {
    }

    public static void onServerStart() {
        try {
            lastTimePlayed = new LastTimePlayed("config/" + MOD_ID + "/last_played.txt");
            ConfigReader.readFile(Path.of("config/" + MOD_ID));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CommandDispatcher<CommandSourceStack> dispatcher = MinecraftServerSupplier.getServer().getCommands().getDispatcher();

        AFK.register(dispatcher);
    }

    public static void onServerStop() {
        lastTimePlayed.shutdown();
    }

    public static void resetLastTime(ServerPlayer player) {
        lastTimePlayed.addElement(player.getUUID(), System.currentTimeMillis());
    }

    public static void onPlayerPlaceBlock(ServerPlayer player) {
        lastTimePlayed.addElement(player.getUUID(), System.currentTimeMillis());
    }

    public static void onPlayerBreakBlock(ServerPlayer player) {
        lastTimePlayed.addElement(player.getUUID(), System.currentTimeMillis());
    }

    public static void onPlayerItemUse(ServerPlayer player) {
        lastTimePlayed.addElement(player.getUUID(), System.currentTimeMillis());
    }
}
