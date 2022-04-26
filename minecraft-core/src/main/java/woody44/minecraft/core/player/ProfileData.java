package woody44.minecraft.core.player;

import com.google.gson.Gson;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class ProfileData {
    public String NAME = "Default";
    public long currency = 0;
    public long currency_premium = 0;
    public String World = "world";
    public double x = 0, y = 0, z = 0;

    public ProfileData(String _name) {
        NAME = _name;
    }

    public String Serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static ProfileData Build(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ProfileData.class);
    }

    public void setLocation(Location loc) {
        World = loc.getWorld().getName();
        x = loc.getX();
        y = loc.getY();
        z = loc.getZ();
    }

    public Location getLocation(){
        return new Location(Bukkit.getWorld(World), x, y, z);
    }
}