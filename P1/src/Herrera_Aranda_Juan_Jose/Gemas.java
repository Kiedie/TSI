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

class Salida {

}

/**
 *
 * @author kiedie
 */
public class Gemas {

    public ArrayList<Observation>[] allGemas;                                   //Todas las gemas del mapa en una estructura rara
    public ArrayList<Vector2d> allGemas_vector;                                 //Vector con todas las gemas del mapa
    public ArrayList<Vector2d> gemas_no_seleccionadas;                          //Gemas que no están en gemas_ordenadas
    public State avatar;
    public Vector2d portal;
    public Vector2d fescala;
    public int gemas_to_pick;                                                   //Cantidad de gemas que queremos recoger
    

    //LVL2
    public ArrayList<ACTIONS> camino;                                           //Representa el camino que recorre el avatar para coger todas las gemas
    public ArrayList<Vector2d> gemas_ordenadas; //Nivel 2                       //Gemas ordenadas por camino

    //LVL Superiores
    public Astar pathfinder;
    

    //Constructor
    public Gemas(State avatar, Vector2d portal, StateObservation stateObs) {

        fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length,
                stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);

        allGemas = stateObs.getResourcesPositions(stateObs.getAvatarPosition());
        
        //Inicializamos avatar
        this.avatar = new State(avatar);
        this.portal = portal;
        gemas_to_pick = 9;
        
        
        gemas_ordenadas         = new ArrayList<>();
        gemas_no_seleccionadas  = new ArrayList<>();
        allGemas_vector         = new ArrayList<>();
        camino                  = new ArrayList<>();

