(define (problem ejc5_p) 
        (:domain ejc5)
    (:objects 
        loc_11 loc_12 loc_13 loc_14 loc_21 loc_22 loc_23 loc_24 loc_31 loc_32 loc_33 loc_34 - Localizacion
        VCE1 VCE2 VCE3  - Unidad
        marine1 marine2 - Unidad
        segador1        - Unidad
        CentroDeMando1 Barracones1 Extractor1 - Edificio
        bahia - Edificio
        ImpulsaSegador1 - Investigacion
    )

    (:init
        (camino loc_11 loc_12) (camino loc_12 loc_11)
        (camino loc_11 loc_21) (camino loc_21 loc_11)
        (camino loc_21 loc_31) (camino loc_31 loc_21)
        (camino loc_31 loc_32) (camino loc_32 loc_31)
        (camino loc_22 loc_32) (camino loc_32 loc_22)
        (camino loc_22 loc_12) (camino loc_12 loc_22)
        (camino loc_22 loc_23) (camino loc_23 loc_22)
        (camino loc_23 loc_13) (camino loc_13 loc_23)
        (camino loc_13 loc_14) (camino loc_14 loc_13)
        (camino loc_14 loc_24) (camino loc_24 loc_14)
        (camino loc_24 loc_34) (camino loc_34 loc_24)
        (camino loc_34 loc_33) (camino loc_33 loc_34)
        
        ;Asignamos localizacion del recurso
        (asig  Minerales loc_23)
        (asig  Minerales loc_33)
        (asig  Gas       loc_13)


        ;INFORMACION SOBRE LA UNIDAD  
            ;VCE
            (reclutaEn         VCE              CentroDeMando)
            (necesitaUni       VCE              Minerales)
            (esTipoUnidad      VCE1             VCE)
            (esTipoUnidad      VCE2             VCE)
            (esTipoUnidad      VCE3             VCE)
            (en                VCE1           loc_11)
            (uniNoAsig         VCE1)
            

            ;MARINE --> En principio no están creados ni por consiguiente, asignados
            (reclutaEn         Marine           Barracones)
            (necesitaUni       Marine           Minerales)
            (esTipoUnidad      marine1          Marine)
            (esTipoUnidad      marine2          Marine)
        
            ;SEGADOR -->En principio no está creado ni por consiguiente, asignado
            (reclutaEn         Segador          Barracones)
            (necesitaInvestUni Segador          ImpulsaSegador)   ;EJC5
            (necesitaUni       Segador          Minerales)
            (necesitaUni       Segador          Gas)
            (esTipoUnidad      segador1         Segador)
        
        ;INFORMACION SOBRE EL EDIFICIO
            (esTipoEdificio    Extractor1        Extractor)
            (esTipoEdificio    Barracones1       Barracones)
            (esTipoEdificio    CentroDeMando1    CentroDeMando)
    
            (necesitaEdif             Extractor       Minerales)
            (necesitaEdif             Barracones      Minerales)
            (necesitaEdif             Barracones      Gas)

            ;Edificios construidos
            (edifConstruido      CentroDeMando1)
            (en                  CentroDeMando1 loc_11)

            ;BAHIA   EJC5
            (esTipoEdificio      bahia BahiaInvestigacion)
            (necesitaEdif        BahiaInvestigacion Gas)
            (necesitaEdif        BahiaInvestigacion Minerales)


        ;INFORMACION INVESTIGACIONES
            ;SEGADOR  EJC5
            (esTipoInvest    ImpulsaSegador1 ImpulsaSegador)
            (necesitaInvestRec   ImpulsaSegador Gas)
            (necesitaInvestRec   ImpulsaSegador Minerales)
    )

    (:goal 
        (and
            (en Barracones1 loc_32)
            (en marine1  loc_31) 
            (en marine2  loc_24)
            (en segador1 loc_12)
        )
    )


 
)