package woody44.minecraft.core.commands;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import woody44.minecraft.core.Core;
import woody44.minecraft.core.items.CustomItem;
import woody44.minecraft.core.player.PlayerCore;

@SuppressWarnings("deprecation")
public class CMDitem extends CoreCommand {

    @Override
    public boolean onCommand(PlayerCore player, CommandSender sender, Command cmd, String label, String[] args) {

        ItemStack item = player.getBukkitPlayer().getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        switch (args[0]) {
            case "create": {
                if (args.length < 2) {
                    player.systemMessage(1, "Missing arguments.");
                    return true;
                }
                if (item == null || item.getType() == Material.AIR) {
                    player.systemMessage(1, "You can't save air.");
                    return true;
                }

                File f = new File(MessageFormat.format("_core/items/{0}.yml", args[1]));
                if (f.exists()) {
                    player.systemMessage(2, "This item already exists");
                    player.systemMessage(2, "use '/item save' to overwrite.");
                    return true;
                }
                new File(f.getParent()).mkdirs();

                try {
                    meta.getPersistentDataContainer().set(new NamespacedKey(Core.Instance, "item"), PersistentDataType.STRING, args[1]);
                    item.setItemMeta(meta);
                    CustomItem.Serialize(item, f);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

                player.systemMessage(0, "item saved.");
                break;
            }
            case "save": {
                if (item == null || item.getType() == Material.AIR) {
                    player.systemMessage(2, "You can't save air.");
                    return true;
                }

                if(!meta.getPersistentDataContainer().has(new NamespacedKey(Core.Instance, "item"))){
                    player.systemMessage(1, "This item does not exist in database.");
                    player.systemMessage(2, "Use /item create instead.");
                    return true;
                }
                
                File f = (args.length < 2) 
                    ? new File(MessageFormat.format("_core/items/{0}.yml", meta.getPersistentDataContainer().get(new NamespacedKey(Core.Instance, "item"), PersistentDataType.STRING))) 
                    : new File(MessageFormat.format("_core/items/{0}.yml", args[1]));

                try {
                    if(args.length > 1)
                    {
                        meta.getPersistentDataContainer().remove(new NamespacedKey(Core.Instance, "item"));
                        meta.getPersistentDataContainer().set(new NamespacedKey(Core.Instance, "item"), PersistentDataType.STRING, args[1]);
                    }
                    CustomItem.Serialize(item, f);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

                if(args.length < 2) 
                    player.systemMessage(0, "item saved.");
                else
                    player.systemMessage(0, "item copy saved as " + args[1]);
                break;
            }
            case "give": {
                if (args.length < 3) {
                    player.systemMessage(1, "Missing arguments.");
                    return true;
                }

                File f = new File(MessageFormat.format("_core/items/{0}.yml", args[2]));
                if (!f.exists()) {
                    player.systemMessage(1, "Item does not exist.");
                    return true;
                }

                Player p = Bukkit.getPlayerExact(args[1]);
                if (p == null) {
                    player.systemMessage(1, "Could not find player " + args[1]);
                    return true;
                }

                item = CustomItem.Deserialize(f);
                int e = p.getInventory().firstEmpty();
                if (e == -1) {
                    player.systemMessage(1, "Player has no empty slot in inventory.");
                    return true;
                }

                p.getInventory().addItem(item);
                player.systemMessage(0,
                        MessageFormat.format("Gave {0}[{1}] to {2}", item.getItemMeta().getDisplayName(), args[2],
                                p.getName()));
                break;
            }
            case "swing": {
                if (args.length < 2) {
                    player.systemMessage(1, "Missing arguments.");
                    return true;
                }

                NamespacedKey key = new NamespacedKey(Core.Instance, "swing");
                switch (args[1]) {
                    case "big": {
                        meta = setKey(meta, key, args[1], true);
                        break;
                    }
                    case "small": {
                        meta = setKey(meta, key, args[1], true);
                        break;
                    }
                    case "sharp": {
                        meta = setKey(meta, key, args[1], true);
                        break;
                    }
                    default: {
                        player.systemMessage(1, "Wrong parameter.");
                        return true;
                    }
                }

                float pitchMin = (args.length > 2) ? parsePitch(args[2]) : 0, pitchMax = (args.length > 3) ? parsePitch(args[3]) : 0;
                
                if (pitchMin == -1 || pitchMax == -1) {
                    player.systemMessage(1, "Pitch has to be a number between 0.1 and 2.0 or 0 to reset it.");
                    return true;
                }
                meta = setPitchBounds(meta, pitchMin, pitchMax, "swing");
                player.systemMessage(0, MessageFormat.format("Set swing sound to {0} [{1} - {2}]", args[1], args[2], args[3]));
                break;
            }
            case "swap": 
            {
                if (args.length < 2) {
                    player.systemMessage(1, "Missing arguments.");
                    return true;
                }
                
                NamespacedKey key = new NamespacedKey(Core.Instance, "swap");
                switch (args[1]) {
                    case "metal": {
                        meta = setKey(meta, key, args[1], true);
                        player.systemMessage(2, "Set swap sound to " + args[1]);
                        break;
                    }
                    default: {
                        player.systemMessage(1, "Wrong parameter.");
                        return true;
                    }
                }

                float pitchMin = (args.length > 2) ? parsePitch(args[2]) : 0, pitchMax = (args.length > 3) ? parsePitch(args[3]) : 0;
                if (pitchMin == -1 || pitchMax == -1) {
                    player.systemMessage(1, "Pitch has to be a number between 0.1 and 2.0 or 0 to reset it.");
                    return true;
                }
                meta = setPitchBounds(meta, pitchMin, pitchMax, "swap");
                break;
            }
            case "name": {
                if (args.length < 2) {
                    player.systemMessage(1, "Missing arguments.");
                    return true;
                }

                meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(args[1].replace('_', ' ')));
                player.systemMessage(0, "Set item name");
                break;
            }
            case "attack":{
                switch(args[1]){
                    case "speed":{
                        meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_SPEED);
                        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "speed", Double.parseDouble(args[2]), Operation.ADD_NUMBER, EquipmentSlot.HAND));
                        break;
                    }

                    case "damage":{
                        meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
                        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "damage", Double.parseDouble(args[2]), Operation.ADD_NUMBER, EquipmentSlot.HAND));
                        break;
                    }

