package io.scct.integration;

import io.scct.integration.api.SnapshotController;
import io.scct.integration.api.SnapshotSummary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/** Starts the whole Spring context once and checks the API returns sensible data. */
@SpringBootTest
class IntegrationServiceApplicationTest {

    @Autowired
    private SnapshotController controller;

    @Test
    void summaryCoversBothCompanies() {
        SnapshotSummary summary = controller.summary();
        assertEquals(4, summary.plants());
        assertEquals("SYNTHETIC", summary.sourceSystem());
        assertEquals(1, summary.openAogRequests());
        assertFalse(summary.watchlist().isEmpty());
    }
}
