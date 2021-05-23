include "globals.mzn";

set of int: fil = 1..6; %Horario de 8 a 14
set of int: col = 1..5; %Dias de la semana
set of int: prof = 1..5;%Numero de profesores, el ultimo es el vigilante del recreo

array[1..10] of string: strasignaturas = ["A1","A2","A3","A4","A5","A6","A7","A8","A9","NA"];
array[1..10] of int:    asignaturas    = [ 1,   2,   3,   4,   5,   6,   7,   8,   9  ,  10];
array[1..10] of int: horasAsig         = [ 4,   2,   4,   4,   4,   2,   2,   2,   1  ,  5 ]; %Numero de horas /asignatura
array[fil,col] of var 1..10: horario; %Matriz. Fila-> hora, Col->dia. Cada entrada asignatura que se imparte tal dia a tal hora
array[prof] of string: strProf = ["P1","P2","P3","P4","Precreo"];
array[fil,col] of var 1..5: profes;%Matriz analoga a la anterior pero cada entrada representa el profesor que imparte la asignatura tal dia a tal hora

%Las asignatuas se imparte en su numero de horas    
constraint forall(k in 1..10)(count([horario[i,j] | i in fil, j in col],k)==horasAsig[k]);


%La cuarta hora se deja para el recreo
constraint forall(j in col)(horario[4,j] == 10);          


%Las asignaturas 1,3,4,5,8 se imparten en bloques de dos horas consecutias
constraint forall(i in 2..5)( forall(j in col )( ( horario[i,j]==1 -> horario[i-1,j]==1 \/ horario[i+1,j]==1)  ));
constraint forall(i in [1] )( forall(j in col )( ( horario[i,j]==1 ->  horario[i+1,j]==1)  ));
constraint forall(i in [6] )( forall(j in col )( ( horario[i,j]==1 ->  horario[i-1,j]==1)  ));

constraint forall(i in 2..5)( forall(j in col )( ( horario[i,j]==3 -> horario[i-1,j]==3 \/ horario[i+1,j]==3)  ));
constraint forall(i in [1] )( forall(j in col )( ( horario[i,j]==3 ->  horario[i+1,j]==3)  ));
constraint forall(i in [6] )( forall(j in col )( ( horario[i,j]==3 ->  horario[i-1,j]==3)  ));

constraint forall(i in 2..5)( forall(j in col )( ( horario[i,j]==4 -> horario[i-1,j]==4 \/ horario[i+1,j]==4)  ));
constraint forall(i in [1] )( forall(j in col )( ( horario[i,j]==4 ->  horario[i+1,j]==4)  ));
constraint forall(i in [6] )( forall(j in col )( ( horario[i,j]==4 ->  horario[i-1,j]==4)  ));

constraint forall(i in 2..5)( forall(j in col )( ( horario[i,j]==5 -> horario[i-1,j]==5 \/ horario[i+1,j]==5)  ));
constraint forall(i in [1] )( forall(j in col )( ( horario[i,j]==5 ->  horario[i+1,j]==5)  ));
constraint forall(i in [6] )( forall(j in col )( ( horario[i,j]==5 ->  horario[i-1,j]==5)  ));

constraint forall(i in 2..5)( forall(j in col )( ( horario[i,j]==8 -> horario[i-1,j]==8 \/ horario[i+1,j]==8)  ));
constraint forall(i in [1] )( forall(j in col )( ( horario[i,j]==8 ->  horario[i+1,j]==8)  ));
constraint forall(i in [6] )( forall(j in col )( ( horario[i,j]==8 ->  horario[i-1,j]==8)  ));
                           
%Las asignaturas 2,6,7 y 9 se imparten en huecos de una hora                                            
constraint forall(i in 2..5)( forall(j in col )( ( horario[i,j]==2 -> horario[i-1,j]!=2 /\ horario[i+1,j]!=2)  ));
constraint forall(i in [1] )( forall(j in col )( ( horario[i,j]==2 ->  horario[i+1,j]!=2)  ));
constraint forall(i in [6] )( forall(j in col )( ( horario[i,j]==2 ->  horario[i-1,j]!=2)  ));