                    case "knockback":{
                        meta.removeAttributeModifier(Attribute.GENERIC_ATTACK_KNOCKBACK);
                        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_KNOCKBACK, new AttributeModifier(UUID.randomUUID(), "knockback", Double.parseDouble(args[2]), Operation.ADD_NUMBER, EquipmentSlot.HAND));
                        break;
                    }
                }
                break;
            }
            case "unbreakable":{
                if(args.length > 1)
                {
                    if(args[1].contentEquals("1"))
                        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                    else if(args[1].contentEquals("0"))
                        meta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                }
                else if(meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE))
                    meta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                else
                    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

                if(meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE))
                    player.systemMessage(0, "Item is now unbreakable.");
                else
                    player.systemMessage(0, "Item is now breakable.");
                break;
            }
            case "groundhit": 
            {
                if (args.length < 2) {
                    player.systemMessage(1, "Missing arguments.");
                    return true;
                }
                
                NamespacedKey key = new NamespacedKey(Core.Instance, "ground-hit");
                switch (args[1]) {
                    case "metal": {
                        meta = setKey(meta, key, args[1], true);
                        player.systemMessage(2, "Set ground-hit sound to " + args[1]);
                        break;
                    }
                    default: {
                        player.systemMessage(1, "Wrong parameter.");
                        return true;
                    }
                }

                float pitchMin = (args.length > 2) ? parsePitch(args[2]) : 0, pitchMax = (args.length > 3) ? parsePitch(args[3]) : 0;
                if (pitchMin == -1 || pitchMax == -1) {
                    player.systemMessage(1, "Pitch has to be a number between 0.1 and 2.0 or 0 to reset it.");
                    return true;
                }
                meta = setPitchBounds(meta, pitchMin, pitchMax, "ground-hit");
                break;
            }
            case "hide":{
                meta.addItemFlags(ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE);
                break;
            }
            case "lore":{
                List<Component> lore = meta.lore();
                if(lore == null)
                    lore = new ArrayList<Component>();
                switch(args[1]) 
                {
                    case "add":
                        lore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(args[2].replace("_", " ")));
                        break;
                    case "insert":
                        try 
                        {
                            lore.add(Integer.parseInt(args[2]), LegacyComponentSerializer.legacyAmpersand().deserialize(args[3].replace("_", " ")));
                            meta.lore(lore);
                        }
                        catch(Exception e)
                        {
                            player.systemMessage(1, "Index must be a number[Integer]| index ∈ <0; "+lore.size()+">!");
                        }
                        break;
                    case "set":
                        try 
                        {
                            if(lore.size()<1)
                            {
                                player.systemMessage(1, "lore is too short to do this.");
                                return true;
                            }

                            if(lore.size() < Integer.parseInt(args[2]))
                            {
                                player.systemMessage(1, "Index must be a number[Integer]| index ∈ <1; "+(lore.size())+">!");
                                return true;
                            }
                            lore.set(Integer.parseInt(args[2])-1, LegacyComponentSerializer.legacyAmpersand().deserialize(args[3].replace("_", " ")));
                            meta.lore(lore);
                        }
                        catch(Exception e)
                        {
                            player.systemMessage(1, "Index must be a number[Integer]| index ∈ <1; "+(lore.size())+">!");
                        }
                        break;
                    case "remove":
                        try 
                        {
                            if(lore.size()<1)
                            {
                                player.systemMessage(1, "lore is too short to do this.");
                                return true;
                            }

                            if(lore.size() < Integer.parseInt(args[2]))
                            {
                                player.systemMessage(1, "Index must be a number[Integer]| index ∈ <1; "+(lore.size())+">!");
                                return true;
                            }

                            lore.remove(Integer.parseInt(args[2])-1);
                        }
                        catch(Exception e)
                        {
                            player.systemMessage(1, "Index must be a number[Integer]| index ∈ <1; "+(lore.size())+">!");
                            return true;
                        }
                        break;
                    case "reset":
                        lore = new ArrayList<>();
                        break;
                }
                meta.lore(lore);
                break;
            }
        }

        item.setItemMeta(meta);
        return true;
    }

    private float parsePitch(String arg) {
        try {
            float pitch = Float.parseFloat(arg);
            if ((pitch < 0.1f || pitch > 2f) && pitch != 0) {
                Core.logger.warning("Pitch has to be a number between 0.1 and 2.0 or 0 to reset it.");
                return -1;
            }
            return pitch;

        } catch (Exception ex) {
            Core.logger.warning("Pitch has to be a number between 0.1 and 2.0 or 0 to reset it.");
            return -1;
        }
    }

    private ItemMeta setPitchBounds(ItemMeta meta, float min, float max, String key) {
        if((max < min) && max !=0 && min != 0)
        {
            float x = max;
            max = min;
            min = x;
        }

        if (max == 0) {
            if (meta.getPersistentDataContainer().has(new NamespacedKey(Core.Instance, key + "-pitch-max")))
                meta.getPersistentDataContainer().remove(new NamespacedKey(Core.Instance, key + "-pitch-max"));
            if(min > 1.2f)
                min = 1.2f;
            //TODO: softcode
        } else
            meta.getPersistentDataContainer().set(new NamespacedKey(Core.Instance, key + "-pitch-max"),
                    PersistentDataType.FLOAT, max);

        if (min == 0) {
            if (meta.getPersistentDataContainer().has(new NamespacedKey(Core.Instance, key + "-pitch-min")))
                meta.getPersistentDataContainer().remove(new NamespacedKey(Core.Instance, key + "-pitch-min"));
            if(max < 0.3f)
                max = 0.3f;
            //TODO: softcode
        } else
            meta.getPersistentDataContainer().set(new NamespacedKey(Core.Instance, key + "-pitch-min"),
                    PersistentDataType.FLOAT, min);

        return meta;
    }

    public ItemMeta swing(ItemMeta meta, String s) {
        NamespacedKey key = new NamespacedKey(Core.Instance, "swing");
        switch (s) {
            case "big":
                return setKey(meta, key, s, true);

            case "small":
                return setKey(meta, key, s, true);

            case "sharp":
                return setKey(meta, key, s, true);
            default: {
                return null;
            }
        }
    }

    private ItemMeta setKey(ItemMeta meta, NamespacedKey key, String value, boolean replace) {
        if (meta.getPersistentDataContainer().has(key)) {
            if (replace)
                meta.getPersistentDataContainer().remove(key);
            else
                return null;
        }

        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
        return meta;
    }
}
