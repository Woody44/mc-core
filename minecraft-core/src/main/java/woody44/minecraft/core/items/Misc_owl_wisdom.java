package woody44.minecraft.core.items;

import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import woody44.minecraft.core.Core;

public class Misc_owl_wisdom implements Listener{
    public String id = "misc/owlwisdom";

    @EventHandler
    public void onShoot(EntityShootBowEvent e){
        if(e.getBow().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Core.Instance, "item"))){
            if(e.getBow().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Core.Instance, "item"), PersistentDataType.STRING).contentEquals(id)){
                e.getProjectile().setMetadata("owlwisdom", new FixedMetadataValue(Core.Instance, Math.floor(2 * e.getProjectile().getVelocity().length()/2)));
            }
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent e){
        if(!e.getEntity().hasMetadata("owlwisdom"))
            return;

        Player p = (Player)e.getEntity().getShooter();

        if(e.getHitEntity() != null && e.getHitEntity() instanceof LivingEntity )
        {
            LivingEntity ent = ((LivingEntity)e.getHitEntity());
            new BukkitRunnable() {
                int runs = 0;

                @Override
                public void run() {
                    runs+=1;

                    ent.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 0));
                    ent.getNearbyEntities(12,12,12).forEach(other->{

                        if(p!= null)
                            p.playSound(p, Sound.BLOCK_AMETHYST_CLUSTER_FALL, SoundCategory.MASTER, 2.0f, 1.5f);
                        if(other instanceof LivingEntity)
                        ((LivingEntity)other).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 0));
                    });

                    if(runs == 3)
                        this.cancel();
                }
                
            }.runTaskTimer(Core.Instance, 15, 60);
        }
        else{
            BlockFace blockFace = e.getHitBlockFace();
            Vector arrowVelocity = e.getEntity().getVelocity();
            Vector mirrorDirection = new Vector(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
                    double dotProduct = arrowVelocity.dot(mirrorDirection);
                    mirrorDirection = mirrorDirection.multiply(dotProduct).multiply(2.0D);
            Vector ricochet = arrowVelocity.subtract(mirrorDirection);
            double speed = arrowVelocity.length();

            int owl = e.getEntity().getMetadata("owlwisdom").get(0).asInt();
            if( owl > 0)
            {
                Arrow ar = e.getEntity().getWorld().spawnArrow(e.getEntity().getLocation(), ricochet, (float)speed * 0.5f, 4.0f);
                ar.setMetadata("owlwisdom", new FixedMetadataValue(Core.Instance, owl-1));
                ar.setShooter(p);
                e.getEntity().remove();
                return;
            }

            
            new BukkitRunnable() {
                int runs = 0;
                @Override
                public void run() {
                    e.getEntity().getNearbyEntities(12, 12, 12).forEach(other->{
                        
                        if(p!= null)
                            p.playSound(p, Sound.BLOCK_AMETHYST_CLUSTER_FALL, SoundCategory.MASTER, 2.0f, 1.5f);
                        if(other instanceof LivingEntity)
                            ((LivingEntity)other).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 0));
                    });;
                    runs+=1;
                    if(runs == 3)
                    {
                        e.getEntity().remove();
                        this.cancel();
                    }
                }
                
            }.runTaskTimer(Core.Instance, 15, 60);
        }
    }
}
