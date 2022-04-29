package woody44.minecraft.core.event_handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import woody44.minecraft.core.Core;

public class OnItemHitGround implements Listener {
    Map<String, BukkitTask> track = new HashMap<>();
    Random randomizer = new Random();

    @EventHandler
    public void onDrop(ItemSpawnEvent e) {
        ItemMeta meta = e.getEntity().getItemStack().getItemMeta();
        if(meta.getPersistentDataContainer().has(new NamespacedKey(Core.Instance, "ground-hit")))
        {
            track.put(
                e.getEntity().getUniqueId().toString(),
                Bukkit.getScheduler().runTaskTimerAsynchronously(Core.Instance, new Runnable() {

                    @Override
                    public void run() {
                        if(e.getEntity().getVelocity().getY() != 0 && e.getEntity().getVelocity().getY() != -0)
                            return;

                        float 
                        pitchMax = meta.getPersistentDataContainer().has(new NamespacedKey(Core.Instance, "ground-hit-pitch-max")) ? meta.getPersistentDataContainer().get(new NamespacedKey(Core.Instance, "ground-hit-pitch-max"), PersistentDataType.FLOAT) : 1.2f,//TODO: softcode
                        pitchMin = meta.getPersistentDataContainer().has(new NamespacedKey(Core.Instance, "ground-hit-pitch-min")) ? meta.getPersistentDataContainer().get(new NamespacedKey(Core.Instance, "ground-hit-pitch-min"), PersistentDataType.FLOAT) : 0.9f;//TODO: softcode
                        Core.logger.warning("test :" + "rpg.ground-hit." + meta.getPersistentDataContainer().get(new NamespacedKey(Core.Instance, "ground-hit"), PersistentDataType.STRING));
                        e.getEntity().getWorld().playSound(
                            e.getEntity().getLocation(), 
                            "rpg.ground-hit." + meta.getPersistentDataContainer().get(new NamespacedKey(Core.Instance, "ground-hit"), PersistentDataType.STRING),
                            SoundCategory.MASTER, 2f, pitchMin + randomizer.nextFloat() * (pitchMax - pitchMin));
                        track.get(e.getEntity().getUniqueId().toString()).cancel();
                        track.remove(e.getEntity().getUniqueId().toString());
                    }
                    
                }, 
                10, 10)
            );
        }
    }
}