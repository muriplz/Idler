package me.muriplz.idler.listener;

import me.muriplz.idler.Idler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class PlayerEvents {

    @SubscribeEvent
    public static void onPlayerPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        Idler.lastTimePlayed.addElement(player.getUUID(), System.currentTimeMillis());
    }

    @SubscribeEvent
    public static void onPlayerBreak(BlockEvent.BreakEvent event) {
        Idler.lastTimePlayed.addElement(event.getPlayer().getUUID(), System.currentTimeMillis());
    }

    @SubscribeEvent
    public static void onItemUse(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        Idler.lastTimePlayed.addElement(player.getUUID(), System.currentTimeMillis());
    }
}
