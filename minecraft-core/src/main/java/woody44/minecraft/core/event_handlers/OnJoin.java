package woody44.minecraft.core.event_handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import woody44.minecraft.core.player.PlayerCore;

public class OnJoin implements Listener {

    @EventHandler
    public void PlayerJoined(PlayerJoinEvent e) {
        PlayerCore.registerPlayer(e.getPlayer().getUniqueId());
        e.joinMessage(null);
        // TODO: Softcode Join Message
    }
}
