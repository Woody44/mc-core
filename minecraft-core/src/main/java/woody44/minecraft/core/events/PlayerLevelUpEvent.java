package woody44.minecraft.core.events;

import javax.validation.constraints.NotNull;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import woody44.minecraft.core.player.PlayerCore;

public class PlayerLevelUpEvent  extends Event{

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private PlayerCore Player;
    private int previousLevel, newLevel, previousExp, newExp;

    public PlayerLevelUpEvent(PlayerCore player, int previousLevel, int newLevel, int previousExp, int newExp) {
        Player = player;
        this.previousLevel = previousLevel;
        this.newExp = newExp;
    }

    public int getLevel(){
        return newLevel;
    }

    public int getExp(){
        return newExp;
    }

    public int getPreviousLevel(){
        return previousLevel;
    }

    public int getPreviousExp(){
        return previousExp;
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
