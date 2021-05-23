% Use this editor as a MiniZinc scratch book
set of int: POS = 1..8;                              %Conjunto de monedas
set of int: VALUES = 0..3;                           %Cantidad que podemos presentar de una moneda
array[POS] of int: valor = [1,2,5,10,20,50,100,200]; %Valor de las monedas en centimos
array[POS] of var VALUES: cant;                      %Variable con el numero de monedas a devolver
float : recibo = 7.99;                               %Cantidad a devolver

%Ajustamos la cantidad máxima de monedas que podemos tener
%Por ejemplo, de monedas de un céntimos tendremos 0 o 1, porque si tuvieramos 
%2 monedas de 1 céntimos la podemos contabilizar con una moneda de 2 céntimos
constraint cant[1] == 0 \/ cant[1] == 1;                 %1
constraint cant[2] == 0 \/ cant[2] == 1 \/ cant[2] == 2; %2
constraint cant[3] == 0 \/ cant[3] == 1;                 %5
constraint cant[4] == 0 \/ cant[4] == 1;                 %10
constraint cant[5] == 0 \/ cant[5] == 1 \/ cant[2] == 2 ;%20
constraint cant[6] == 0 \/ cant[6] == 1;                 %50
constraint cant[7] == 0 \/ cant[7] == 1;                 %100

%Hacemos que la suma de todas las monedas que tengamos sea igual al recibo
constraint sum(i in 1..8)(cant[i]*valor[i]) == recibo*100;
%Minimizamos
solve minimize(sum(i in 1..8)(cant[i]));

output
["Monedas: ["] ++
["\(cant[i])," | i in 1..8] ++
["]"];

output
["\nTotal monedas: \(sum(i in 1..8)(cant[i]))" ];