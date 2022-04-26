package woody44.minecraft.core;

import org.bukkit.inventory.ItemStack;

public class EntryItem extends EntryBase{
    private ItemStack item;
    
    public EntryItem(ItemStack _item, int _weight){
        super(_weight);
        item = _item;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public String getItemID() {
        return item.getType().toString();
    }
}