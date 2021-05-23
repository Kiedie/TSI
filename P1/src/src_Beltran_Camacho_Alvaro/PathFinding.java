/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src_Beltran_Camacho_Alvaro;

import core.game.Observation;
import core.game.StateObservation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import ontology.Types;
import tools.Vector2d;

/**
 *
 * @author alvar
 */
public class PathFinding {
    ArrayList<Observation>[][] mundo;
    ArrayList<Types.ACTIONS> path;
    Nodo Current;
    
    PathFinding(StateObservation stateObs){
        mundo=stateObs.getObservationGrid();
        path=new ArrayList<Types.ACTIONS>();
    }
    
    class Compare implements Comparator<Nodo> { 
        @Override
        public int compare(Nodo nodo1, Nodo nodo2) 
        { 
            if(nodo1.f+nodo1.h<nodo2.f+nodo2.h){
                return -1;
            }else if(nodo1.f+nodo1.h>nodo2.f+nodo2.h){
                return 1;
            }
            return 0;
        }
    }
    
    public boolean Aestrela(Avatar avatar,Vector2d portal){
        int muro=0;
        PriorityQueue<Nodo> abiertos=new PriorityQueue<Nodo>(new PathFinding.Compare());
        HashMap<Double, Nodo> cerrados=new HashMap<Double, Nodo>();

        Current = new Nodo(avatar.pos,avatar.ori);
        abiertos.add(Current);

        while(!abiertos.isEmpty() && (Current.estado.posicion.x!=portal.x || Current.estado.posicion.y != portal.y)){
            abiertos.poll();
            cerrados.put(Current.id, Current);
            for(int i=0;i<4;i++){
                Types.ACTIONS act=Types.ACTIONS.ACTION_NIL;
                switch(i) {
                    case 0:
                        act=Types.ACTIONS.ACTION_RIGHT;
                        break;
                    case 1:
                        act=Types.ACTIONS.ACTION_LEFT;
                        break;
                    case 2:
                        act=Types.ACTIONS.ACTION_UP;
                        break;
                    case 3:
                        act=Types.ACTIONS.ACTION_DOWN;
        
                }

                Nodo hijo=new Nodo(Current,act,portal);

                if(hijo.estado.posicion.x>=0 && hijo.estado.posicion.x<mundo.length && hijo.estado.posicion.y>=0 && hijo.estado.posicion.y<mundo.length)
                    if(!cerrados.containsKey(hijo.id) ){
                        if(mundo[(int) hijo.estado.posicion.x][(int) hijo.estado.posicion.y].size()==0){
                            abiertos.add(hijo);
                        }else{
                            for(Observation obs : mundo[(int) hijo.estado.posicion.x][(int) hijo.estado.posicion.y]){
                                if(obs.itype==muro){
                                    cerrados.put(hijo.id, hijo);
                                }else{
                                    abiertos.add(hijo);
                                }
                            }
                        }

                    }else{
                        if(hijo.f+hijo.h<cerrados.get(hijo.id).f+cerrados.get(hijo.id).h){
                            cerrados.remove(hijo.id);
                            abiertos.add(hijo);
                        }
                    }
            }
            if(!abiertos.isEmpty()){
                Current=abiertos.peek();
            }

        }
        if(Current.estado.posicion.x==portal.x && Current.estado.posicion.y == portal.y){
            path=(ArrayList<Types.ACTIONS>) Current.recorrido.clone();
            return true;
        }else{
            return false;
        }


    }
}