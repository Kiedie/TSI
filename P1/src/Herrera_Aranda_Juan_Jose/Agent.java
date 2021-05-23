/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Herrera_Aranda_Juan_Jose;

import java.util.ArrayList;
import java.util.Collections;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;




public class Agent extends AbstractPlayer{

    Vector2d fescala;
    Vector2d portal;
    State avatar;
    Astar pathfinder;
    boolean isPath;             //Nos dice si tenemos un camino o no
     
    
    //Gemas
    int numGemas;               //Numero de gemas a recoger
    int havingGemas;            //Gemas que tenemos
    int posicion_orden_gemas;   //Indica el indice de la siguiente gema a recoger
    boolean isGemas;            //True si ya hemos conseguido todas las gemas
    Gemas   gemas_order;        //Abstracción de las gemas para el orden de busqueda
    
    //Niveles
    boolean lvl_1;              
    boolean lvl_2;
    boolean lvl_3;
    boolean lvl_5;   
    
    //LVL2
    ACTIONS last_action;
    int size_actual_path;
    boolean isPortal;
    ArrayList<Vector2d> gemas_recogidas;
    
    //Factores para las funciones del nivel 3, 4 y 5 
    int factor_muro;
    int factor_enemigo;
    
    //LVL3_4
    ArrayList<ArrayList<Integer>> HeatMap;
    ArrayList<Vector2d> enemies;
    ArrayList<Vector2d> muros;
    int nivel_peligro;
    int limite_x;       //Ancho del mapa
    int limite_y;       //Largo del mapa
    int radio = 8;          //Radio del mapa de calor del enemigo
    int mitad_lado = 3;      //Radio muro
    
    
    
    //LVL_5
    ArrayList<ACTIONS> camino_lvl5;
    Vector2d next_gema;
    
    
    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
    
