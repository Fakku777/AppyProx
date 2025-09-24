package dev.aprilrenders.appyprox.ui;

import dev.aprilrenders.appyprox.AppyProxFabricClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integrates AppyProx buttons into Minecraft's existing menus
 */
@Environment(EnvType.CLIENT)
public class MenuIntegration {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuIntegration.class);
    
    /**
     * Initialize menu integrations
     */
    public static void initialize() {
        LOGGER.info("Initializing AppyProx menu integration...");
        
        // Register screen event handlers
        ScreenEvents.AFTER_INIT.register(MenuIntegration::onScreenInit);
        
        LOGGER.info("Menu integration initialized successfully");
    }
    
    /**
     * Called after a screen is initialized
     */
    private static void onScreenInit(net.minecraft.client.MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
        if (screen instanceof MultiplayerScreen) {
            addMultiplayerMenuButton((MultiplayerScreen) screen);
        } else if (screen instanceof GameMenuScreen) {
            addPauseMenuButton((GameMenuScreen) screen);
        }
    }
    
    /**
     * Add AppyProx button to the multiplayer menu
     */
    private static void addMultiplayerMenuButton(MultiplayerScreen multiplayerScreen) {
        try {
            // Create AppyProx button
            ButtonWidget appyProxButton = ButtonWidget.builder(
                Text.literal("AppyProx"),
                (button) -> {
                    net.minecraft.client.MinecraftClient.getInstance().setScreen(
                        new AppyProxManagerScreenNew(multiplayerScreen)
                    );
                }
            )
            .dimensions(multiplayerScreen.width / 2 + 4, multiplayerScreen.height - 28, 100, 20)
            .build();
            
            // Add the button to the screen
            Screens.getButtons(multiplayerScreen).add(appyProxButton);
            
            LOGGER.debug("Added AppyProx button to multiplayer menu");
            
        } catch (Exception e) {
            LOGGER.error("Failed to add AppyProx button to multiplayer menu", e);
        }
    }
    
    /**
     * Add AppyProx button to the pause menu (in-game)
     */
    private static void addPauseMenuButton(GameMenuScreen pauseScreen) {
        try {
            // Only add if we're connected to a server (multiplayer)
            if (net.minecraft.client.MinecraftClient.getInstance().getNetworkHandler() == null) {
                return; // Single player, skip
            }
            
            // Create AppyProx button
            ButtonWidget appyProxButton = ButtonWidget.builder(
                Text.literal("AppyProx"),
                (button) -> {
                    net.minecraft.client.MinecraftClient.getInstance().setScreen(
                        new AppyProxManagerScreenNew(pauseScreen)
                    );
                }
            )
            .dimensions(pauseScreen.width / 2 - 102, pauseScreen.height / 4 + 120, 100, 20)
            .build();
            
            // Add the button to the screen
            Screens.getButtons(pauseScreen).add(appyProxButton);
            
            LOGGER.debug("Added AppyProx button to pause menu");
            
        } catch (Exception e) {
            LOGGER.error("Failed to add AppyProx button to pause menu", e);
        }
    }
    
    /**
     * Create an AppyProx status indicator button
     */
    private static ButtonWidget createStatusButton(Screen parentScreen) {
        var manager = AppyProxFabricClient.getManager();
        
        String statusText = "AppyProx";
        if (manager != null) {
            if (manager.isConnected()) {
                statusText = manager.isProxyMode() ? "§aAppyProx (Active)" : "§eAppyProx (Connected)";
            } else {
                statusText = "§cAppyProx (Offline)";
            }
        }
        
        return ButtonWidget.builder(
            Text.literal(statusText),
            (button) -> {
                net.minecraft.client.MinecraftClient.getInstance().setScreen(
                    new AppyProxManagerScreenNew(parentScreen)
                );
            }
        )
        .dimensions(0, 0, 120, 20) // Position will be set by caller
        .build();
    }
    
    /**
     * Get the appropriate button text based on current AppyProx state
     */
    private static Text getButtonText() {
        var manager = AppyProxFabricClient.getManager();
        
        if (manager == null) {
            return Text.literal("AppyProx");
        }
        
        if (!manager.isConnected()) {
            return Text.literal("AppyProx §c(Offline)");
        }
        
        if (manager.isProxyMode()) {
            int instanceCount = manager.getProxyModeManager().getActiveInstanceCount();
            return Text.literal(String.format("AppyProx §a(%d Active)", instanceCount));
        }
        
        return Text.literal("AppyProx §e(Ready)");
    }
}