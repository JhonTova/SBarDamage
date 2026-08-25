package me.sirius.sbardamage;

public class DamageData {
    
    private double damageDealt;
    private double damageReceived;
    private long lastUpdate;
    private final long timeout;
    
    public DamageData(long timeout) {
        this.damageDealt = 0;
        this.damageReceived = 0;
        this.lastUpdate = System.currentTimeMillis();
        this.timeout = timeout;
    }
    
    public void addDamageDealt(double amount) {
        this.damageDealt += amount;
        this.lastUpdate = System.currentTimeMillis();
    }
    
    public void addDamageReceived(double amount) {
        this.damageReceived += amount;
        this.lastUpdate = System.currentTimeMillis();
    }
    
    public double getDamageDealt() {
        return damageDealt;
    }
    
    public double getDamageReceived() {
        return damageReceived;
    }
    
    public long getLastUpdate() {
        return lastUpdate;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() - lastUpdate > timeout;
    }
}