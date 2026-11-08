package com.telecom.contrataciones.reporte.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.telecom.contrataciones.reporte.dto.Agrupacion;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PeriodosTest {

    @Test
    void listaTodosLosDiasDelRangoInclusive() {
        assertThat(Periodos.entre(LocalDate.of(2026, 10, 30), LocalDate.of(2026, 11, 2), Agrupacion.DIA))
                .containsExactly("2026-10-30", "2026-10-31", "2026-11-01", "2026-11-02");
    }

    @Test
    void listaLosMesesAunqueElRangoNoEmpieceElDiaUno() {
        assertThat(Periodos.entre(LocalDate.of(2026, 11, 15), LocalDate.of(2027, 1, 10), Agrupacion.MES))
                .containsExactly("2026-11", "2026-12", "2027-01");
    }
}
