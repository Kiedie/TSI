include "globals.mzn";

%Tenemos 20 pacientes y 4 tipos de síntomas
set of int: PACIENTES = 1..20;
set of int: SINTOMAS = 1..4;
%Vector con los diagnosticos de todos los pacientes
array[PACIENTES] of string: strEnfermedades = ["COVID","COVID","COVID","COVID","COVID","COVID","COVID","COVID","COVID","COVID","GRIPE","GRIPE","GRIPE","GRIPE","GRIPE","GRIPE","ALERGIA","ALERGIA","ALERGIA","ALERGIA"];

%Guardamos en un vector las distancia del paciente que vamos a diagnosticar al resto
array[PACIENTES] of var int: distancia_paciente;
%Paciente que vamos a diagnosticar
array[SINTOMAS] of var int: paciente = [0,1,1,1];

%Pacientes ya diagnosticados
array[SINTOMAS] of  int: paciente1  = [0,0,1,1]; %1
array[SINTOMAS] of  int: paciente2  = [1,1,1,0];
array[SINTOMAS] of  int: paciente3  = [1,1,0,1];%3
array[SINTOMAS] of  int: paciente4  = [1,0,0,1];
array[SINTOMAS] of  int: paciente5  = [1,1,0,1];%5
array[SINTOMAS] of  int: paciente6  = [0,1,1,1];
array[SINTOMAS] of  int: paciente7  = [1,0,0,0];%7
array[SINTOMAS] of  int: paciente8  = [1,1,0,0];
array[SINTOMAS] of  int: paciente9  = [0,1,0,0];
array[SINTOMAS] of  int: paciente10 = [0,0,1,1];%10
array[SINTOMAS] of  int: paciente11 = [1,0,0,1];
array[SINTOMAS] of  int: paciente12 = [1,0,0,1];%12
array[SINTOMAS] of  int: paciente13 = [0,1,1,0];
array[SINTOMAS] of  int: paciente14 = [1,1,0,0];
array[SINTOMAS] of  int: paciente15 = [1,0,1,0];%15
array[SINTOMAS] of  int: paciente16 = [1,0,0,0];
array[SINTOMAS] of  int: paciente17 = [1,0,0,1];%17
array[SINTOMAS] of  int: paciente18 = [0,1,1,0];
array[SINTOMAS] of  int: paciente19 = [0,0,1,0];
array[SINTOMAS] of  int: paciente20 = [1,0,1,0];%20

var PACIENTES: sol;
% Calcular la distancia entre dos pacientes.
predicate distancia(array[int] of int: a, array[int] of var int: v, var int: d) = d = sum( i in index_set(a) )( (a[i]-v[i])*(a[i]-v[i]) ) /\ d >= 0 ;

% Recuperar el índice del menor valor de un array (i.e. argmin)
predicate indice_minimo(var int: mi, array[int] of var int: x) = exists( i in index_set(x) )( x[i] = min(x) /\ mi = i );

%Calculamos las distancias
constraint distancia( paciente1,  paciente, distancia_paciente[1]  );
constraint distancia( paciente2,  paciente, distancia_paciente[2]  );
constraint distancia( paciente3,  paciente, distancia_paciente[3]  );
constraint distancia( paciente4,  paciente, distancia_paciente[4]  );
constraint distancia( paciente5,  paciente, distancia_paciente[5]  );
constraint distancia( paciente6,  paciente, distancia_paciente[6]  );
constraint distancia( paciente7,  paciente, distancia_paciente[7]  );
constraint distancia( paciente8,  paciente, distancia_paciente[8]  );
constraint distancia( paciente9,  paciente, distancia_paciente[9]  );
constraint distancia( paciente10, paciente, distancia_paciente[10] );
constraint distancia( paciente11, paciente, distancia_paciente[11] );
constraint distancia( paciente12, paciente, distancia_paciente[12] );
constraint distancia( paciente13, paciente, distancia_paciente[13] );
constraint distancia( paciente14, paciente, distancia_paciente[14] );
constraint distancia( paciente15, paciente, distancia_paciente[15] );
constraint distancia( paciente16, paciente, distancia_paciente[16] );
constraint distancia( paciente17, paciente, distancia_paciente[17] );
constraint distancia( paciente18, paciente, distancia_paciente[18] );
constraint distancia( paciente19, paciente, distancia_paciente[19] );
constraint distancia( paciente20, paciente, distancia_paciente[20] );

%Obtenemos el mínimo
constraint indice_minimo(sol,distancia_paciente);
solve minimize sol;
%Si ejecutamos solve satisfy obtenemos dos soluciones ya que hay dos distancias que valen cero y tienen diagnosticos distintos
%Como en clase nos comentó que solo quería una solución, aplicamos solve minimize sol;
%solve satisfy;
%Moostramos salida
output["Distancia con el resto de pacientes: " ++ show(distancia_paciente)];
output["\nEl paciente más cercano en el número: " ++ show(sol)];
output["\nEl paciente tiene: \(strEnfermedades[fix(sol)])" ];