        //Inicializamos el factor de escala entre el mundo del Avatar y el Grid
        fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length , 
                               stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);
        
        //Se crea una lista de observaciones de portales, ordenada por cercania al avatar
        ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
        //Seleccionamos el portal más proximo
        portal = posiciones[0].get(0).position;
        portal.x = Math.floor(portal.x/fescala.x);
        portal.y  = Math.floor(portal.y/fescala.y);
        
        //Obtenemos la posicion del avatar.
        Vector2d posicion = new Vector2d(stateObs.getAvatarPosition().x / fescala.x, stateObs.getAvatarPosition().y / fescala.y);
        Vector2d orientacion = stateObs.getAvatarOrientation();
        avatar = new State(posicion,orientacion);

        
        //Inicializamos el pathfinder
        isPath = false;
        pathfinder = new Astar();
        
        //Gemas
        isGemas=false;
        numGemas=9;         
        havingGemas=0;
        
        
        //Niveles
        lvl_1 = (stateObs.getNPCPositions() == null && stateObs.getResourcesPositions() == null );
        lvl_2 = (stateObs.getNPCPositions() == null && stateObs.getResourcesPositions() != null );
        lvl_3 = (stateObs.getNPCPositions() != null && stateObs.getResourcesPositions() == null );
        lvl_5 = (stateObs.getNPCPositions() != null && stateObs.getResourcesPositions() != null );
        
        if(lvl_2)
            inicializar_lvl2( avatar, portal, stateObs,elapsedTimer);
        if(lvl_3) //Valido tambien para el nivel 4
            inicializar_lvl3(stateObs,elapsedTimer);
        if(lvl_5)
            inicializar_lvl5(stateObs,elapsedTimer);
    }

    
    
    /**
     * Inicializamos variables para el nivel 2
     * @param stateObs
     * @param elapsedTimer 
     */
    public void inicializar_lvl2(State avatar, Vector2d portal,StateObservation stateObs,ElapsedCpuTimer elapsedTimer){
        
        this.size_actual_path = 0;
        this.posicion_orden_gemas = 0;
        this.last_action = ACTIONS.ACTION_NIL;
        this.gemas_order = new Gemas(avatar, portal, stateObs);
        isPortal = false;
        
        //isGemas = gemas_order.obtain_closer_path_ManhatanGreedy(avatar, stateObs);
        //isGemas = gemas_order.obtain_closer_path_AGreedy(avatar, stateObs);
        this.gemas_recogidas = new ArrayList<>();
        //System.out.println(gemas_order.gemas_ordenadas.toString());
    }
    
    
    
    public void inicializar_lvl3(StateObservation stateObs,ElapsedCpuTimer elapsedTimer){
        enemies         = new ArrayList<>();
        muros           = new ArrayList<>();
        nivel_peligro   = 0;
        HeatMap         = new ArrayList<>();
        limite_x        = stateObs.getObservationGrid().length;
        limite_y        = stateObs.getObservationGrid()[0].length;
        
        
        //Inicializamos el mapa de calor con todos los valores a cero 
        for(int j = 0 ; j < limite_y ; j++)         //Filas 
        {
            this.HeatMap.add(new ArrayList<>());
            for(int i = 0 ; i < limite_x ; i++)     //Columnas
                this.HeatMap.get(j).add(0);
        }
        //Actualizamos los enemigos e inicializamos los muros
        this.updateEnemies(stateObs); 
        this.inicializarMuros(stateObs);
        
        //Ajustamos el radio de calor del enemigo y los muros y los factores de escala
        if(enemies.size() == 1){    //Nivel 3
            factor_enemigo = 120;
            factor_muro = 20;
            radio = 8;
            mitad_lado = 3;
        }else if(enemies.size()  > 1){ // nivel 4
            factor_enemigo = 120;
            factor_muro = 15;
            radio = 5;
            mitad_lado = 3;
        }
        //Creamos mapa de calor
        this.create_heat_map(stateObs,radio);  
    }
        
    
    
    public void inicializar_lvl5(StateObservation stateObs,ElapsedCpuTimer elapsedTimer)
    {
        //Comportamiento deliberativo
        this.size_actual_path       = 0;
        this.posicion_orden_gemas   = 0;
        this.last_action            = ACTIONS.ACTION_NIL;
        this.gemas_order = new Gemas(avatar,portal, stateObs);
        this.gemas_recogidas = new ArrayList<>();
        isPortal = false;
        this.camino_lvl5 = new ArrayList<>();
        this.next_gema = new Vector2d();
        
        //Comportamiento reactivo
        inicializar_lvl3(stateObs,elapsedTimer);
        factor_muro = 20;
        factor_enemigo = 500;
        radio = 5;
        mitad_lado = 3; 
    }
    
    
    
    @Override
    public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
        
        Types.ACTIONS act = Types.ACTIONS.ACTION_NIL;
        
        //Actualizamos el avatar, en cada momento  
        avatar.position = new Vector2d(stateObs.getAvatarPosition().x / fescala.x, stateObs.getAvatarPosition().y / fescala.y);
        avatar.orientation = stateObs.getAvatarOrientation();
        
        if (lvl_1)
        {
            if(!isPath)                                                         //Si no tenemos camino
            {  
                isPath = pathfinder.pathfind_Astar_2(avatar, portal, stateObs); //Lo calculamos
                pathfinder.setPathBacktraking();                                //Trazamos camino
                //pathfinder.printPathFinder();
            }
            act = pathfinder.getNextAction();                                   //Tomamos la siguiente acción a ejecutar
            
        }
        
        if(lvl_2)
        {
            State gema_partida = new State();   //va a ser la gema de la que vamos a partir
            Vector2d gema = new Vector2d();     //Gema objetivo

              
            //Si estoy en una casilla con una gema que no haya recogido la cojo 
            if(is_Gema_casilla(avatar.position) && !gemas_recogidas.contains(avatar.position))
            {
                this.gemas_recogidas.add(new Vector2d(avatar.position));        //Añado la gema a la lista de recogidas
                havingGemas++;                                                  //Incremento el numero de gemas que tengo
                this.gemas_order.update_no_seleccionadas_gemas(avatar.position);//Actualizo el vector con las gems no seleccionadas eliminandola
            }
            
            //Si tenemos todas las gemas 
            if(havingGemas == numGemas) 
            {
                isPortal = true;                                                //Activamos portal
                isPath   = false;                                               //Buscamos camino al portal
            }

            
            //Si no tenemos camino
            if(!this.isPath)
            {                 
                if(!isPortal){
                    
                    //Vamos en busca de las gemas
                    gema = gemas_order.next_gema_Manhatan(avatar,stateObs);                  //Gema objetivo
                                           
                    isPath  = pathfinder.pathfind_Astar(avatar, gema, stateObs);             //Calculamos camino
                    pathfinder.setPathBacktraking();                                         //Calculamos ruta
                    gemas_order.update_no_seleccionadas_gemas(gema);                         //La borramos del vector de gemas no seleccionadas, ESTE PASO LO UPEDO QUITAR PORQUE YA LA BORRO ARRIBA
                    this.size_actual_path = pathfinder.getPath().size() - 1;                 //Calculamos el numero de acciones a llevar a cabo=> [0, tamanio-1[

                }else{ //Si el portal está activado
                    isPath = pathfinder.pathfind_Astar(avatar, portal, stateObs);
                    pathfinder.setPathBacktraking();
                    
                }          
            }
            act = pathfinder.getNextAction();                                   //Tomamos la siguiente acción a realizar 
            this.last_action = act;                                             //Guardamos la ultma accion
            if(this.size_actual_path==0)                                        //Si ya no tenemos más acciones a realizar
                isPath=false;                                                   //Indicamos que se ha acabado el camino
            this.size_actual_path--;                                            //Decrementamos en tamaño de la ruta
            
        }
        
        if(lvl_3)
        {
            
            //Actualizamos el mapa de calor
            this.create_heat_map(stateObs, radio);
            
            if(dangerous(avatar.position) == 0)                                 //Si estamos en una poscion en la que no hay peligro
            {
                act = ACTIONS.ACTION_NIL;                                       //No hacemos nada
            }   
            else                                                                //Si estamos en una posicion de peligro
            {           
                act = camino_escape();                                          //Escapamos
            }
        }
        
        if(lvl_5)
        {   
            //Actualizamos al mapa de calor
            this.create_heat_map(stateObs, radio);
            //Calculamos el peligro en la casilla en la que nos encontramos
            int peligro_actual = dangerous(avatar.position);
            
            //Si estoy en una casilla en la que hay gema y no la he recogido antes
            if(is_Gema_casilla(avatar.position) && !gemas_recogidas.contains(avatar.position))
            {
                this.gemas_recogidas.add(new Vector2d(avatar.position));        //Añado la gema a la lista de recogidas
                havingGemas++;                                                  //Incremento el numero de gemas que tengo
                gemas_order.gemas_no_seleccionadas.remove(avatar.position);     //Eliminamos la gema dentro del conjunto de las no seleccionadas
            }
            
            //Si tenemos todas las gemas 
            if(havingGemas == numGemas) 
            {
                isPortal = true;                                                //Activamos portal
                isPath   = false;                                               //Buscamos camino al portal
            }
            
            
            //Comportamiento deliberativo 
            if(peligro_actual  < 50) //Zonas de calor con un valor inferior a 50 se consideran aptas para ir a buscar gemas
            {
                if(!isPath) // si no hay camino
                {
                    //Si el portal no esta activo
                    if(!isPortal)
                    {
                        //Buscamos la gema más cercana al avatar
                        this.next_gema = this.gemas_order.next_gema_Manhatan(avatar, stateObs);
                        isPath = pathfinder.pathfind_Astar(avatar, next_gema, stateObs);            //Calculamos el camino
                        pathfinder.setPathBacktraking();                                            //Trazamos el camino
                        this.size_actual_path = pathfinder.getPath().size() - 1;                    //Calculamos la longitud del camino
                    }else//Si el portal esta activo
                    {
                        isPath = pathfinder.pathfind_Astar(avatar, portal, stateObs);               //Calculamos camino al portal
                        pathfinder.setPathBacktraking();                                            //Trazamos el camino
                    }
                
                }          
                act = pathfinder.getNextAction();                               //Tomamos la siguiente acción del camino
                this.last_action = act;                                         //La guardamos como la ultima acción realizada
                
                if(this.size_actual_path==0)                                    //Si ya hemos recorrido todo el camino
                    isPath=false;                                               //indicamos que no tenemos má camino
                this.size_actual_path--;                                        //Decrementamos el numero de acciones
                
            }else //Si tenemos un peligro mayor de 50, tenemos que escapar
            { 
                //Comportamiento reactivo
                isPath = false;                                                 //Dejamos de realizar cualquier camino que estuvieramos haciendo
                act = this.camino_escape();                                     //Tomo una acción para escapar el enemigo
            }
           
        }
        
        //System.out.println(act.toString());
        return act;
    }
    
    
    
    /**
     * Dada una acción nos devuelve su orientación
     * @param act
     * @return 
     */
    public Vector2d get_Orientation_From_Action(ACTIONS act) {
        if (act == ACTIONS.ACTION_DOWN) {
            return (new Vector2d(0, 1));
        } else if (act == ACTIONS.ACTION_UP) {
            return (new Vector2d(0, -1));
        } else if (act == ACTIONS.ACTION_LEFT) {
            return (new Vector2d(-1, 0));
        } else if (act == ACTIONS.ACTION_RIGHT) {
            return (new Vector2d(1, 0));
        } else {
            return null;
        }
    }
    
    
    
    /**
     * Dado un camino nos devuelve la orientación en la que quedaría el avatar tras recorrerlo
     * @param camino
     * @return 
     */
    public Vector2d get_orientation_from_path(ArrayList<ACTIONS> camino)
    {
        Vector2d ret = new Vector2d();
        ACTIONS act = camino.get(camino.size()-1); //La orientacion nos la da la ultima posicion del camino
        ret = get_Orientation_From_Action(act);
        return ret;
    }
    
    
    
    /**
     * Nos dice si en la casilla que le pasamos por argumento tiene una gema
     * @param casilla: Vector indicando una posiccion en el mapa
     * @return 
     */
    public boolean is_Gema_casilla(Vector2d casilla)
    {
        return ( this.gemas_order.get_all_gemas().contains(casilla) );
    }

    
    
    //Computa la distancia manhatan entre dos posiciones
    public int distancia_Manhatan(Vector2d inicio, Vector2d fin)
    {
        return (int) (Math.abs(inicio.x - fin.x) + Math.abs(inicio.y-fin.y));
    }
    
    

    /**
     * Nos dice si el vector esta fuera o dentro del mapa
     * @param pos
     * @return True si está dentro del mapa y false si está fuera
     */
    public boolean is_position_on_grid(Vector2d pos)
    {   
        //Compruebo que esten dentro del rango
        return ( 0<= pos.x && pos.x < limite_x  && 0<= pos.y && pos.y< limite_y );
    }
     
    
    
    /**
     * Actualiza el vector con la posicion de los enemigos
     * @param stateObs 
     */
    public void updateEnemies(StateObservation stateObs)    
    {
        //Vaciamos el vector de enemigos
        if(!enemies.isEmpty())
            enemies.clear();
        //Buscamos a los enemigos
        ArrayList<Observation>[] npcs = stateObs.getNPCPositions();
        for (int i = 0; i < npcs.length; i++){
            for (Observation obs : npcs[i])
                enemies.add(new Vector2d((int)(obs.position.x/fescala.x), (int)(obs.position.y/fescala.y)));
        }
    }
    
    
    
    /**
     * Guarda la posicion de los muros en el atributo reservado para ello
     * @param stateObs 
     */
    public void inicializarMuros(StateObservation stateObs)
    {
        //Obtenemos las posiciones de los muros en conjunto
        ArrayList<Observation>[] obstaculos = stateObs.getImmovablePositions();
        for (int i = 0; i < obstaculos[0].size(); i++){
            //Obtenemos la posición de cada uno
            muros.add( new Vector2d(Math.floor(obstaculos[0].get(i).position.x / fescala.x), Math.floor(obstaculos[0].get(i).position.y / fescala.y)));
        }
    }
    
    
       
    /**
     * Añade al mapa de calor, el calor que proporcionan los muros
     * @param stateObs 
     */
    public void introduce_hot_wall(StateObservation stateObs, int mitad_lado)
    {
        Vector2d hijo = new Vector2d();
        Vector2d muro = new Vector2d();
        //Para cada muro 
        for(int i = 0 ; i < muros.size() ; i++) 
        {
           muro.set(muros.get(i).x, muros.get(i).y);
           //Metemos un calor muy alto
           this.HeatMap.get( (int)muro.y).set((int)muro.x, 500);    
           
           //Exploramos sus hijos
           for(int x_i = -mitad_lado ; x_i <= mitad_lado ; x_i++ )
               for(int y_i = -mitad_lado ; y_i <= mitad_lado ; y_i++)
               {
                   //Esto es un hijo
                   hijo.set(x_i + muro.x, y_i + muro.y );
                   //Si el hijo está dentro del mapa y no es un muro
                   if( is_position_on_grid(hijo) && !muros.contains(hijo) )
                   {
                        //Si esta contiguo o no le metemos un calor menor o mayor 
                        if(distancia_Manhatan(hijo,muro) != 0)
                            HeatMap.get((int)hijo.y).set((int)hijo.x, HeatMap.get((int)hijo.y).get((int)hijo.x) + (int)funcion_hiperbola_exponencial(distancia_Manhatan(hijo,muro)) );  //Ojo, no confundir, las filas y las columnas                         
                   }     
               }
        }
    }
    
    
    
    /**
     * Funcion matemática, en base a la distancia duelve un valor
     * @param distancia
     * @return 
     */
    public int funcion_hiperbolica(int distancia)
    {
        double hip;
        if(distancia != 0)
        {
            hip = 1/(double)distancia;
            hip = hip *100;
            return (int) hip;
        }else
            return -1;
    }
    
    
    
    /**
     * Funcion matemática, en base a la distancia duelve un valor
     * @param distancia
     * @return 
     */
    public int funcion_coseno_hiperbolico(int distancia)
    {
        double hip;
        if(distancia != 0)
        {
            hip = 1/Math.cosh((double)distancia/2.0);
            hip = hip *factor_enemigo;
            //System.out.println(hip);
            return (int) hip;
        }else
            return -1;
    }
    
    
    
    /**
     * Funcion matemática, en base a la distancia duelve un valor
     * @param distancia
     * @return 
     */
    public int funcion_hiperbola_exponencial(int distancia)
    {
        double hip;
        if(distancia != 0)
        {
            hip = 1/Math.exp((double)distancia);
            hip = hip*factor_muro;
            return (int) hip;
        }else
            return -1;
    }
    
    
    
    /**
     * Crea un calor para el enemigo 
     * @param stateObs
     * @param diagonal 
     */
    public void introduce_hot_rombo_enemy(StateObservation stateObs, int diagonal)
    {
        Vector2d enemy = new Vector2d();
        Vector2d hijo  = new Vector2d();
        int distancia;
        
        //Para cada enemigo
        for(int k = 0 ; k < this.enemies.size() ; k++)
        {
            enemy = this.enemies.get(k);
            
            //Expandimos los hijos 
            for(int x_i = (int)enemy.x - diagonal  ; x_i <= (int)enemy.x + diagonal ; x_i++)
                for(int y_i = (int)enemy.y - diagonal  ; y_i <= (int)enemy.y + diagonal  ; y_i++)
                {
                    hijo.set(x_i, y_i);
                   
                    //Si mi hijo esta dentro de tablero y no es ningun muro
                    if(is_position_on_grid(hijo) && !muros.contains(hijo))
                    {   
                        //Calculo la distancia que hay entre el hijo y el enemigo
                        distancia = distancia_Manhatan(hijo,enemy);
                        
                        //Si estoy en el rombo
                        if(distancia <= diagonal)
                        {   //Posicion que corresponde al enemigo
                            if(distancia == 0)
                            {
                                //Le pongo el maximo de calor
                                HeatMap.get( (int)hijo.y ).set( (int)hijo.x, HeatMap.get( (int)hijo.y ).get( (int)hijo.x )  + 1000);
                            }
                            else 
                            {
                                //Etoy en sus cercanias por tanto le ajusto el calor
                                HeatMap.get( (int)hijo.y ).set( (int)hijo.x, HeatMap.get( (int)hijo.y ).get( (int)hijo.x )  + funcion_coseno_hiperbolico(distancia));                     
                            }
                        }
                    }
                }
        }
    }
    
    
    
    /**
     * Creamos un mapa de calor 
     * @param stateObs
     * @param radio: Radio que queremos quetenga el enemigo
     */
    public void create_heat_map(StateObservation stateObs, int radio)
    {
        //Limpiamos el mapa
        if(!this.HeatMap.isEmpty())
            this.HeatMap.clear();
        
        //Inicializamos el mapa de calor con todos los valores a cero 
        for(int j = 0 ; j < limite_y ; j++)         //Filas 
        {
            this.HeatMap.add(new ArrayList<>());
            for(int i = 0 ; i < limite_x ; i++)     //Columnas
                this.HeatMap.get(j).add(0);
        }
        
        //Inicializamos calor de los muros y sus cercanias
        this.introduce_hot_wall(stateObs,mitad_lado);
        
        //Inicializamos el calor del enemigo y sus cercanías
        this.updateEnemies(stateObs);
        this.introduce_hot_rombo_enemy(stateObs, radio);     
    }
    
    
    
    public void printHeatMap()
    {
        for(int i = 0 ; i < limite_y; i++)
        {
            for( int j = 0 ; j < limite_x; j++)
            {
                System.out.print(HeatMap.get(i).get(j) + "\t  ");
            }
            System.out.println();
        }
        System.out.println();
    }


    
    /**
     * Calcula el nivel de peligro de una casilla
     * @param pos: vector con la posicon de la casilla
     * @return entero que indica el peligro
     */
    public int dangerous(Vector2d pos)
    {
        return (HeatMap.get((int)pos.y).get((int)pos.x));
    }
    
    
    
    /**
     * Calculamos una accion para escapar
     * @return 
     */
    public ACTIONS camino_escape()
    {
        ArrayList<ACTIONS> ret = new ArrayList<>();
        ArrayList<Vector2d> hijos = new ArrayList<>();
        int minimo = dangerous(avatar.position);
        int peligro;
        int pos = -1;
        int peligro_mas_cercano = 10000000;
        
        //Generos los hijos nodos en forma de cruz  izquierda, deracha, arriba y abajo
        hijos.add(new Vector2d(avatar.position.x + 0 , avatar.position.y + 1));             //Posicion 0 --> sur
        hijos.add(new Vector2d(avatar.position.x + 1 , avatar.position.y + 0));             //Posicion 1 --> derecha
        hijos.add(new Vector2d(avatar.position.x + 0 , avatar.position.y - 1));             //Posicion 2 --> norte
        hijos.add(new Vector2d(avatar.position.x - 1 , avatar.position.y + 0));             //Posicion 3 --> izquierda
        
        
        //Considero el peligro de la casilla cuyo coste de desplazamiento es uno por ser contigua a la horientacion
        if(is_position_on_grid(hijos.get(0)) && !this.muros.contains(hijos.get(0)))   //Si el hijo es válido, i.e., está en el grid y no es un muro
        {
            if(same_orientation(avatar.orientation,ACTIONS.ACTION_DOWN) && dangerous(avatar.position) >= dangerous(hijos.get(0)) ){ //Si estoy orientado según la accion y el peligro del hijo es menor igual
                peligro_mas_cercano = dangerous(hijos.get(0));                  //El peligro más cercano al que me puedo mover es al del hijo
                pos = 0;                                                        //Capturo su posicin
            }
        }
        if(is_position_on_grid(hijos.get(1)) && !this.muros.contains(hijos.get(1)) && dangerous(avatar.position) >= dangerous(hijos.get(1))) //Analogo para el resto
        {
            if(same_orientation(avatar.orientation,ACTIONS.ACTION_RIGHT)){
                peligro_mas_cercano = dangerous(hijos.get(1));
                pos = 1;
            }
        }
        if(is_position_on_grid(hijos.get(2)) && !this.muros.contains(hijos.get(2)) && dangerous(avatar.position) >= dangerous(hijos.get(2)))
        {
            if(same_orientation(avatar.orientation,ACTIONS.ACTION_UP)){
                peligro_mas_cercano = dangerous(hijos.get(2));
                pos = 2;
            }
        }
        if(is_position_on_grid(hijos.get(3)) && !this.muros.contains(hijos.get(3)) && dangerous(avatar.position) >= dangerous(hijos.get(3)))
        {
            if(same_orientation(avatar.orientation,ACTIONS.ACTION_LEFT)){
                peligro_mas_cercano = dangerous(hijos.get(3));
                pos = 3;
            }
        }
        
        
        //Para cada hijo calculo la posición de peligro y me quedo con la mas segura
        for(int i = 0 ; i < 4 ; i++)
        {
            if(is_position_on_grid(hijos.get(i)) && !this.muros.contains(hijos.get(i))) //Si el hijo esta en el grid y no es un muro
            {
                peligro = dangerous(hijos.get(i));
                if(peligro < minimo && peligro < peligro_mas_cercano) //Si el peligro del hijo es menor que el padre y menor que el hijo más cercano
                {
                    minimo = peligro;       //Tomo como siguiente nodo al hijo con el menor peligro
                    pos = i;
                }
            }
        }

        
        //Proceso que accion debo de tomar para ir al hijo
        if (pos == 0)
            return ACTIONS.ACTION_DOWN;
        else if (pos == 1)
            return ACTIONS.ACTION_RIGHT;
        else if (pos == 2)
            return ACTIONS.ACTION_UP;
        else if (pos == 3)
            return ACTIONS.ACTION_LEFT;
        else{
            return ACTIONS.ACTION_NIL;
        }
        
    }
    
    
    
    /**
     * Nos informa si una orientación esta orientada segun la accion pasada por argumento
     * @param orien
     * @param act
     * @return True, si la orientacion es la de la acción y false en caso contrario
     */
    boolean same_orientation(Vector2d orien, ACTIONS act)
    {
        if (act == ACTIONS.ACTION_DOWN && orien.y == 1.0)
            return true;
        else if(act == ACTIONS.ACTION_UP && orien.y == -1.0)
            return true;
        else if (act == ACTIONS.ACTION_LEFT && orien.x == -1.0)
            return true;
        else if (act == ACTIONS.ACTION_RIGHT && orien.x == 1.0)
            return true;
        else return false;
        
    }
    
    
    
    
}

