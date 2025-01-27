package me.muriplz.idler.mixin;

import me.muriplz.idler.Idler;
import me.muriplz.idler.afk.AfkPlayer;
import me.muriplz.idler.afk.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// This class has been mostly made by afkdisplay mod
// https://github.com/beabfc/afkdisplay
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Entity implements AfkPlayer {

    @Shadow
    @Final
    public MinecraftServer server;

    @Unique
    public ServerPlayer stuff$player = (ServerPlayer) (Object) this;
    @Unique
    private boolean stuff$isAfk;

    public ServerPlayerMixin(EntityType<?> type, ServerLevel world) {
        super(type, world);
    }

    @Unique
    public boolean stuff$isAfk() {
        return this.stuff$isAfk;
    }

    @Unique
    public void stuff$enableAfk() {
        if (stuff$isAfk()) return;
        stuff$setAfk(true);
    }

    @Unique
    public void stuff$disableAfk() {
        Idler.lastTimePlayed.addElement(stuff$player.getUUID(), System.currentTimeMillis());
        if (!stuff$isAfk) return;
        stuff$setAfk(false);
    }

    @Unique
    private void stuff$setAfk(boolean isAfk) {
        this.stuff$isAfk = isAfk;
        this.server
                .getPlayerList()
                .broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, stuff$player));
    }

    @Inject(method = "resetLastActionTime", at = @At("TAIL"))
    private void onActionTimeUpdate(CallbackInfo ci) {
        stuff$disableAfk();
    }

    @Inject(method = "getTabListDisplayName", at = @At("RETURN"), cancellable = true)
    private void replacePlayerListName(CallbackInfoReturnable<Component> cir) {
        MutableComponent name = stuff$player.getName().copy().withStyle(ChatFormatting.WHITE);

        if (Config.PlayerListOptions.enableListDisplay && stuff$isAfk) {
            ChatFormatting color = ChatFormatting.GRAY;
            name = name.withStyle(color);
        }

        cir.setReturnValue(name);
    }
}