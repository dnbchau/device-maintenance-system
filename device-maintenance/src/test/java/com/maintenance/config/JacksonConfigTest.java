package com.maintenance.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maintenance.dto.request.RepairHistoryRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@Import(JacksonConfig.class)
class JacksonConfigTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldDeserializeRepairDateWithIsoSeconds() throws Exception {
        String json = """
                {
                  "title": "Sua may lanh",
                  "repairDate": "2024-01-22T10:00:00",
                  "cost": 800000,
                  "deviceId": 1
                }
                """;

        RepairHistoryRequest request = objectMapper.readValue(json, RepairHistoryRequest.class);

        assertThat(request.getRepairDate()).isEqualTo(LocalDateTime.of(2024, 1, 22, 10, 0, 0));
    }

    @Test
    void shouldDeserializeRepairDateWithoutSeconds() throws Exception {
        String json = """
                {
                  "title": "Sua may lanh",
                  "repairDate": "2024-01-22T10:00",
                  "cost": 800000,
                  "deviceId": 1
                }
                """;

        RepairHistoryRequest request = objectMapper.readValue(json, RepairHistoryRequest.class);

        assertThat(request.getRepairDate()).isEqualTo(LocalDateTime.of(2024, 1, 22, 10, 0, 0));
    }
}
