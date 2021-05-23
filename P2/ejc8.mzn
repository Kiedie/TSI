include "globals.mzn";
%Tenemos 9 tareas que se reprsentan con 9 letras y tres trabajadores
set of int: LETRAS=1..9;
set of int: WORKERS=1..3;
var int: total_workers = 3;
array[1..10] of var string: strletras = ["0","A","B","C","D","E","F","G","H","I"];
%Duracion de cada tarea junto con el numero de trabajadores que hacen falta para realizarla
array[LETRAS] of int: duracion = [7,3,1,8,2,1,1,3,2];
array[LETRAS] of var int: ntrabtask=[2,3,2,2,1,2,1,1,2];
%Vector de cuando se inicia y acaba la tarea y el tiempo total
int: tiempo_max = 29;
array[LETRAS] of var 0..tiempo_max: ini; %Tengo que ponerle ese rango para que funcione el predicado cumulative
array[LETRAS] of var 0..tiempo_max: fin; %Tengo que ponele ese rango para que funcione el predicado cymulative
%Matriz de asignacion  
array[WORKERS,1..tiempo_max] of var 0..9: asignacion;

%Tarea A, como no hacemos otra a la vez, no tenemos en cuenta a los tabajadores
constraint  ini[1] = 1;
constraint  fin[1] = (duracion[1] + ini[1]);
%Tarea B
constraint ini[2] >= fin[1] /\ total_workers-ntrabtask[2] >= 0;
constraint fin[2] = (duracion[2] + ini[2]);
%Tarea C
constraint ini[3] >= fin[2] /\ total_workers-ntrabtask[3] >= 0;
constraint fin[3] = (duracion[3] + ini[3]);
%Tarea D
constraint ini[4] >= fin[1] /\ total_workers-ntrabtask[4] >= 0;
constraint fin[4] = (duracion[4] + ini[4]);
%Tarea E
constraint ini[5] >= fin[3] /\ ini[5] >= fin[4] /\ total_workers-ntrabtask[5] >= 0;
constraint fin[5]  = (duracion[5] + ini[5]);
%Tarea F
constraint ini[6] >= fin[3] /\ ini[6]>=fin[4] /\ total_workers-ntrabtask[6] >= 0;
constraint fin[6]  = (duracion[6] + ini[6]);
%Tarea G  
constraint ini[7] >= fin[3] /\ ini[7] >= fin[4] /\ total_workers-ntrabtask[7] >= 0;
constraint fin[7]  = (duracion[7] + ini[7]);
%Tarea H
constraint ini[8] >= fin[1] /\ total_workers-ntrabtask[8] >= 0;
constraint fin[8] = (duracion[8] + ini[8]);
%Tarea I 
constraint ini[9] >= fin[6] /\ ini[9]>=fin[8] /\ total_workers-ntrabtask[9] >= 0;
constraint fin[9]  = (duracion[9] + ini[9]);

%Requiere que un conjunto de tareas dado por las horas de inicio, las duraciones  y los requisitos de recursos  , nunca requieran más que un recurso global limitado en un momento dado . Las horas de inicio son variables opcionales, por lo que no es necesario programar las tareas ausentes.
constraint cumulative(ini,duracion,ntrabtask,total_workers);
solve minimize max(fin);

%Las posiciones superiores al tiempo maximo en la matriz deben de ser cero 
constraint forall(i in WORKERS)(forall(j in 1..tiempo_max where j >= max(fin))(asignacion[i,j]==0)); 

%Creamos la matriz de asignación
%La tarea A es la primera
constraint forall(j in 1..duracion[1])(asignacion[1,j]=1 /\ asignacion[2,j]=1 /\ asignacion[3,j]=0);
constraint forall(j in 1..tiempo_max where j>duracion[1])(asignacion[1,j] != 1 /\ asignacion[2,j]!=1 /\ asignacion[3,j]!=1); %En ninguna otra posicion de la matriz habrá otra A
%Para la tarea B que es de 3 trabajadores buscamos donde está y la metemos en la matriz directamente con esta restriccion
constraint forall(j in 1..duracion[2])(asignacion[1,ini[2]+j-1]=2 /\ asignacion[2,ini[2]+j-1]=2 /\ asignacion[3,ini[2]+j-1]=2);
constraint forall(i in WORKERS)(count([asignacion[i,j]| j in 1..tiempo_max],2)==3);%Ninguna otra posicion valdrá 2 en la matriz

