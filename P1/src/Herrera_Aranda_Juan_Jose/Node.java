/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Herrera_Aranda_Juan_Jose;

import ontology.Types;
import ontology.Types.ACTIONS;
import tools.Direction;
import tools.Vector2d;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Clase State para modelar el estado del avatar.
 * Compuesta por dos Vectores, uno que indica la posicion y otro la orientacion
 * @author kiedie
 */

class State{
        public Vector2d position;
        public Vector2d orientation;
        
        //Constructor por defecto
        public State()
        {
            position = new Vector2d();
            orientation = new Vector2d();
        }
        
        //Constructor por parametros
        public State(Vector2d pos, Vector2d ori)
        {
            position    = new Vector2d(pos);
            orientation = new Vector2d(ori);
        }
        
        //Constructor copia
        public State (State e)
        {
            this.position    = new Vector2d(e.position);
            this.orientation = new Vector2d(e.orientation);
        }
        
        /**
         * Crea un nuevo estado a partir de un nodo y una acción a realizar
         * El nuevo estado será la futura posición y orientación del nodo al realizar la accion
         * @param node: Nodo de comienzo
         * @param act:  Acción que ejecuta el nodo
         * @return Nuevo estado
         */
        public State ( Node node, Types.ACTIONS act)
        {
            this.orientation = new Vector2d(node.state.orientation);
            this.position    = new Vector2d(node.state.position);
            
            if(act == Types.ACTIONS.ACTION_RIGHT)               //Si nos movemos a la derecha
            {
                if( this.orientation.x == 1 )                   //Y estatamos orientados a la derecha
                {
                    this.position.x = this.position.x + 1;      //Nos desplazamos
                }
                this.orientation = new Vector2d(1,0);           //Nuestra orientacion es la de la derecha
            }
            
            if(act == Types.ACTIONS.ACTION_LEFT)                //El resto es análogo
            {
                if( this.orientation.x == -1 )
                {
                    this.position.x = this.position.x -1;
                }
                this.orientation = new Vector2d(-1,0);
            }
            
            if(act == Types.ACTIONS.ACTION_UP)
            {
                if( this.orientation.y == -1 )
                {
                    this.position.y = this.position.y -1;
                }
                this.orientation = new Vector2d(0,-1);
            }
            
            if(act == Types.ACTIONS.ACTION_DOWN)
            {
                if( this.orientation.y == 1 )
                {
                    this.position.y = this.position.y + 1;
                }
                this.orientation = new Vector2d(0,1);
            }
        }
        
        //Igual que la función anterior pero en vez de con un nodo, con una casilla
        public State ( State casilla, Types.ACTIONS act)
        {
            this.orientation = new Vector2d(casilla.orientation);
            this.position    = new Vector2d(casilla.position);
            
            if(act == Types.ACTIONS.ACTION_RIGHT)
            {
                if( this.orientation.x == 1 )
                {
                    this.position.x = this.position.x + 1;
                }
                this.orientation = new Vector2d(1,0);
            }
            
            if(act == Types.ACTIONS.ACTION_LEFT)
            {
                if( this.orientation.x == -1 )
                {
                    this.position.x = this.position.x -1;
                }
                this.orientation = new Vector2d(-1,0);
            }
            
            if(act == Types.ACTIONS.ACTION_UP)
            {
                if( this.orientation.y == -1 )
                {
                    this.position.y = this.position.y -1;
                }
                this.orientation = new Vector2d(0,-1);
            }
            
            if(act == Types.ACTIONS.ACTION_DOWN)
            {
                if( this.orientation.y == 1 )
                {
                    this.position.y = this.position.y + 1;
                }
                this.orientation = new Vector2d(0,1);
            }
        }

}



public class Node implements Comparable<Node>{
    
    public State state;                             //Posicion y orientacion que tendrá el avatar en ese nodo
    public Node parent;                             //Nodo padre
    public Types.ACTIONS comingFrom;                //Representa la acción que ha tenido que tomar el padre para llegar al hijo
    
    public int moveCost;
    public int heuristicCost;
    public int id;                                  //Me identifica al nodo de forma única
 
    /**
     * Contructor con parametros
     * @param posicion      
     * @param orientacion 
     */
    public Node (Vector2d posicion, Vector2d orientacion)
    {
        state           = new State(posicion,orientacion);
        parent          = null;
        comingFrom      = Types.ACTIONS.ACTION_NIL;
        heuristicCost   = 0;
        moveCost        = 0;
        id              = ((int)state.position.x)*1000000+ ((int)state.position.y)*1000 +10*((int)state.orientation.x) + ((int)state.orientation.y);
        
    }

