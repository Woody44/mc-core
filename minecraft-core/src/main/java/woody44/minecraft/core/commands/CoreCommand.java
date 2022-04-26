package woody44.minecraft.core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import woody44.minecraft.core.player.PlayerCore;

public abstract class CoreCommand implements CommandExecutor {
    public abstract boolean onCommand(PlayerCore player, CommandSender sender, Command cmd, String label,
            String[] args);

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return onCommand(PlayerCore.getPlayer(((Player) sender).getUniqueId()), sender, cmd, label, args);
    }
}