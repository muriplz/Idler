package me.muriplz.idler;

import com.mojang.brigadier.CommandDispatcher;
import me.muriplz.idler.commands.AFK;
import me.muriplz.idler.config.ConfigReader;
import me.muriplz.idler.storage.LastTimePlayed;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.io.IOException;
import java.nio.file.Path;

@Mod(Idler.MODID)
public class Idler {
    public static final String MODID = "idler";
    public static LastTimePlayed lastTimePlayed;

    public Idler() {
        try {
            ConfigReader.readFile(Path.of("config/" + MODID));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        try {
            lastTimePlayed = new LastTimePlayed("config/" + MODID + "/last_played.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CommandDispatcher<CommandSourceStack> dispatcher = event.getServer().getCommands().getDispatcher();

        AFK.register(dispatcher);
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        lastTimePlayed.shutdown();
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        lastTimePlayed.addElement(player.getUUID(), System.currentTimeMillis());
    }
}
