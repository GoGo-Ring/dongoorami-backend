package com.gogoring.dongoorami.concert.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConcertTest {

    @Test
    @DisplayName("공연 상태를 알맞게 수정할 수 있다.")
    void success_updateStatus() {
        // given
        Concert concert1 = Concert.builder()
                .startedAt(LocalDate.now().minusDays(1).toString())
                .endedAt(LocalDate.now().plusDays(1).toString())
                .status("공연예정")
                .build();

        Concert concert2 = Concert.builder()
                .startedAt(LocalDate.now().minusDays(1).toString())
                .endedAt(LocalDate.now().minusDays(1).toString())
                .status("공연중")
                .build();

        // when
        concert1.updateStatus();
        concert2.updateStatus();

        // then
        assertThat(concert1.getStatus()).isEqualTo("공연중");
        assertThat(concert2.getStatus()).isEqualTo("공연종료");
    }
}