%Para la tarea C en adelaante, primeramente asignamos el trabajor que la va a realizar
%Tras ello, hacemos que el trabajdor esté trabajando el numero de dias que corresponda en esa tarea
%Como la tarea requiere de varios trabajadores, nos cercioramos de que no solo uno esté trabajando
%Finalmente comprobamos que si un trabajador la ejecute o cero veces o lo que tarda su duracion, para así las posciones que no cubramos poco a poco vayamos restringuiendo su rango de variables para que valgan cero
%Por último, si la tarea es de dos trabajadores, hacemos que el tercero no la pueda cursar, si es de un trabajador hacemos que los otros dos no la hagan
constraint ( (asignacion[1,ini[3]]=3 /\ asignacion[2,ini[3]]=3 /\ asignacion[3,ini[3]]!=3 ) 
             \/ (asignacion[1,ini[3]]=3 /\ asignacion[2,ini[3]]!=3 /\ asignacion[3,ini[3]]=3 )
             \/ (asignacion[1,ini[3]]!=3 /\ asignacion[2,ini[3]]=3 /\ asignacion[3,ini[3]]=3 ));
constraint forall(i in WORKERS)(forall(j in 1..duracion[3])(     (asignacion[i,ini[3]] == 3 -> asignacion[i,ini[3]+j-1]==3))) ;
constraint forall(j in 1..duracion[3])(sum(i in WORKERS)(asignacion[i,ini[3]+j-1]) >= 6);
constraint forall( i in WORKERS)(count([asignacion[i,j]|j in 1..tiempo_max],3)==0 \/ count([asignacion[i,j]|j in 1..tiempo_max],3)==1);
%La tarea C se hace con dos trabajadores => Hacemos que uno no la haga  
constraint ( if (count([asignacion[1,j]|j in 1..tiempo_max],3)!=0 /\ count([asignacion[2,j]|j in 1..tiempo_max],3)!=0) then count([asignacion[3,j]|j in 1..tiempo_max],3)==0 endif); 
constraint ( if (count([asignacion[1,j]|j in 1..tiempo_max],3)!=0 /\ count([asignacion[3,j]|j in 1..tiempo_max],3)!=0) then count([asignacion[2,j]|j in 1..tiempo_max],3)==0 endif);
constraint ( if (count([asignacion[2,j]|j in 1..tiempo_max],3)!=0 /\ count([asignacion[3,j]|j in 1..tiempo_max],3)!=0) then count([asignacion[1,j]|j in 1..tiempo_max],3)==0 endif);



%Tarea D, tods son análogas a lo explicado en la tarea C
constraint ( (asignacion[1,ini[4]]=4 /\ asignacion[2,ini[4]]=4 /\ asignacion[3,ini[4]]!=4 ) 
          \/ (asignacion[1,ini[4]]=4 /\ asignacion[2,ini[4]]!=4 /\ asignacion[3,ini[4]]=4 )
          \/ (asignacion[1,ini[4]]!=4 /\ asignacion[2,ini[4]]=4 /\ asignacion[3,ini[4]]=4 ));

constraint forall(i in WORKERS)(forall(j in 1..duracion[4])(     (asignacion[i,ini[4]] == 4 -> asignacion[i,ini[4]+j-1]==4))) ;
constraint forall(j in 1..duracion[4])(sum(i in WORKERS)(asignacion[i,ini[4]+j-1]) >= 8);
constraint forall( i in WORKERS)(count([asignacion[i,j]|j in 1..tiempo_max],4)==0 \/ count([asignacion[i,j]|j in 1..tiempo_max],4)==8); 
constraint ( if (count([asignacion[1,j]|j in 1..tiempo_max],4)!=0 /\ count([asignacion[2,j]|j in 1..tiempo_max],4)!=0) then count([asignacion[3,j]|j in 1..tiempo_max],4)==0 endif); 
constraint ( if (count([asignacion[1,j]|j in 1..tiempo_max],4)!=0 /\ count([asignacion[3,j]|j in 1..tiempo_max],4)!=0) then count([asignacion[2,j]|j in 1..tiempo_max],4)==0 endif);
constraint ( if (count([asignacion[2,j]|j in 1..tiempo_max],4)!=0 /\ count([asignacion[3,j]|j in 1..tiempo_max],4)!=0) then count([asignacion[1,j]|j in 1..tiempo_max],4)==0 endif);
%Tarea E
constraint ( (asignacion[1,ini[5]]=5 /\ asignacion[2,ini[5]]!=5 /\ asignacion[3,ini[5]]!=5 ) 
                                     \/ (asignacion[1,ini[5]]!=5 /\ asignacion[2,ini[5]]!=5 /\ asignacion[3,ini[5]]=5 )
                                     \/ (asignacion[1,ini[5]]!=5 /\ asignacion[2,ini[5]]=5 /\ asignacion[3,ini[5]]!=5 ));
