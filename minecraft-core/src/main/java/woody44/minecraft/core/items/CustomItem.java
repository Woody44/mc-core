package woody44.minecraft.core.items;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public final class CustomItem {

    public static void Serialize(ItemStack item, File f) throws IOException {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
        fc.set("item", item);
        fc.save(f);
    }

    public static ItemStack Deserialize(File f) {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
        return fc.getItemStack("item");
    }
    
}
