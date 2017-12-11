package com.bt.nextgen.service.json;

public interface JsonStreamStrategy {
    String processName(String name);

    String processValue(String value);

    String processNumber(String number);

}
