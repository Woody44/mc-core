package woody44.minecraft.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PermissionsControler {
    public static FileConfiguration file;
    
    public static void Load()
    {
        File f = new File("_core/permissions.yml");
        if(!f.exists())
        {
            file = new YamlConfiguration();
            
            file.createSection("groups");
            file.createSection("players");

            CreateGroup("default");
            SetGroup("default", "permission.example");

            List<String> perms = new ArrayList<>();
            perms.add("Damian.Chuj");
            perms.add("Jebac.Pis");
            SetGroupRange("default", perms);

            CreatePlayer("8c9c749a-d589-45e5-8e5b-2ec6ba57286c");
            CreatePlayerProfile("8c9c749a-d589-45e5-8e5b-2ec6ba57286c", "Default");
            SetPlayer("8c9c749a-d589-45e5-8e5b-2ec6ba57286c", "Default", "-revoked.permission.example");

            SetPlayerRange("8c9c749a-d589-45e5-8e5b-2ec6ba57286c", "Default", perms);
            SetPlayerGroup("8c9c749a-d589-45e5-8e5b-2ec6ba57286c", "Default", "default");

            Save();

            return;
        }

        file = YamlConfiguration.loadConfiguration(f);
    }

    public static boolean SetGroupRange(String _name, List<String> _permissions)
    {
        if(!file.getConfigurationSection("groups").contains(_name))
            return false;
        
        ConfigurationSection cs = file.getConfigurationSection("groups").getConfigurationSection(_name);
        List<String> p = cs.getStringList("permissions");
        for(String s : _permissions)
        {
            if(p.contains(s))
                continue;
            else
                p.add(s);    
        }

        cs.set("permissions", p);
        return true;
    }

    public static boolean GroupExists(String _name){
        ConfigurationSection cs = file.getConfigurationSection("groups").getConfigurationSection(_name);
        return cs != null;
    }

    public static boolean SetGroup(String _name, String _permission, boolean unset)
    {
        if(!file.getConfigurationSection("groups").contains(_name))
        return false;
    
        ConfigurationSection cs = file.getConfigurationSection("groups").getConfigurationSection(_name);
        List<String> p = cs.getStringList("permissions");
        if(p == null)
            p = new ArrayList<String>();

        if(!unset)
        {
            if(!p.contains(_permission))
                p.add(_permission);
        }
        else
        {
            if(p.contains(_permission))
                p.remove(_permission);
        }

        cs.set("permissions", p);
        file.set("groups." + _name, cs);
        return true;
    }
    public static boolean SetGroup(String _name, String _permission)
    {
        return SetGroup(_name, _permission, false);
    }

    public static boolean SetPlayerRange(String _uuid, String profile, List<String> _permissions)
    {
        if(!file.getConfigurationSection("players").contains(_uuid))
            return false;
        
        ConfigurationSection cs = file.getConfigurationSection("players").getConfigurationSection(_uuid).getConfigurationSection("profile." + profile);
        List<String> p = cs.getStringList("permissions");
        if(p == null)
            p = new ArrayList<String>();
            
        for(String s : _permissions)
        {
            if(p.contains(s))
                continue;
            else
                p.add(s);    
        }

        cs.set("permissions", p);
        return true;
    }

    public static boolean SetPlayer(String _uuid, String profile, String _permission, boolean unset)
    {
        if(!file.getConfigurationSection("players").contains(_uuid))
            return false;
    
        ConfigurationSection cs = file.getConfigurationSection("players").getConfigurationSection(_uuid).getConfigurationSection("profile." + profile);
        List<String> p = cs.getStringList("permissions");
        if(p == null)
            p = new ArrayList<String>();

        if(!unset)
        {
            if(!p.contains(_permission))
                p.add(_permission);
        }
        else
        {
            if(p.contains(_permission))
                p.remove(_permission);
        }

        cs.set("permissions", p);
        return true;
    }
    public static boolean SetPlayer(String _uuid, String profile, String _permission)
    {
        return SetPlayer(_uuid, profile, _permission, false);
    }

    public static boolean SetPlayerGroup(String _uuid, String profile, String _name)
    {
        ConfigurationSection cs = file.getConfigurationSection("players").getConfigurationSection(_uuid).getConfigurationSection("profile." + profile);
        List<String> g = cs.getStringList("groups");
        if(g == null)
            g = new ArrayList<String>();

        if(!g.contains(_name))
            g.add(_name);
        else
            return false;

        cs.set("groups", g);
        return true;
    }

    public static boolean CreateGroup(String _name){

        if(file.getConfigurationSection("groups").contains(_name))
            return false;

        file.getConfigurationSection("groups").createSection(_name);
            return true;
    }

    public static boolean CreatePlayer(String _uuid){

        if(file.getConfigurationSection("players").contains(_uuid))
            return false;

        file.getConfigurationSection("players").createSection(_uuid);
            return true;
    }

    public static boolean CreatePlayerProfile(String _uuid, String profile){
        ConfigurationSection cs = file.getConfigurationSection("players").getConfigurationSection(_uuid);
        if( cs.getConfigurationSection("profiles." + profile) != null)
            return false;

        cs.createSection("profile." + profile);
        return true;
    }

    public static void Save(){
        try {
            new File("_core/permissions.yml").delete();
            file.save(new File("_core/permissions.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
