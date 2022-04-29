package woody44.minecraft.core.commands;

import java.text.MessageFormat;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import woody44.minecraft.core.player.PlayerCore;

public class CMDInfo extends CoreCommand {

    @Override
    public boolean onCommand(PlayerCore player, CommandSender sender, Command cmd, String label, String[] args) {
        
        Player _p = null;
        if(args.length>0)
            _p = Bukkit.getPlayerExact(args[0]);
        else
            _p = player.getBukkitPlayer();
        
        if(_p == null){
            player.systemMessage(1, "Could not find player.");
            return true;
        }
        PlayerCore _pp = PlayerCore.getPlayer(_p.getUniqueId());
        player.systemMessage(3, 
            MessageFormat.format(
                "\n&6_____ Spy on &r&e&l&n{0} &r&6&l _____\n" +
                "&r&6Connection: &r&e{1} &6[&e{2}ms&6]\n"+
                "&r&6Profile: &r&e{3}\n"+
                "&r&6Gamemode: &r&e{4}\n" +
                "&r&6GPS: &r&e[{5}], {7}, {6}\n" +
                "&r&6Status: \n" +
                "&r&6-- &4Health&6:&c {8}% &4[&c{9}&4/&c{10}&4]\n" +
                "&r&6-- &6Hunger&6:&6 {11}% &6[&6{12}&6/&6{13}&6]\n" +
                "&r&6-- &eMoney&6:&e {14}\n"
                , 
                _p.getName(),
                
                _p.getAddress().toString(), _p.spigot().getPing(),
                _pp.profile.getID(),
                _p.getGameMode().name().substring(0, 1) + _p.getGameMode().name().substring(1).toLowerCase(),
                _p.getWorld().getName().replace("_core/worlds/", ""), _p.getLocation().getBlockX() + "/ " + _p.getLocation().getBlockZ(),
                _p.getFacing().toString().substring(0, 1).toUpperCase() + _p.getFacing().toString().substring(1).toLowerCase().replace("_", " "),
                
                ((_pp.profile.stats.Health / _pp.profile.stats.Health_max) * 100), _pp.profile.stats.Health, _pp.profile.stats.Health_max,
                ((_pp.profile.stats.Hunger / _pp.profile.stats.Hunger_max) * 100), _pp.profile.stats.Hunger, _pp.profile.stats.Hunger_max,
                _pp.profile.data.currency
            )
        );
        return true;
    }
}
