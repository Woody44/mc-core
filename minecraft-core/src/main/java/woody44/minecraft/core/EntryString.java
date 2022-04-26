package woody44.minecraft.core;

import org.bukkit.inventory.ItemStack;

public class EntryString extends EntryBase{

    String ID;
    public EntryString(String _ID, int _weight) {
        super(_weight);
        ID = _ID;
    }

    @Override
    public ItemStack getItem() {
        return null;
    }

    @Override
    public String getItemID() {
        return ID;
    }

}
