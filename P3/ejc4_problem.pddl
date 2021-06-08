(define (problem ejc4_p) 
        (:domain ejc4)
    (:objects 
        loc_11 loc_12 loc_13 loc_14 loc_21 loc_22 loc_23 loc_24 loc_31 loc_32 loc_33 loc_34 - Localizacion
        ;loc_11 loc_12 loc_13 loc_14 loc_21 loc_22 loc_23 loc_24 loc_31 loc_32 loc_33 loc_34 loc_41 loc_42 loc_43 loc_44 - Localizacion
        VCE1 VCE2 VCE3  - Unidad
        marine1 marine2 - Unidad
        segador1        - Unidad
        CentroDeMando1 Barracones1 Extractor1 - Edificio
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


        ;(camino loc_12 loc_22) (camino loc_22 loc_11)
        ;(camino loc_21 loc_22) (camino loc_22 loc_21)
        ;(camino loc_31 loc_21) (camino loc_21 loc_31)
        ;(camino loc_13 loc_22) (camino loc_22 loc_13)
        ;(camino loc_31 loc_32) (camino loc_32 loc_31)
        ;(camino loc_31 loc_41) (camino loc_41 loc_31)
        ;(camino loc_41 loc_43) (camino loc_43 loc_41)
        ;(camino loc_32 loc_42) (camino loc_42 loc_32)
        ;(camino loc_23 loc_33) (camino loc_33 loc_23)
        ;(camino loc_33 loc_43) (camino loc_43 loc_33)
        ;(camino loc_33 loc_13) (camino loc_13 loc_33)
        ;(camino loc_33 loc_24) (camino loc_24 loc_33)
        ;(camino loc_33 loc_44) (camino loc_44 loc_33)
        ;(camino loc_34 loc_44) (camino loc_44 loc_34)
        ;(camino loc_11 loc_12) (camino loc_12 loc_11)

        
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
    )

    (:goal 
        (and
            ;(edifConstruido Barracones1)
            (en Barracones1 loc_32)
            (en marine1  loc_31) 
            (en marine2  loc_24)
            (en segador1 loc_12)
        )
    )


 
)