    /**
     * Constructor copia
     * @param e: Nodo del cual vamos a hacer la copia
     */
    public Node (Node e)
    {

        state = new State( new Vector2d(e.state.position.x,e.state.position.y) , new Vector2d(e.state.orientation.x, e.state.orientation.y) );       
        parent = e.parent;
        comingFrom = e.comingFrom;
        heuristicCost = e.heuristicCost;
        moveCost = e.moveCost;
        id = e.id;
    }

    /**
     * CONSTRUCTOR 
     * Crea un nodo hijo a través de la accion que toma el padre
     * @param parent:   Nodo padre
     * @param act:      Accion llevada a cabo por el nodo padre
     * @param goal:     Objetivo, es necesario para computar el coste de la heuristica
     */
    public Node(Node parent,Types.ACTIONS act ,Vector2d goal)
    {
       this.state         = new State( parent, act);
       this.heuristicCost = (int) (Math.abs(this.state.position.x - goal.x) + Math.abs(this.state.position.y - goal.y));
       this.moveCost      = parent.calculateMoveCost(act) ;
       this.parent        = parent;
       this.comingFrom    = act;
       
       this.id = ((int)state.position.x)*1000000 + ((int)state.position.y)*1000 +10*((int)state.orientation.x) + ((int)state.orientation.y);
    }
    
    //Calcula la distancia manhatan de la posicion que tiene la clase hasta la pasada por parametro
    public int distanceManhatan( Vector2d fin )
    {
        return (int) (Math.abs(this.state.position.x - fin.x) + 
                      Math.abs(this.state.position.y-fin.y));
    }
    
    /**
     * Compara si el nodo de la clase tiene un coste mejor que el nodo del argumento
     * Entendemos por mejor coste aquel que es menor
     * @param n: Nodo con el cual queremos comparar
     * @return True Si el nodo de la clase tiene un coste menor y False en el caso contrario
     */
    public boolean isBest(Node n)
    {
        if(this.heuristicCost + this.moveCost < n.heuristicCost + n.moveCost)
            return true;
        if(this.heuristicCost + this.moveCost > n.heuristicCost + n.moveCost)
            return false; 
        return false;
    }
    
    /**
     * Calcula el coste de moverse
     * @param act
     * @return el coste en movimiento del nodo si realiza la acción
     */
    public int calculateMoveCost(Types.ACTIONS act)
    {
        int ret = 0;
        if(act == Types.ACTIONS.ACTION_NIL)
            ret = this.moveCost;
        //Esto lo tengo así aunque parezca un poco redundante,
        //este metodo me daba algunos fallos,
        //y decidí que tras arreglarlo, no tocar nada por si acaso 
        if (act==Types.ACTIONS.ACTION_RIGHT) {
            ret = this.moveCost+1;
            if(this.state.orientation.x==-1 ){ //Estoy mirando a la izquierda
                ret = this.moveCost+1;
            }
        }else if (act==Types.ACTIONS.ACTION_LEFT) {
            ret = this.moveCost+1;
            if(this.state.orientation.x==1){ //Estoy mirando hacia la derecha
                ret = this.moveCost+1;
            }
        }else if (act==Types.ACTIONS.ACTION_UP) {
            ret = this.moveCost+1;
            if(this.state.orientation.y==1){ //Estoy mirando hacia abajo
                ret = this.moveCost+1;
            }
        }else if (act==Types.ACTIONS.ACTION_DOWN) {
            ret = this.moveCost+1;
            if(this.state.orientation.y==-1){ //Estoy irando haccia arriba
                ret = this.moveCost+1;
            }
        }
        
        return ret;
    }
    
    /**
     * Calcula la ruta de acciones que hay que realizar para ir desde la raiz al nodo actual
     * @return lista de acciones
     */
    public ArrayList<ACTIONS> calculate_path_from_node()
    {
        ArrayList<ACTIONS> ret = new ArrayList<>();
        Node current = this.parent;
        while(current.parent != null)                   //Mientres no llegue al nodo raid
        {
            ret.add(current.comingFrom);                //Meto la accion que realizó mi padre
        }
        Collections.reverse(ret);                       //Doy la vuelta porque he añadido en orden inverso
        return ret;
    }
    
    
    //Compara si un nodo es mayor o menor que otro.
    //La funcion isBest es similar
    //Esta funcion será usada en el A estrella como sobrecarga del operador menor y mayor
    @Override
    public int compareTo(Node n) 
    {
        if(this.heuristicCost + this.moveCost < n.heuristicCost + n.moveCost)
            return -1;
        if(this.heuristicCost + this.moveCost > n.heuristicCost + n.moveCost)
            return 1;
        return 0;
    }
    
    //Compara si don nodos son iguales.
    //Dos nodos son iguales si tienen el mismo identificador
    @Override
    public boolean equals(Object o)
    {
        return this.id == ((Node)o).id;
    }
    
    
    

    
    
    

    
    
}
