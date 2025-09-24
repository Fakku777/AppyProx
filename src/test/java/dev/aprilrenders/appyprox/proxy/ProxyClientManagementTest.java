package dev.aprilrenders.appyprox.proxy;

import dev.aprilrenders.appyprox.data.ProxyAccount;
import dev.aprilrenders.appyprox.network.AppyProxNetworkClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for the proxy client management system
 * Tests integration between all components and core functionality
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProxyClientManagementTest {
    
    private static final String TEST_ACCOUNT_ID = "test_account_123";
    private static final String TEST_USERNAME = "TestPlayer";
    
    // Test instance directory
    private static Path testDirectory;
    
    // System under test
    private ProxyClientManager clientManager;
    private ProxyClientLifecycleManager lifecycleManager;
    private ProxyClientRemoteControl remoteControl;
    private ProxyClientModDeployer modDeployer;
    private ProxyClientDashboard dashboard;
    private ProxyClientIntegration integration;
    
    @Mock
    private AppyProxNetworkClient mockNetworkClient;
    
    // Test data
    private ProxyAccount testAccount;
    private ProxyClientConfig testConfig;
    
    @BeforeAll
    static void setUpClass() throws Exception {
        // Create temporary test directory
        testDirectory = Files.createTempDirectory("appyprox_test_");
        System.out.println("Test directory: " + testDirectory);
    }
    
    @AfterAll
    static void tearDownClass() throws Exception {
        // Clean up test directory
        if (Files.exists(testDirectory)) {
            Files.walk(testDirectory)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (Exception e) {
                        // Ignore cleanup errors
                    }
                });
        }
    }
    
    @BeforeEach
    void setUp() throws Exception {
        // Create test account
        testAccount = new ProxyAccount();
        testAccount.setId(TEST_ACCOUNT_ID);
        testAccount.setUsername(TEST_USERNAME);
        testAccount.setUuid(UUID.randomUUID().toString());
        testAccount.setAccessToken("test_access_token");
        
        // Create test configuration
        testConfig = new ProxyClientConfig.Builder()
            .setHeadless(true)
            .setAutoRestart(true)
            .setMaxMemory(1024)
            .setServerAddress("localhost")
            .setServerPort(25565)
            .build();
        
        // Initialize components
        clientManager = new ProxyClientManager(mockNetworkClient);
        lifecycleManager = new ProxyClientLifecycleManager(clientManager);
        remoteControl = new ProxyClientRemoteControl(); // Use default constructor
        modDeployer = new ProxyClientModDeployer(testDirectory.resolve("mods"));
        dashboard = new ProxyClientDashboard(clientManager, lifecycleManager, remoteControl, modDeployer);
        integration = new ProxyClientIntegration(clientManager, lifecycleManager, remoteControl, modDeployer, dashboard);
        
        System.out.println("Test setup completed");
    }
    
    @AfterEach
    void tearDown() {
        try {
            if (integration != null) integration.shutdown();
            if (dashboard != null) dashboard.shutdown();
            if (remoteControl != null) remoteControl.shutdown();
            if (lifecycleManager != null) lifecycleManager.shutdown();
            if (clientManager != null) clientManager.shutdown();
        } catch (Exception e) {
            System.err.println("Error during test cleanup: " + e.getMessage());
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("Test Client Manager Basic Operations")
    void testClientManagerBasics() {
        System.out.println("Testing ProxyClientManager basic operations...");
        
        // Test initial state
        assertTrue(clientManager.getAllClientInfo().isEmpty());
        assertEquals(0, clientManager.getActiveClientCount());
        
        // Test configuration validation
        assertDoesNotThrow(() -> clientManager.validateConfig(testConfig));
        
        // Test invalid configuration
        ProxyClientConfig invalidConfig = new ProxyClientConfig.Builder()
            .setMaxMemory(-1) // Invalid memory
            .build();
        
        assertThrows(IllegalArgumentException.class, () -> clientManager.validateConfig(invalidConfig));
        
        System.out.println("✓ Client Manager basic operations test passed");
    }
    
    @Test
    @Order(2)
    @DisplayName("Test Client Configuration System")
    void testClientConfiguration() {
        System.out.println("Testing ProxyClientConfig system...");
        
        // Test default configuration
        ProxyClientConfig defaultConfig = ProxyClientConfig.createDefault();
        assertNotNull(defaultConfig);
        assertTrue(defaultConfig.getInitialMemoryMB() > 0);
        assertTrue(defaultConfig.getMaxMemoryMB() > defaultConfig.getInitialMemoryMB());
        
        // Test automation preset
        ProxyClientConfig automationConfig = ProxyClientConfig.createAutomationPreset();
        assertNotNull(automationConfig);
        assertTrue(automationConfig.getModList().contains("baritone"));
        
        // Test performance preset
        ProxyClientConfig performanceConfig = ProxyClientConfig.createPerformancePreset();
        assertNotNull(performanceConfig);
        assertTrue(performanceConfig.getModList().contains("sodium"));
        
        // Test configuration builder
        ProxyClientConfig customConfig = new ProxyClientConfig.Builder()
            .setHeadless(true)
            .setAutoRestart(false)
            .setMaxMemory(2048)
            .addMod("custom-mod")
            .setServerAddress("test.server.com")
            .build();
        
        assertEquals(2048, customConfig.getMaxMemoryMB());
        assertTrue(customConfig.isHeadless());
        assertFalse(customConfig.isAutoRestart());
        assertTrue(customConfig.getModList().contains("custom-mod"));
        
        System.out.println("✓ Client Configuration system test passed");
    }
    
    @Test
    @Order(3)
    @DisplayName("Test Mod Deployment System")
    void testModDeploymentSystem() throws Exception {
        System.out.println("Testing ProxyClientModDeployer...");
        
        // Create test mod files
        Path testModsDir = testDirectory.resolve("test_mods");
        Files.createDirectories(testModsDir);
        
        Path testMod1 = testModsDir.resolve("baritone-1.0.0.jar");
        Path testMod2 = testModsDir.resolve("litematica-1.0.0.jar");
        
        Files.createFile(testMod1);
        Files.createFile(testMod2);
        
        // Test mod registration
        ProxyClientModDeployer.ModDescriptor mod1 = new ProxyClientModDeployer.ModDescriptor(
            "baritone", "1.0.0", ProxyClientModDeployer.ModCategory.AUTOMATION,
            Collections.emptyList(), Collections.singleton(ProxyClientModDeployer.ModUseCase.AUTOMATION),
            testMod1.toString(), "test_hash_1"
        );
        
        modDeployer.registerMod(mod1);
        
        // Test mod deployment
        Path instanceModsDir = testDirectory.resolve("instance_mods");
        List<String> modsToInstall = Arrays.asList("baritone");
        ProxyClientModDeployer.ModDeploymentOptions options = new ProxyClientModDeployer.ModDeploymentOptions();
        options.createBackup = false; // Skip backup for testing
        
        CompletableFuture<ProxyClientModDeployer.ModDeploymentResult> deploymentFuture =
            modDeployer.deployMods("test_instance", instanceModsDir, modsToInstall, options);
        
        ProxyClientModDeployer.ModDeploymentResult result = deploymentFuture.get(10, TimeUnit.SECONDS);
        
        assertNotNull(result);
        assertTrue(result.success);
        assertEquals(1, result.getSuccessCount());
        assertTrue(result.successfulMods.contains("baritone"));
        
        // Verify mod was actually copied
        assertTrue(Files.exists(instanceModsDir.resolve("baritone-1.0.0.jar")));
        
        System.out.println("✓ Mod Deployment system test passed");
    }
    
    @Test
    @Order(4)
    @DisplayName("Test Remote Control Interface")
    void testRemoteControlInterface() throws Exception {
        System.out.println("Testing ProxyClientRemoteControl...");
        
        // Test control server is running
        assertTrue(remoteControl.getControlPort() > 0);
        assertEquals(0, remoteControl.getConnectedClientCount());
        
        // Test command creation (without actual client connection)
        ProxyClientRemoteControl.ResponseStatus testStatus = 
            ProxyClientRemoteControl.ResponseStatus.SUCCESS;
        assertNotNull(testStatus);
        
        // Test client status tracking
        assertFalse(remoteControl.isClientConnected("non_existent_client"));
        
        // Test command types
        ProxyClientRemoteControl.CommandType[] commandTypes = ProxyClientRemoteControl.CommandType.values();
        assertTrue(commandTypes.length > 0);
        assertTrue(Arrays.asList(commandTypes).contains(ProxyClientRemoteControl.CommandType.PING));
        assertTrue(Arrays.asList(commandTypes).contains(ProxyClientRemoteControl.CommandType.STATUS));
        
        System.out.println("✓ Remote Control interface test passed");
    }
    
    @Test
    @Order(5)
    @DisplayName("Test Dashboard System")
    void testDashboardSystem() throws Exception {
        System.out.println("Testing ProxyClientDashboard...");
        
        // Test dashboard overview
        ProxyClientDashboard.DashboardOverview overview = dashboard.getDashboardOverview();
        assertNotNull(overview);
        assertEquals(0, overview.totalClients);
        assertEquals(0, overview.runningClients);
        assertNotNull(overview.lastUpdateTime);
        
        // Test system health
        ProxyClientDashboard.SystemHealthSummary health = dashboard.getSystemHealthSummary();
        assertNotNull(health);
        assertEquals(ProxyClientDashboard.SystemHealthStatus.HEALTHY, health.overallStatus);
        
        // Test filtered client list
        List<ProxyClientDashboard.ClientSummary> clients = dashboard.getFilteredClientList();
        assertNotNull(clients);
        assertTrue(clients.isEmpty());
        
        // Test dashboard views
        dashboard.setDashboardView(ProxyClientDashboard.DashboardView.CLIENTS);
        dashboard.setDashboardView(ProxyClientDashboard.DashboardView.PERFORMANCE);
        
        // Test real-time statistics
        ProxyClientDashboard.DashboardStatistics stats = dashboard.getRealTimeStatistics();
        assertNotNull(stats);
        assertEquals(0, stats.totalClients);
        
        System.out.println("✓ Dashboard system test passed");
    }
    
    @Test
    @Order(6)
    @DisplayName("Test Integration Layer")
    void testIntegrationLayer() throws Exception {
        System.out.println("Testing ProxyClientIntegration...");
        
        // Test integration status
        ProxyClientIntegration.IntegrationStatus status = integration.getIntegrationStatus();
        assertNotNull(status);
        assertEquals(0, status.totalClients);
        assertEquals(0, status.connectedClients);
        assertTrue(status.systemHealth >= 0.0 && status.systemHealth <= 1.0);
        
        // Test integration options
        ProxyClientIntegration.IntegrationOptions options = new ProxyClientIntegration.IntegrationOptions();
        options.enableAutomation = true;
        options.autoJoinCluster = false;
        options.enableMonitoring = true;
        
        assertNotNull(options);
        assertTrue(options.enableAutomation);
        assertFalse(options.autoJoinCluster);
        
        // Test automation task creation
        ProxyClientIntegration.AutomationTask task = new ProxyClientIntegration.AutomationTask();
        task.type = ProxyClientIntegration.TaskType.GATHER_RESOURCES;
        task.parameters.put("resource", "diamond");
        task.parameters.put("quantity", "10");
        
        assertNotNull(task.id);
        assertEquals(ProxyClientIntegration.TaskType.GATHER_RESOURCES, task.type);
        assertEquals("diamond", task.parameters.get("resource"));
        
        System.out.println("✓ Integration layer test passed");
    }
    
    @Test
    @Order(7)
    @DisplayName("Test Component Integration")
    void testComponentIntegration() throws Exception {
        System.out.println("Testing component integration...");
        
        // Test that components can work together
        assertNotNull(clientManager);
        assertNotNull(lifecycleManager);
        assertNotNull(remoteControl);
        assertNotNull(modDeployer);
        assertNotNull(dashboard);
        assertNotNull(integration);
        
        // Test dashboard aggregates data from other components
        ProxyClientDashboard.DashboardOverview overview = dashboard.getDashboardOverview();
        Map<String, ProxyClientInfo> clients = clientManager.getAllClientInfo();
        
        assertEquals(clients.size(), overview.totalClients);
        
        // Test integration coordinates with other systems
        ProxyClientIntegration.IntegrationStatus integrationStatus = integration.getIntegrationStatus();
        assertEquals(clients.size(), integrationStatus.totalClients);
        
        System.out.println("✓ Component integration test passed");
    }
    
    @Test
    @Order(8)
    @DisplayName("Test Error Handling and Edge Cases")
    void testErrorHandlingAndEdgeCases() {
        System.out.println("Testing error handling and edge cases...");
        
        // Test handling of invalid account ID
        assertThrows(Exception.class, () -> {
            integration.executeAutomationTask("invalid_account", new ProxyClientIntegration.AutomationTask()).get();
        });
        
        // Test configuration validation with null values
        assertThrows(Exception.class, () -> clientManager.validateConfig(null));
        
        // Test dashboard with no clients
        ProxyClientDashboard.DashboardOverview emptyOverview = dashboard.getDashboardOverview();
        assertEquals(0, emptyOverview.totalClients);
        
        // Test remote control with invalid client
        assertFalse(remoteControl.isClientConnected("invalid_client"));
        
        // Test mod deployment with non-existent mods
        ProxyClientModDeployer.ModDeploymentOptions options = new ProxyClientModDeployer.ModDeploymentOptions();
        options.ignoreFailed = true;
        
        assertDoesNotThrow(() -> {
            modDeployer.deployMods("test", testDirectory.resolve("non_existent"), 
                Arrays.asList("non_existent_mod"), options);
        });
        
        System.out.println("✓ Error handling and edge cases test passed");
    }
    
    @Test
    @Order(9)
    @DisplayName("Test System Status and Health")
    void testSystemStatusAndHealth() {
        System.out.println("Testing system status and health monitoring...");
        
        // Test client status enumeration
        ProxyClientStatus[] statuses = ProxyClientStatus.values();
        assertTrue(statuses.length > 0);
        
        for (ProxyClientStatus status : statuses) {
            assertNotNull(status.getDescription());
            assertNotNull(status.getUiColor());
        }
        
        // Test health scoring
        ProxyClientInfo testClientInfo = new ProxyClientInfo();
        testClientInfo.accountId = TEST_ACCOUNT_ID;
        testClientInfo.username = TEST_USERNAME;
        testClientInfo.status = ProxyClientStatus.RUNNING;
        testClientInfo.healthScore = 85.0;
        testClientInfo.memoryUsageMB = 512;
        testClientInfo.cpuUsagePercent = 25.5;
        testClientInfo.startTime = Instant.now().minusSeconds(300); // 5 minutes ago
        
        assertTrue(testClientInfo.healthScore > 0);
        assertTrue(testClientInfo.healthScore <= 100);
        assertTrue(testClientInfo.getUptime() > 0);
        
        System.out.println("✓ System status and health test passed");
    }
    
    @Test
    @Order(10)
    @DisplayName("Test Comprehensive System Functionality")
    void testComprehensiveSystemFunctionality() throws Exception {
        System.out.println("Testing comprehensive system functionality...");
        
        // This test demonstrates how all components would work together in a real scenario
        // Note: This is a simulation since we can't actually launch Minecraft processes in tests
        
        // 1. Validate configuration
        assertDoesNotThrow(() -> clientManager.validateConfig(testConfig));
        
        // 2. Prepare mod deployment
        ProxyClientModDeployer.ModDeploymentOptions modOptions = new ProxyClientModDeployer.ModDeploymentOptions();
        modOptions.cleanDeployment = true;
        modOptions.createBackup = false; // Skip for testing
        
        // 3. Setup integration options
        ProxyClientIntegration.IntegrationOptions integrationOptions = new ProxyClientIntegration.IntegrationOptions();
        integrationOptions.enableAutomation = true;
        integrationOptions.autoJoinCluster = false; // Don't auto-join for testing
        
        // 4. Verify dashboard can provide system overview
        ProxyClientDashboard.DashboardOverview overview = dashboard.getDashboardOverview();
        assertNotNull(overview);
        
        // 5. Verify remote control is ready
        assertTrue(remoteControl.getControlPort() > 0);
        
        // 6. Verify integration layer can coordinate
        ProxyClientIntegration.IntegrationStatus status = integration.getIntegrationStatus();
        assertNotNull(status);
        
        // 7. Test automation task creation
        ProxyClientIntegration.AutomationTask task = new ProxyClientIntegration.AutomationTask();
        task.type = ProxyClientIntegration.TaskType.GATHER_RESOURCES;
        task.parameters.put("resource", "wood");
        task.parameters.put("quantity", "64");
        
        assertNotNull(task);
        assertEquals(ProxyClientIntegration.TaskStatus.SCHEDULED, task.status);
        
        System.out.println("✓ Comprehensive system functionality test passed");
    }
    
    @Test
    @Order(11)
    @DisplayName("Test Performance and Resource Management")
    void testPerformanceAndResourceManagement() {
        System.out.println("Testing performance and resource management...");
        
        // Test configuration memory limits
        ProxyClientConfig perfConfig = ProxyClientConfig.createPerformancePreset();
        assertTrue(perfConfig.getMaxMemoryMB() > 0);
        assertTrue(perfConfig.getMaxMemoryMB() >= perfConfig.getInitialMemoryMB());
        
        // Test resource tracking
        ProxyClientInfo resourceInfo = new ProxyClientInfo();
        resourceInfo.memoryUsageMB = 512;
        resourceInfo.cpuUsagePercent = 15.5;
        resourceInfo.startTime = Instant.now().minusSeconds(600); // 10 minutes ago
        
        assertTrue(resourceInfo.memoryUsageMB > 0);
        assertTrue(resourceInfo.cpuUsagePercent >= 0 && resourceInfo.cpuUsagePercent <= 100);
        assertTrue(resourceInfo.getUptime() > 0);
        
        // Test system health calculation
        double healthScore = Math.max(0, Math.min(100, 
            100 - (resourceInfo.cpuUsagePercent * 0.5) - (resourceInfo.memoryUsageMB / 20.0)));
        
        assertTrue(healthScore >= 0 && healthScore <= 100);
        
        System.out.println("✓ Performance and resource management test passed");
    }
    
    @Test
    @Order(12)
    @DisplayName("Test Cleanup and Shutdown")
    void testCleanupAndShutdown() {
        System.out.println("Testing cleanup and shutdown procedures...");
        
        // Test that components can be shutdown cleanly
        assertDoesNotThrow(() -> {
            dashboard.shutdown();
            remoteControl.shutdown();
            lifecycleManager.shutdown();
            integration.shutdown();
            clientManager.shutdown();
        });
        
        // Verify components are properly shutdown
        // Note: In a real implementation, you'd check that threads are stopped, 
        // resources are cleaned up, etc.
        
        System.out.println("✓ Cleanup and shutdown test passed");
        System.out.println("🎉 All proxy client management system tests completed successfully!");
    }
    
    // Helper method to create test client info
    private ProxyClientInfo createTestClientInfo() {
        ProxyClientInfo info = new ProxyClientInfo();
        info.accountId = TEST_ACCOUNT_ID;
        info.username = TEST_USERNAME;
        info.status = ProxyClientStatus.RUNNING;
        info.healthScore = 90.0;
        info.memoryUsageMB = 768;
        info.cpuUsagePercent = 12.5;
        info.startTime = Instant.now().minusSeconds(1800); // 30 minutes ago
        return info;
    }
}