package woody44.minecraft.core.event_handlers;

import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class OnDamage implements Listener {

    @EventHandler
    public void playerDamageFrame(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof ItemFrame){
            ItemFrame frame = (ItemFrame)e.getEntity();
            if(frame.getItem() != null && frame.getItem().getType() != Material.AIR)
            {
                if(e.getDamager() instanceof Player)
                    ((Player)e.getDamager()).getInventory().addItem(frame.getItem());
                
                frame.setItem(null);
                e.setCancelled(true);
            }
        }
    }
}
