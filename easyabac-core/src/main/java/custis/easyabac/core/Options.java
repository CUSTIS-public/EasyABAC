package custis.easyabac.core;

public class Options {
    private final boolean optimizeRequest;
    private final boolean enableTrace;
    private final boolean enableAudit;

    public static Options getDefaultOptions() {
        return new OptionsBuilder()
                .optimizeRequest(true)
                .enableTrace(false)
                .enableAudit(false)
                .build();
    }

    public Options(boolean optimizeRequest, boolean enableTrace, boolean enableAudit) {
        this.optimizeRequest = optimizeRequest;
        this.enableTrace = enableTrace;
        this.enableAudit = enableAudit;
    }

    public boolean isOptimizeRequest() {
        return optimizeRequest;
    }

    public boolean isEnableTrace() {
        return enableTrace;
    }

    public boolean isEnableAudit() {
        return enableAudit;
    }

    public static class OptionsBuilder {
        private boolean optimizeRequest;
        private boolean enableTrace;
        private boolean enableAudit;

        public OptionsBuilder optimizeRequest(boolean optimizeRequest) {
            this.optimizeRequest = optimizeRequest;
            return this;
        }

        public OptionsBuilder enableTrace(boolean enableTrace) {
            this.enableTrace = enableTrace;
            return this;
        }

        public OptionsBuilder enableAudit(boolean enableAudit) {
            this.enableAudit = enableAudit;
            return this;
        }

        public Options build() {
            return new Options(optimizeRequest, enableTrace, enableAudit);
        }
    }

}
