package woody44.minecraft.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerKickEvent.Cause;

import net.kyori.adventure.text.Component;
import woody44.minecraft.core.events.PlayerAuthEvent;
import woody44.minecraft.core.player.PlayerCore;

public class CMDLogin extends CoreCommand {

    @Override
    public boolean onCommand(PlayerCore player, CommandSender sender, Command cmd, String label, String[] args) {
        if (player.IsAuthed()) {
            player.systemMessage(2, "You are already logged in!");
            return true;
        }

        PlayerAuthEvent event = new PlayerAuthEvent(player, player.Login(args[0]));
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            player.getBukkitPlayer().kick(Component.text("Your login try was rejected by server."), Cause.PLUGIN);
            return true;
        }

        if (event.GetResult()) {
            player.systemMessage(0, "Logged in.");
        } else
            player.systemMessage(1, "Bad password or account does not exist.");
        return true;
    }
}
