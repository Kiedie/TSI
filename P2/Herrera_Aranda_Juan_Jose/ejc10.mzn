include "globals.mzn";

set of int: OBJETOS = 1..12;  %Numero de objetos
var int: tope=5000;           %Peso maximo que no debemos traspasar


array[OBJETOS] of var string: strobjetos= ["Mapa","Cuchillo","Prismaticos","Agua","Linterna","Cerveza","Novela","Manual","Platanos","Caramelos","Cerillas","Espejo"];
array[OBJETOS] of int : pesos =           [50,     500,       1000,         3000,     1500,     3000,       500, 1000,         2000  , 500,       250,          500];
array[OBJETOS] of var int: preferencia =  [100,    90,        75,            95,      85,        10,        5,    70,         90,      20,        50,            40];
array[OBJETOS] of var 0..1: mochila;

%Imponemos que la suma de los pesos de los objetos que metemos en la mochila no cuele del tope
constraint  sum(i in OBJETOS where mochila[i]==1)(pesos[i]) <= tope;

%Maximizamos la suma de las preferencias
solve maximize  sum(i in OBJETOS where mochila[i]==1)(preferencia[i]);


%Mostramos por pantalla
output["Objetos: "];
output[if fix(mochila[i])==1 then "\(strobjetos[i]) " else "" endif | i in OBJETOS ];
output["\nPreferencias: " ++ "\(sum(i in OBJETOS where mochila[i]==1)(preferencia[i]))" ];
output["\nPesoFinal: " ++ "\(sum(i in OBJETOS where mochila[i]==1)(pesos[i])/1000)" ];