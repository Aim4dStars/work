package com.bt.nextgen.config;

@SuppressWarnings("squid:S2094")
public class JsonViews {
    private JsonViews() {
        super();
    };

    public static class Write {
    }

    public static class Read extends Write {
    }

    public static class Restricted extends Read {
    }
}
