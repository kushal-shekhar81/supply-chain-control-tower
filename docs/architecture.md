# Architecture

## 1. Functional view: silos around one loop

Each business function runs on its own SAP module and its own KPIs. The control tower sits
between them. It reads one shared picture from all of them and pushes decisions back out.

| Silo | SAP | What it sends the tower | Pain without the tower | What the tower adds |
|---|---|---|---|---|
| Demand & S&OP | SD, PP demand management | Call-offs, forecasts, MRO demand | Forecast changes reach suppliers late (bullwhip) | Buyers and planners see demand shifts on the same day |
| Procurement & suppliers | MM, QM | POs, confirmations, supplier quality | Single-sourced parts, no Tier-2/3 view | Supplier risk score, early shortage warnings |
| Production | PP, MRP | Production orders, BOMs | Line stops discovered at shift start | Parts at risk per order, days ahead |
| Inventory & warehouse | MM-IM, EWM | Stock by plant, quality holds | Excess in one plant, shortage in another | Network-wide stock and ideas for moving it |
| Logistics | TM, carriers | Shipments, ETAs | Tracking lives in emails and portals | Predicted ETAs, late-shipment exceptions |
| Aftermarket & MRO | PM, spares | Work orders, AOG requests | AOG is very costly; spares are spread thin | AOG priority queue, nearest spare |

The loop the tower runs: **Sense → Predict → Decide → Act → Learn**.

## 2. Technical view

```mermaid
flowchart LR
    BTP[SAP BTP ABAP<br/>Z tables → CDS → RAP OData V4] --> INT
    HUB[SAP Business Accelerator Hub sandbox] --> INT
    SYN[Synthetic feeds<br/>carriers, supplier portal, IoT] --> INT
    INT[integration-service<br/>Spring Boot · canonical model] --> K[(Kafka topics:<br/>orders, stock, shipments, quality)]
    K --> VIS[visibility-service]
    K --> EXC[exception-service]
    K --> RISK[risk-service]
    K --> SCN[scenario-service]
    VIS & EXC & RISK & SCN --> GW[api-gateway<br/>Spring Cloud Gateway · JWT]
    GW --> UI[Angular UI]
    EXC -. act: write-back e.g. expedite PO .-> INT
```

| Layer | Technology | Concept it teaches | Supply chain link |
|---|---|---|---|
| Sources | ABAP Cloud: CDS, RAP, OData V4 | Exposing ERP data as clean APIs | The ERP is the system of record |
| Integration | Spring Boot, OData client | Anti-corruption layer, canonical model | Breaks silos at the data level: same IDs, same meaning |
| Event bus | Kafka | Event-driven architecture, pub/sub | Know in minutes, not after the nightly batch |
| Domain services | Spring Boot + Postgres | Microservices, bounded contexts | Each capability (visibility, exceptions, risk) evolves separately |
| Gateway | Spring Cloud Gateway | Single entry point, auth, routing | Role-based views for buyer, planner, executive |
| UI | Angular (signals, lazy routes) | Reactive UI, component design | Everyone looks at the same picture |
| Hosting | AWS: EC2, RDS, S3 + CloudFront; GitHub Actions | Cloud deployment, CI/CD | A tower is only useful if it is always on, like a NOC |

## 3. Where each SAP field comes from

See the Javadoc on each record in `backend/canonical-model`. Summary:

| Canonical field | SAP table-field | SAP GUI transaction to see it |
|---|---|---|
| Plant.plantId | T001W-WERKS | OX10 (config), MMBE |
| Supplier.supplierId | LFA1-LIFNR | BP / XK03 |
| Part.partId | MARA-MATNR | MM03 |
| Part.leadTimeDays | MARC-PLIFZ | MM03 → MRP 2 view |
| Part.approvedSupplierIds | EORD (source list) | ME03 |
| StockPosition.unrestricted | MARD-LABST | MMBE |
| StockPosition.qualityInspection | MARD-INSME | MMBE |
| StockPosition.safetyStock | MARC-EISBE | MM03 → MRP 2 view |
| PurchaseOrderLine.poNumber / item | EKKO-EBELN / EKPO-EBELP | ME23N |
| PurchaseOrderLine.deliveryDate | EKET-EINDT | ME23N → schedule lines; ME2M |
| DemandRequirement | RESB (reservations), VBEP (SD schedule lines) | MD04 |
