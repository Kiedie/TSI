(define (problem problem_name) (:domain ejercicio1)
    (:objects 
        ; Definimos las variables
        vce1 - unidad
        centroDeMando1 - edificio
        mineral1 mineral2 - tipoRecurso
        loc_1_1 loc_1_2 loc_1_3 loc_1_4 loc_2_1 loc_2_2 loc_2_3 loc_2_4 loc_3_1 loc_3_2 loc_3_3 loc_3_4 - localizacion
    )

    (:init
        ; Tipos de las unidades
        (esTipoUnidad vce1 VCE)

        ; Tipos de los edificios
        (esTipoEdificio centroDeMando1 CentroDeMando)
        ; Construcción y localización del Centro de Mando
        (construido centroDeMando1)
        (edificioEn centroDeMando1 loc_1_1)

        ; Tipos de los recursos
        ; Localización de los recursos
        (asignarRecursoEn mineral1 loc_2_3)
        (asignarRecursoEn mineral2 loc_3_3)

        ; Localización de vce1 al inicio
        (unidadEn vce1 loc_1_1)

        ; Declaración de los camino
        ; Camino entre loc11 y loc 12
        (existeCamino loc_1_1 loc_1_2)
        (existeCamino loc_1_2 loc_1_1)
        ; Camino entre loc11 y loc 21
        (existeCamino loc_1_1 loc_2_1)
        (existeCamino loc_2_1 loc_1_1)
        ; Camino entre loc12 y loc 22
        (existeCamino loc_1_2 loc_2_2)
        (existeCamino loc_2_2 loc_1_2)
        ; Camino entre loc21 y loc 31
        (existeCamino loc_2_1 loc_3_1)
        (existeCamino loc_3_1 loc_2_1)
        ; Camino entre loc31 y loc 32
        (existeCamino loc_3_1 loc_3_2)
        (existeCamino loc_3_2 loc_3_1)
        ; Camino entre loc22 y loc 32
        (existeCamino loc_2_2 loc_3_2)
        (existeCamino loc_3_2 loc_2_2)    
        ; Camino entre loc22 y loc 23
        (existeCamino loc_2_2 loc_2_3)
        (existeCamino loc_2_3 loc_2_2) 
        ; Camino entre loc13 y loc 23
        (existeCamino loc_1_3 loc_2_3)
        (existeCamino loc_2_3 loc_1_3)
        ; Camino entre loc13 y loc 14
        (existeCamino loc_1_3 loc_1_4)
        (existeCamino loc_1_4 loc_1_3)
        ; Camino entre loc14 y loc 24
        (existeCamino loc_1_4 loc_2_4)
        (existeCamino loc_2_4 loc_1_4)
        ; Camino entre loc24 y loc 34
        (existeCamino loc_2_4 loc_3_4)
        (existeCamino loc_3_4 loc_2_4)
        ; Camino entre loc33 y loc 34
        (existeCamino loc_3_3 loc_3_4)
        (existeCamino loc_3_4 loc_3_3)
    )

    (:goal 
        (or
            (extrae vce1 mineral1)
            (extrae vce1 mineral2)
        )
    )

)

