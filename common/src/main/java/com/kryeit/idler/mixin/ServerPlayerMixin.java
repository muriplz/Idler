package com.kryeit.idler.mixin;

import com.kryeit.idler.Idler;
import com.kryeit.idler.afk.AfkPlayer;
import com.kryeit.idler.afk.Config;
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
    public ServerPlayer idler$player = (ServerPlayer) (Object) this;
    @Unique
    private boolean idler$isAfk;

    public ServerPlayerMixin(EntityType<?> type, ServerLevel world) {
        super(type, world);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onPlayerLogin(CallbackInfo ci) {
        System.out.println("Player " + idler$player.getName().getString() + " has joined the game");
        Idler.lastTimePlayed.addElement(idler$player.getUUID(), System.currentTimeMillis());
    }

    @Unique
    public boolean idler$isAfk() {
        return this.idler$isAfk;
    }

    @Unique
    public void idler$enableAfk() {
        if (idler$isAfk()) return;
        idler$setAfk(true);
    }

    @Unique
    public void idler$disableAfk() {
        Idler.lastTimePlayed.addElement(idler$player.getUUID(), System.currentTimeMillis());
        if (!idler$isAfk) return;
        idler$setAfk(false);
    }

    @Unique
    private void idler$setAfk(boolean isAfk) {
        this.idler$isAfk = isAfk;
        this.server
                .getPlayerList()
                .broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, idler$player));
    }

    @Inject(method = "resetLastActionTime", at = @At("TAIL"))
    private void onActionTimeUpdate(CallbackInfo ci) {
        idler$disableAfk();
    }

    @Inject(method = "getTabListDisplayName", at = @At("RETURN"), cancellable = true)
    private void replacePlayerListName(CallbackInfoReturnable<Component> cir) {
        MutableComponent name = idler$player.getName().copy().withStyle(ChatFormatting.WHITE);

        if (Config.PlayerListOptions.enableListDisplay && idler$isAfk) {
            ChatFormatting color = ChatFormatting.GRAY;
            name = name.withStyle(color);
        }

        cir.setReturnValue(name);
    }

    @Inject(method = "getLastActionTime", at = @At("HEAD"), cancellable = true)
    private void onGetLastActionTime(CallbackInfoReturnable<Long> cir) {
        cir.setReturnValue(Idler.lastTimePlayed.getElement(idler$player.getUUID()));
    }
}