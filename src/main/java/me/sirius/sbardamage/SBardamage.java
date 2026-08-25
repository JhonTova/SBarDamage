package me.sirius.sbardamage;

import me.sirius.core.SiriusPlugin;

public class SBardamage extends SiriusPlugin {
    
    private static SBardamage instance;
    private DamageManager damageManager;
    
    @Override
    protected void onStart() {
        instance = this;
        
        saveDefaultConfig();
        
        this.damageManager = new DamageManager(this);
        
        getServer().getPluginManager().registerEvents(new DamageListener(this), this);
        
        getLogger().info("§b╔═══════════════════════════════════════╗");
        getLogger().info("§b║ §d          ✦ SBarDamage ✦             §b║");
        getLogger().info("§b║ §7      by §bSirius §8| §7GitHub: §fJhonTova      §b║");
        getLogger().info("§b║ §7       Discord: §fksirius                   §b║");
        getLogger().info("§b║ §a     Plugin habilitado con éxito!           §b║");
        getLogger().info("§b╚═══════════════════════════════════════╝");
    }
    
    @Override
    protected void onStop() {
        if (damageManager != null) {
            damageManager.shutdown();
        }
        getLogger().info("§b╔═══════════════════════════════════════╗");
        getLogger().info("§b║ §d          ✦ SBarDamage ✦             §b║");
        getLogger().info("§b║ §7      by §bSirius §8| §7GitHub: §fJhonTova      §b║");
        getLogger().info("§b║ §7       Discord: §fksirius                   §b║");
        getLogger().info("§b║ §a     Plugin deshabilitado con éxito!           §b║");
        getLogger().info("§b╚═══════════════════════════════════════╝");
    }
    
    public static SBardamage getInstance() {
        return instance;
    }
    
    public DamageManager getDamageManager() {
        return damageManager;
    }
}