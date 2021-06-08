(define (domain ejc4)
(:requirements :strips  :typing :adl)

    (:types 
        Movible NoMovible                        - object
        tipoEdificio tipoRecurso tipoUnidad      - object
        Unidad Edificio                          - Movible 
        Localizacion Recurso                     - NoMovible
    )

    (:constants 
        VCE  Marine  Segador                 - tipoUnidad
        CentroDeMando Barracones Extractor   - tipoEdificio
        Minerales Gas                        - tipoRecurso
    )

    (:predicates 
        ;Determina si un edificio o unidad está en una localizacion concreta
        (en                   ?m - Movible      ?loc - Localizacion )
        ;Representa que existe un camino entre dos localizaciones
        (camino               ?x - Localizacion   ?y - Localizacion )
        ;Determina si un edificio está construido
        (edifConstruido      ?ed - Edificio)
        ;Indica si un VCE está extrayendo un recurso
        (extrayendoRecurso    ?u - Unidad         ?r - tipoRecurso)
        ;Dice si un nodo de un recurso concreto está asignado a una localizacion
        (asig                 ?r - tipoRecurso  ?loc - Localizacion)
        ;Que recurso necesita cada edificio para ser construido
        (necesitaEdif         ?e - tipoEdificio   ?r - tipoRecurso)

        
        ;Predicados alternativos

        ;Indica si una unidad está libre
        (uniNoAsig            ?u - Unidad)
        ;Para hacer distinciones sobre los distintos tipos de unidades
        (esTipoUnidad         ?u - Unidad       ?tipo - tipoUnidad )
        ;Para hacer distinciones sobre los distintos tipos de edificios
        (esTipoEdificio       ?e - Edificio     ?tipo - tipoEdificio)
        ;Indica si tengo o no un recurso
        (tengoRecurso         ?r - tipoRecurso)
        ;Indica en que tipo de edificio se recluta a un tipo de unidad
        (reclutaEn            ?u - tipoUnidad   ?tipo - tipoEdificio)
        ;Indica el tipo de recurso que necesita un tipo de unidad para ser reclutada
        (necesitaUni          ?u - tipoUnidad   ?tipo - tipoRecurso )
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



    ;Asigna un VCE a un nodo de recursos
    (:action asignar
        :parameters (?u - Unidad ?loc - Localizacion ?tipo - tipoRecurso)
        :precondition 
            (and 
                (uniNoAsig            ?u)          ;La unidad debe de estar libre                          
                (esTipoUnidad         ?u    VCE)   ;La unidad debe de ser un VCE  
                (en                   ?u    ?loc)  ;La unidad debe estar en la localizacion del recurso
                (asig                 ?tipo ?loc)  ;El recurso debe estar asignado a la localizacion
               
                
                
                (or ;El recurso o es un mineral o es un Gas
                    (= ?tipo Minerales)   

                    ;Exista un extractor en esta localizacion
                    (exists (?edif - Edificio)
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
    ;Hay que tener en cuenta que un edificio puede requerir mśa de un tipo de recurso
    ;Esta acción debe inferir por sí misma si se tiene el tipo de recurso necesrio para poder ejecutarse
    ;Además, debe evitar que se construya más de un edificio en la misma localizacion
    (:action construir
        :parameters (?u - Unidad ?e - Edificio ?loc - Localizacion )
        :precondition 
            (and
                (not (edifConstruido      ?e))
                (uniNoAsig         ?u)                  ;La unidad tiene que estar libre
                (en                ?u      ?loc)        ;La unidad debe estar en la localizacion                                        
                (esTipoUnidad      ?u      VCE)         ;La unidad debe de ser un VCE                        

                  
                (not (exists (?edificio - Edificio)     ;No Existe un edificio que esté en esta ocalizacion
                        (and 
                            (en ?edificio ?loc)
                        )
                    )
                )

                ;Debe de existir un tipo de edificio tal que
                (exists (?tipoedif - tipoEdificio)
                    (and                    
                        (esTipoEdificio       ?e ?tipoedif)  ;El edificio sea de ese tipo
                        (forall (?tiporec - tipoRecurso)     ;Para cada recurso
                            (or
                                (not (necesitaEdif   ?tipoedif   ?tiporec)) ;O no lo necesita
                                (and
                                    (necesitaEdif   ?tipoedif    ?tiporec)  ; o si lo neccesita
                                    (tengoRecurso ?tiporec)             ;Disponiemos del recurso                                  
                                )
                            )
                        )
                    )
                )
            )
        :effect 
            (and 
                (en              ?e     ?loc )       ;El edif se localiza en loc
                (edifConstruido  ?e)                 ;El edificio ha sido construido 
            )
    )


    (:action reclutar
        :parameters ( ?edificio - Edificio  ?u - Unidad  ?loc - Localizacion)
        :precondition (and
            
            (edifConstruido      ?edificio)         ;El edificio debe estar construido
            (en                  ?edificio ?loc)    ;En la localización concreta
            ;(not (uniNoAsig            ?u))         ;La unidad que reclutamos no puede estar asignada - si lo descomentamos produce inconsistencias
            
            ;La unidad que creamos no puede estar en ninguna localizacion porque aun no ha nacido
            (not (exists ( ?loca - Localizacion)(and (en ?u ?loca )) ))
            
            ;Compruebo que la unidad es del tipo que se crea en el tipo de edificio
            (exists (?tipoUni - tipoUnidad ?tipoEdif - tipoEdificio)
                
                (and
                    (esTipoEdificio       ?edificio     ?tipoedif)
                    (esTipoUnidad         ?u            ?tipoUni)
                    (reclutaEn            ?tipoUni      ?tipoEdif)
                )
            
            )

            ;Compruebo los recursos que necesitan las unidades
            (forall (?tiporec - tipoRecurso)                                ;Para cada tipo de recurso
                (exists (?tipoUni - tipoUnidad)                             ;Existe un tipo de unidad tal que
                    (and
                        (esTipoUnidad       ?u     ?tipoUni)                ;La unidad es de ese tipo y 
                        (or
                            ( not (necesitaUni          ?tipoUni   ?tiporec ))    ;O no necesita el recurso
                            (and
                                (necesitaUni          ?tipoUni   ?tiporec )       ;O si lo necesita
                                (tengoRecurso         ?tiporec)             ;Ya lo tenemos
                            )
                        )
                    )
                )              
            )




            ;(exists (?tipoUni - tipoUnidad)                             ;Existe un tipo de unidad tal que
            ;        (and
            ;            (esTipoUnidad       ?u     ?tipoUni)                ;La unidad es de ese tipo y 
            ;            (forall (?tiporec - tipoRecurso)                                ;Para cada tipo de recurso
            ;            (or
            ;                ( not (necesitaUni          ?tipoUni   ?tiporec ))    ;O no necesita el recurso
            ;                (and
            ;                    (necesitaUni          ?tipoUni   ?tiporec )       ;O si lo necesita
            ;                    (tengoRecurso         ?tiporec)             ;Ya lo tenemos
            ;                )
            ;            )
            ;        )
            ;    )              
            ;)





        
        )
        :effect 
        (and 
            (uniNoAsig  ?u)             ;dejo libre la unidad
            (en         ?u ?loc)        ;unidad se crea en la localizacion
        )
    )
    

)