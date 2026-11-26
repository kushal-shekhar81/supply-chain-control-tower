CLASS zcl_scct_hello DEFINITION
  PUBLIC
  FINAL
  CREATE PUBLIC .

  PUBLIC SECTION.

    INTERFACES if_oo_adt_classrun .
  PROTECTED SECTION.
  PRIVATE SECTION.
ENDCLASS.



CLASS zcl_scct_hello IMPLEMENTATION.


  METHOD if_oo_adt_classrun~main.
    out->write( 'control tower online' ).
  ENDMETHOD.
ENDCLASS.
