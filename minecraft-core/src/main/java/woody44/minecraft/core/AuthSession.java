package woody44.minecraft.core;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import woody44.minecraft.core.player.PlayerCore;


public class AuthSession {

    public static HashMap<String, AuthSession> Sessions = new HashMap<>();
    public static int loginTimeout = 90;
    public static byte volume = 35;

    public int taskID;                             
    public AuthSession(int taskid)
    { 
        taskID = taskid; 
    }

    public static void Start(PlayerCore pp)
    {
        Player p = pp.getBukkitPlayer();
        p.setAllowFlight(true);
        p.setFlying(true);

        p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.0);
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(0.1);

        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 225));
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 225));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 225));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 225));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Integer.MAX_VALUE, 225));
        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 255));

        p.setFoodLevel(0);
        p.setExp(0);

        p.setInvulnerable(true);
        p.setInvisible(true);

        //pp.SetMusicPath("auth/");

        Sessions.put(p.getUniqueId().toString(), new AuthSession(Bukkit.getScheduler().scheduleSyncRepeatingTask(Core.Instance, new Runnable(){
            int timeToKick = loginTimeout;
            @Override
            public void run() {
                timeToKick --;
                if(timeToKick < 0){
                    p.kick(Component.text("Login Timeout.").color(TextColor.color(0xb88a3e)));
                    Bukkit.getScheduler().cancelTask(Sessions.get(p.getUniqueId().toString()).taskID);
                    return;
                }
                p.setLevel(timeToKick);
                p.setExp((float) timeToKick / (float) loginTimeout);

                p.sendActionBar(
                    Component.text().content("Log in or Register!").color(TextColor.color(236, 145, 250))
                );
            }
        }, 0, 20)));
    }

    public static void End(PlayerCore pp)
    {
        Player p = pp.getBukkitPlayer();
        p.removePotionEffect(PotionEffectType.BLINDNESS);
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        p.removePotionEffect(PotionEffectType.SLOW);
        p.removePotionEffect(PotionEffectType.SLOW_FALLING);
        p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
        p.removePotionEffect(PotionEffectType.JUMP);

        p.setInvulnerable(false);
        p.setInvisible(false);

        Bukkit.getScheduler().cancelTask(AuthSession.Sessions.get(p.getUniqueId().toString()).taskID);
        
        if(!p.isOp())
        {
            p.setAllowFlight(false);
            p.setFlying(false);
        }
        else
        {
            p.setGameMode(GameMode.CREATIVE);
            p.setAllowFlight(true);
            p.setFlying(true);
        }

        p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.09);
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(0.1);
        p.setFoodLevel(20);
        Sessions.remove(p.getUniqueId().toString());
    }
}