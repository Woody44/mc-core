package woody44.minecraft.core;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import woody44.minecraft.core.commands.*;
import woody44.minecraft.core.event_handlers.*;
import woody44.minecraft.core.player.PlayerCore;
import woody44.minecraft.core.items.*;

public class Core extends JavaPlugin {
    public static FileConfiguration _CONFIG;
    public static Core Instance;
    public static Server server;
    public static PluginManager plugman;
    public static Logger logger;

    @Override
    public void onEnable() {
        Init();
        HandleConfig();
        MakeDirs();
        RegisterCommands();
        RegisterEvents();
        PermissionsControler.Load();

        HandleReload();
    }

    private void RegisterEvents() {
        plugman.registerEvents(new OnJoin(), this);
        plugman.registerEvents(new OnChat(), this);
        plugman.registerEvents(new OnQuit(), this);
        plugman.registerEvents(new OnSwing(), this);
        plugman.registerEvents(new OnSwap(), this);
        plugman.registerEvents(new OnItemHitGround(), this);
        plugman.registerEvents(new OnDamage(), this);

        plugman.registerEvents(new Misc_owl_wisdom(), this);
    }

    private void RegisterCommands() {
        server.getPluginCommand("login").setExecutor(new CMDLogin());
        server.getPluginCommand("register").setExecutor(new CMDRegister());
        server.getPluginCommand("item").setExecutor(new CMDitem());
        server.getPluginCommand("lvl").setExecutor(new CMDLevel());
        server.getPluginCommand("world").setExecutor(new CMDWorld());
        server.getPluginCommand("info").setExecutor(new CMDInfo());
    }

    private void Init() {
        server = this.getServer();
        plugman = server.getPluginManager();
        logger = server.getLogger();
    }

    private void MakeDirs() {
        new File("_core/players/").mkdirs();
        new File("_core/items/").mkdirs();
        new File("_core/abilities/").mkdirs();
        new File("_core/quests/").mkdirs();
        new File("_core/locations/").mkdirs();
        new File("_core/music/").mkdirs();
        new File("_core/abilities/").mkdirs();
    }

    private void HandleReload() {
        for (Player p : getServer().getOnlinePlayers()) {
            PlayerCore.registerPlayer(p.getUniqueId());
        }
    }

    private void HandleConfig() {
        File configFile = new File("_core/config.yml");
        InputStream is = getResource("config.yml");
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
                OutputStream out = new FileOutputStream(configFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = is.read(buf)) > 0)
                    out.write(buf, 0, len);
                out.close();
                is.close();
            } else {
                FileConfiguration fc = YamlConfiguration.loadConfiguration(configFile);
                if (!fc.getString("version").contentEquals(getDescription().getVersion())) {
                    if (!configFile.renameTo(new File("_core/config.old_"
                            + DateTimeFormatter.ofPattern("dd-MM-yyyy HH.mm.ss").format(LocalDateTime.now()) + ".yml")))
                        ;
                    logger.info("Generating new config file!");

                    configFile = new File("_core/config.yml");
                    configFile.createNewFile();
                    OutputStream out = new FileOutputStream(configFile);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = is.read(buf)) > 0)
                        out.write(buf, 0, len);
                    out.close();
                    is.close();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        _CONFIG = YamlConfiguration.loadConfiguration(configFile);
        _CONFIG.set("version", getDescription().getVersion());
        try {
            _CONFIG.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        for(PlayerCore player : PlayerCore.PLAYERS.values()){
            player.saveAll();
        }
    }

    @Override
    public void onLoad() {
        Instance = this;
    }
}
