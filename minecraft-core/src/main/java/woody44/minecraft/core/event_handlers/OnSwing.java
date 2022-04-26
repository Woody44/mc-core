package woody44.minecraft.core.event_handlers;

import java.util.Random;

import org.bukkit.NamespacedKey;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import woody44.minecraft.core.Core;

public class OnSwing implements Listener {
    Random randomizer = new Random();

    @EventHandler
    public void onSwing(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR
                || e.getAction() == Action.PHYSICAL)
            return;

        if (e.getItem() == null)
            return;

        ItemMeta meta = e.getItem().getItemMeta();
        if (meta.getPersistentDataContainer().has(new NamespacedKey(Core.Instance, "swing"))) {
            float 
            pitchMax = meta.getPersistentDataContainer().has(new NamespacedKey(Core.Instance, "swing-pitch-max")) ? meta.getPersistentDataContainer().get(new NamespacedKey(Core.Instance, "swing-pitch-max"), PersistentDataType.FLOAT) : 1.2f,//TODO: softcode
            pitchMin = meta.getPersistentDataContainer().has(new NamespacedKey(Core.Instance, "swing-pitch-min")) ? meta.getPersistentDataContainer().get(new NamespacedKey(Core.Instance, "swing-pitch-min"), PersistentDataType.FLOAT) : 0.3f;//TODO: softcode

            e.getPlayer().playSound(
                    e.getPlayer().getLocation(),
                    "rpg.swing." + meta.getPersistentDataContainer().get(new NamespacedKey(Core.Instance, "swing"),
                            PersistentDataType.STRING),
                             SoundCategory.MASTER, 2f, pitchMin + randomizer.nextFloat() * (pitchMax - pitchMin));
        }
    }
}