constraint forall(i in 2..5)( forall(j in col )( ( horario[i,j]==6 -> horario[i-1,j]!=6 /\ horario[i+1,j]!=6)  ));
constraint forall(i in [1] )( forall(j in col )( ( horario[i,j]==6 ->  horario[i+1,j]!=6)  ));
constraint forall(i in [6] )( forall(j in col )( ( horario[i,j]==6 ->  horario[i-1,j]!=6)  ));

constraint forall(i in 2..5)( forall(j in col )( ( horario[i,j]==7 -> horario[i-1,j]!=7 /\ horario[i+1,j]!=7)  ));
constraint forall(i in [1] )( forall(j in col )( ( horario[i,j]==7 ->  horario[i+1,j]!=7)  ));
constraint forall(i in [6] )( forall(j in col )( ( horario[i,j]==7 ->  horario[i-1,j]!=7)  ));

constraint forall(i in 2..5)( forall(j in col )( ( horario[i,j]==9 -> horario[i-1,j]!=9 /\ horario[i+1,j]!=9)  ));
constraint forall(i in [1] )( forall(j in col )( ( horario[i,j]==9 ->  horario[i+1,j]!=9)  ));
constraint forall(i in [6] )( forall(j in col )( ( horario[i,j]==9 ->  horario[i-1,j]!=9)  ));

%En cada dia de la semana solo se puede impartir, como maximo, un bloque de cada asignatura
  
  %Para las asignatuas de dos bloques
  constraint forall(i in fil)(forall (j in col)(
    (horario[i,j]==1 \/ horario[i,j]==3 \/ horario[i,j]==4 \/ horario[i,j]==5 \/ horario[i,j]==8) 
      -> (count([horario[k,j] | k in fil],horario[i,j])==2)
    ));

  %Para las asignaturas de un bloque
  constraint forall(i in fil)(forall (j in col)(
    (horario[i,j]==2 \/ horario[i,j]==6 \/ horario[i,j]==7 \/ horario[i,j]==9)
    -> (count([horario[k,j] | k in fil],horario[i,j])==1)
  ));

%Ajusto la asignatura del profesor
  constraint forall(j in col)(profes[4,j] == 5);  %Meto el del recreo
  constraint forall( i in fil where i!= 4)(forall(j in col)(profes[i,j] != 5));
  constraint forall( i in fil where i!= 4)(forall(j in col)((profes[i,j]==1->(horario[i,j]==1 \/ horario[i,j]==3)) )); %P1-> A1 y A3         
  constraint forall( i in fil where i!= 4)(forall(j in col)((profes[i,j]==2->(horario[i,j]==4 \/ horario[i,j]==5)) )); %P2-> A4 y A5  
  constraint forall( i in fil where i!= 4)(forall(j in col)((profes[i,j]==3->(horario[i,j]==6 \/ horario[i,j]==9)) )); %P3-> A6 y A9  
  constraint forall( i in fil where i!= 4)(forall(j in col)((profes[i,j]==4->(horario[i,j]==2 \/ horario[i,j]==7 \/ horario[i,j]==8)))); %P4-> A2, A7 y A8
  
%Cada profesor solo puede impartir un bloque de alguna de sus asignatuas cada día, excepto P4, que puede impartir más de una
 constraint forall(i in fil)(forall (j in col)(
    ( (profes[i,j]==1 \/ profes[i,j]==2 \/ profes[i,j]==3) /\ (horario[i,j]!=1 /\ horario[i,j]!=3 /\ horario[i,j]!=4 /\ horario[i,j]!=5 /\ horario[i,j]!=8)) 
      -> (count([profes[k,j] | k in fil],profes[i,j])==1)
    ));
    
 constraint forall(i in fil)(forall (j in col)(
    ( (profes[i,j]==1 \/ profes[i,j]==2 \/ profes[i,j]==3) /\ (horario[i,j]!=2 /\ horario[i,j]!=6 /\ horario[i,j]!=7 /\ horario[i,j]!=9)) 
      -> (count([profes[k,j] | k in fil],profes[i,j])==2)
    ));
     
        
