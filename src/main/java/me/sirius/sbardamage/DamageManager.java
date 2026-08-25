package me.sirius.sbardamage;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DamageManager {
    
    private final SBardamage plugin;
    private final Map<UUID, LinkedHashMap<UUID, DamageData>> playerDamageMap;
    private final Map<UUID, DeathDisplayData> deathDisplayMap;
    private BukkitTask updateTask;
    private long timeout;
    private long deathDisplayTime;
    private String actionbarFormat;
    
    public DamageManager(SBardamage plugin) {
        this.plugin = plugin;
        this.playerDamageMap = new ConcurrentHashMap<>();
        this.deathDisplayMap = new ConcurrentHashMap<>();
        loadConfig();
        startUpdateTask();
    }
    
    private void loadConfig() {
        this.timeout = plugin.getConfig().getLong("timeout-seconds", 15) * 1000;
        this.deathDisplayTime = plugin.getConfig().getLong("death-display-seconds", 3) * 1000;
        this.actionbarFormat = ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("actionbar-format", "&e%player% &8| &cDaño Hecho: &f%damage_dealt% &8| &aDaño Recibido: &f%damage_received%"));
    }
    
    public void reloadConfig() {
        loadConfig();
    }
    
    private void startUpdateTask() {
        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            deathDisplayMap.entrySet().removeIf(entry -> {
                if (entry.getValue().isExpired()) {
                    Player player = Bukkit.getPlayer(entry.getKey());
                    if (player != null && player.isOnline()) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
                    }
                    return true;
                }
                return false;
            });
            
            for (UUID playerUUID : new HashSet<>(playerDamageMap.keySet())) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player == null || !player.isOnline()) {
                    playerDamageMap.remove(playerUUID);
                    continue;
                }
                
                if (deathDisplayMap.containsKey(playerUUID)) {
                    continue;
                }
                
                LinkedHashMap<UUID, DamageData> damageMap = playerDamageMap.get(playerUUID);
                if (damageMap == null) continue;
                
                damageMap.entrySet().removeIf(entry -> entry.getValue().isExpired());
                
                if (damageMap.isEmpty()) {
                    playerDamageMap.remove(playerUUID);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
                } else {
                    updateActionBar(player);
                }
            }
        }, 0L, 5L);
    }
    
    public void addDamage(Player damager, Player victim, double damage) {
        if (damage <= 0) return;
        
        if (deathDisplayMap.containsKey(victim.getUniqueId())) {
            deathDisplayMap.remove(victim.getUniqueId());
        }
        
        if (deathDisplayMap.containsKey(damager.getUniqueId())) {
            deathDisplayMap.remove(damager.getUniqueId());
        }
        
        playerDamageMap.putIfAbsent(damager.getUniqueId(), new LinkedHashMap<>());
        LinkedHashMap<UUID, DamageData> damageMap = playerDamageMap.get(damager.getUniqueId());
        
        DamageData data = damageMap.remove(victim.getUniqueId());
        if (data == null) {
            data = new DamageData(timeout);
        }
        data.addDamageDealt(damage);
        
        if (damageMap.size() >= 3) {
            Iterator<UUID> iterator = damageMap.keySet().iterator();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }
        
        damageMap.put(victim.getUniqueId(), data);
        
        playerDamageMap.putIfAbsent(victim.getUniqueId(), new LinkedHashMap<>());
        LinkedHashMap<UUID, DamageData> victimDamageMap = playerDamageMap.get(victim.getUniqueId());
        
        DamageData victimData = victimDamageMap.remove(damager.getUniqueId());
        if (victimData == null) {
            victimData = new DamageData(timeout);
        }
        victimData.addDamageReceived(damage);
        
        if (victimDamageMap.size() >= 3) {
            Iterator<UUID> iterator = victimDamageMap.keySet().iterator();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }
        
        victimDamageMap.put(damager.getUniqueId(), victimData);
        
        updateActionBar(damager);
        updateActionBar(victim);
    }
    
    public void handleDeath(Player player) {
        LinkedHashMap<UUID, DamageData> damageMap = playerDamageMap.get(player.getUniqueId());
        
        if (damageMap != null && !damageMap.isEmpty()) {
            UUID lastTargetUUID = null;
            for (UUID uuid : damageMap.keySet()) {
                lastTargetUUID = uuid;
            }
            
            if (lastTargetUUID != null) {
                Player target = Bukkit.getPlayer(lastTargetUUID);
                if (target != null) {
                    DamageData data = damageMap.get(lastTargetUUID);
                    String message = actionbarFormat
                        .replace("%player%", target.getName())
                        .replace("%damage_dealt%", String.format("%.1f", data.getDamageDealt()))
                        .replace("%damage_received%", String.format("%.1f", data.getDamageReceived()));
                    
                    deathDisplayMap.put(player.getUniqueId(), new DeathDisplayData(message, deathDisplayTime));
                }
            }
        }
        
        playerDamageMap.remove(player.getUniqueId());
        
        for (Map.Entry<UUID, LinkedHashMap<UUID, DamageData>> entry : playerDamageMap.entrySet()) {
            UUID otherPlayerUUID = entry.getKey();
            LinkedHashMap<UUID, DamageData> otherDamageMap = entry.getValue();
            
            if (otherDamageMap.remove(player.getUniqueId()) != null) {
                Player otherPlayer = Bukkit.getPlayer(otherPlayerUUID);
                if (otherPlayer != null && !deathDisplayMap.containsKey(otherPlayerUUID)) {
                    updateActionBar(otherPlayer);
                }
            }
        }
    }
    
    private void updateActionBar(Player player) {
        if (deathDisplayMap.containsKey(player.getUniqueId())) {
            DeathDisplayData displayData = deathDisplayMap.get(player.getUniqueId());
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(displayData.getMessage()));
            return;
        }
        
        LinkedHashMap<UUID, DamageData> damageMap = playerDamageMap.get(player.getUniqueId());
        if (damageMap == null || damageMap.isEmpty()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            return;
        }
        
        UUID lastTargetUUID = null;
        for (UUID uuid : damageMap.keySet()) {
            lastTargetUUID = uuid;
        }
        
        if (lastTargetUUID == null) return;
        
        Player target = Bukkit.getPlayer(lastTargetUUID);
        if (target == null) {
            damageMap.remove(lastTargetUUID);
            updateActionBar(player);
            return;
        }
        
        DamageData data = damageMap.get(lastTargetUUID);
        String message = actionbarFormat
            .replace("%player%", target.getName())
            .replace("%damage_dealt%", String.format("%.1f", data.getDamageDealt()))
            .replace("%damage_received%", String.format("%.1f", data.getDamageReceived()));
        
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
    
    public void shutdown() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        playerDamageMap.clear();
        deathDisplayMap.clear();
    }
}