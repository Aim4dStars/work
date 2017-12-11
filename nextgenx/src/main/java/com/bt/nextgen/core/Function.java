package com.bt.nextgen.core;

/**
 * Created by L062329 on 20/11/2015.
 */
/**
 *
 */
public interface Function<S, T, R> {
    R apply(S s, T t);
}