        //Inicializamos el vector con las gemas no seleccionadas que en principio son todas
        for (int i = 0; i < allGemas[0].size(); i++) {
            gemas_no_seleccionadas.add(allGemas[0].get(i).position);
            gemas_no_seleccionadas.get(i).x = Math.floor(gemas_no_seleccionadas.get(i).x / fescala.x);
            gemas_no_seleccionadas.get(i).y = Math.floor(gemas_no_seleccionadas.get(i).y / fescala.y);
        }
        //Inicializamos el vector con todas las gemas
        for (int j = 0; j < gemas_no_seleccionadas.size(); j++) {
            allGemas_vector.add(new Vector2d(gemas_no_seleccionadas.get(j).x, gemas_no_seleccionadas.get(j).y));

        }
        //Inicializo el pathfinder
        pathfinder = new Astar();

    }

    
    
    /**
     * Devuelve todas las gemas en el juego
     * @return 
     */
    public ArrayList<Vector2d> get_all_gemas(){return this.allGemas_vector;}
    
    
    
    /**
     * Devuelve el camino de un estado de comienzo a uno de fin
     *
     * @param start: Estado de inicio
     * @param fin:   Estado de fin 
     * @param stateObs
     * @return camino a recorrer desde inicio a fin 
     */
    public ArrayList<ACTIONS> get_path_from_to(State start, Vector2d fin, StateObservation stateObs) {
        //Calculamos el camino
        boolean ispath = pathfinder.pathfind_Astar(start, fin, stateObs);
        if (!ispath) {
            System.out.println("Echale un ojo al metodo get_parh_from_to de la clase gemas");
        }
        //Hacemos backtraking para obtener el camino
        pathfinder.setPathBacktraking();
        
        //Metemos el camino en un array
        ArrayList<ACTIONS> ret = new ArrayList<>();
        for (int i = 0; i < pathfinder.getPath().size(); i++) {
            ret.add(pathfinder.getPath().get(i));
        }
        //Limpiamos el pathfinder
        pathfinder.clearPath(); 

        if (ret == null) {
            System.out.println("El retorno de la funcion get_path_from_to es nulo, cuidado con el clear");
        }
        //Devolvemos el camino
        return ret;
    }

    
    
    /**
     * Nos devuelve la distancia de un camino
     * @param path: Array de acciones
     * @return tamaño del array 
     */
    public int get_distance_from_path(ArrayList<ACTIONS> path) {return path.size();}

    
    
    /** 
     * PARA EL NIVEL 2 Calculo el camino de gemas ordenadas. A medida que meto
     * en la variable gemas_ordenadas, voy sacando de gemas_no_seleccionadas
     *
     */
    public boolean obtain_closer_path_AGreedy(State start, StateObservation stateObs) {

        ArrayList<ACTIONS> camino   = new ArrayList<>();
        int current_distance        = 0;
        int minimum_distance;
        Vector2d orientation        = new Vector2d();
        Vector2d closer_gema        = null;                                     //Gema mas cercana
        State partida               = new State(start);
        ACTIONS last_action;
        boolean copia               = true;
        
        //Mientras no hayamos metido todas las gemas
        while (gemas_ordenadas.size() < gemas_to_pick) {
            minimum_distance = 1000000;
            //Para cada gema que no hayamos seleccionado
            for (Vector2d gema : gemas_no_seleccionadas) {
                //Calculamos el camino y la distancia
                
                camino = deep_copy(get_path_from_to(partida, gema, stateObs));          
                current_distance = get_distance_from_path(camino);
               
                //Obtenemos el mínimo
                if (current_distance < minimum_distance) {
                    minimum_distance = current_distance;
                    closer_gema = gema;                                         //Actualizamos a gema mas cercana
                    last_action = camino.get(camino.size() - 1);                //Guardo la ultima accion
                    orientation = get_Orientation_From_Action(last_action);     //Recupero la orientacion en funcion de la ultima accion

                }
            }
            //Actualizamos la siguiente posicion de partida
            partida.orientation = orientation;
            partida.position = closer_gema;
            
            //Si la gemas mas cercana existe
            if (closer_gema != null) {
                //La metemos
                gemas_ordenadas.add(closer_gema);                                            
            } else {
                System.out.println("Mirate el método obtain_closer_path de la Clase Gemas");
            }

            //Eliminamos la gema que metemos en seleccionadas
            gemas_no_seleccionadas.remove(closer_gema);
        }
        
        //Si tenemos todas las gemas deseadas
        if (gemas_ordenadas.size() == this.gemas_to_pick) {
            return true;
        } else { //En caso contrario
            return false;
        }
    }

    
    
    //Dada una acción especificamos la orientación tras llevarla acabo
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
     * Imprime un vector
     * @param vector 
     */
    public void printGemas(ArrayList<Vector2d> vector) {
        for (Vector2d gema : vector) {
            String ret = gema.toString();
            System.out.println(ret);
        }
    }
    
    
    
    /**
     * Hace una copia profunda del array pasado por argumento
     * @param array
     * @return 
     */
    public ArrayList<ACTIONS> deep_copy(ArrayList<ACTIONS> array)
    {
        ArrayList<ACTIONS> ret = new ArrayList<>();
        for(int i = 0; i < array.size(); i++)
            ret.add(array.get(i));
        return ret;
    }
    
    
    
    /**
     * Dado un camino, nos devuelve la orientacion en la que queda
     * el avatar tras llevarlo a cabo
     * @param camino: array de acciones
     * @return Orientacion tras la que queda el avatar
     */
    public Vector2d get_orientation_from_path(ArrayList<ACTIONS> camino)
    {
        Vector2d ret = new Vector2d();
        //Tomamos la ultima accion del camino
        ACTIONS act = camino.get(camino.size()-1);
        //A partir de esa accion calculamos la orientacion
        ret = get_Orientation_From_Action(act);
        return ret;
    }
    
    
    //Distancia manhatan
    public int distance_Manhatan(Vector2d inicio, Vector2d fin)
    {
        return (int) (Math.abs(inicio.x - fin.x) + Math.abs(inicio.y-fin.y));
    }
    
    
    
    /**
     * Dado una posicion de inicio y un camino nos dice si por casualidad
     * hemos pasado por alguna gema que no teniamos previsto
     * @param comienzo
     * @param camino
     * @param stateObs
     * @return El numero de gemas que tiene el camino sin contar la ultima
     */
    public int get_num_gemas_from_path(State comienzo, ArrayList<ACTIONS> camino, StateObservation stateObs)
    {
        int num_gemas = 0;                                                      //Empezamos a contar de cero gemas
        State casilla_anterior = new State(comienzo);                           //casilla actual
        State nueva_casilla = new State();
        int x,y;
        for(int i = 0 ; i < camino.size() - 1; i++)                             //La ultima posicion del camino siempre tiene una gema, que es la que buscamos y no se cuenta 
        {   //Siguiente casilla
            nueva_casilla = new State(casilla_anterior,camino.get(i));
            //Obtenemos coordenadas
            x = (int) nueva_casilla.position.x;
            y = (int) nueva_casilla.position.y;
            //Si en esa casilla hay una gema, la añadimos
            for(Observation obs : stateObs.getObservationGrid()[x][y])
                if ( obs.itype == 6 )
                    num_gemas++;
            
        }
        return num_gemas;
    }
    
    
    
    /**
     * Greedy para obtener las gemas mas cercanas siguiendo una distancia manhatan
     * @param start: Poosicion de comienzo
     * @param stateObs
     * @return True si todo ha ido bien y false en caso de haber algo extranio
     */
    public boolean obtain_closer_path_ManhatanGreedy(State start, StateObservation stateObs)
    {

        int distancia_minima;
        int posicion=-1; //OJO AL FALLO
        
        ArrayList<Integer> pos_distancia_repetida = new ArrayList<>();          //Array con los indices de las gemas que estan a distancia repetidas
        ArrayList<Integer> distancias = new ArrayList<>();
        ArrayList<ACTIONS> camino = new ArrayList<>();
        ArrayList<ACTIONS> ruta_optima = new ArrayList<>();
        
        State partida = new State(start);
        Vector2d gema = new Vector2d();
        
        while(this.gemas_ordenadas.size() < gemas_to_pick)                      //Mientras no hayamos ordenado todas las gemas
        {
            
            distancia_minima = 10000000;
            for( int j = 0 ; j <  this.gemas_no_seleccionadas.size(); j++)      //Para cada gema no seleccionada
            {
                gema = new Vector2d(gemas_no_seleccionadas.get(j));
                distancias.add( distance_Manhatan(partida.position, gema) );    //Calculamos la distancia manhatan
                if( distancias.get(j) < distancia_minima)                       //Si tenemos un nuevo minimo
                {
                    pos_distancia_repetida.clear();                             //Limpiamos las distancias repetidas
                    distancia_minima = distancias.get(j);                       //Ajustamos nuevo minimo
                    posicion = j;                                               //Decimos en que posicion se encuentra
                    pos_distancia_repetida.add(j);                              //Añadimso al array 
                } else if(distancias.get(j) == distancia_minima)                //Si esta repetido
                    pos_distancia_repetida.add(j);                              //Lo incluimos en el array de elementos minimos repetidos
            }
            distancias.clear();                                                 
            if(pos_distancia_repetida.size() == 1)                              //Tengo un único repetido
            {
               if(posicion == -1)
                   System.out.println("Mirar metodo obtain_closer_path_manhatan de la clase gemas");
               gema = new Vector2d(gemas_no_seleccionadas.get(posicion));       
               camino = get_path_from_to(partida,gema,stateObs);                //Calculo el camino para obtener la orientacion
               partida.position = gema;                                         //Inicializo la siguiente posiccion de partida
               partida.orientation = get_orientation_from_path(camino); 
               this.gemas_ordenadas.add(gema);                                  //Aniadimos gema ordenada
               this.gemas_no_seleccionadas.remove(gema);                        //La seleccionamos y la quitamos de las no seleccionadas
            }
            
            if(pos_distancia_repetida.size() > 1)                               //Si tengo algunas repetidas
            {
                Vector2d mejor_gema = new Vector2d();
                distancia_minima = 10000000;
                //Me quedo con la más cercana segun el greedy 
                for( int k = 0 ; k < pos_distancia_repetida.size(); k++)        //Para cada gema con misma distancia manhatan
                {
                    gema = new Vector2d(gemas_no_seleccionadas.get(k));                   
                    camino = deep_copy(get_path_from_to(partida, gema, stateObs));//Calculo el greedy
                    if(get_distance_from_path(camino) < distancia_minima)       //Me quedo con la más cercana
                    {
                       distancia_minima = get_distance_from_path(camino);
                       mejor_gema = new Vector2d(gemas_no_seleccionadas.get(k));    
                    }
                    camino.clear();
                }
                camino = deep_copy(get_path_from_to(partida,mejor_gema,stateObs));
                partida.position = mejor_gema;
                partida.orientation = get_orientation_from_path(camino);
                this.gemas_ordenadas.add(mejor_gema);
                this.gemas_no_seleccionadas.remove(mejor_gema);
                
                
            }
        }
        if(this.gemas_ordenadas.size() == this.gemas_to_pick )
            return true;
        else 
            return false;
    }


    
    //======================PARA EL NIVEL 5=========================
    
    /**
     * Nos dice la ruta a seguir para ir a la gema más cercana según la distancia Manhatan
     * @param comienzo: Posicion de partida
     * @param stateObs
     * @return ArrayList de acciones
     */
    public ArrayList<ACTIONS> next_gema_path_Manhatan(State comienzo, StateObservation stateObs)
    {
       ArrayList<ACTIONS> ret   = new ArrayList<>();
       Vector2d gema            = new Vector2d();
       Vector2d closer_gema     = new Vector2d();
       int minimo               = 10000000;
       int current_distancia;
       
       //Para cada gemas que no hayamos cogido
       for(int i = 0 ; i < this.gemas_no_seleccionadas.size(); i++)
       {   //Capturamos la gema y calculamos la distancia
           gema = new Vector2d(this.gemas_no_seleccionadas.get(i));
           current_distancia = this.distance_Manhatan(gema, comienzo.position);
           //Me quedo con la que tenga menor distancia
           if(current_distancia < minimo)
           {
               minimo = current_distancia;
               closer_gema = new Vector2d(gema);
           }
       }
       
       //Obtenemos el camino a al gema
       camino = this.get_path_from_to(comienzo, closer_gema, stateObs);
       if(camino.isEmpty())
           System.out.println("Echale un ojo al metodo next_gema_");
      
       
       return ret;
    }
    
    
    
    public void update_no_seleccionadas_gemas(Vector2d gema)
    {
        this.gemas_no_seleccionadas.remove(gema);
    }
     
    
    
    
    //Esta funcion al final la uso en el nivel dos debido a un fallo de ultima hora
    /**
     * Nos devuelve la gema más cercana desde la posicion de comienzo
     * @param comienzo: Posicion de partida
     * @param stateObs
     * @return Vector con la posicion de la gema
     */
    public Vector2d next_gema_Manhatan(State comienzo, StateObservation stateObs)
    {
       ArrayList<ACTIONS> ret   = new ArrayList<>();
       Vector2d gema            = new Vector2d();
       Vector2d closer_gema     = new Vector2d();
       int minimo               = 10000000;
       int current_distancia;
       
       //Para cada gemas que no hayamos cogido
       for(int i = 0 ; i < this.gemas_no_seleccionadas.size(); i++)
       {   //Capturamos la gema y calculamos la distancia
           gema = new Vector2d(this.gemas_no_seleccionadas.get(i));
           current_distancia = this.distance_Manhatan(gema, comienzo.position);
           //Me quedo con la que tenga menor distancia
           if(current_distancia < minimo)
           {
               minimo = current_distancia;
               closer_gema = new Vector2d(gema);
           }
       }
          
       return closer_gema;
    }
    
}