constraint forall(i in WORKERS)(forall(j in 1..duracion[5])(     (asignacion[i,ini[5]] == 5 -> asignacion[i,ini[5]+j-1]==5))) ;
constraint forall(j in 1..duracion[5])(sum(i in WORKERS)(asignacion[i,ini[5]+j-1]) >= 5);
constraint forall( i in WORKERS)(count([asignacion[i,j]|j in 1..tiempo_max],5)==0 \/ count([asignacion[i,j]|j in 1..tiempo_max],5)==2);
constraint ( if count([asignacion[1,j]|j in 1..tiempo_max],5)!=0  then (count([asignacion[3,j]|j in 1..tiempo_max],5)==0 /\ count([asignacion[2,j]|j in 1..tiempo_max],5)==0) endif);
constraint ( if count([asignacion[2,j]|j in 1..tiempo_max],5)!=0  then (count([asignacion[1,j]|j in 1..tiempo_max],5)==0 /\ count([asignacion[3,j]|j in 1..tiempo_max],5)==0) endif);
constraint ( if count([asignacion[3,j]|j in 1..tiempo_max],5)!=0  then (count([asignacion[1,j]|j in 1..tiempo_max],5)==0 /\ count([asignacion[2,j]|j in 1..tiempo_max],5)==0) endif);

%Tarea F
constraint      ( (asignacion[1,ini[6]]=6 /\ asignacion[2,ini[6]]=6 /\ asignacion[3,ini[6]]!=6 ) 
                      \/ (asignacion[1,ini[6]]=6 /\ asignacion[2,ini[6]]!=6 /\ asignacion[3,ini[6]]=6 )
                     \/ (asignacion[1,ini[6]]!=6 /\ asignacion[2,ini[6]]=6 /\ asignacion[3,ini[6]]=6 ));
constraint forall(i in WORKERS)(forall(j in 1..duracion[6])(     (asignacion[i,ini[6]] == 6 -> asignacion[i,ini[6]+j-1]==6))) ;
constraint forall(j in 1..duracion[6])(sum(i in WORKERS)(asignacion[i,ini[6]+j-1]) >= 12);
constraint forall( i in WORKERS)(count([asignacion[i,j]|j in 1..tiempo_max],6)==0 \/ count([asignacion[i,j]|j in 1..tiempo_max],6)==1);
constraint ( if (count([asignacion[1,j]|j in 1..tiempo_max],6)!=0 /\ count([asignacion[2,j]|j in 1..tiempo_max],6)!=0) then count([asignacion[3,j]|j in 1..tiempo_max],6)==0 endif); 
constraint ( if (count([asignacion[1,j]|j in 1..tiempo_max],6)!=0 /\ count([asignacion[3,j]|j in 1..tiempo_max],6)!=0) then count([asignacion[2,j]|j in 1..tiempo_max],6)==0 endif);
constraint ( if (count([asignacion[2,j]|j in 1..tiempo_max],6)!=0 /\ count([asignacion[3,j]|j in 1..tiempo_max],6)!=0) then count([asignacion[1,j]|j in 1..tiempo_max],6)==0 endif);
%Tarea G
constraint                         ( (asignacion[1,ini[7]]=7 /\ asignacion[2,ini[7]]!=7 /\ asignacion[3,ini[7]]!=7 ) 
                                     \/ (asignacion[1,ini[7]]!=7 /\ asignacion[2,ini[7]]!=7 /\ asignacion[3,ini[7]]=7 )
                                     \/ (asignacion[1,ini[7]]!=7 /\ asignacion[2,ini[7]]=7 /\ asignacion[3,ini[7]]!=7 ));
constraint forall(i in WORKERS)(forall(j in 1..duracion[7])(     (asignacion[i,ini[7]] == 7 -> asignacion[i,ini[7]+j-1]==7))) ;
constraint forall(j in 1..duracion[7])(sum(i in WORKERS)(asignacion[i,ini[7]+j-1]) >= 7);
constraint forall( i in WORKERS)(count([asignacion[i,j]|j in 1..tiempo_max],7)==0 \/ count([asignacion[i,j]|j in 1..tiempo_max],7)==1);
constraint ( if count([asignacion[1,j]|j in 1..tiempo_max],7)!=0  then (count([asignacion[3,j]|j in 1..tiempo_max],7)==0 /\ count([asignacion[2,j]|j in 1..tiempo_max],7)==0) endif);
constraint ( if count([asignacion[2,j]|j in 1..tiempo_max],7)!=0  then (count([asignacion[1,j]|j in 1..tiempo_max],7)==0 /\ count([asignacion[3,j]|j in 1..tiempo_max],7)==0) endif);
constraint ( if count([asignacion[3,j]|j in 1..tiempo_max],7)!=0  then (count([asignacion[1,j]|j in 1..tiempo_max],7)==0 /\ count([asignacion[2,j]|j in 1..tiempo_max],7)==0) endif);
%Tarea H
constraint                           ( (asignacion[1,ini[8]]=8 /\ asignacion[2,ini[8]]!=8 /\ asignacion[3,ini[8]]!=8 ) 
                                     \/ (asignacion[1,ini[8]]!=8 /\ asignacion[2,ini[8]]!=8 /\ asignacion[3,ini[8]]=8 )
                                     \/ (asignacion[1,ini[8]]!=8 /\ asignacion[2,ini[8]]=8 /\ asignacion[3,ini[8]]!=8 ));
