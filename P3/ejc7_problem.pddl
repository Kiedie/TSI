(define (problem ejc7)  
        (:domain ejc7)
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





        ;Inicialmente no tengo ningun VCE asignado a ningun 
        ;nodo de recurso en ninguna localizacion 
        (=(vceAsignadoLocExtrae loc_11 Gas) 0)
        (=(vceAsignadoLocExtrae loc_12 Gas) 0)
        (=(vceAsignadoLocExtrae loc_14 Gas) 0)
        (=(vceAsignadoLocExtrae loc_13 Gas) 0)
        (=(vceAsignadoLocExtrae loc_21 Gas) 0)
        (=(vceAsignadoLocExtrae loc_22 Gas) 0)
        (=(vceAsignadoLocExtrae loc_23 Gas) 0)
        (=(vceAsignadoLocExtrae loc_24 Gas) 0)
        (=(vceAsignadoLocExtrae loc_31 Gas) 0)
        (=(vceAsignadoLocExtrae loc_32 Gas) 0)
        (=(vceAsignadoLocExtrae loc_33 Gas) 0)
        (=(vceAsignadoLocExtrae loc_34 Gas) 0)



        (=(vceAsignadoLocExtrae loc_11 Minerales) 0)
        (=(vceAsignadoLocExtrae loc_12 Minerales) 0)
        (=(vceAsignadoLocExtrae loc_14 Minerales) 0)
        (=(vceAsignadoLocExtrae loc_13 Minerales) 0)
        (=(vceAsignadoLocExtrae loc_21 Minerales) 0)
        (=(vceAsignadoLocExtrae loc_22 Minerales) 0)
        (=(vceAsignadoLocExtrae loc_23 Minerales) 0)
        (=(vceAsignadoLocExtrae loc_24 Minerales) 0)
        (=(vceAsignadoLocExtrae loc_31 Minerales) 0)
        (=(vceAsignadoLocExtrae loc_32 Minerales) 0)
        (=(vceAsignadoLocExtrae loc_33 Minerales) 0)
        (=(vceAsignadoLocExtrae loc_34 Minerales) 0)


        


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
            (necesitaUni       Marine           Gas)
            ;(necesitaUni       Marine           Gas)
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





        ;EJERCICIO 6 
        (= (recolecto Gas) 10)
        (= (recolecto Minerales) 10)
        ;Estableccemos el tope de recursos
            (= (limiteRecursos Minerales) 60)
            (= (limiteRecursos Gas) 60)
        ;Establecemos el conste de constuir o reclutar elementos
            (=(consume Barracones Minerales) 50)
            (=(consume Barracones Gas)       20)
            (=(consume Extractor  Minerales) 33)
            (=(consume Extractor  Gas)        0)
            (=(consume VCE        Minerales) 10)
            (=(consume VCE        Gas)        0)
            (=(consume Marine     Minerales) 20)
            (=(consume Marine     Gas)       10)
            (=(consume Segador    Minerales) 30)
            (=(consume Segador    Gas)       30)
        
        (= (recursoAlmacenado Gas) 0)
        (= (recursoAlmacenado Minerales) 0)
        (= (tiempoAccion) 0 )
        
    )

    (:goal 
        (and
            (en Barracones1 loc_32)
            (en marine1  loc_31) 
            (en marine2  loc_24)
            (en segador1 loc_12)
        )
    )

    (:metric minimize (tiempoAccion))

 
)