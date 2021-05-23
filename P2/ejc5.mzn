include "globals.mzn";

set of int: col = 1..3; %aulas
set of int: fil = 1..4; %horas
set of int: set_grupos = 1..4; % grupos
set of int: set_profes = 1..4; %profesores

array [fil,col] of var 1..12: grupos; %Matriz donde vemos el horario de los grupos, filas-horas y columnas-asignatura 
                                      % cada entrada de la matriz hace referencia al indice de la clase del vector clases
                                      
array [fil,col] of var set_profes: profes;   %Matriz donde vemos el horario de los profesores

array[1..12] of string: clases = ["TSI-G1","TSI-G2","TSI-G3","TSI-G4",
                                  "FBD-G1","FBD-G2","FBD-G3","FBD-G4",
                                  "IA -G1","IA -G2","IA -G3","IA -G4"];

%Cada aula solo puede estar ocupada por un unico profesor al mismo tiempo
constraint forall(i in fil)(all_different([profes[i,j] | j in col]));

%Cada grupo da una hora a la semana
constraint all_different(grupos);

%Cada grupo recibe docencia de una única asignatura en cada momento,i.e., G1 no puede tener clase a la misma hora de dos asignaturas distintas
constraint forall( i in fil )( %%Para cada hora
             (grupos[i,1] mod 4) != (grupos[i,2] mod 4) /\ (grupos[i,1] mod 4)!= (grupos[i,3] mod 4) /\ (grupos[i,2] mod 4) != (grupos[i,3] mod 4)              
            );
%Cada profesor imparte docencia en un unico subgrupo en cada momento
constraint forall( i in fil )( %%Para cada hora
             (profes[i,1] mod 4) != (profes[i,2] mod 4) /\ (profes[i,1] mod 4)!= (profes[i,3] mod 4) /\ (profes[i,2] mod 4) != (profes[i,3] mod 4)              
            );

%El profesor 2, solo puede dar clase en el Aula 2 y el profesor 4 solo puede dar clase en el aula 3
constraint forall( i in fil)(
            forall(j in col)(
               ( ( j!=2 -> profes[i,j]!=2) )
            )
          );
constraint forall( i in fil)(
            forall(j in col)(
               (  (j!=3 -> profes[i,j]!=4))
            )
          );
%La asignatura de TSI solo se puede dar en las Aulas 2 o 3
constraint forall( i in fil)(

             grupos[i,1] != 1 /\ grupos[i,1] != 2 /\grupos[i,1] != 3 /\grupos[i,1] != 4 
             
           );
              
%Los grupos 1 y 3  de IA deben tener clase de 9-10 y de 11-12 respectivamente.
%El grupo 3 de IA tiene que dar clase en el Aula 1 obligatoriamente.
  %IA: G1 --> clase[9] ; G2 --> clse[11]
  %Horario: 9 a 10 --> fila 1 ; 11 a 12 --> fila 3
constraint grupos[1,1] == 9 \/ grupos[1,2] == 9 \/ grupos[1,3] == 9;
constraint grupos[3,1] == 11; 

%Restricciones sobre el horario de los profesores.
  %Solo modificamos el profesor 2 pues es el que tiene restricciones 
constraint forall( i in fil where i!=1 /\ i != 2)( %Para las horas que no sean priemra y segunda
            forall( j in col)(
              profes[i,j] != 2 %El profesor 2 no imparte clase
             )
            );
              
%Restricciones sobre las asignaturas de los profesores
constraint forall( i in fil)(
            forall(j in col)(
              ((profes[i,j] == 1) -> (grupos[i,j] == 5 \/ grupos[i,j] == 6 \/ grupos[i,j] == 7 \/ grupos[i,j] == 9)))); 
              
constraint forall( i in fil)(
            forall(j in col)(
              ((profes[i,j] == 2) -> (grupos[i,j] == 10 \/ grupos[i,j] == 8)))); 

constraint forall( i in fil)(
            forall(j in col)(
              ((profes[i,j] == 3) -> (grupos[i,j] == 11 \/ grupos[i,j] == 1 \/ grupos[i,j] == 2 \/ grupos[i,j] == 4))));                                 

constraint forall( i in fil)(
            forall(j in col)(
              ((profes[i,j] == 4) -> (grupos[i,j] == 12 \/ grupos[i,j] == 3)))); 
    

output ["\n\tAula1 \t\tAula2 \t\tAula3 \n"] ++
[ "9-10   "] ++ [if (fix(grupos[1,z]) == k /\ fix(profes[1,z]) == j) then "\t\(clases[k])" ++ "P\(j)" else "" endif | z in 1..3,k in 1..12,j in 1..4 ] ++
[ "\n10-11"] ++ [if (fix(grupos[2,z]) == k /\ fix(profes[2,z]) == j) then "\t\(clases[k])" ++ "P\(j)" else "" endif | z in 1..3,k in 1..12,j in 1..4 ] ++
[ "\n11-12"] ++ [if (fix(grupos[3,z]) == k /\ fix(profes[3,z]) == j) then "\t\(clases[k])" ++ "P\(j)" else "" endif | z in 1..3,k in 1..12,j in 1..4 ] ++
[ "\n12-13"] ++ [if (fix(grupos[4,z]) == k /\ fix(profes[4,z]) == j) then "\t\(clases[k])" ++ "P\(j)" else "" endif | z in 1..3,k in 1..12,j in 1..4 ];



solve satisfy;


