package woody44.minecraft.core.event_handlers;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import woody44.minecraft.core.Core;
import woody44.minecraft.core.player.PlayerCore;



public class OnSwap implements Listener {
    Random randomizer = new Random();

    @EventHandler
    public void onSwap(PlayerItemHeldEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItem(e.getNewSlot());
        if (item == null || item.getType() == Material.AIR)
            return;

        PlayerCore pc = PlayerCore.getPlayer(e.getPlayer().getUniqueId());

        ItemMeta meta = item.getItemMeta();
        if (meta.getPersistentDataContainer().has(new NamespacedKey(Core.Instance, "swap"))) {
            float 
            pitchMax = meta.getPersistentDataContainer().has(new NamespacedKey(Core.Instance, "swap-pitch-max")) ? meta.getPersistentDataContainer().get(new NamespacedKey(Core.Instance, "swap-pitch-max"), PersistentDataType.FLOAT) : 1.2f,//TODO: softcode
            pitchMin = meta.getPersistentDataContainer().has(new NamespacedKey(Core.Instance, "swap-pitch-min")) ? meta.getPersistentDataContainer().get(new NamespacedKey(Core.Instance, "swap-pitch-min"), PersistentDataType.FLOAT) : 0.3f;//TODO: softcode

            e.getPlayer().playSound(
                    e.getPlayer().getLocation(),
                    "rpg.swap." + meta.getPersistentDataContainer().get(new NamespacedKey(Core.Instance, "swap"),
                            PersistentDataType.STRING),
                    SoundCategory.MASTER, 2f, pitchMin + randomizer.nextFloat() * (pitchMax - pitchMin));
        }

        if (meta.getPersistentDataContainer().has(new NamespacedKey(Core.Instance, "item"))) {
            String itemID = meta.getPersistentDataContainer().get(new NamespacedKey(Core.Instance, "item"), PersistentDataType.STRING);
            if(pc.getCooldown(itemID)>0)
            {
                pc.getBukkitPlayer().setCooldown(item.getType(), pc.getCooldown(itemID)/ 2);
            }
        }else{
            pc.getBukkitPlayer().setCooldown(item.getType(), 0);
        }
    }
}