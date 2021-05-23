(define (domain ejercicio1)
    (:requirements :strips :typing :adl)

    ; TIPOS
    (:types
        unidad edificio recurso localizacion - object
        tipoUnidad tipoEdificio tipoRecurso - constants
    )

    ; CONSTANTES

    (:constants
        VCE - tipoUnidad
        CentroDeMando Barracones - tipoEdificio
        Mineral Gas - tipoRecurso
    )

    ; PREDICADOS

    (:predicates
        ; Predicados para saber que un objeto es de un tipo
        (esTipoUnidad ?unidad - unidad ?tipo - tipoUnidad)
        (esTipoEdificio ?edificio - edificio ?tipo - tipoEdificio)
        ;(esTipoRecurso ?recurso - recurso ?tipo - tipoRecurso)

        ; Determinar si un edificio o unidad está en una localización concreta
        (edificioEn ?edificio - edificio ?localizacion - localizacion)
        (unidadEn ?unidad - unidad ?localizacion - localizacion)
                
        ; Representar que existe un camino entre dos localizaciones
        (existeCamino ?localizacion1 - localizacion ?localizacion2 - localizacion)
        
        ; Determinar si un edificio está construido
        (construido ?edificio - edificio)
        
        ; Asignar un nodo de un recurso concreto a una localización concreta
        (asignarRecursoEn ?recurso - tipoRecurso ?localizacion - localizacion)
        
        ; Indicar si un VCE está extrayendo un recurso 
        (extrae ?unidad - unidad ?recurso - tipoRecurso)

        ; Unidad asignada
        (unidadNoAsig ?unidad - unidad)
    )

    ; ACCIONES
    ; Navegar, mueve una unidad entre 2 localizaciones
    (:action Navegar
        :parameters (?unidad - unidad, ?localizacionOrigen - localizacion, ?localizacionDestino - localizacion)
        :precondition 
            (and
                ; Existe un camino entre las 2 localizaciones
                (existeCamino ?localizacionOrigen ?localizacionDestino)
                ; La unidad está en la localización de origen
                (unidadEn ?unidad ?localizacionOrigen)
                ; La unidad no ha sido asignada
                (not (unidadNoAsig ?unidad)) 
            )
        :effect 
            (and 
                ; Nos movemos a la nueva posición
                (unidadEn ?unidad ?localizacionDestino)
                ; Eliminamos la antigua posición
                (not (unidadEn ?unidad ?localizacionOrigen))
            )
    )
    
    ; Asignar, asigna un VCE a un nodo de recurso
    (:action Asignar
        :parameters (?unidad - unidad, ?localizacion - localizacion, ?recurso - tipoRecurso)
        :precondition 
            (and
                ; La unidad debe ser un VCE
                (esTipoUnidad ?unidad VCE)
                ; La unidad está en la localización 
                (unidadEn ?unidad ?localizacion)
                ; El recurso está en la localización
                (asignarRecursoEn ?recurso ?localizacion)
                ; La unidad no ha sido asignada
                (not (unidadNoAsig ?unidad)) 
            )
        :effect 
            (and 
                ; Asignamos a la unidad el recurso que extrae
                (extrae ?unidad ?recurso)
            )
    )
)