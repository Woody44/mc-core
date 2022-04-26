package woody44.minecraft.core.player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.google.gson.Gson;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import woody44.minecraft.core.AuthSession;
import woody44.minecraft.core.Core;
import woody44.minecraft.core.events.PlayerAuthEvent;
import woody44.minecraft.core.events.PlayerLevelUpOnceEvent;
import woody44.minecraft.core.events.PlayerLoadedEvent;

public class PlayerCore implements Listener {

    public Profile profile;
    private Player _player;

    public PlayerData DATA;

    private String _UUID;
    private boolean isAuthed;
    private boolean isLoaded;

    private BukkitTask regenTask;

    Map<String, Long> cooldowns = new HashMap<>();

    public PlayerCore(Player player){
        this(player.getUniqueId());
    }

    public PlayerCore(UUID uuid) {
        _player = Bukkit.getPlayer(uuid);
        _UUID = uuid.toString();

        _player.setInvulnerable(true);
        _player.setInvisible(true);
        _player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999999, 240));

        MakeDirs();
        loadData();
        profile = new Profile(this, DATA.latestProfile);
        profile.load();

        registerEvents();
        isLoaded = true;

        PlayerLoadedEvent e = new PlayerLoadedEvent(this);
        Bukkit.getPluginManager().callEvent(e);
    }

    public String getId(){
        return _UUID;
    }

    public void setLatestProfile(String profileId){
        DATA.latestProfile = profileId;
    }

    public long getWeight(){
        long weight = 100;
        ItemStack[] items = _player.getInventory().getContents();
        for (ItemStack itemStack : items) {
            if(itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Core.Instance, "weight")))
            {
                weight += itemStack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Core.Instance, "weight"), PersistentDataType.LONG);
            }
        }

        return weight;
    }

    private void registerEvents() {
        Core.plugman.registerEvents(this, Core.Instance);
    }

    private void MakeDirs() {
        new File(MessageFormat.format("_core/players/{0}/profiles/", _UUID)).mkdirs();
    }

    // Player Data
    private void loadData() {
        File dataFile = new File("_core/players/" + _UUID + "/data.json");
        if (!dataFile.exists()) {
            DATA = createData();
            return;
        } else
            try {
                DATA = PlayerData.Build(Files.readString(Path.of("_core/players/" + _UUID + "/data.json")));
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private PlayerData createData() {
        File dataFile = new File("_core/players/" + _UUID + "/data.json");
        try {
            dataFile.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        PlayerData _data = new PlayerData();
        try {
            PrintWriter dataWriter = new PrintWriter("_core/players/" + _UUID + "/data.json");
            dataWriter.write(_data.Serialize());
            dataWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return _data;
    }

    // Armor
    public double[] getArmorToughness() {
        double[] val = new double[4];
        val[0] = getArmorToughness(EquipmentSlot.HEAD);
        val[1] = getArmorToughness(EquipmentSlot.CHEST);
        val[2] = getArmorToughness(EquipmentSlot.LEGS);
        val[3] = getArmorToughness(EquipmentSlot.FEET);
        return val;
    }

    public double getArmorToughness(EquipmentSlot slot) {
        double val = 0;
        ItemStack item = _player.getInventory().getItem(slot);
        if (item != null && item.hasItemMeta())
            for (AttributeModifier attr : _player.getInventory().getItem(slot).getItemMeta()
                    .getAttributeModifiers(Attribute.GENERIC_ARMOR_TOUGHNESS)) {
                val += attr.getAmount();
            }

        return val;
    }

    public double[] getArmor() {
        double[] val = new double[4];
        val[0] = getArmor(EquipmentSlot.HEAD);
        val[1] = getArmor(EquipmentSlot.CHEST);
        val[2] = getArmor(EquipmentSlot.LEGS);
        val[3] = getArmor(EquipmentSlot.FEET);
        return val;
    }

    public double getArmor(EquipmentSlot slot) {
        double val = 0;

        ItemStack item = _player.getInventory().getItem(slot);
        if (item != null && item.hasItemMeta())
            for (AttributeModifier attr : item.getItemMeta().getAttributeModifiers(Attribute.GENERIC_ARMOR)) {
                val += attr.getAmount() - attr.getAmount() * ((Damageable) item.getItemMeta()).getDamage()
                        / item.getType().getMaxDurability();
            }

        return val;
    }

    // Runtime
    public boolean IsAuthed() {
        return isAuthed;
    }

    public Player getBukkitPlayer() {
        return _player;
    }

    public boolean isOnline() {
        return _player != null;
    }

    public static boolean isOnline(String value) {
        return Bukkit.getPlayerExact(value) != null || Bukkit.getPlayer(UUID.fromString(value)) != null;
    }

    public void save() {
        String _data = DATA.Serialize();

        try {
            PrintWriter dataWriter = new PrintWriter("_core/players/" + _UUID + "/data.json");
            dataWriter.write(_data);
            dataWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveAll(){
        save();
        profile.save();
    }

    public void systemMessage(int code, String msg) {
        TextComponent txt;
        switch (code) {
            case 0:
                txt = Component.text()
                        .content("[SYSTEM]")
                        .color(TextColor.color(0x41c467))
                        .append(Component.text().content(" " + msg).color(TextColor.color(0x6ff796)).build())
                        .build();
                break;

            case 1:
                txt = Component.text()
                        .content("[SYSTEM]")
                        .color(TextColor.color(0xb5443c))
                        .append(Component.text().content(" " + msg).color(TextColor.color(0xfa6e64)).build())
                        .build();
                break;

            case 2:
                txt = Component.text()
                        .content("[SYSTEM]")
                        .color(TextColor.color(0xd69d40))
                        .append(Component.text().content(" " + msg).color(TextColor.color(0xfac570)).build())
                        .build();
                break;

            default:
                txt = Component.text()
                        .content("[SYSTEM]")
                        .color(TextColor.color(0xd69d40))
                        .append(LegacyComponentSerializer.legacyAmpersand().deserialize(msg))
                        .build();
                break;
        }

        _player.sendMessage(txt);
    }

    public String getFolderPath() {
        return MessageFormat.format("_core/players/{0}", _UUID);
    }

    public boolean Login(String pass) {
        File f = new File(getFolderPath() + "/auth.json");
        if (!f.exists())
            return false;

        Gson gson = new Gson();

        try {
            String json = Files.readString(Path.of(getFolderPath() + "/auth.json"));
            Auth a = gson.fromJson(json, Auth.class);

            isAuthed = pass.contentEquals(a.pass);
            return isAuthed;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean Register(String pass) {
        return SetPassword(pass);
    }

    public boolean SetPassword(String pass) {
        Auth auth = new Auth();
        auth.pass = pass;
        // auth.pass = HashIt(pass);
        File f = new File(getFolderPath() + "/auth.json");
        if (f.exists())
            return false;

        Gson g = new Gson();

        try {
            Files.createFile(Path.of(getFolderPath() + "/auth.json"));
            PrintWriter writer = new PrintWriter(getFolderPath() + "/auth.json");
            writer.write(g.toJson(auth));
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Events
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (!e.getPlayer().getUniqueId().toString().equals(_UUID))
            return;

        saveAll();
        PlayerCore.unRegisterPlayer(e.getPlayer().getUniqueId());
        regenTask.cancel();
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!e.getPlayer().getUniqueId().toString().equals(_UUID))
            return;

        if (!isLoaded || !isAuthed)
            e.setCancelled(true);
    }

    @EventHandler
    public void OnPlayerInteract(PlayerInteractEvent e) {
        if (!e.getPlayer().getUniqueId().toString().equals(_UUID))
            return;

        if (!isLoaded || !isAuthed)
            e.setCancelled(true);
    }

    @EventHandler
    public void OnPlayerDropItem(PlayerDropItemEvent e) {
        if (!e.getPlayer().getUniqueId().toString().equals(_UUID))
            return;

        if (!isLoaded || !isAuthed)
            e.setCancelled(true);
    }

    @EventHandler
    public void OnPlayerDragItem(InventoryDragEvent e) {
        if (!e.getWhoClicked().getUniqueId().toString().equals(_UUID))
            return;

        if (!isLoaded || !isAuthed)
            e.setCancelled(true);
    }

    @EventHandler
    public void OnPlayerClickItem(InventoryClickEvent e) {
        if (!e.getWhoClicked().getUniqueId().toString().equals(_UUID))
            return;

        if (!isLoaded || !isAuthed)
            e.setCancelled(true);
    }

    @EventHandler
    public void OnPlayerCommand(PlayerCommandPreprocessEvent e) {
        if (!e.getPlayer().getUniqueId().toString().equals(_UUID))
            return;

        if ((!isLoaded || !isAuthed) && (!e.getMessage().startsWith("/l") && !e.getMessage().startsWith("/log")
                && !e.getMessage().startsWith("/r") && !e.getMessage().startsWith("/reg")))
            e.setCancelled(true);
    }

    @EventHandler
    public void OnPlayerDamaged(EntityDamageEvent e) {
        if (!e.getEntity().getUniqueId().toString().equals(_UUID))
            return;

        if (!isLoaded || !isAuthed) {
            e.setCancelled(true);
            return;
        }

        switch(e.getCause())
        {
            case BLOCK_EXPLOSION:
                takeDamage(e.getDamage());
                break;
            case CONTACT:
            takeDamage(e.getDamage());
                break;
            case CRAMMING:
            takeDamage(e.getDamage());
                break;
            case CUSTOM:
            takeDamage(e.getDamage());
                break;
            case DRAGON_BREATH:
            takeDamage(e.getDamage());
                break;
            case DROWNING:
                takeDamage(profile.stats.Health_max * 0.25, true);
                break;
            case DRYOUT:
            takeDamage(e.getDamage());
                break;
            case ENTITY_ATTACK:
            takeDamage(e.getDamage());
                break;
            case ENTITY_EXPLOSION:
            takeDamage(e.getDamage());
                break;
            case ENTITY_SWEEP_ATTACK:
            takeDamage(e.getDamage());
                break;
            case FALL:
                if(!e.isCancelled())
                    takeDamage(((_player.getFallDistance() - 3) * (_player.getFallDistance() - 3)) * 0.40 * getWeight(), true);
                break;
            case FALLING_BLOCK:
            takeDamage(e.getDamage());
                break;
            case FIRE:
            takeDamage(e.getDamage()*5);
                break;
            case FIRE_TICK:
            takeDamage(e.getDamage());
                break;
            case FLY_INTO_WALL:
            takeDamage(e.getDamage());
                break;
            case FREEZE:
            takeDamage(e.getDamage());
                break;
            case HOT_FLOOR:
            takeDamage(e.getDamage());
                break;
            case LAVA:
            takeDamage(e.getDamage()*10);
                break;
            case LIGHTNING:
            takeDamage(e.getDamage());//TODO: Enhance iron armor damage
                break;
            case MAGIC:
            takeDamage(e.getDamage(), true);
                break;
            case MELTING:
            takeDamage(e.getDamage());
                break;
            case POISON:
            takeDamage(e.getDamage(), true);
                break;
            case PROJECTILE:
            takeDamage(e.getDamage());
                break;
            case STARVATION:
                e.setDamage(0.00001);
                takeDamage(profile.stats.Health * 0.15 + 5, true);
                break;
            case SUFFOCATION:
                takeDamage(profile.stats.Health * 0.2, true);
                break;
            case SUICIDE:
                takeDamage(profile.stats.Health_max, true);
                break;
            case THORNS:
            takeDamage(e.getDamage());
                break;
            case VOID:
                takeDamage(profile.stats.Health_max, true);
                break;
            case WITHER:
            takeDamage(e.getDamage(), true);
                break;
            default:
                break;
            
        }
        e.setDamage(0.00000001);
    }

    @EventHandler
    public void OnPlayerAuth(PlayerAuthEvent e) {
        if (!e.GetResult())
            return;

        AuthSession.End(this);
        profile.init();
        _player.getAdvancementProgress(Bukkit.getAdvancement(NamespacedKey.fromString("rpg:root"))).awardCriteria("impossible");
        
        regenTask = Bukkit.getScheduler().runTaskTimer(Core.Instance, new Runnable() {
            @Override
            public void run() {
                heal(profile.stats.Health_regen_ooc);
            }

        }, 20 * 5, 20 * 10);
    }

    @EventHandler
    public void OnPlayerExp(PlayerPickupExperienceEvent e) {
        if (!e.getPlayer().getUniqueId().toString().equals(_UUID))
            return;

        e.setCancelled(true);
        int value = e.getExperienceOrb().getExperience();
        if (e.getExperienceOrb().hasMetadata("core")) {
            /*
             * String type = e.getExperienceOrb().getMetadata("core").get(0).asString(),
             * owner = e.getExperienceOrb().getMetadata("core").get(0).asString(),
             * target = e.getExperienceOrb().getMetadata("core").get(0).asString();
             */
        } else {
            value *= 0.1;
            profile.addExp(value);
        }
        e.getExperienceOrb().remove();
    }

    @EventHandler
    public void OnPlayerRespawn(PlayerRespawnEvent e) {
        if (!e.getPlayer().getUniqueId().toString().equals(_UUID))
            return;

        profile.stats.Health = 0.25 * profile.stats.Health_max;
    }

    @EventHandler
    public void OnBlockBreak(BlockBreakEvent e) {
        if (!e.getPlayer().getUniqueId().toString().equals(_UUID))
            return;

        if(_player.getGameMode() != GameMode.CREATIVE)
        {
            if(e.getBlock().getType().toString().contains("GLASS"))
                if(_player.getInventory().getItemInMainHand() == null || _player.getInventory().getItemInMainHand().getType() == Material.AIR)
                    takeDamage(5, true);
        }
    }

    @EventHandler
    public void OnLevelUpOnce(PlayerLevelUpOnceEvent e) {
        if (!e.getPlayer().getId().equals(_UUID))
            return;

        if (profile.stats.Level % 10 == 0 && profile.stats.Level % 50 != 0 && profile.stats.Level % 100 != 0)
            _player.playSound(_player, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 2, 0.57f);
        else if (profile.stats.Level % 50 == 0 && profile.stats.Level % 100 != 0)
            _player.playSound(_player, Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 2, 1.4f);
        else if (profile.stats.Level % 100 == 0)
            levelSequence();
        else
            _player.playSound(_player, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 2,
                    2f - 2 * profile.stats.Level / 100);
    }

    public void setCooldown(String name, long ticks, boolean overwrite){
        if(cooldowns.containsKey(name)){
            if(!overwrite)
                return;
            
            cooldowns.remove(name);
        }

        cooldowns.put(name, new Date().getTime() + 20 * ticks);
        Core.logger.warning("set cooldown " + name + " to " + ticks +"["+ 20 * ticks +"] /" + new Date().getTime() + 20 * ticks);
    }

    public int getCooldown(String name){
        if(cooldowns.containsKey(name)){
            Core.logger.warning("GET cooldown " + name + "is: " + (int)(cooldowns.get(name) - new Date().getTime()) / 20);
            return (int)(cooldowns.get(name) - new Date().getTime()) / 20;
        }
        else
            return -1;
    }

    public void takeDamage(double value){
        takeDamage(value, false);
    }

    public void takeDamage(double value, boolean passArmor) {
        if(!passArmor){
            double[] armor = getArmor();
            double[] tough = getArmorToughness();
            for (int i = 0; i < armor.length; i++)
                value -= armor[i] * tough[i];
        }

        if (value <= 0)
            return;

        profile.stats.Health -= value;
        calculateHealth();
    }

    public void heal(double value) {
        if (_player.isDead())
            return;

        if (profile.stats.Health + value >= profile.stats.Health_max)
            profile.stats.Health = profile.stats.Health_max;
        else
            profile.stats.Health += value;

        calculateHealth();
    }

    public static TextColor[] HealthColors = new TextColor[] {
            TextColor.color(92, 7, 7),
            TextColor.color(153, 17, 17),
            TextColor.color(171, 54, 19),
            TextColor.color(212, 101, 28),
            TextColor.color(237, 162, 40),
            TextColor.color(214, 237, 40),
            TextColor.color(109, 237, 40),
            TextColor.color(2, 247, 23)
    };
    DecimalFormat df = new DecimalFormat("###.##");
    DecimalFormat dfnd = new DecimalFormat("###");

    public static HashMap<String, PlayerCore> PLAYERS = new HashMap<String, PlayerCore>();

    public void SendActionBar() {
        if (_player.getGameMode() != GameMode.CREATIVE && _player.getGameMode() != GameMode.SPECTATOR)
            _player.sendActionBar(
                    Component.text().content("❤ ").color(TextColor.color(245, 66, 87))
                            .append(Component.text().content(dfnd.format(profile.stats.Health) + "").color(
                                    HealthColors[(int) (profile.stats.Health / profile.stats.Health_max * HealthColors.length - 1)])
                                    .decorate(TextDecoration.BOLD))

                            .append(Component.text().content(" ⛨ ").color(TextColor.color(143, 225, 247)))
                            .append(Component.text().content(dfnd.format(getArmor()) + "")
                                    .color(TextColor.color(88, 205, 237)).decorate(TextDecoration.BOLD))

                            .append(Component.text().content("│ ").color(TextColor.color(255, 255, 255)))
                            .append(Component.text().content(dfnd.format(/* TODO:MagicRes() */0) + "")
                                    .color(TextColor.color(153, 88, 237)).decorate(TextDecoration.BOLD))

                            .append(Component.text().content(" ★ ").color(TextColor.color(25, 52, 230)))
                            .append(Component.text().content(dfnd.format(profile.stats.Mana) + "")
                                    .color(TextColor.color(96, 108, 191)).decorate(TextDecoration.BOLD)));
        else {
            _player.sendActionBar(
                    Component.text().content("X: ").color(TextColor.color(255, 247, 0)).decorate(TextDecoration.BOLD)
                            .append(Component.text(df.format(_player.getLocation().getX()))
                                    .color(TextColor.color(9, 255, 0)))
                            .append(Component.text().content(" Y: ").color(TextColor.color(255, 247, 0))
                                    .decorate(TextDecoration.BOLD))
                            .append(Component.text(df.format(_player.getLocation().getY()))
                                    .color(TextColor.color(9, 255, 0)))
                            .append(Component.text().content(" Z: ").color(TextColor.color(255, 247, 0))
                                    .decorate(TextDecoration.BOLD))
                            .append(Component.text(df.format(_player.getLocation().getZ()))
                                    .color(TextColor.color(9, 255, 0))));
        }
    }

    public void swapProfile(String name) {
        if (profile.data.NAME.equals(name))
            return;
        
        if(profile != null)
            profile.save();

        profile = new Profile(this, name);
        profile.load();
        profile.init();
    }

    private void levelSequence() {
        _player.playSound(_player, Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 2, 0.2f);
    }

    public void calculateHealth() {
        if (profile.stats.Health > profile.stats.Health_max)
            profile.stats.Health = profile.stats.Health_max;
        if (profile.stats.Health <= 0) {
            _player.setHealth(0);
            return;
        }
        _player.setHealth(20 * profile.stats.Health / profile.stats.Health_max);
    }

    public static boolean unRegisterPlayer(UUID uuid) {
        if (!PLAYERS.containsKey(uuid.toString()))
            return false;
    
        PLAYERS.remove(uuid.toString());
        return true;
    }

    public static boolean registerPlayer(UUID uuid) {
        if (PLAYERS.containsKey(uuid.toString()))
            return false;
    
        PLAYERS.put(uuid.toString(), new PlayerCore(uuid));
        AuthSession.Start(PLAYERS.get(uuid.toString()));
        return true;
    }

    public static PlayerCore getPlayer(UUID uuid) {
        if (PLAYERS.containsKey(uuid.toString()))
            return PLAYERS.get(uuid.toString());
        else
            return null;
    }

    public static PlayerCore getPlayer(Player p) {
        if (PLAYERS.containsKey(p.getUniqueId().toString()))
            return PLAYERS.get(p.getUniqueId().toString());
        else
            return null;
    }
}

class Auth {
    String pass;
}