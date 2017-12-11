package com.bt.nextgen.service.json;

public class DefaultStreamStrategy implements JsonStreamStrategy {
    @Override
    public String processName(String name) {
        return toCamelCase(name);
    }

    @Override
    public String processValue(String value) {
        return value;
    }

    @Override
    public String processNumber(String number) {
        return number;
    }
    
    private String toCamelCase(String name){
        StringBuilder stringBuilder = new StringBuilder(name);
        for (int i = 0; i < stringBuilder.length(); i++) {
            if (stringBuilder.charAt(i) == '_') {
               stringBuilder.deleteCharAt(i);
               stringBuilder.replace(i, i+1, String.valueOf(Character.toUpperCase(stringBuilder.charAt(i))));
            }
        }
        return stringBuilder.toString();
     }

}
