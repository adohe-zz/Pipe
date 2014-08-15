package com.westudio.android.sdk.utils;

import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

public class Serializer {

    public <T extends SpecificRecordBase> ByteArrayOutputStream serializer(T request, Class<T> clazz) throws IOException {
        SpecificDatumWriter<T> writer = new SpecificDatumWriter<T>(clazz);
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        JsonEncoder encoder = EncoderFactory.get().jsonEncoder(request.getSchema(), os);
        writer.write(request, encoder);
        encoder.flush();

        return os;
    }

    public <T extends SpecificRecordBase> T deserialize(InputStream is, Class<T> clazz) throws IOException {
        T obj = null;

        try {
            Object o = clazz.newInstance();
            Decoder decoder = DecoderFactory.get().jsonDecoder((Schema)clazz.getDeclaredMethod("getSchema", null).invoke(o, null), is);
            SpecificDatumReader<T> reader = new SpecificDatumReader<T>(clazz);

            reader.read((T)o, decoder);
            return (T)o;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
        }

        return null;
    }
}
