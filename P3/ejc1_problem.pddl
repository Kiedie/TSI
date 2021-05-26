(define (problem ejc1_p) 
        (:domain ejc1)
    (:objects 
        loc_11 loc_12 loc_13 loc_14 loc_21 loc_22 loc_23 loc_24 loc_31 loc_32 loc_33 loc_34 - Localizacion
        VCE1            - Unidad
        CentroDeMando1  - Edificio
        
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
        

        (asig  Minerales loc_23)
        (asig  Minerales loc_33)

        ;Indicamos el tipo de la unidad y del edificio
        (esTipoUnidad      VCE1             VCE)
        (esTipoEdificio    CentroDeMando1   CentroDeMando)

        ;Indicamos que el edificio hay construido y decimos donde está
        (edifConstruido    CentroDeMando1)
        (en                CentroDeMando1 loc_11)

        ;Decimos donde está la unidad y además que está libre
        (en                VCE1          loc_11)
        (uniNoAsig         VCE1)
    
    )

    (:goal 
        (and
            (extrayendoRecurso VCE1 Minerales)
        )
    )


 
)
