/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.common.config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import xaero.map.WorldMap;
import xaero.map.common.config.CommonConfig;

public class CommonConfigIO {
    private final Path configFilePath;

    public CommonConfigIO(Path configFilePath) {
        this.configFilePath = configFilePath;
    }

    public void save(CommonConfig config) {
        try {
            Path parentFolder = this.configFilePath.getParent();
            if (parentFolder != null) {
                Files.createDirectories(parentFolder, new FileAttribute[0]);
            }
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
            return;
        }
        try (BufferedOutputStream bufferedOutput = new BufferedOutputStream(new FileOutputStream(this.configFilePath.toFile()));
             PrintWriter writer = new PrintWriter(bufferedOutput);){
            this.write(config, writer);
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public CommonConfig load() {
        try (BufferedInputStream bufferedOutput = new BufferedInputStream(new FileInputStream(this.configFilePath.toFile()));){
            CommonConfig commonConfig;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(bufferedOutput));){
                String line;
                CommonConfig.Builder builder = CommonConfig.Builder.begin();
                while ((line = reader.readLine()) != null) {
                    this.readLine(builder, line.split(":"));
                }
                commonConfig = builder.build();
            }
            return commonConfig;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void write(CommonConfig config, PrintWriter writer) {
        writer.println("allowCaveModeOnServer:" + config.allowCaveModeOnServer);
        writer.println("allowNetherCaveModeOnServer:" + config.allowNetherCaveModeOnServer);
        writer.println("registerStatusEffects:" + config.registerStatusEffects);
        writer.println("everyoneTracksEveryone:" + config.everyoneTracksEveryone);
    }

    private boolean readLine(CommonConfig.Builder configBuilder, String[] args) {
        if (args[0].equals("allowCaveModeOnServer")) {
            configBuilder.setAllowCaveModeOnServer(args[1].equals("true"));
            return true;
        }
        if (args[0].equals("allowNetherCaveModeOnServer")) {
            configBuilder.setAllowNetherCaveModeOnServer(args[1].equals("true"));
            return true;
        }
        if (args[0].equals("registerStatusEffects")) {
            configBuilder.setRegisterStatusEffects(args[1].equals("true"));
            return true;
        }
        if (args[0].equals("everyoneTracksEveryone")) {
            configBuilder.setEveryoneTracksEveryone(args[1].equals("true"));
            return true;
        }
        return false;
    }
}

