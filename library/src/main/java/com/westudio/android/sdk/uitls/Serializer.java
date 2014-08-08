package com.westudio.android.sdk.uitls;

import org.apache.avro.SchemaBuilder;
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

public class Serializer {

    public <T extends SpecificRecordBase> ByteArrayOutputStream serializer(T request) throws IOException {
        SpecificDatumWriter<T> writer = new SpecificDatumWriter<T>();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        JsonEncoder encoder = EncoderFactory.get().jsonEncoder(request.getSchema(), os);
        writer.write(request, encoder);
        encoder.flush();

        return os;
    }

    public <T extends SpecificRecordBase> T deserialize(InputStream is) throws IOException {
        T obj = null;

        Decoder decoder = DecoderFactory.get().jsonDecoder(, is);
        SpecificDatumReader<T> reader = new SpecificDatumReader<T>();

        reader.read(obj, decoder);

        return obj;
    }
}
