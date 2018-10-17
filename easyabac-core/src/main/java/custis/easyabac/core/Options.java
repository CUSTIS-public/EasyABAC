package custis.easyabac.core;

public class Options {
    private final boolean optimizeRequest;

    public static Options getDefaultOptions() {
        return new OptionsBuilder()
                .optimizeRequest(true)
                .build();
    }

    public Options(boolean optimizeRequest) {
        this.optimizeRequest = optimizeRequest;
    }

    public boolean isOptimizeRequest() {
        return optimizeRequest;
    }

    public static class OptionsBuilder {
        private boolean optimizeRequest;

        public OptionsBuilder optimizeRequest(boolean optimizeRequest) {
            this.optimizeRequest = optimizeRequest;
            return this;
        }

        public Options build() {
            return new Options(optimizeRequest);
        }
    }

}
