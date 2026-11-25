package io.scct.integration.source;

import io.scct.model.SupplyChainSnapshot;

/**
 * A system the control tower reads from. Implementations so far:
 * <ul>
 *   <li>{@link SyntheticSource} - week 1, deterministic fictional data</li>
 *   <li>BtpAbapSource - week 3, reads the RAP OData V4 service you build on BTP in week 2</li>
 *   <li>ApiHubSandboxSource - week 3, reads SAP Business Accelerator Hub sandbox APIs</li>
 * </ul>
 * This is the Strategy pattern: callers depend on the interface, and configuration decides
 * which source is active.
 */
public interface SupplyChainSource {

    SupplyChainSnapshot fetchSnapshot();
}
