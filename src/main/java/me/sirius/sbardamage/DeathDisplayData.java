package me.sirius.sbardamage;

public class DeathDisplayData {
    
    private final String message;
    private final long expirationTime;
    
    public DeathDisplayData(String message, long displayDuration) {
        this.message = message;
        this.expirationTime = System.currentTimeMillis() + displayDuration;
    }
    
    public String getMessage() {
        return message;
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() >= expirationTime;
    }
}