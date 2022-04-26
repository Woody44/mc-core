package woody44.minecraft.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import woody44.minecraft.core.player.PlayerCore;

public class CMDLevel extends CoreCommand {

    @Override
    public boolean onCommand(PlayerCore player, CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length <2) {
            player.systemMessage(1, "Missing arguments.");
            return true;
        }

        PlayerCore pc = PlayerCore.getPlayer(Bukkit.getPlayerExact(args[0]));
        int val = 0;

        switch(label){
            case "lvl":{
                    val = Integer.parseInt(args[1]);
                    pc.profile.stats.Experience = 0;
                    pc.profile.stats.Level = Integer.parseInt(args[1]);
                    pc.profile.calculateExp();
                break;
            }
            case "xp":{
                if(args[1].contains("%"))
                    val = Integer.parseInt(args[1].replace("%", "")) * pc.profile.getExpToNextLevel() / 100;
                else
                    val = Integer.parseInt(args[1]);

                pc.profile.stats.Experience = val;
                pc.profile.calculateExp();
                break;
            }
        }
        return true;
    }
}
