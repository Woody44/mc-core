package woody44.minecraft.core.player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import woody44.minecraft.core.events.PlayerLevelUpEvent;
import woody44.minecraft.core.events.PlayerLevelUpOnceEvent;

public class Profile {
    public static final float xpFactor = 0.1f, factorGrowth = 0.01f, xpBase = 100;

    private final String ownerID, profileID;
    public ProfileData data;
    public QuestsData quests;
    public Stats stats;
    public final PlayerCore player;

    int expNeeded;

    public Profile(PlayerCore _player, String _profileID){
        player = _player;
        ownerID = _player.getId();
        profileID = _profileID;
    }

    public static boolean exists(String ownerID, String profileID){
        return new File(MessageFormat.format("_core/players/{0}/profiles/{1}/profile.json", ownerID, profileID)).exists();
    }

    public void load() {
        new File(getFolderString()).mkdirs();
        try {
            data = ProfileData.Build(Files.readString(Path.of(getFolderString()+"profile.json")));
            loadStats();
            fixStats();
            calculateExp();
            player.DATA.latestProfile = profileID;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addExp(int value) {
        stats.Experience += value;
        calculateExp();
    }

    public static ProfileData create(PlayerCore playerCore, String profileID) {
        return create(playerCore.getId(), profileID);
    }
    
    public static ProfileData create(String ownerID, String profileID){
        ProfileData pd = new ProfileData(profileID);
        File profileData = new File(getFolderString(ownerID, profileID)+"profile.json");
        if (!profileData.exists()) {
            try {
                profileData.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
    
            try {
                PrintWriter dataWriter = new PrintWriter(getFolderString(ownerID, profileID)+"profile.json");
                dataWriter.write(pd.Serialize());
                dataWriter.close();

                createStats(ownerID, profileID);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    
        return pd;
    }

    private void loadStats() {
        try {
            stats = Stats.Build(Files.readString(Path.of(getFolderString()+"stats.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Stats createStats(String ownerID, String profileID) {
        File dataFile = new File(getFolderString(ownerID, profileID)+"stats.json");
        try {
            dataFile.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    
        Stats _stats = new Stats();
        try {
            PrintWriter dataWriter = new PrintWriter(getFolderString(ownerID, profileID)+"stats.json");
            dataWriter.write(_stats.Serialize());
            dataWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    
        return _stats;
    }

    public void init() {
        player.getBukkitPlayer().teleport(new Location(Bukkit.getWorld(data.World), data.x, data.y, data.z));
        player.getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        player.getBukkitPlayer().setHealth(20 * stats.Health / stats.Health_max);
        player.getBukkitPlayer().setLevel(stats.Level);
        player.getBukkitPlayer().setExp((float) stats.Experience / expNeeded);

        File f = new File(getFolderString(ownerID, profileID)+"inventory.yml");
        if(!f.exists())
            return;
        FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
        player.getBukkitPlayer().getInventory().setContents(fc.getList("items").toArray(new ItemStack[41]));
    }

    public void calculateExp() {
        int prevLvL = stats.Level, prevExp = stats.Experience;

        expNeeded = (int) (xpBase * ((stats.Level - 1) * (xpFactor + xpFactor * factorGrowth * stats.Level)) + xpBase);
        boolean lvlup = stats.Experience >= expNeeded;
        while (stats.Experience >= expNeeded) {
            stats.Experience -= expNeeded;
            stats.Level += 1;
            expNeeded = (int) (xpBase * ((stats.Level - 1) * (xpFactor + xpFactor * factorGrowth * stats.Level))
                    + xpBase);

            PlayerLevelUpEvent event = new PlayerLevelUpEvent(player, prevLvL, stats.Level, stats.Experience, prevExp);
            Bukkit.getPluginManager().callEvent(event);
        }
        if (lvlup)
        {
            PlayerLevelUpOnceEvent event = new PlayerLevelUpOnceEvent(player, prevLvL, stats.Level, stats.Experience, prevExp);
            Bukkit.getPluginManager().callEvent(event);
        }

        player.getBukkitPlayer().setExp((float) stats.Experience / (float) expNeeded);
        player.getBukkitPlayer().setLevel(stats.Level);
    }

    public void fixStats() {
        if (stats.Health > stats.Health_max)
            stats.Health = stats.Health_max;
    }

    public void save(){
        data.setLocation(player.getBukkitPlayer().getLocation());
        String _stats = stats.Serialize();
        String _profile = data.Serialize();

        try {
            PrintWriter dataWriter = new PrintWriter(getFolderString()+"profile.json");
            dataWriter.write(_profile);
            dataWriter.close();

            dataWriter = new PrintWriter(getFolderString()+"stats.json");
            dataWriter.write(_stats);
            dataWriter.close();

            File f = new File(getFolderString()+"inventory.yml");
            FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
            fc.set("items", player.getBukkitPlayer().getInventory().getContents());
            fc.save(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getFolderString(){
        return MessageFormat.format("_core/players/{0}/profiles/{1}/", ownerID, profileID);
    }

    public static String getFolderString(String ownerID, String profileID){
        return MessageFormat.format("_core/players/{0}/profiles/{1}/", ownerID, profileID);
    }

    public int getExpToNextLevel(){
        return expNeeded;
    }

    public String getID(){
        return profileID;
    }
}
