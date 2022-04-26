package woody44.minecraft.core.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import woody44.minecraft.core.player.PlayerCore;

public class PlayerRegisterEvent extends Event implements Cancellable {
    
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;
    
    private boolean Auth;
    private PlayerCore Player;
    
    public PlayerRegisterEvent(PlayerCore player, boolean auth){
        Player = player;
        Auth = auth;
    }

    public PlayerCore GetPlayer(){
        return Player;
    }

    public boolean GetResult(){
        return Auth;
    }
    
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean arg0) {
        isCancelled = arg0;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
    
}
