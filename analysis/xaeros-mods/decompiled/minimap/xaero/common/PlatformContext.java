/*
 * Decompiled with CFR 0.152.
 */
package xaero.common;

import xaero.common.IXaeroMinimap;
import xaero.common.PlatformContextLoaderClientOnly;
import xaero.common.PlatformContextLoaderCommon;
import xaero.common.cache.BlockStateShortShapeCache;
import xaero.common.events.ClientEvents;
import xaero.common.events.CommonEvents;
import xaero.common.events.ModClientEvents;
import xaero.common.events.ModCommonEvents;
import xaero.common.message.MinimapMessageHandler;
import xaero.common.minimap.highlight.HighlighterRegistry;
import xaero.common.minimap.write.MinimapWriter;
import xaero.common.mods.SupportMods;
import xaero.hud.minimap.module.MinimapSession;

public abstract class PlatformContext {
    public abstract ClientEvents createClientEvents(IXaeroMinimap var1);

    public abstract CommonEvents createCommonEvents(IXaeroMinimap var1);

    public abstract MinimapMessageHandler createMessageHandler(IXaeroMinimap var1);

    public abstract PlatformContextLoaderClientOnly getLoaderClientOnly();

    public abstract PlatformContextLoaderCommon getLoaderCommon();

    public abstract ModClientEvents createModClientEvents(IXaeroMinimap var1);

    public abstract SupportMods createSupportMods(IXaeroMinimap var1);

    public abstract ModCommonEvents createModCommonEvents(IXaeroMinimap var1);

    public abstract MinimapWriter createMinimapWriter(IXaeroMinimap var1, MinimapSession var2, BlockStateShortShapeCache var3, HighlighterRegistry var4);

    public abstract String getModInfoVersion();
}

