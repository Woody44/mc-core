package woody44.minecraft.core;

import org.bukkit.inventory.ItemStack;

public abstract class EntryBase {
    int weight;
    private double chance;

    public EntryBase(int _weight){
        weight = _weight;
    }

    public int getWeight(){
        return weight;
    }

    public void setChance(double _chance){
        chance = _chance;
    }

    public double getChance(){
        return chance;
    }

    public abstract ItemStack getItem();
    public abstract String getItemID();
}
