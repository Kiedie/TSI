% Autor: Adrián Rodríguez Montero
% Ejercicio 7, Construcción Avión

include "globals.mzn";

% 1. Definición de variables y constantes
% Número de tareas 
set of int : NUMTAREAS = 1..12; 
% Tareas
array[NUMTAREAS] of string : strTareas = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"];
% Duración de las tareas
array[NUMTAREAS] of int : duracion = [60, 10, 5, 5, 5, 2, 7, 3, 7, 2, 5, 1];
% Inicio de cada tarea
array[NUMTAREAS] of var int : inicio;
% Final de cada tarea
array[NUMTAREAS] of var int : fin;
% Tiempo final
var int : tiempo;

% 2. Definición de restricciones
% Tarea A
% Empieza en tiempo 1
constraint inicio[1] = 1;
% Obtenemos el tiempo cuando finaliza
constraint fin[1] = (inicio[1] + duracion[1]);
% Tarea B
% Empieza cuando acaba A
constraint inicio[2] >= (inicio[1] + duracion[1]);
% Obtenemos el tiempo cuando finaliza
constraint fin[2] = (inicio[2] + duracion[2]);
% Tarea C
% Empieza cuando acaba B
constraint inicio[3] >= (inicio[2] + duracion[2]); 
% Obtenemos el tiempo cuando finaliza
constraint fin[3] = (inicio[3] + duracion[3]);
% Tarea D
% Empieza cuando acaba C
constraint inicio[4] >= (inicio[3] + duracion[3]);
% Obtenemos el tiempo cuando finaliza
constraint fin[4] = (inicio[4] + duracion[4]);
% Tarea E
% Empieza cuando acaba D
constraint inicio[5] >= (inicio[4] + duracion[4]);
% Obtenemos el tiempo cuando finaliza
constraint fin[5] = (inicio[5] + duracion[5]);
% Tarea F
% Empieza cuando acaba D
constraint inicio[6] >= (inicio[4] + duracion[4]);
% Obtenemos el tiempo cuando finaliza
constraint fin[6] = (inicio[6] + duracion[6]);
% Tarea G
% Empieza cuando acaban D y E
constraint inicio[7] >= (inicio[4] + duracion[4]);
constraint inicio[7] >= (inicio[5] + duracion[5]);
% Obtenemos el tiempo cuando finaliza
constraint fin[7] = (inicio[7] + duracion[7]);
% Tarea H
% Empieza cuando acaba C
constraint inicio[8] >= (inicio[3] + duracion[3]); 
% Obtenemos el tiempo cuando finaliza
constraint fin[8] = (inicio[8] + duracion[8]);
% Tarea I
% Empieza cuando acaban E y F
constraint inicio[9] >= (inicio[5] + duracion[5]);
constraint inicio[9] >= (inicio[6] + duracion[6]);
% Obtenemos el tiempo cuando finaliza
constraint fin[9] = (inicio[9] + duracion[9]);
% Tarea J
% Empieza cuando acaban E y F
constraint inicio[10] >= (inicio[5] + duracion[5]);
constraint inicio[10] >= (inicio[6] + duracion[6]);
% Obtenemos el tiempo cuando finaliza
constraint fin[10] = (inicio[10] + duracion[10]);
% Tarea K
% Empieza cuando acaban I y J
constraint inicio[11] >= (inicio[9] + duracion[9]);
constraint inicio[11] >= (inicio[10] + duracion[10]);
% Obtenemos el tiempo cuando finaliza
constraint fin[11] = (inicio[11] + duracion[11]);
% Tarea L
% Empieza cuando acaba K
constraint inicio[12] >= (inicio[11] + duracion[11]);
% Obtenemos el tiempo cuando finaliza
constraint fin[12] = (inicio[12] + duracion[12]);

tiempo = max(fin);

% 3. Definición del problema
solve minimize tiempo;

% 4. Definición del output
output ["TIEMPO MINIMO = \(tiempo)\n"];
output ["TAREA \(strTareas[i]) empieza en tiempo \(inicio[i])\n" | i in NUMTAREAS];