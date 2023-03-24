package se325.flights.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeDeserializer extends StdDeserializer<ZonedDateTime> {

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public ZonedDateTimeDeserializer() {
        this(null);
    }

    public ZonedDateTimeDeserializer(Class<ZonedDateTime> clazz) {
        super(clazz);
    }

    @Override
    public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return ZonedDateTime.parse(jsonParser.getText(), FORMATTER);
    }
}
