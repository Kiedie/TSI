include "globals.mzn";

set of int: semana=1..7;        %Numero de dias de la semana
set of int: num_amigos = 1..7;  %Numero de amigos que participan en la tertulia

array[semana] of var string: stramigos = ["Juan","Maria","Carmen","Francisco","Marta","Olga","Pepe"]; 
array[semana] of string: strdias = ["Lunes","Martes","Miercoles","Jueves","Viernes","Sabado","Domingo"];
array[semana] of var bool: dia;          %Variable que nos dirá cual es el dia de la semana
array[num_amigos] of var bool: sincero;  %Variable que nos diera quien dice la verdad
array[num_amigos,semana] of var 0..1: tabla; %Tabla auxiliar para descubir al sincero y el dia de la semana. Filas->amigos y columnas->dia

%Juan dice que hoy es martes
constraint forall(j in semana)( (j==2 /\ tabla[1,j]==1) \/ (j!=2 /\ tabla[1,j]==0) );
%Maria -> Lunes
constraint forall(j in semana)( (j==1 /\ tabla[2,j]==1) \/ (j!=1 /\ tabla[2,j]==0) );
%Carmen -> miercoles
constraint forall(j in semana)( (j==3 /\ tabla[3,j]==1) \/ (j!=3 /\ tabla[3,j]==0) );
%Francico-> Domingo
constraint forall(j in semana)( (j==7 /\ tabla[4,j]==1) \/ (j!=7 /\ tabla[4,j]==0) );
%Marta -> jueves o viernes
constraint forall(j in semana where j!=5)( (j==4 /\ tabla[5,j]==1) \/ (j!=4 /\ tabla[5,j]==0) );
constraint forall(j in semana where j!=4)( (j==5 /\ tabla[5,j]==1) \/ (j!=5 /\ tabla[5,j]==0) );
%Olga -> Domingo
constraint forall(j in semana)( (j==7 /\ tabla[6,j]==1) \/ (j!=7 /\ tabla[6,j]==0) );
%Pepe -> Hoy no es domingo
constraint forall(j in semana)( (j!=7 /\ tabla[7,j]==1) \/ (j==7 /\ tabla[7,j]==0) );

%Buscamos el dia de la semana
constraint forall(j in semana)( sum(i in num_amigos)(tabla[i,j])==1 -> (dia[j]==true ));
constraint forall(j in semana)( count([tabla[k,j] | k in num_amigos],1)==2 -> dia[j]==false);
%Buscamos aquel que dice la verdad 
constraint forall(j in semana where dia[j]==true)( forall(i in num_amigos )(tabla[i,j]==1 -> sincero[i] == true));
constraint forall(j in semana where dia[j]==true)( forall(i in num_amigos )(tabla[i,j]==0 -> sincero[i] == false));

solve satisfy;

%Mostramos por pantalla
output [ if fix(dia[j]) then "hoy: \(strdias[j])" else "" endif | j in semana];
output [ if fix(sincero[j]) then "\nemisor: \(stramigos[j])" else "" endif | j in num_amigos];

%Hemos modelado el problema con una matriz en la que los días son las columnas y las filas las personas, el día correcto es
%aquel en el que la suma de la columna sea uno, porque si es cero significa que dos personas dicen que es ese día,
%Y como una sola dice la verdad y no dos, ese día no puede ser. 