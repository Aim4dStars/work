package com.bt.nextgen.core.cache;

import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Upul Doluweera on 6/03/2016.
 */
public class DiskCacheSerializer<T> {

    private Class<T> type;

    public DiskCacheSerializer(Class<T> type) {
        this.type = type;
    }

    static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    public T readObjectFromFile(String file) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(file);
        FSTObjectInput in = conf.getObjectInput(fileInputStream);
        T result = (T) in.readObject(type);
        fileInputStream.close();
        return result;
    }

    public void writeObjectToFile(String filePath, T toWrite) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        FSTObjectOutput out = conf.getObjectOutput(fileOutputStream);
        out.writeObject(toWrite, type);
        out.flush();
        fileOutputStream.close();
    }

}
