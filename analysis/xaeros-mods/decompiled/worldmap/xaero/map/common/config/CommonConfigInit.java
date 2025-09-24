/*
 * Decompiled with CFR 0.152.
 */
package xaero.map.common.config;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import xaero.map.WorldMap;
import xaero.map.common.config.CommonConfig;
import xaero.map.common.config.CommonConfigIO;
import xaero.map.platform.Services;

public class CommonConfigInit {
    public void init(String configFileName) {
        CommonConfigIO io;
        Path oldConfigPath;
        Path configDestinationPath = Services.PLATFORM.getConfigDir();
        Path configPath = configDestinationPath.resolve(configFileName);
        if (Services.PLATFORM.isDedicatedServer() && !Files.exists(configPath, new LinkOption[0]) && Files.exists(oldConfigPath = Services.PLATFORM.getGameDir().resolve(configFileName), new LinkOption[0])) {
            configPath = oldConfigPath;
        }
        WorldMap.commonConfigIO = io = new CommonConfigIO(configPath);
        WorldMap.commonConfig = Files.exists(configPath, new LinkOption[0]) ? io.load() : CommonConfig.Builder.begin().build();
        io.save(WorldMap.commonConfig);
    }
}

