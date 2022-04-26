package woody44.minecraft.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import woody44.minecraft.core.player.PlayerCore;

public class PlayerLoadedEvent extends Event{

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private PlayerCore Player;

    public PlayerLoadedEvent(PlayerCore player) {
        Player = player;
    }

    public PlayerCore GetPlayer() {
        return Player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
