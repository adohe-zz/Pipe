package com.westudio.android.sdk.uitls;

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

    public <T extends SpecificRecordBase> ByteArrayOutputStream serializer(T request) throws IOException {
        SpecificDatumWriter<T> writer = new SpecificDatumWriter<T>();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        JsonEncoder encoder = EncoderFactory.get().jsonEncoder(request.getSchema(), os);
        writer.write(request, encoder);
        encoder.flush();

        return os;
    }

    public <T extends SpecificRecordBase> T deserialize(InputStream is, Class<T> clazz) throws IOException {
        T obj = null;

        try {
            Decoder decoder = DecoderFactory.get().jsonDecoder((Schema)clazz.getDeclaredMethod("getSchema").invoke(null), is);
            SpecificDatumReader<T> reader = new SpecificDatumReader<T>();

            reader.read(obj, decoder);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return obj;
    }
}
