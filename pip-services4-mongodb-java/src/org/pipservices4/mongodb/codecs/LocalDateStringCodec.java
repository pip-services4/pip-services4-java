package org.pipservices4.mongodb.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.LocalDate;

public class LocalDateStringCodec implements Codec<LocalDate> {

	@Override
	public Class<LocalDate> getEncoderClass() {
		return LocalDate.class;
	}

	@Override
	public void encode(BsonWriter writer, LocalDate value,
		EncoderContext encoderContext) {
		writer.writeString(value.toString());
	}

	@Override
	public LocalDate decode(BsonReader reader, DecoderContext decoderContext) {
		return LocalDate.parse(reader.readString());
	}

}