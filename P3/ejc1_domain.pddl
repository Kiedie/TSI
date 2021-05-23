(define (domain ejc1)
(:requirements :strips  :typing :adl)

    (:types 
        Movible NoMovible       - object
        Unidad Edificio         - Movible 
        Localizacion Recurso    - NoMovible
        tipoUnidad              - Unidad
        tipoEdificio            - Edificio
        tipoRecurso             - Recurso
    )

    (:constants 
        VCE                         - tipoUnidad
        CentroDeMando Barracones    - tipoEdificio
        Minerales Gas               - Recurso
    )

    (:predicates 
        (en                   ?m - Movible      ?loc - Localizacion )
        (camino               ?x - Localizacion   ?y - Localizacion )
        (extrayendoRecurso    ?u - Unidad         ?r - Recurso)
        (edifConstruido      ?ed - Edificio)
        (asig                 ?r - Recurso      ?loc - Localizacion)
        ;Predicados alternativos
        (uniNoAsig            ?u - Unidad)
        (esTipoUnidad         ?u - Unidad      ?tipo - tipoUnidad)
        (esTipoEdificio       ?e - Edificio    ?tipo - tipoEdificio)
    )

    ;Mueve una unidad enre dos localizaciones
    (:action navegar
        :parameters (?u - Unidad ?origen - Localizacion ?destino - Localizacion)
        :precondition                           ;exista camino y que la unidad este en el origen
            (and 
                (camino     ?origen   ?destino)
                (en         ?u        ?origen)
                (uniNoAsig            ?u)
            )
        :effect                                 ;La unidad deja de estar en el origen y esta en el destinio
            (and 
                (not (en ?u ?origen))
                (en      ?u ?destino)
            )
    )

    ;Asigna un VCE a un nodo de recurso. Un VCE asignado a un nodo de recurso ya no podrá haer nada más el resto de la ejecución. 
    (:action asignar
        :parameters (?u - Unidad ?loc - Localizacion ?tipo - Recurso)
        :precondition 
            (and 
                (en                   ?u ?loc)     ;La unidad debe estar en la localizacion del recurso
                (asig                 ?tipo ?loc)  ;El recurso debe estar asignado a la localizacion
                (uniNoAsig            ?u)          ;La unidad debe de estar libre
                ;(esTipoUnidad         ?u  VCE)     ;La unidad debe de ser del tipo VCE
            )
        :effect 
            (and 
                (extrayendoRecurso  ?u       ?tipo) ;Indicamos que se está extrayendo el recurso 
                (not (uniNoAsig  ?u))               ;Ponemos a la unidad ocupada
            )
    )
    
    

)