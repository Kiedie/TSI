(define (domain mono)
    (:requirements :strips :typing)
    (:types
        movible localizacion - object
        mono caja - movible
    )
    (:predicates
        (en ?obj - movible ?x - localizacion)
        (tienePlatano ?m - mono)
        (sobre ?m - mono ?c - caja)
        (platanoEn ?x - localizacion)
    )
    
    (:action cogerPlatanos
        :parameters (?m - mono ?c - caja)
        :precondition
            (and
                (sobre ?m ?c)
            )
        :effect
            (and
                (tienePlatano ?m)
            )
    )
    
    (:action irA
        :parameters (?m - mono ?x ?y - localizacion)
        :precondition 
            (and
                (en ?m ?y)
            )
        :effect
            (and
                (en ?m ?x)
                (not (en ?m ?y))
            )
    
    )
    
    (:action moverA
        :parameters(?m - mono ?c - caja ?x ?y - localizacion)
        :precondition
            (and
                (en ?m ?x)
                (en ?c ?x)
            )
        :effect
            (and
                (not(en ?m ?x))
                (not(en ?c ?x))
                (en ?m ?y)
                (en ?c ?y)
            )
    )
    
    (:action subirA
        :parameters(?m - mono ?c - caja ?x - localizacion)
        :precondition
            (and
                (en ?m ?x)
                (en ?c ?x)
                (not (sobre ?m ?c))
            )
        :effect
            (and
                (sobre ?m ?c )
            )
    )
    
    (:action bajarDe
        :parameters(?m - mono ?c - caja)
        :precondition
            (and
                (sobre ?m ?c)
            )
        :effect
            (and
                (not (sobre ?m ?c))
            )
    )
)
