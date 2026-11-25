# SAP side (ABAP Cloud on SAP BTP)

From week 2, ABAP objects built in the BTP ABAP trial are pushed here with **abapGit**.
This folder is then the GitHub copy of your SAP package (`sap/src/`).

## One-time set-up (week 1)

1. **BTP trial:** in the trial cockpit, go to *Boosters* and run **Prepare an Account for ABAP Trial**.
   Save the service key or the system URL it gives you.
2. **Eclipse + ADT:** install Eclipse IDE for Java Developers, then add ABAP Development
   Tools from `https://tools.hana.ondemand.com/latest`.
3. **Connect:** File → New → ABAP Cloud Project, using the service key or URL from step 1.
4. **Package:** create package `ZSCCT_<your initials>` (for example `ZSCCT_KS`) with
   superpackage `ZLOCAL`. The trial system is shared, so names must be unique.
5. **Smoke test:** create class `ZCL_SCCT_HELLO` that implements `IF_OO_ADT_CLASSRUN`.
   Run it with F9 and check the console output.
6. **abapGit for ADT:** Help → Install New Software → `https://eclipse.abapgit.org/updatesite/`.
   Open the *abapGit Repositories* view, link this repository (branch `main`) to your package,
   then Stage and Push using a GitHub personal access token.
   `/.abapgit.xml` tells abapGit that ABAP objects live in `sap/src/`.

## Week 2 plan

| Object | Type | Purpose |
|---|---|---|
| `ZSCCT_PART`, `ZSCCT_SUPPLIER`, `ZSCCT_STOCK`, `ZSCCT_PO` | Database tables | Simplified copies of MARA, LFA1, MARD and EKPO for the fictional companies |
| `ZCL_SCCT_DATA_GENERATOR` | Class | Fills the tables with the same data as `backend/synthetic-data` |
| `ZI_SCCT_*` | CDS view entities (interface layer) | Clean, joined views over the tables |
| `ZC_SCCT_*` | CDS projection views | What the API exposes |
| `ZUI_SCCT_O4` | Service definition + binding (OData V4) | The endpoint Spring Boot calls in week 3 |

## Using the firm sandboxes (learning only)

Use S4N to see the real processes behind each table: MMBE (stock), ME2M / ME23N (purchase
orders), MD04 (stock vs requirements) and CO03 (production orders). Nothing from firm systems
is copied into this repository.
