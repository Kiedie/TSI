/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Herrera_Aranda_Juan_Jose;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.*;
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.Vector2d;


public class Astar {
    
    public ArrayList<ACTIONS> path;
    public int muro;
    public Node nodePath;               //Nodo a partir del cual recuperaremos el camino
    
    
    
    //Constructor
    Astar(){
        path    = new ArrayList<>();
        muro    =0; 
        nodePath = null;
    }
    
    
    
    /**
     * Limpia el path y el nodo raíz
     */
    public void clearPath()
    {
        path.clear();
        nodePath = null;
    }
    
    
    
    /**
     * Comprueba que una posicion es segura
     * una posicion es segura si no existe ningun muro.
     * No comprueba si el avatar se va fuera del mapa
     * @param position: Posicion a comprobar
     * @param stateObs: state observation actual
     * @return True si es segura y false en caso contrario
     */
    public boolean isSafe(Vector2d position, StateObservation stateObs){
        
        boolean ret = true;                                                     //Por defecto devuelve true
        int x = (int) position.x;                                               //Tomo las cordenadas de la posicion
        int y = (int) position.y;
        
        for(Observation obs : stateObs.getObservationGrid()[x][y])              //Si en el grid hay un muro
            if ( obs.itype == muro ){
                ret = false;                                                    //Devuelvo que la posicion no es segura
            }
        return ret;
    }
    
    
    
    /**
     * Metodo que genera un vecindario.
     * Entendemos por vecindarios a los nodos contiguos al actual.
     * @param node      Nodo actual
     * @param objetive  Objetivo al que queremos llegar
     * @param stateObs
     * @return Lista de nodos vecinos
     */
    public ArrayList<Node> generateNeighbourdhood(Node node, Vector2d objetive, StateObservation stateObs){
        
        ArrayList<Node> ret = new ArrayList<>();
        //Genero tods los hijos posibles a traves de las 4 acciones
        Node left   = new Node(node,Types.ACTIONS.ACTION_LEFT, objetive);
        Node right  = new Node(node,Types.ACTIONS.ACTION_RIGHT,objetive);
        Node up     = new Node(node,Types.ACTIONS.ACTION_UP,   objetive);
        Node down   = new Node(node,Types.ACTIONS.ACTION_DOWN, objetive);

        //Compruebo que los hijos son factibles
        if (isSafe(left.state.position, stateObs))  ret.add(left);
        if (isSafe(right.state.position,stateObs))  ret.add(right);
        if (isSafe(up.state.position,   stateObs))  ret.add(up);
        if (isSafe(down.state.position, stateObs))  ret.add(down);
       
        return ret; 
    }
    
    
    
    //Compruebo si el nodo pasado por argumento es el objetivo al que quieremos llegar
    public boolean isObjetivo(Node nodo, Vector2d portal){
        return nodo.state.position.equals(portal);
    }
    
    
    
    /**
     * Nos modifica el estado interno de la clase con el nodo campeon.
     * Es decir, el nodo que tiene todo el camino. 
     * NO RECONSTRUYE EL CAMINO. ESO SE HACE EN OTRA FUNCION 
     * @param state: Posicion y orientascion del Avatar
     * @param fin:   Objetivo al que queremos llegar
     * @param stateObs
     * @return: True si se ha encontrado el camino y False en caso contrario 
     */
    public boolean pathfind_Astar(State state, Vector2d fin, StateObservation stateObs)
    {
        clearPath();                                                            
        boolean isFind;
        boolean nodoObjetivo = false;
        //Usamos dos ED para la lista de abiertos
        HashMap<Integer,Node> abiertos_tree  = new HashMap<>();                 //Para búsqueda
        PriorityQueue<Node>   abiertos_cola  = new PriorityQueue<>();           //Para orden
        HashMap<Integer,Node> cerrados       = new HashMap<>();
        
        Node current            = new Node(state.position,state.orientation);   //ojo, aqui las heuristicas son cero
        ArrayList<Node> vecinos = new ArrayList<>();
        
        abiertos_cola.add(current);
        abiertos_tree.put(current.id, current);
        
        //En la primera iteracion se comprueba que es el objetivo
        do{
           //Extraemos el nodo candidato
           current = abiertos_cola.poll();
           abiertos_tree.remove(current.id);
           //Lo sacamos de cerrados
           cerrados.put(current.id, current);
           //Comprobamos que es objetivo
           nodoObjetivo = isObjetivo(current,fin);
           
           //Si no lo es
            if(nodoObjetivo == false){
                //Expandimos el nodo
                vecinos = generateNeighbourdhood(current,fin,stateObs);
                for (Node vecin : vecinos)   //para cada vecino
                {
                    //Si el nodo ya está en cerrados y el nuevo coste es mejor que el anterior
                    if(cerrados.containsKey(vecin.id) && vecin.isBest( cerrados.get(vecin.id) ) )
                    {   //Lo exploraré más tarde, para ello:
                        cerrados.remove(vecin.id);                 //Saco de cerrados
                        abiertos_cola.add((vecin));                //Meto en abiertos
                        abiertos_tree.put(vecin.id, vecin);
                    }
                    else //Si el nodo no está en cerrados o el coste no es mejor
                    {
                        //Si no está ni en cerrados ni en abiertos
                        if(!cerrados.containsKey(vecin.id) && !abiertos_tree.containsKey(vecin.id))
                        {
                            //Lo meto en abiertos
                            abiertos_cola.add((vecin));
                            abiertos_tree.put(vecin.id, vecin);
                        }
                        else //Si está en alguno de los dos
                        {    //Si está en abiertos pero con un coste peor
                            if (abiertos_tree.containsKey(vecin.id) && vecin.isBest( abiertos_tree.get(vecin.id) ) ) 
                            {   //Actualizo la lista de abiertos:
                                //Saco el antiguo
                                Node nodoFuera = new Node(abiertos_tree.get(vecin.id)); //Esto es necesario para borrarlo de la cola de prioridad
                                abiertos_cola.remove(nodoFuera);
                                abiertos_tree.remove(vecin.id);

                                //Meto el nuevo
                                abiertos_cola.add(vecin);
                                abiertos_tree.put(vecin.id, vecin);
                            }
                            
                        }
                    }
                }
                
            } else //Si ya hemos encontrado al objetivo
            {
                nodoObjetivo = true;
                nodePath = current;
                
            }
        } while( nodoObjetivo == false && !abiertos_cola.isEmpty() );
        
        return nodoObjetivo;
    }
    
    
    
