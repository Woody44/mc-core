package woody44.minecraft.core.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import net.kyori.adventure.util.TriState;
import woody44.minecraft.core.Core;
import woody44.minecraft.core.player.PlayerCore;

public class CMDWorld extends CoreCommand {

    @Override
    public boolean onCommand(PlayerCore player, CommandSender sender, Command cmd, String label, String[] args) {
        
        switch(args[0])
        {
            case "create":{
                if(Bukkit.getWorld(args[1]) != null || new File("_core/worlds/"+args[1]).exists())
                {
                    player.systemMessage(1, "world already exists.");
                    player.systemMessage(2, "Use /world load " + args[1] + " instead");
                    return true;
                }

                WorldCreator wc = new WorldCreator(new NamespacedKey(Core.Instance, args[1]));
                wc.environment(Environment.valueOf(args[2].toUpperCase()));
                wc.type(WorldType.valueOf(args[3].toUpperCase()));
                
                if(args.length > 4)
                    wc.seed(Integer.parseInt(args[4]));
                
                if(args.length > 5)
                    wc.generateStructures(Boolean.parseBoolean(args[5]));

                if(args.length > 6)
                    wc.keepSpawnLoaded(TriState.valueOf(args[6]));

                World WORLD = wc.createWorld();
                player.getBukkitPlayer().teleport(WORLD.getSpawnLocation());
                player.systemMessage(0, "World created!");
                break;
            }

            case "tp":{
                Player _p = null;
                World w = null;
                if(args.length > 2)
                {
                    _p = Bukkit.getPlayerExact(args[1]);
                    w = Bukkit.getWorld(args[2]); 
                }
                else{
                    _p = player.getBukkitPlayer();
                    w = Bukkit.getWorld(args[1]); 
                }

                if(_p == null){
                    player.systemMessage(1, "Could not find player.");
                    return true;
                }

                if(w != null)
                {
                    player.getBukkitPlayer().teleport(w.getSpawnLocation());
                    player.systemMessage(0, MessageFormat.format("Teleported {0} to {1}", _p.name(), w.getName()));
                }
                else
                {
                    if(args.length > 2)
                    {
                        if(new File("_core/worlds/"+args[2]).exists())
                            player.systemMessage(2, "World is unloaded.");
                        else
                            player.systemMessage(1, "World does not exist!"); 
                    }
                    else{
                        if(new File("_core/worlds/"+args[1]).exists())
                            player.systemMessage(2, "World is unloaded.");
                        else
                            player.systemMessage(1, "World does not exist!"); 
                    }
                }
                break;
            }

            case "load":{
                if(Bukkit.getWorld(args[1]) != null)
                    player.systemMessage(2, "World is already loaded.");
                else{
                    if(!new File("_core/worlds/"+args[1]).exists())
                        player.systemMessage(1, "World does not exist."); 
                    else
                    {
                        new WorldCreator(args[1]).createWorld();
                        player.systemMessage(0, "World " + args[1] + " loaded");
                    }
                }
                break;
            }

            case "unload":{
                World w = Bukkit.getWorld(args[1]);
                if(w==null)
                {
                    if(!new File("_core/worlds/"+args[1]).exists())
                        player.systemMessage(1, "World does not exist."); 
                    else
                        player.systemMessage(1, "World is not loaded.");
                    
                    return true;
                }

                Properties props = new Properties();
                try {
                    props.load(new FileInputStream("server.properties"));
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

                String wn = props.getProperty("level-name");
                Bukkit.getWorld(args[1]).getPlayers().forEach((_p)->{
                    _p.teleport(Bukkit.getWorld(wn).getSpawnLocation(), TeleportCause.COMMAND);
                });

                if(args.length > 2)
                {
                    Bukkit.unloadWorld(args[1], Boolean.parseBoolean(args[2]));
                    if(Boolean.parseBoolean(args[2]))
                        player.systemMessage(0, "Saved and unloaded world " + args[1]);
                    else
                        player.systemMessage(0, "Unloaded world " + args[1]);
                }
                else
                {
                    Bukkit.unloadWorld(args[1], true);
                    player.systemMessage(0, "Saved and unloaded world " + args[1]);
                }
                break;
            }
        
            case "set":{
                File f = new File("_core/worlds/"+args[1]);
                if(!f.exists())
                {
                    player.systemMessage(1, "World does not exist.");
                    return true;
                }

                f = new File("_core/worlds/"+args[1]+"/config.yaml");
                FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
                
                switch(args[2]){
                    case "autostart":{
                        fc.set("autostart", Boolean.parseBoolean(args[3]));
                        try {
                            fc.save(f);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return false;
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }
}
