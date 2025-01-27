package me.muriplz.idler.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.muriplz.idler.afk.AfkPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class AFK {

    public static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayer();

        if (player == null) {
            source.sendSystemMessage(
                    Component.literal("Can't execute from console")
            );
            return 0;
        }

        AfkPlayer afkPlayer = (AfkPlayer) player;
        afkPlayer.stuff$enableAfk();

        return Command.SINGLE_SUCCESS;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("afk")
                .executes(AFK::execute)
        );
    }

}
