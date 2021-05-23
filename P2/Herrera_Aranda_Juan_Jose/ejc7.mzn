include "globals.mzn";
%Tenemos 12 tareas que s representan con letras
set of int: letras=1..12;
array[letras] of var string: strletras = ["A","B","C","D","E","F","G","H","I","J","K","L"];
%Duracion de cada tarea
array[letras] of var int: duracion = [60,10,5,5,5,2,7,3,7,2,5,1];
%Vector de cuando se inicia y acaba la tarea y el tiempo total
array[letras] of var int: ini;
array[letras] of var int: fin;


%Tarea A
constraint  ini[1] = 1;
constraint  fin[1] = (60 + ini[1]);
%Tarea B
constraint ini[2] >= fin[1]; 
constraint fin[2] = (ini[2] + duracion[2]);
%Tarea C
constraint ini[3] >= fin[2]; 
constraint fin[3] = (ini[3] + duracion[3]);
%Tarea D
constraint ini[4] >= fin[3];
constraint fin[4] = (ini[4] + duracion[4]);
%Tarea E
constraint ini[5] >= fin[4];
constraint fin[5] = (ini[5] + duracion[5]);
%Tarea F
constraint ini[6] >= fin[4];
constraint fin[6] = (ini[6] + duracion[6]);
%Tarea G
constraint ini[7] >= fin[4] /\ ini[7] >= fin[5];
constraint fin[7] = (ini[7] + duracion[7]);
%Tarea H
constraint ini[8] >= fin[3]; 
constraint fin[8] = (ini[8] + duracion[8]);
%Tarea I
constraint ini[9] >= fin[5] /\ ini[9] >= fin[6]; 
constraint fin[9] = (ini[9] + duracion[9]);
%Tarea J
constraint ini[10] >= fin[5] /\ ini[10] >= fin[6]; 
constraint fin[10] = (ini[10] + duracion[10]);
%Tarea K
constraint ini[11] >= fin[9] /\ ini[11] >= fin[10]; 
constraint fin[11] = (ini[11] + duracion[11]);
%Tarea L
constraint ini[12] >= fin[11];
constraint fin[12] = (ini[12] + duracion[12]);


%Minimizamos el tiempo total que es el tiempo que tarda en ejecutarse la ultima tarea que es la L. corresponde a fin[12]
solve minimize fin[12];
output ["TIEMPO MINIMO = \(fin[12])\n"];
output ["TAREA \(strletras[i]) empieza en tiempo \(ini[i])\n" | i in 1..12];