constraint forall(i in WORKERS)(forall(j in 1..duracion[8])(     (asignacion[i,ini[8]] == 8 -> asignacion[i,ini[8]+j-1]==8))) ;
constraint forall(j in 1..duracion[8])(sum(i in WORKERS)(asignacion[i,ini[8]+j-1]) >= 8);
constraint forall( i in WORKERS)(count([asignacion[i,j]|j in 1..tiempo_max],8)==0 \/ count([asignacion[i,j]|j in 1..tiempo_max],8)==3);
constraint ( if count([asignacion[1,j]|j in 1..tiempo_max],8)!=0  then (count([asignacion[3,j]|j in 1..tiempo_max],8)==0 /\ count([asignacion[2,j]|j in 1..tiempo_max],8)==0) endif);
constraint ( if count([asignacion[2,j]|j in 1..tiempo_max],8)!=0  then (count([asignacion[1,j]|j in 1..tiempo_max],8)==0 /\ count([asignacion[3,j]|j in 1..tiempo_max],8)==0) endif);
constraint ( if count([asignacion[3,j]|j in 1..tiempo_max],8)!=0  then (count([asignacion[1,j]|j in 1..tiempo_max],8)==0 /\ count([asignacion[2,j]|j in 1..tiempo_max],8)==0) endif);

%Tarea I
constraint                         ( (asignacion[1,ini[9]]=9 /\ asignacion[2,ini[9]]=9 /\ asignacion[3,ini[9]]!=9 ) 
                                     \/ (asignacion[1,ini[9]]=9 /\ asignacion[2,ini[9]]!=9 /\ asignacion[3,ini[9]]=9 )
                                     \/ (asignacion[1,ini[9]]!=9 /\ asignacion[2,ini[9]]=9 /\ asignacion[3,ini[9]]=9 ));
                                     
constraint forall(i in WORKERS)(forall(j in 1..duracion[9])(     (asignacion[i,ini[9]] == 9 -> asignacion[i,ini[9]+j-1]==9))) ;
constraint forall(j in 1..duracion[9])(sum(i in WORKERS)(asignacion[i,ini[9]+j-1]) >= 18);
constraint forall( i in WORKERS)(count([asignacion[i,j]|j in 1..tiempo_max],9)==0 \/ count([asignacion[i,j]|j in 1..tiempo_max],9)==2);
constraint ( if (count([asignacion[1,j]|j in 1..tiempo_max],9)!=0 /\ count([asignacion[2,j]|j in 1..tiempo_max],9)!=0) then count([asignacion[3,j]|j in 1..tiempo_max],9)==0 endif); 
constraint ( if (count([asignacion[1,j]|j in 1..tiempo_max],9)!=0 /\ count([asignacion[3,j]|j in 1..tiempo_max],9)!=0) then count([asignacion[2,j]|j in 1..tiempo_max],9)==0 endif);
constraint ( if (count([asignacion[2,j]|j in 1..tiempo_max],9)!=0 /\ count([asignacion[3,j]|j in 1..tiempo_max],9)!=0) then count([asignacion[1,j]|j in 1..tiempo_max],9)==0 endif);




%output[show(ini)];
%output["\n"];
%output[show(fin)];
%output["\n"];
%output[show(max(fin))];
%output["\n"];
output["TIEMPO MINIMO = " ++ "\(max(fin))" ++ "\n*****\n"];
output["Tarea " ++ "\(strletras[k])" ++ " empieza en tiempo " ++ "\(ini[k])" ++ "\n" | k in LETRAS] ;

output["\nLlamando a las tareas por numeros:\n"];
output["\(asignacion[i,j]) " ++ if j==29 then "\n" else "" endif | i in WORKERS, j in 1..29];

output["\nLlamando a las tareas por letras\n"];
output[if (fix(asignacion[1,z]) == k ) then "\(strletras[k+1]) " else "" endif | z in 1..29, k in 0..9];
output["\n"];
output[if (fix(asignacion[2,z]) == k ) then "\(strletras[k+1]) " else "" endif | z in 1..29, k in 0..9];
output["\n"];
output[if (fix(asignacion[3,z]) == k ) then "\(strletras[k+1]) " else "" endif | z in 1..29, k in 0..9];
