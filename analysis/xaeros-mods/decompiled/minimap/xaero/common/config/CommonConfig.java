/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.config;

public final class CommonConfig {
    public final boolean registerStatusEffects;
    public boolean allowCaveModeOnServer;
    public boolean allowNetherCaveModeOnServer;
    public boolean allowRadarOnServer;
    public boolean everyoneTracksEveryone;

    private CommonConfig(boolean registerStatusEffects) {
        this.registerStatusEffects = registerStatusEffects;
    }

    public static final class Builder {
        public boolean registerStatusEffects;
        public boolean allowCaveModeOnServer;
        public boolean allowNetherCaveModeOnServer;
        public boolean allowRadarOnServer;
        public boolean everyoneTracksEveryone;

        private Builder() {
        }

        public Builder setDefault() {
            this.setRegisterStatusEffects(true);
            this.setAllowCaveModeOnServer(true);
            this.setAllowNetherCaveModeOnServer(true);
            this.setAllowRadarOnServer(true);
            return this;
        }

        public Builder setRegisterStatusEffects(boolean registerStatusEffects) {
            this.registerStatusEffects = registerStatusEffects;
            return this;
        }

        public Builder setAllowCaveModeOnServer(boolean allowCaveModeOnServer) {
            this.allowCaveModeOnServer = allowCaveModeOnServer;
            return this;
        }

        public Builder setAllowNetherCaveModeOnServer(boolean allowNetherCaveModeOnServer) {
            this.allowNetherCaveModeOnServer = allowNetherCaveModeOnServer;
            return this;
        }

        public Builder setAllowRadarOnServer(boolean allowRadarOnServer) {
            this.allowRadarOnServer = allowRadarOnServer;
            return this;
        }

        public Builder setEveryoneTracksEveryone(boolean everyoneTracksEveryone) {
            this.everyoneTracksEveryone = everyoneTracksEveryone;
            return this;
        }

        public CommonConfig build() {
            CommonConfig result = new CommonConfig(this.registerStatusEffects);
            result.allowCaveModeOnServer = this.allowCaveModeOnServer;
            result.allowNetherCaveModeOnServer = this.allowNetherCaveModeOnServer;
            result.allowRadarOnServer = this.allowRadarOnServer;
            result.everyoneTracksEveryone = this.everyoneTracksEveryone;
            return result;
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

