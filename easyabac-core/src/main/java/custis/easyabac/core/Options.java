package custis.easyabac.core;

public class Options {
    private final boolean enableOptimization;
    private final int optimizationThreshold;
    private final boolean enableTrace;
    private final boolean enableAudit;


    public static Options getDefaultOptions() {
        return new OptionsBuilder()
                .enableOptimization(true)
                .enableTrace(false)
                .enableAudit(false)
                .optimizationThreshold(10)
                .build();
    }

    public Options(boolean enableOptimization, int optimizationThreshold, boolean enableTrace, boolean enableAudit) {
        this.enableOptimization = enableOptimization;
        this.enableTrace = enableTrace;
        this.enableAudit = enableAudit;
        this.optimizationThreshold = optimizationThreshold;
    }

    public boolean isEnableOptimization() {
        return enableOptimization;
    }

    public boolean isEnableTrace() {
        return enableTrace;
    }

    public boolean isEnableAudit() {
        return enableAudit;
    }

    public int getOptimizationThreshold() {
        return optimizationThreshold;
    }

    public static class OptionsBuilder {
        private boolean enableOptimization;
        private boolean enableTrace;
        private boolean enableAudit;
        private int optimizationThreshold;

        public OptionsBuilder enableOptimization(boolean enableOptimization) {
            this.enableOptimization = enableOptimization;
            return this;
        }

        public OptionsBuilder optimizationThreshold(int optimizationThreshold) {
            if (optimizationThreshold < 0) {
                optimizationThreshold = 0;
            }
            this.optimizationThreshold = optimizationThreshold;
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
            return new Options(enableOptimization, optimizationThreshold, enableTrace, enableAudit);
        }
    }

}
