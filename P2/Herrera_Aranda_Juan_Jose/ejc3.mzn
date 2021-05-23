include "globals.mzn";
set of int: POS = 1..13;
set of int: color = 1..5;

array[POS] of var color: reg; %posicion del pais que contiene el color
array[color] of var int: precio = [100,      200,      300,    400,    500];    %Precio de cada color
array[color] of string: nombre = ["Verde","Naranja","Rojo","Azul","Amarillo"];
%Array con el nombre de los paises
array[POS] of string: paises = 
  ["Argentina", "Bolivia", "Brasil", "Chile","Colombia",
  "Ecuador","Guayana Fr", "Guyama", "Paraguay","Peru    ", 
  "Surinam", "Uruguay", "Venezuela"];
  
  %Análogo al ejemplo visto en clase con Australia y sus regiones
  constraint reg[1] != reg[2] /\ reg[1] != reg[3] /\ reg[1] != reg[4] /\ reg[1] != reg[9] /\ reg[1] != reg[12];
  constraint reg[2] != reg[3] /\ reg[2] != reg[4] /\ reg[2] != reg[9] /\ reg[2] != reg[10];
  constraint reg[3] != reg[5] /\ reg[3] != reg[7] /\ reg[3] != reg[8] /\ reg[3] != reg[9] /\ reg[3] != reg[10]
          /\ reg[3] != reg[11] /\ reg[3] != reg[12] /\ reg[3] != reg[13];
  constraint reg[4] != reg[10];
  constraint reg[5] != reg[6] /\ reg[5] != reg[10] /\ reg[5] != reg[13];
  constraint reg[6] != reg[10];
  constraint reg[7] != reg[11];
  constraint reg[8] != reg[11] /\ reg[8] != reg[13];
  constraint reg[9] != reg[12];
  
  %Sumamos todos los precios y buscamos aquel que sea minimo
  var int: vari = sum(i in 1..13)(precio[reg[i]]);
  solve minimize(vari);
  output ["total: \(sum(i in 1..13)(precio[reg[i]]))"];
  output ["\n"];
  output ["\n\(paises[i]) \t\t\t: \(color[reg[i]])   \t\t\t  \(precio[reg[i]])" | i in 1..13];