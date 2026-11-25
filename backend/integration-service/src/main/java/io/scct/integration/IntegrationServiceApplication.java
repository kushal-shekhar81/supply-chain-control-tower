package io.scct.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

/**
 * Integration service: the only service that talks to SAP.
 *
 * <p>Design rule: every other service consumes the canonical model, never SAP tables directly.
 * If the SAP side changes (ECC to S/4HANA, on-prem to BTP), only this service changes. That
 * isolation is called an "anti-corruption layer" in domain-driven design.
 */
@SpringBootApplication
public class IntegrationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegrationServiceApplication.class, args);
    }

    /** Injecting a Clock (instead of calling now() everywhere) makes time testable. */
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