    //Metodo alternativo que solo lo he usado para depurar el anterior, NO HACER CASO
    public boolean pathfind_Astar_2(State state, Vector2d fin, StateObservation stateObs)
    {
        boolean isFind;
        boolean nodoObjetivo = false;
        //HashMap<Integer,Node> abiertos_tree  = new HashMap<>();             //Para búsqueda
        PriorityQueue<Node>   abiertos_cola  = new PriorityQueue<>();       //Para orden
        HashMap<Integer,Node> cerrados = new HashMap<>();
        
        Node current = new Node(state.position,state.orientation); //ojo, aqui la heuristicas son cero
        ArrayList<Node> vecinos = new ArrayList<>();
        
        abiertos_cola.add(current);
        //abiertos_tree.put(current.id, current);
        
        while(!abiertos_cola.isEmpty())
        {
            current = abiertos_cola.poll();
            //abiertos_tree.remove(current.id);
            
            if(!cerrados.containsKey(current.id))
            {
                if(isObjetivo(current,fin))
                {
                    nodoObjetivo = true;
                    nodePath = current;
                    return true;
                }
                else
                {
                    vecinos = generateNeighbourdhood(current,fin,stateObs);
                    for(Node vecin : vecinos)
                    {
                        if(!cerrados.containsKey(vecin.id))
                        {
                            abiertos_cola.add(vecin);
                            //abiertos_tree.put(vecin.id, vecin);
                        }
                    }
                }
                cerrados.put(current.id, current);
            }
            
        }
        return false;
        
    }
    
    
    
    /**
     * Metodo que modifica el estado interno de la clase. 
     * Recupera el camino a través del nodo
     * @param  node: Nodo por el cual recuperaremos el camino
     */
   public void setPathBacktraking()
   {
       if(this.nodePath != null)            //Si no es la raiz
       {
            Node current = this.nodePath;   //Asciendo al padre
            while(current.parent != null)
            {
                path.add(current.comingFrom);//Añado la posicion que realiza el padre
                current = current.parent;
            }
            Collections.reverse(path);       //Revierto el orden   
       }else{
           System.out.println("Ups, algo ha salido mal. Echale un ojo al metodo setPathBacktraking en Astar.java");
       }
   }
   
   
   //Método get
    public ArrayList<ACTIONS> getPath(){ return path; }
    
    
    
    /**
     * Devuelve la siguiente accion a realizar cuando ya tengamos un camino
     * Además la borra del path
     * @return 
     */
    public ACTIONS getNextAction()
    {
        Types.ACTIONS act = Types.ACTIONS.ACTION_NIL;                           //Por defecto es nula
        if(!this.path.isEmpty()){                                               //Mientras no hayamos dado todo el camino
            
            act = this.path.get(0);                                             //Tomamos la primera accion
            this.path.remove(0);                                                //La borramos
        }
        return act;
    }
    
    
    //Imprime el camino
    public void printPathFinder()
    {
        if(this.path.isEmpty())
        {
            System.out.println("No Path");
            return;
        }
        
        int contador = 0;
        while(contador<this.path.size())
        {
            System.out.println(this.path.get(contador));
            contador++;
        }
        
    }
            
   
    
}
