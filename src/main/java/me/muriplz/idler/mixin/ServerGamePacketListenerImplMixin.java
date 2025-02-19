package me.muriplz.idler.mixin;

import me.muriplz.idler.Idler;
import me.muriplz.idler.PlayerApi;
import me.muriplz.idler.afk.AfkPlayer;
import me.muriplz.idler.afk.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// This class has been mostly made by afkdisplay mod
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "tick", at = @At("HEAD"))
    private void updateAfkStatus(CallbackInfo ci) {
        AfkPlayer afkPlayer = (AfkPlayer) player;
        int timeoutSeconds = Config.PacketOptions.timeoutSeconds;
        if (timeoutSeconds <= 0) return;
        if (Idler.lastTimePlayed.getElement(player.getUUID()) == -1L) return;

        long afkDuration = System.currentTimeMillis() - Idler.lastTimePlayed.getElement(player.getUUID());
        if (afkDuration > timeoutSeconds * 1000L) {
            afkPlayer.stuff$enableAfk();
            Idler.lastTimePlayed.addElement(player.getUUID(), System.currentTimeMillis());

            PlayerApi playerApi = new PlayerApi();
            if (playerApi.check(player.getUUID(), "idler.afk")) return;

            player.connection.disconnect(Component.literal("You have been kicked for being AFK too long."));
        }
    }

    @Inject(method = "handleMovePlayer", at = @At("HEAD"))
    private void checkPlayerLook(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
        if (Config.PacketOptions.resetOnLook && packet.hasRotation()) {
            float yaw = player.getYRot();
            float pitch = player.getXRot();
            if (pitch != packet.getXRot(pitch) || yaw != packet.getYRot(yaw))
                player.resetLastActionTime();
        }

        if (Config.PacketOptions.resetOnMovement && packet.hasPosition()) {
            double x = player.getX();
            double y = player.getY();
            double z = player.getZ();
            if (x != packet.getX(x) || y != packet.getY(y) || z != packet.getZ(z))
                player.resetLastActionTime();
        }
    }
}
