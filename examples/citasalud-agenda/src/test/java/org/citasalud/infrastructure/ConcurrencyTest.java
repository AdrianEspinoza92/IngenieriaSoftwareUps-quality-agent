package org.citasalud.infrastructure;

import org.citasalud.domain.exception.FranjaOcupadaException;
import org.citasalud.domain.model.Cita;
import org.citasalud.domain.port.in.ReservarCitaUseCase;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class ConcurrencyTest {

    private static final Logger log = LoggerFactory.getLogger(ConcurrencyTest.class);

    @Autowired
    private ReservarCitaUseCase reservarCitaUseCase;

    private static final UUID MEDICO_ID   = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID PACIENTE_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID FRANJA_ID   = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccc04");
    private static final LocalDate FECHA  = LocalDate.of(2026, 7, 15);

    @Test
    void reservar_dosHilosSimultaneos_soloUnoTieneExito() throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch  = new CountDownLatch(2);

        AtomicInteger exitosos = new AtomicInteger(0);
        AtomicInteger fallidos  = new AtomicInteger(0);
        AtomicReference<Cita>      citaRef  = new AtomicReference<>();
        AtomicReference<Exception> errorRef = new AtomicReference<>();

        Runnable tarea = () -> {
            try {
                startLatch.await();
                Cita cita = reservarCitaUseCase.reservar(PACIENTE_ID, MEDICO_ID, FRANJA_ID, FECHA);
                citaRef.set(cita);
                exitosos.incrementAndGet();
            } catch (FranjaOcupadaException e) {
                errorRef.set(e);
                fallidos.incrementAndGet();
            } catch (DataAccessException e) {
                // Cubre ObjectOptimisticLockingFailureException y otros fallos de Spring Data
                log.info("Fallo de concurrencia capturado: {} - {}", e.getClass().getSimpleName(), e.getMessage());
                errorRef.set(e);
                fallidos.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.warn("Excepción inesperada en hilo de concurrencia: {} - {}", e.getClass().getName(), e.getMessage());
                errorRef.set(e);
                fallidos.incrementAndGet();
            } finally {
                doneLatch.countDown();
            }
        };

        Thread t1 = new Thread(tarea);
        Thread t2 = new Thread(tarea);
        t1.start();
        t2.start();

        startLatch.countDown();
        doneLatch.await();

        assertThat(exitosos.get()).isEqualTo(1);
        assertThat(fallidos.get()).isEqualTo(1);
        assertThat(citaRef.get().getNumeroReferencia()).startsWith("CIT-");
    }
}
