package dev.aprilrenders.appyprox.ui;

import dev.aprilrenders.appyprox.AppyProxFabricClient;
import dev.aprilrenders.appyprox.control.DirectAccountController;
import dev.aprilrenders.appyprox.core.AppyProxManager;
import dev.aprilrenders.appyprox.data.ProxyAccount;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Account selection screen for direct control switching
 */
public class AccountSelectionScreen extends Screen {
    private final Screen parent;
    private final AppyProxManager manager;
    private final DirectAccountController accountController;
    
    private List<ProxyAccount> availableAccounts = new ArrayList<>();
    private List<ButtonWidget> accountButtons = new ArrayList<>();
    private ButtonWidget returnToOriginalButton;
    private ButtonWidget refreshButton;
    
    private boolean isLoading = true;
    private String statusMessage = "Loading accounts...";
    
    public AccountSelectionScreen(Screen parent) {
        super(Text.literal("Select Account to Control"));
        this.parent = parent;
        this.manager = AppyProxFabricClient.getManager();
        this.accountController = manager != null ? manager.getDirectAccountController() : null;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Return to original account button (if currently controlling proxy)
        if (accountController != null && accountController.isControllingProxyAccount()) {
            returnToOriginalButton = ButtonWidget.builder(
                Text.literal("§eReturn to Original Account"),
                (button) -> returnToOriginalAccount()
            )
            .dimensions(this.width / 2 - 100, 50, 200, 20)
            .build();
            this.addDrawableChild(returnToOriginalButton);
        }
        
        // Refresh accounts button
        refreshButton = ButtonWidget.builder(
            Text.literal("Refresh Accounts"),
            (button) -> loadAccounts()
        )
        .dimensions(this.width / 2 - 50, accountController != null && accountController.isControllingProxyAccount() ? 80 : 50, 100, 20)
        .build();
        this.addDrawableChild(refreshButton);
        
        // Back button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Back"),
            (button) -> this.close()
        )
        .dimensions(this.width / 2 - 50, this.height - 40, 100, 20)
        .build());
        
        // Load available accounts
        loadAccounts();
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        
        // Title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        
        // Current control status
        if (accountController != null) {
            String currentStatus;
            Formatting statusColor;
            
            if (accountController.isControllingProxyAccount()) {
                ProxyAccount controlled = accountController.getCurrentControlledAccount();
                currentStatus = "Currently controlling: " + (controlled != null ? controlled.getUsername() : "Unknown");
                statusColor = Formatting.GREEN;
            } else {
                currentStatus = "Currently controlling: Original Account";
                statusColor = Formatting.YELLOW;
            }
            
            context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal(currentStatus).formatted(statusColor),
                this.width / 2,
                35,
                0xFFFFFF
            );
        }
        
        // Status message
        if (isLoading || availableAccounts.isEmpty()) {
            context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal(statusMessage).formatted(Formatting.GRAY),
                this.width / 2,
                120,
                0xFFFFFF
            );
        }
        
        // Instructions
        if (!availableAccounts.isEmpty()) {
            context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("Select an account to take direct control:").formatted(Formatting.AQUA),
                this.width / 2,
                110,
                0xFFFFFF
            );
        }
    }
    
    private void loadAccounts() {
        if (accountController == null) {
            statusMessage = "§cDirect control not available - proxy mode disabled";
            isLoading = false;
            return;
        }
        
        isLoading = true;
        statusMessage = "Loading accounts...";
        
        // Clear existing account buttons
        accountButtons.forEach(this::remove);
        accountButtons.clear();
        
        accountController.getAvailableAccounts().thenAccept(accounts -> {
            this.availableAccounts = accounts;
            this.isLoading = false;
            
            if (accounts.isEmpty()) {
                statusMessage = "§eNo accounts available for direct control";
            } else {
                statusMessage = "";
                createAccountButtons();
            }
        }).exceptionally(throwable -> {
            this.isLoading = false;
            this.statusMessage = "§cFailed to load accounts: " + throwable.getMessage();
            return null;
        });
    }
    
    private void createAccountButtons() {
        // Run on main thread
        this.client.execute(() -> {
            int startY = 140;
            int buttonHeight = 25;
            int buttonWidth = 250;
            int spacing = 30;
            
            for (int i = 0; i < availableAccounts.size() && i < 10; i++) { // Limit to 10 accounts
                ProxyAccount account = availableAccounts.get(i);
                int yPos = startY + (i * spacing);
                
                // Skip if would go beyond screen
                if (yPos > this.height - 80) {
                    break;
                }
                
                ButtonWidget accountButton = ButtonWidget.builder(
                    createAccountButtonText(account),
                    (button) -> switchToAccount(account)
                )
                .dimensions(this.width / 2 - buttonWidth / 2, yPos, buttonWidth, buttonHeight)
                .build();
                
                this.addDrawableChild(accountButton);
                accountButtons.add(accountButton);
            }
        });
    }
    
    private Text createAccountButtonText(ProxyAccount account) {
        String buttonText = String.format("%s (%s)", account.getUsername(), account.getStatus());
        
        // Add health/food info if low
        if (account.getHealth() < 15 || account.getFood() < 15) {
            buttonText += String.format(" - ❤%d 🍖%d", account.getHealth(), account.getFood());
        }
        
        // Add current task if any
        if (account.getCurrentTask() != null && !account.getCurrentTask().isEmpty()) {
            buttonText += " - " + account.getCurrentTask();
        }
        
        // Color based on status
        Formatting color = switch (account.getStatus().toLowerCase()) {
            case "online" -> Formatting.GREEN;
            case "busy", "working" -> Formatting.YELLOW;
            case "idle" -> Formatting.AQUA;
            default -> Formatting.WHITE;
        };
        
        return Text.literal(buttonText).formatted(color);
    }
    
    private void switchToAccount(ProxyAccount account) {
        if (accountController == null) {
            return;
        }
        
        // Disable all buttons during switch
        setButtonsEnabled(false);
        statusMessage = "§eSwitching to account: " + account.getUsername() + "...";
        
        accountController.switchToAccount(account.getId()).thenAccept(success -> {
            this.client.execute(() -> {
                if (success) {
                    // Close screen and return to game
                    this.close();
                } else {
                    statusMessage = "§cFailed to switch to account: " + account.getUsername();
                    setButtonsEnabled(true);
                }
            });
        });
    }
    
    private void returnToOriginalAccount() {
        if (accountController == null || !accountController.isControllingProxyAccount()) {
            return;
        }
        
        setButtonsEnabled(false);
        statusMessage = "§eReturning to original account...";
        
        accountController.returnToOriginalAccount().thenAccept(success -> {
            this.client.execute(() -> {
                if (success) {
                    // Refresh the screen state
                    this.init(this.client, this.width, this.height);
                } else {
                    statusMessage = "§cFailed to return to original account";
                    setButtonsEnabled(true);
                }
            });
        });
    }
    
    private void setButtonsEnabled(boolean enabled) {
        accountButtons.forEach(button -> button.active = enabled);
        if (returnToOriginalButton != null) {
            returnToOriginalButton.active = enabled;
        }
        refreshButton.active = enabled;
    }
    
    @Override
    public boolean shouldPause() {
        return false; // Don't pause the game
    }
    
    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }
}
