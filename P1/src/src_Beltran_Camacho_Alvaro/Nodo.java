/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src_Beltran_Camacho_Alvaro;

import java.util.ArrayList;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.Vector2d;

/**
 *
 * @author alvar
 */


public class Nodo {
    ArrayList<ACTIONS> recorrido;
    Estado estado;
    int h,f;
    double id;
    
    public Nodo(Vector2d posini,Vector2d orini){
        h=0;
        f=0;
        recorrido=new ArrayList<ACTIONS>();
        estado=new Estado(posini,orini);
        id=estado.posicion.x*1000000+estado.posicion.y*1000+estado.orientacion.x*10+estado.orientacion.y;
    }
    
    public Nodo(Nodo padre,Types.ACTIONS act,Vector2d portal){
        h=0;
        f=0;
        Types.ACTIONS act_prev=Types.ACTIONS.ACTION_NIL;
        recorrido=(ArrayList<ACTIONS>) padre.recorrido.clone(); 
        recorrido.add(act);
        
        f=padre.f+1;
        
        estado = new Estado(padre, act);
        
        h=(int) (Math.abs(estado.posicion.x - portal.x) + Math.abs(estado.posicion.y-portal.y));
        id=estado.posicion.x*1000000+estado.posicion.y*1000+estado.orientacion.x*10+estado.orientacion.y;
    }
    
}

class Estado{
    public  Vector2d posicion;
    public  Vector2d orientacion;
    
    public Estado(Vector2d posini,Vector2d orini){
        posicion= new Vector2d(posini.x,posini.y);
        orientacion= new Vector2d(orini.x,orini.y);
    }
    
    public Estado(Nodo padre, Types.ACTIONS act){
        
        posicion = new Vector2d(padre.estado.posicion.x, padre.estado.posicion.y);
        orientacion = new Vector2d(padre.estado.orientacion.x, padre.estado.orientacion.y);
        
        if (act==Types.ACTIONS.ACTION_RIGHT) {
            if(orientacion.x==1){
                posicion = new Vector2d(posicion.x +1, posicion.y);
            }
            orientacion= new Vector2d(1,0);
        }else if (act==Types.ACTIONS.ACTION_LEFT) {
            if(orientacion.x==-1){
                posicion = new Vector2d(posicion.x -1, posicion.y);
            }
            orientacion= new Vector2d(-1,0);
        }else if (act==Types.ACTIONS.ACTION_UP) {
            if(orientacion.y==-1){
                posicion = new Vector2d(posicion.x, posicion.y-1);
            }
            orientacion= new Vector2d(0,-1);
        }else if (act==Types.ACTIONS.ACTION_DOWN) {
            if(orientacion.y==1){
                posicion = new Vector2d(posicion.x, posicion.y+1);
            }
            orientacion= new Vector2d(0,1);
        }
        
        
    }
}