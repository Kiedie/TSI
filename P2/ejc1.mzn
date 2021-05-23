include "globals.mzn";

%Declaracion de variables
set of int: POS = 1..9;      %Numero de letras
set of int: VALUES = 0..9;    %Valores que pueden tomar las letras
array[POS] of string: letras=["I","E","S","X","Y","V","W","N","T"];
array[POS] of var VALUES: n;  %Suma

%restricciones
%Cada letra codifica un único dígito
constraint all_different(n); 
%Hacemos que la suma sea como tiene que ser
constraint 
  100*n[3] + 10*n[1] + n[4] +
  20000*n[3] + 2000*n[2] + 200*n[6] + 20*n[2] + 2*n[8] 
  == 100000*n[9] + 10000*n[7] + 1000*n[2] + 100*n[8] + 10*n[9] + n[5]; 


% output ["\(letras[i]) = \(n[i]) \n" | i in POS];
output["      " ++"\(n[3]) "++"\(n[1]) "++"\(n[4])\n"];
output["+ \(n[3]) \(n[2]) \(n[6]) \(n[2]) \(n[8])\n"];
output["+ \(n[3]) \(n[2]) \(n[6]) \(n[2]) \(n[8])\n"];
output["---------\n"];
output["\(n[9]) \(n[7]) \(n[2]) \(n[8]) \(n[9]) \(n[5])\n"];

solve satisfy;