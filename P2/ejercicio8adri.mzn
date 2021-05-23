% Autor: Adrián Rodríguez Montero
% Ejercicio 8, Construcción Casa

include "globals.mzn";

% 1. Definición de variables y constantes
% Número de tareas 
set of int : NUMTAREAS = 1..9; 
% Número de trabajadores disponibles
set of int : NUMTRABAJADORES = 1..3;
% Tareas
array[NUMTAREAS] of string : strTareas = ["A", "B", "C", "D", "E", "F", "G", "H", "I"];
% Trabajadores necesarios para cada tarea
array[NUMTAREAS] of int : trabajadores = [2, 3, 2, 2, 1, 2, 1, 1, 2];
% Duración de las tareas
array[NUMTAREAS] of int : duracion = [7, 3, 1, 8, 2, 1, 1, 3, 2];
% Duración máxima, A empieza en tiempo 1 y sumamos la duración del resto de tareas como si fuera secuencial para obtener la duracion máxima
int: DURACIONMAXIMA = sum(i in NUMTAREAS)(duracion[i]);
% Número máximo de días
set of int : NUMDIASMAX = 1..DURACIONMAXIMA;
% Tiempo de inicio de cada tarea
array[NUMTAREAS] of var NUMDIASMAX : inicio;
% Tiempo transcurrido hasta finalizar cada tarea
array[NUMTAREAS] of var NUMDIASMAX : fin;
% Tiempo empleado
var int : tiempo;
% Matriz en la que cada fila es un trabajador y cada columna es un día ponemos el máximo de días posibles porque no sabemos cuando acabaremos
array[NUMTRABAJADORES,NUMDIASMAX] of var 0..9 : matriz;

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
% Empieza cuando acaba A
constraint inicio[4] >= (inicio[1] + duracion[1]);
% Obtenemos el tiempo cuando finaliza
constraint fin[4] = (inicio[4] + duracion[4]);
% Tarea E
% Empieza cuando acaba C y D
constraint inicio[5] >= (inicio[3] + duracion[3]) /\ inicio[5] >= (inicio[4] + duracion[4]);
% Obtenemos el tiempo cuando finaliza
constraint fin[5] = (inicio[5] + duracion[5]);
% Tarea F
% Empieza cuando acaba C y D
constraint inicio[6] >= (inicio[3] + duracion[3]) /\ inicio[6] >= (inicio[4] + duracion[4]);
% Obtenemos el tiempo cuando finaliza
constraint fin[6] = (inicio[6] + duracion[6]);
% Tarea G
% Empieza cuando acaban C y D
constraint inicio[7] >= (inicio[3] + duracion[3]) /\ inicio[7] >= (inicio[4] + duracion[4]);
% Obtenemos el tiempo cuando finaliza
constraint fin[7] = (inicio[7] + duracion[7]);
% Tarea H
% Empieza cuando acaba A
constraint inicio[8] >= (inicio[1] + duracion[1]); 
% Obtenemos el tiempo cuando finaliza
constraint fin[8] = (inicio[8] + duracion[8]);
% Tarea I
% Empieza cuando acaban F y H
constraint inicio[9] >= (inicio[6] + duracion[6]) /\ inicio[9] >= (inicio[8] + duracion[8]);
% Obtenemos el tiempo cuando finaliza
constraint fin[9] = (inicio[9] + duracion[9]);

% Construimos la matriz en la que cada fila es un trabajador y cada columna es un día
% Las tareas asociadas a cada trabajador deben estar en el tiempo prefijado fuera de ese tiempo no se puede empezar una tarea o seguir con ella
constraint forall(i in NUMTRABAJADORES, j in NUMDIASMAX)(if matriz[i,j]!= 0 then (j>=inicio[matriz[i,j]] /\ j< inicio[matriz[i,j]]+duracion[matriz[i,j]])  endif);
% Compobamos por columnas que la suma de los trabajadores que realizan una tarea desde su inicio hasta su final debe ser igual a los trabajadores que se requieren para realizar dicha tarea
constraint forall(k in NUMTAREAS, j in 0..duracion[k]-1)(count([matriz[i,inicio[k]+j] | i in NUMTRABAJADORES],k)==trabajadores[k]);

% 3. Definición del problema
% Hay que restar uno porque si no restamos 1 estaríamos mostrando el tiempo en el que empezaría la siguiente tarea
tiempo = max(fin) - 1;
solve minimize tiempo;

% 4. Definición del output
output ["TIEMPO MINIMO = \(tiempo)\n"];
output ["TAREA \(strTareas[i]) empieza en tiempo \(inicio[i])\n" | i in NUMTAREAS];
% Imprimimos la matriz
output["\nDia      "];
output[if i < 10 then "0\(i)   " else "\(i)   " endif| i in NUMDIASMAX];
output["\n"];
output["Trab1   "];
output[if (fix(matriz[1,i] > 0)) then "\(strTareas[fix(matriz[1,i])])  " else " 0   " endif | i in NUMDIASMAX];
output["\n"];
output["Trab2   "];
output[if (fix(matriz[2,i] > 0)) then "\(strTareas[fix(matriz[2,i])])  " else " 0   " endif | i in NUMDIASMAX];
output["\n"];
output["Trab3   "];
output[if (fix(matriz[3,i] > 0)) then "\(strTareas[fix(matriz[3,i])])  " else " 0   " endif | i in NUMDIASMAX];