% primer bloque de cada asignatura se posiciona en un día anterior que el segundo bloque.
 constraint forall( i in fil)( forall( j in [1])(
                 forall(l in 1..7)( %Asignatura
                 (horario[i,j]==l->(count([horario[k,j+1] | k in fil],l)!=0) )
               )));      
 constraint forall( i in fil)( forall( j in [5])(
                 forall(l in [1,2,3,4])(
                 (horario[i,j]==l->(count([horario[k,j-1] | k in fil],l)!=0) )
               )));   
 constraint forall( i in fil)( forall( j in 2..4)(
                 forall(l in [1,2,3,4])(
                 (horario[i,j]==l->(count([horario[k,j+1] | k in fil],l)!=0 \/ count([horario[k,j-1] | k in fil],l)!=0) )
               )));       

                              
                    
                          
%Varios profesores tienen ciertas restricciones horarias:
  %1) El profesor 1 solo puede dar clase en las dos ultimas horas de la mañana
  constraint forall( i in fil where i!= 5 /\ i!= 6)( forall(j in col)(profes[i,j]!=1 ));
  %2) El profesor 2 solo puede dar clase en las dos primeras horas de la mañana
  constraint forall( i in fil where i!=1 /\ i!=2)( forall(j in col)(profes[i,j]!=2));
  %3) El profesor 3 solo puede dar clase en la hora justo antes del recreo
  constraint forall( i in fil where i != 3)(forall (j in col)(profes[i,j]!=3));
  
%Varias asignatuas también deben ser impartidas ciertos días concretos de la semana
  %A1: lunes o martes
  constraint forall(i in fil)(forall(j in col where j!=1 /\ j!=2 )(horario[i,j]!=1));
  %A3: miercoles o jueves
  constraint forall(i in fil)(forall(j in col where j!=3 /\ j!= 4)(horario[i,j]!=3));
  %A4: lunes o martes
  constraint forall(i in fil)(forall(j in col where j!=1 /\ j!=2 )(horario[i,j]!=4));
  %A5: jueves o viernes
  constraint forall(i in fil)(forall(j in col where j!=4 /\ j!=5 )(horario[i,j]!=5));
  %A6: miercoles o viernes
  constraint forall(i in fil)(forall(j in col where j!=3 /\ j!=5 )(horario[i,j]!=6));
  %A7: martes o viernes
  constraint forall(i in fil)(forall(j in col where j!=2 /\ j!=5 )(horario[i,j]!=7));
  %A8: miercoles
  constraint forall(i in fil)(forall(j in col where j!=3 )(horario[i,j]!=8));
  %A9: lunes
  constraint forall(i in fil)(forall(j in col where j!=1 )(horario[i,j]!=9));
  

output ["\n\t\t\tLunes   \tMartes   \tMiercoles   \tJueves   \tViernes \n"] ++
[ "8-9   ->"] ++ [if (fix(horario[1,z]) == k /\ fix(profes[1,z]) == j) then "\t\t\(asignaturas[k]) - " ++ "P\(j)" else "" endif | z in col,k in 1..10,j in 1..5 ] ++
[ "\n9-10  -> "] ++ [if (fix(horario[2,z]) == k /\ fix(profes[2,z]) == j) then "\t\t\(asignaturas[k]) - " ++ "P\(j)" else "" endif | z in col,k in 1..10,j in 1..5 ] ++
[ "\n10-11 -> "] ++ [if (fix(horario[3,z]) == k /\ fix(profes[3,z]) == j) then "\t\t\(asignaturas[k]) - " ++ "P\(j)" else "" endif | z in col,k in 1..10,j in 1..5 ] ++
[ "\n11-12 -> "] ++ [if (fix(horario[4,z]) == k /\ fix(profes[4,z]) == j) then "\t\tNa" else "" endif | z in col,k in 1..10,j in 1..5 ] ++
[ "\n12-13 -> "] ++ [if (fix(horario[5,z]) == k /\ fix(profes[5,z]) == j) then "\t\t\(asignaturas[k]) - " ++ "P\(j)" else "" endif | z in col,k in 1..10,j in 1..5 ] ++
[ "\n13-14 -> "] ++ [if (fix(horario[6,z]) == k /\ fix(profes[6,z]) == j) then "\t\t\(asignaturas[k]) - " ++ "P\(j)" else "" endif | z in col,k in 1..10,j in 1..5 ];
