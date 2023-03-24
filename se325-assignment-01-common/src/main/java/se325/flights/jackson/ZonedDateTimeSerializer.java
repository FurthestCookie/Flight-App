package se325.flights.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeSerializer extends StdSerializer<ZonedDateTime> {

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public ZonedDateTimeSerializer() {
        this(null);
    }

    public ZonedDateTimeSerializer(Class<ZonedDateTime> clazz) {
        super(clazz);
    }

    @Override
    public void serialize(ZonedDateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(dateTime.format(FORMATTER));
    }
}
