package com.bt.nextgen.service.cmis.converter;

/**
 * Created by L062329 on 20/07/2015.
 */
public  interface  Converter<T, K> {

    T convert(K k);

    K convertTo(T t);
}
