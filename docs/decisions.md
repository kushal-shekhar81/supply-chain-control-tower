# Decision log

Short architecture decision records (ADRs). Writing down *why* is what consultants do. It is
also the best material for interview answers.

## ADR-001 · One repository for everything
**Decision:** A single monorepo for backend, frontend, ABAP and docs.
**Why:** One place to review, one CI pipeline, and a reviewer sees the whole system at once.
**Trade-off:** Real enterprises often split these up by team. At this size, splitting would add work without benefit.

## ADR-002 · Spring Boot 4.1 and Java 21
**Decision:** Spring Boot 4.1.x (the 4.x generation, released from November 2025) on Java 21 LTS.
**Why:** 4.2.0 only shipped on 19 Nov 2026, so 4.1 gives the libraries around it time to catch up. Moving to 4.2 later is a small change in the parent POM.
**Note:** Spring Boot 4 renamed some starters, e.g. `spring-boot-starter-web` became `spring-boot-starter-webmvc`. Older tutorials use the old names.

## ADR-003 · Angular 22, standalone components and signals
**Decision:** The current Angular major. No NgModules; state is held in signals.
**Why:** AngularJS (1.x) is end-of-life. Signals are the direction Angular is going in.

## ADR-004 · SAP: BTP trial for the product, firm sandboxes only for learning
**Decision:** The public project's SAP backend is the BTP ABAP trial. Firm sandboxes (S4N, ES8, HTR) are used only to learn transactions and classic ABAP.
**Why:** Code and data from employer systems must not go into a public repository, and connecting a personal cloud account to firm systems needs approval.
**Fallback:** If the BTP trial cannot expose a service to an outside application, the integration service reads the SAP Business Accelerator Hub sandbox APIs plus synthetic data. The design stays the same either way, because sources sit behind the `SupplyChainSource` interface.

## ADR-005 · Synthetic data with a fixed seed
**Decision:** Data is generated in code from a fixed random seed.
**Why:** Demos repeat exactly, tests stay stable, and the data deliberately contains the problems the tower must catch: single sourcing, low stock, overdue POs and an AOG.

## ADR-006 · Kafka in Docker locally; no Amazon MSK
**Decision:** Kafka runs in Docker locally and on one EC2 instance in AWS.
**Why:** Managed Kafka (MSK) costs too much for a portfolio project. The services only need a Kafka address, so moving to MSK later is a configuration change.
