/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.config;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import xaero.common.IXaeroMinimap;
import xaero.common.config.CommonConfig;
import xaero.common.config.CommonConfigIO;
import xaero.common.platform.Services;

public class CommonConfigInit {
    public void init(IXaeroMinimap modMain, String configFileName) {
        Path oldConfigPath;
        Path configDestinationPath = Services.PLATFORM.getConfigDir();
        Path configPath = configDestinationPath.resolve(configFileName);
        if (Services.PLATFORM.isDedicatedServer() && !Files.exists(configPath, new LinkOption[0]) && Files.exists(oldConfigPath = Services.PLATFORM.getGameDir().resolve(configFileName), new LinkOption[0])) {
            configPath = oldConfigPath;
        }
        CommonConfigIO io = new CommonConfigIO(configPath);
        modMain.setCommonConfigIO(io);
        if (Files.exists(configPath, new LinkOption[0])) {
            modMain.setCommonConfig(io.load());
        } else {
            modMain.setCommonConfig(CommonConfig.Builder.begin().build());
        }
        io.save(modMain.getCommonConfig());
    }
}

