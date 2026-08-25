package me.sirius.sbardamage;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DamageListener implements Listener {
    
    private final SBardamage plugin;
    
    public DamageListener(SBardamage plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;
        
        Player damager = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();
        
        if (damager.getUniqueId().equals(victim.getUniqueId())) return;
        
        double damage = event.getFinalDamage();
        
        if (damage > 0) {
            plugin.getDamageManager().addDamage(damager, victim, damage);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        plugin.getDamageManager().handleDeath(player);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getDamageManager().handleDeath(player);
    }
}