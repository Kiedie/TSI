(define (domain ejc2)
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
        VCE                                   - tipoUnidad
        CentroDeMando Barracones Extractor    - tipoEdificio
        Minerales Gas                         - Recurso
    )

    (:predicates 
        (en                   ?m - Movible      ?loc - Localizacion )
        (camino               ?x - Localizacion   ?y - Localizacion )
        (extrayendoRecurso    ?u - Unidad         ?r - Recurso)
        (edifConstruido      ?ed - Edificio)
        (asig                 ?r - Recurso      ?loc - Localizacion)
        (necesita             ?e - Edificio       ?r - Recurso)
        ;Predicados alternativos
        (uniNoAsig            ?u - Unidad)
        (esTipoUnidad         ?u - Unidad      ?tipo - tipoUnidad)
        (esTipoEdificio       ?e - Edificio    ?tipo - tipoEdificio)
        (esTipoRecurso        ?r - Recurso     ?tipo - tipoRecurso)
    )

    ;Mueve una unidad enre dos localizaciones
    (:action navegar
        :parameters (?u - Unidad ?origen - Localizacion ?destino - Localizacion)
        :precondition                           ;exista camino y que la unidad este en el origen
            (and 
                (camino     ?origen   ?destino)
                (en         ?u        ?origen)
            )
        :effect                                 ;La unidad deja de estar en el origen y esta en el destinio
            (and 
                (not (en ?u ?origen))
                (en      ?u ?destino)
            )
    )

    ;Asigna un VCE a un nodo de recurso. 
    ;Un VCE asignado a un nodo de recurso ya no podrá hacer nada más el resto de la ejecución. 
    (:action asignar
        :parameters (?u - Unidad ?loc - Localizacion ?tipo - Recurso)
        :precondition 
            (and 
                (en                   ?u ?loc)     ;La unidad debe estar en la localizacion del recurso
                (asig                 ?tipo ?loc)  ;El recurso debe estar asignado a la localizacion
                (uniNoAsig            ?u)          ;La unidad debe de estar libre
                (esTipoUnidad         ?u  VCE)     ;La unidad debe de ser del tipo VCE
                (when (and ( esTipioRecurso ?tipo Gas))
                    (and 
                        (edifConstruido  Extractor)
                    )
                )
            )
        :effect 
            (and 
                (extrayendoRecurso  ?u       ?tipo) ;Indicamos que se está extrayendo el recurso 
                (not (uniNoAsig  ?u))               ;Ponemos a la unidad ocupada
            )
    )

    ;Ordena aun VCE libre que construya un edifico en una localizacion.
    ;Cada edif sólo requerirá un único tipo de recurso para ser construido
    ;Se permite que existan varios edificios en la misma localización
    (:action construir
        :parameters (?u - Unidad ?e - Edificio ?loc - Localizacion ?r - Recurso)
        :precondition 
            (and
                (uniNoAsig         ?u)               ;La unidad tiene que estar libre
                (esTipoUnidad      ?u      VCE)      ;La unidad debe ser VCE
                (en                ?u      ?loc)     ;La unidad debe estar en la localizacion
                (necesita          ?e      ?r)       ;El edificio necesita de un recurso
            )
        :effect 
            (and 
                (en              ?e     ?loc )       ;El edif se localiza en loc
                (edifConstruido  ?e)                 ;El edificio ha sido construido 
            )
    )
    
    
    

)