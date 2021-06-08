(define (domain ejcv2)
(:requirements :strips  :typing :adl)

    (:types 
        Movible NoMovible                        - object
        tipoEdificio tipoRecurso tipoUnidad      - object
        Unidad Edificio                          - Movible 
        Localizacion Recurso                     - NoMovible

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
        (asig                 ?r - tipoRecurso  ?loc - Localizacion)
        ;Que recurso necesita cada edificio para ser construido
        (necesita             ?e - Edificio       ?r - tipoRecurso)

        
        ;Predicados alternativos
        ;Indica si una unidad está libre
        (uniNoAsig            ?u - Unidad)
        ;Para hacer distinciones sobre los distintos tipos de unidades
        (esTipoUnidad         ?u - Unidad     ?tipo - tipoUnidad )
        ;Para hacer distinciones sobre los distintos tipos de edificios
        (esTipoEdificio       ?e - Edificio   ?tipo - tipoEdificio)
        ;Para hacer distinciones sobre el tipo de recurso
        (esTipoRecurso        ?r - Recurso    ?tipo - tipoRecurso)
        ;Indica si tengo o no un recurso
        (tengoRecurso ?r - tipoRecurso)
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
    ;Un VCE asignado a un nodo de recurso ya no podrá hacer nada más el resto de la ejecución. 
    ;Para poder obtener Gas Vepeno debe existir un edificio Exractor construido previamente
    ;sobre dicho nodo de recurso. No hay cambios para obtener recuros de mineral
    (:action asignar
        :parameters (?u - Unidad ?loc - Localizacion ?tipo - tipoRecurso)
        :precondition 
            (and 
                (uniNoAsig            ?u)          ;La unidad debe de estar libre                            
                (esTipoUnidad         ?u    VCE)   ;La unidad debe de ser del tipo VCE 
                (en                   ?u    ?loc)  ;La unidad debe estar en la localizacion del recurso
                (asig                 ?tipo ?loc)  ;El recurso debe estar asignado a la localizacion
               
                
                
                (or ;El recurso o es un mineral o es un Gas
                    (esTipoRecurso ?tipo Minerales)   

                    ;Exista un extractor en esta localizacion
                    (exists (?edif - tipoEdificio)
                        (and
                            (esTipoEdificio       ?edif    Extractor)
                            (en                   ?edif      ?loc )
                        )
                    )

                )
            )
        :effect 
            (and 
                (extrayendoRecurso  ?u       ?tipo) ;Indicamos que se está extrayendo el recurso 
                (not (uniNoAsig  ?u))               ;Ponemos a la unidad ocupada
                (tengoRecurso ?tipo)
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
                                                 
                (esTipoUnidad      ?u      VCE)    ;La unidad debe ser VCE  

                (en                ?u      ?loc)     ;La unidad debe estar en la localizacion
                (necesita          ?e      ?r)       ;El edificio necesita de un recurso
                (tengoRecurso ?r)
                
            )
        :effect 
            (and 
                (en              ?e     ?loc )       ;El edif se localiza en loc
                (edifConstruido  ?e)                 ;El edificio ha sido construido 
            )
    )
    
    
    

)