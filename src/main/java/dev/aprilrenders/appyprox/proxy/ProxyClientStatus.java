package dev.aprilrenders.appyprox.proxy;

/**
 * Status of a proxy client
 */
public enum ProxyClientStatus {
    OFFLINE("Not running", "#808080"),
    STARTING("Starting up", "#FFA500"),
    CONNECTING("Connecting to server", "#FFFF00"),
    CONNECTED("Connected", "#00FF00"),
    RUNNING("Running normally", "#00FF00"),
    BUSY("Executing task", "#0000FF"),
    ERROR("Error occurred", "#FF0000"),
    STOPPING("Shutting down", "#FFA500"),
    FAILED("Failed to start", "#FF0000");
    
    private final String description;
    private final String uiColor;
    
    ProxyClientStatus(String description, String uiColor) {
        this.description = description;
        this.uiColor = uiColor;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getUiColor() {
        return uiColor;
    }
}
