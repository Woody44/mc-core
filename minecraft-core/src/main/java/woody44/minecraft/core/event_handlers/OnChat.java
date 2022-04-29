package woody44.minecraft.core.event_handlers;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import woody44.minecraft.core.player.PlayerCore;

public class OnChat implements Listener {
    @EventHandler
    public void OnPlayerChat(AsyncChatEvent e) {
        PlayerCore pc = PlayerCore.getPlayer(e.getPlayer().getUniqueId());
        if (!pc.IsAuthed()) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cLog in to send messages!"));
            return;
        }

        e.getPlayer().sendMessage(e.getPlayer().getInventory().getContents().length+"");
        // TODO: Chat Formatting        
    }
}