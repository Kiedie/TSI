(define (domain ejc1)
(:requirements :strips  :typing :adl)

    (:types 
        Movible NoMovible                           - object
        Unidad Edificio   tipoUnidad tipoEdificio   - Movible 
        Localizacion Recurso  tipoRecurso           - NoMovible
    )

    (:constants 
        VCE                         - tipoUnidad
        CentroDeMando Barracones    - tipoEdificio
        Minerales Gas               - tipoRecurso
    )

    (:predicates 
        ;Determina si un edificio o unidad está en una localizacion concreta
        (en                   ?m - Movible        ?loc - Localizacion )
        ;Representa que existe un camino entre dos localizaciones
        (camino               ?x - Localizacion     ?y - Localizacion )
        ;Determina si un edificio está construido
        (edifConstruido      ?ed - Edificio)
        ;Indica si un VCE está extrayendo un recurso
        (extrayendoRecurso    ?u - Unidad         ?r - tipoRecurso)
        ;Dice si un nodo de un recurso concreto está asignado a una localizacion
        (asig                 ?r - tipoRecurso      ?loc - Localizacion)
        
        ;Predicados alternativos

        ;Indica si una unidad está libre
        (uniNoAsig            ?u - Unidad)
        ;Para hacer distinciones sobre los distintos tipos de unidades
        (esTipoUnidad         ?u - Unidad     ?tipo - tipoUnidad )
        ;Para hacer distinciones sobre los distintos tipos de edificios
        (esTipoEdificio       ?e - Edificio   ?tipo - tipoEdificio)
        ;Para indicar que tengo el recurso 
        (tengoRecurso         ?r - tipoRecurso)

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

    ;Asigna un VCE a un nodo de recurso. 
    ;Un VCE asignado a un nodo de recurso ya no podrá haer nada más el resto de la ejecución. 
    (:action asignar
        :parameters (?u - Unidad ?loc - Localizacion ?tipo - tipoRecurso)
        :precondition 
            (and 
                (en                   ?u ?loc)     ;La unidad debe estar en la localizacion del recurso
                (asig                 ?tipo ?loc)  ;El recurso debe estar asignado a la localizacion
                (uniNoAsig            ?u)          ;La unidad debe de estar libre
                (esTipoUnidad ?u  VCE)             ;La unidad debe de ser un VEC
            )
        :effect 
            (and 
                (extrayendoRecurso  ?u       ?tipo) ;Indicamos que se está extrayendo el recurso 
                (not (uniNoAsig  ?u))               ;Ponemos a la unidad ocupada
                (tengoRecurso         ?tipo)
            )
    )
    
    

)