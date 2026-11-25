# Week 1 — Set-up, the canonical model and the first screen

## What was built

- A monorepo with a Maven multi-module backend, an Angular frontend, Docker Compose and CI.
- A **canonical model**: Plant, Supplier, Part, StockPosition, PurchaseOrderLine,
  DemandRequirement. Each field is mapped to its SAP table and field.
- **Synthetic data** for Aurora Mobility (auto) and Stratos Aero (MRO), with problems built in on purpose.
- An **integration service** with `GET /api/v1/snapshot` and `GET /api/v1/snapshot/summary`.
- A **dashboard** showing four KPIs and a watchlist sorted by urgency (AOG first, then days of cover).

## Your tasks this week (about 6 hours)

- [ ] Push this repository to GitHub; check that the CI workflow goes green
- [ ] `mvn verify`, then run the integration service and open `/api/v1/snapshot/summary`
- [ ] `npm start` in `frontend` and open the dashboard
- [ ] BTP: run the ABAP trial booster, install Eclipse + ADT + abapGit, run a hello-world class (see `sap/README.md`)
- [ ] S4N sandbox: open MMBE, ME2M and MD04 for any material and find the fields in the mapping table in `docs/architecture.md`
- [ ] Read the concepts below and say the interview answers out loud once

---

## Technical concepts

**Monorepo with Maven modules.** The parent POM pins every version once. Each module is either
a library (`canonical-model`, `synthetic-data`) or a deployable service (`integration-service`).
A library can be shared; a service can be deployed on its own.

**Java records.** `record Part(String partId, ...)` gives you a constructor, getters, `equals`,
`hashCode` and `toString` for free, and the fields cannot be changed. Immutable data is safe to
pass between threads and services. The compact constructor (`public Part { ... }`) is where
validation goes, for example in `Supplier`.

**Canonical data model.** SAP calls a part `MATNR`, a carrier's system calls it `sku`, and a
supplier portal calls it `itemNo`. A canonical model chooses one name and one meaning, and every
source is translated into it at the edge. Without one, each service would translate every
system's format itself, and the silos would come back in the code.

**Anti-corruption layer.** Only `integration-service` knows SAP's field names. If you move from
ECC to S/4HANA, or from the trial to a real system, only that service changes.

**Strategy pattern.** `SupplyChainSource` is an interface, and `SyntheticSource` is one
implementation of it. In week 3 you add `BtpAbapSource`, and configuration picks which one runs.
The controller never changes.

**Injecting a `Clock`.** Calling `LocalDate.now()` everywhere makes code impossible to test for
"what happens on the 31st?". With an injected Clock, a test can fix the date.

**Business time zone.** "Today" for a planner in Pune is not "today" in UTC between midnight and
05:30 IST. The service therefore decides the date in a configured business zone.

**Angular signals.** `signal()` holds state. `computed()` derives values from it
(`visibleWatchlist`). The template re-renders only what reads a changed signal.

**Lazy-loaded routes.** The dashboard's code is downloaded only when its route opens. That keeps
the first load small as more screens are added.

**Dev proxy.** `proxy.conf.json` forwards `/api` from `:4200` to `:8081`, so the browser sees one
origin and no CORS set-up is needed in development.

**Docker Compose.** One file describes Postgres, Kafka and Kafka UI. `docker compose up -d`
gives every developer, and later the AWS host, the same set-up.

**Kafka in KRaft mode.** Recent Kafka versions manage themselves without ZooKeeper. The
container has two listeners: one for other containers (`kafka:29092`) and one for apps on your
laptop (`localhost:9092`).

---

## Supply chain concepts

**What a control tower is.** A control tower is not a dashboard. It is a working model:
1. **Visibility:** what is happening, end to end.
2. **Prediction:** what will happen, such as a shortage or a delay.
3. **Prescription:** what to do about it.
4. **Autonomy:** low-risk actions are carried out automatically.

Most companies are at stage 1 or 2. This project builds 1 and 2 properly, with a start on 3.

**SCOR model.** The industry's standard map of supply chain processes:
Plan, Source, Make, Deliver, Return and Enable. Recent versions rename some of these, but these
six are the ones interviewers know. Each silo on the Whiteboard maps to one of them.

**Days of cover.** Usable stock divided by average daily demand. It answers "how long can we keep
running?". Stock alone means nothing: 1,000 units is plenty for a part used 10 a day and a crisis
for one used 2,400 a day.

**Safety stock.** A buffer held against variability in demand and in supplier lead times. Going
below it does not mean you have run out; it means you have used up your protection.

**Single sourcing.** One approved supplier for a part. It is cheaper (volume discounts, one
qualification) but fragile. In aerospace it is often forced: requalifying a supplier for a
forged turbine blade can take a year or more, so "just add a second source" is not a quick fix.

**AOG (aircraft on ground).** An aircraft that cannot fly until a part arrives. It is the most
urgent request an MRO handles, because the airline loses revenue and has to rebook passengers.
It is the aerospace equivalent of an automotive line stop.

**Auto vs aerospace supply chains**

| | Automotive | Aerospace (MRO) |
|---|---|---|
| Volume | Millions of units, high run rates | Few units, many part numbers |
| Lead times | Days to weeks | Months to over a year for castings and forgings |
| Stock strategy | Lean: JIT / JIS (just-in-sequence) | Strategic spares and rotable pools |
| Worst event | Line stop | AOG |
| Regulation | IATF 16949 quality, PPAP part approval | AS9100, airworthiness release, full traceability |
| Typical fragility | Chips, batteries, single-source castings | Forgings, castings, long-lead avionics |

---

## Interview practice

**"What is your project, in one minute?"**
I run shifts in a network operations centre. That is a control tower for IT: we watch everything
in one place, triage by severity and follow runbooks. Supply chains need the same thing, because
demand, procurement, production and logistics each sit in their own system and spot problems
late. I built a supply chain control tower on SAP, Spring Boot and Angular for a fictional auto
supplier and an aerospace MRO. It joins their data into one model, watches days of cover, overdue
orders and single-sourced parts, and raises exceptions with an owner and a playbook before a line
stops or an aircraft stays on the ground.

**"How exactly does it break silos?"**
In three ways. Data: one canonical model, so "part" means the same thing everywhere. Time: events
instead of nightly reports, so everyone sees a change within minutes. Ownership: every exception
has one owner and one playbook, so buyers and planners don't work on the same problem separately.

**"Why use days of cover instead of stock levels?"**
Stock without consumption has no meaning. Days of cover normalises across parts and plants, so a
planner can sort the whole network by urgency.

**"How does automotive differ from aerospace?"**
Use the table above. The key line: auto optimises flow, and its failure is a line stop; aero
optimises availability, and its failure is an AOG.

**"Why synthetic data?"**
Client data is confidential, and synthetic data lets me plant the exact scenarios the tower must
catch, so the demo is repeatable.
