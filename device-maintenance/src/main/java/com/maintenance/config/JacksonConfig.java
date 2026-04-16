package com.maintenance.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Configuration
public class JacksonConfig {

    private static final List<DateTimeFormatter> SUPPORTED_DATE_TIME_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ISO_LOCAL_DATE
    );

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.deserializerByType(
                LocalDateTime.class,
                new MultiFormatLocalDateTimeDeserializer()
        );
    }

    static class MultiFormatLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            String rawValue = parser.getValueAsString();
            if (rawValue == null || rawValue.isBlank()) {
                return null;
            }

            String value = rawValue.trim();
            for (DateTimeFormatter formatter : SUPPORTED_DATE_TIME_FORMATS) {
                try {
                    if (formatter == DateTimeFormatter.ISO_LOCAL_DATE) {
                        return LocalDate.parse(value, formatter).atStartOfDay();
                    }
                    return LocalDateTime.parse(value, formatter);
                } catch (DateTimeParseException ignored) {
                    // Try the next supported format.
                }
            }

            throw InvalidFormatException.from(
                    parser,
                    "Invalid LocalDateTime format",
                    value,
                    LocalDateTime.class
            );
        }
    }
}
