% Autor: Adrián Rodríguez Montero
% Ejercicio 5, Asignación de Horarios

include "globals.mzn";

% 1. Definición de variables y constantes
% Número de aulas
set of int : NUMAULAS = 1..3;
% Horas disponibles
set of int : HORAS = 9..12;
% Número de asignaturas combinadas con los grupos
set of int : NUMASIGNATURASGRUPOS = 1..12;
% Número de profesores
set of int : NUMPROFESORES = 1..4;
% Array de las aulas
array[NUMAULAS] of string : strAulas = ["Aula 1", "Aula 2", "Aula 3"];
% Array de las asignaturas con los grupos
array[NUMASIGNATURASGRUPOS] of string : strAsignaturasGrupos = ["IA-G1 ", "IA-G2 ", "IA-G3 ", "IA-G4 ", "TSI-G1", "TSI-G2", "TSI-G3", "TSI-G4", "FBD-G1", "FBD-G2", "FBD-G3", "FBD-G4"];
% Array de los profesores
array[NUMPROFESORES] of string  : strProfesores = ["P1", "P2", "P3", "P4"];
% Array multidimensional con la asignatura y el grupo que se imparte en una hora y un aula
array[HORAS,NUMAULAS] of var NUMASIGNATURASGRUPOS : asignaturasGrupos;
% Array multidimensional con el profesor seleccionado para una hora y un aula 
array[HORAS,NUMAULAS] of var NUMPROFESORES : profesores;

% 2. Definición de restricciones
% Cada grupo recibe docencia de una asignatura por semana
constraint all_different(asignaturasGrupos);
% Cada grupo recibe docencia de una única asignatura en cada momento
constraint forall(i in HORAS)((asignaturasGrupos[i,1] mod 4) != (asignaturasGrupos[i,2] mod 4) /\ (asignaturasGrupos[i,1] mod 4) != (asignaturasGrupos[i,3] mod 4) /\ (asignaturasGrupos[i,2] mod 4) != (asignaturasGrupos[i,3]) mod 4);
% Cada profesor imparte docencia de un único grupo/asignatura en cada momento
constraint forall(i in HORAS)(all_different([profesores[i,j] | j in NUMAULAS]));
% El profesor 2 solo da clase en el Aula 2
constraint forall(i in HORAS)(forall(j in NUMAULAS where j != 2)(profesores[i,j] != 2));
% El profesor 4 solo da clase en el Aula 3
constraint forall(i in HORAS)(forall(j in NUMAULAS where j != 3)(profesores[i,j] != 4));
% La asignatura de TSI solo se puede dar en las Aulas 2 o 3
constraint forall(i in HORAS)(asignaturasGrupos[i,1] != 5 /\ asignaturasGrupos[i,1] != 6 /\ asignaturasGrupos[i,1] != 7 /\ asignaturasGrupos[i,1] != 8);
% El grupo 1 de IA tiene clase de 9:00 a 10:00 
constraint forall(i in HORAS where i != 9)(forall(j in NUMAULAS)(asignaturasGrupos[i,j] != 1));
% El grupo 3 de IA tiene clase de 11:00 a 12:00 y tiene que dar clase en el Aula 1
%constraint forall(i in HORAS)(forall(j in NUMAULAS)((i != 11 /\ j != 1) -> (asignaturasGrupos[i,j] != 3)));

constraint asignaturasGrupos[11,1] == 3;

% Restricciones horarias de profesores
% El profesor 2 solo puede dar clase de 9:00 a 10:00 y de 10:00 a 11:00
constraint forall(i in HORAS where (i != 9 /\ i != 10))(forall(j in NUMAULAS) (profesores[i,j] != 2));
% Docencia de cada profesor
% Profesor 1
constraint forall(i in HORAS)(forall(j in NUMAULAS)((profesores[i,j] == 1) -> (asignaturasGrupos[i,j] == 9 \/ asignaturasGrupos[i,j] == 10 \/  asignaturasGrupos[i,j] == 11 \/ asignaturasGrupos[i,j] == 1)));
% Profesor 2
constraint forall(i in HORAS)(forall(j in NUMAULAS)((profesores[i,j] == 2) ->  (asignaturasGrupos[i,j] == 2 \/ asignaturasGrupos[i,j] == 12)));
% Profesor 3
constraint forall(i in HORAS)(forall(j in NUMAULAS)((profesores[i,j] == 3) ->  (asignaturasGrupos[i,j] == 3 \/ asignaturasGrupos[i,j] == 5 \/  asignaturasGrupos[i,j] == 6 \/ asignaturasGrupos[i,j] == 8)));
% Profesor 4
constraint forall(i in HORAS)(forall(j in NUMAULAS)((profesores[i,j] == 4) ->  (asignaturasGrupos[i,j] == 4 \/ asignaturasGrupos[i,j] == 7)));

% 3. Definición del problema
solve satisfy;

% 4. Definición del output
output ["\t\t\(strAulas[1]) \t    \(strAulas[2]) \t     \(strAulas[3])"];
output ["\n  9 - 10 -> "];
output ["\(strAsignaturasGrupos[fix(asignaturasGrupos[9,i])])  \(strProfesores[fix(profesores[9,i])])\t" | i in NUMAULAS];
output ["\n 10 - 11 -> "];
output ["\(strAsignaturasGrupos[fix(asignaturasGrupos[10,i])])  \(strProfesores[fix(profesores[10,i])])\t" | i in NUMAULAS];
output ["\n 11 - 12 -> "];
output ["\(strAsignaturasGrupos[fix(asignaturasGrupos[11,i])])  \(strProfesores[fix(profesores[11,i])])\t" | i in NUMAULAS];
output ["\n 12 - 13 -> "];
output ["\(strAsignaturasGrupos[fix(asignaturasGrupos[12,i])])  \(strProfesores[fix(profesores[12,i])])\t" | i in NUMAULAS];