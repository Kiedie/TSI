package src_Beltran_Camacho_Alvaro;

import java.util.ArrayList;
import java.util.Collections;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

class Avatar{
    Vector2d pos;
    Vector2d ori;
}

public class Agent extends AbstractPlayer{
        
        
        int numGemas;
        boolean isgemas;
        boolean ispath;
        boolean isEscapando;
        boolean nivel_1y2;
        boolean nivel_3y4;
        boolean nivel_5;
        int num_gemas_ant;
        Vector2d posGemaAct;
	Vector2d fescala;
	Vector2d portal;
        ArrayList<ArrayList<Integer>>mapaCalor;
        ArrayList<Observation>[][] mundo;
        ArrayList<Observation> enemigos;
        PathFinding pathFinding;
        OrdenGemas ordenGemas;
        Avatar avatar;
	ArrayList<Observation>[] gemas;
	/**
	 * initialize all variables for the agent
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 */
	public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
            ispath=false;
            isgemas=false;
            isEscapando=false;
            numGemas=0;
            posGemaAct= new Vector2d(-1,-1);
            
		//Calculamos el factor de escala entre mundos (pixeles -> grid)
            fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length , 
                            stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);      

            //Se crea una lista de observaciones de portales, ordenada por cercania al avatar
            ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
            mundo=stateObs.getObservationGrid();
            //Seleccionamos el portal mas proximo
            portal = posiciones[0].get(0).position;
            portal.x = Math.floor(portal.x / fescala.x);
            portal.y = Math.floor(portal.y / fescala.y);
            
            pathFinding= new PathFinding(stateObs);
            
            avatar = new Avatar();
            avatar.pos =  new Vector2d(stateObs.getAvatarPosition().x / fescala.x, stateObs.getAvatarPosition().y / fescala.y);
            avatar.ori = stateObs.getAvatarOrientation(); 
            
            nivel_1y2=stateObs.getNPCPositions()==null;
            nivel_3y4=stateObs.getNPCPositions()!=null && stateObs.getResourcesPositions()==null;
            nivel_5=stateObs.getNPCPositions()!=null && stateObs.getResourcesPositions()!=null;
            
            
            
            //gemas
            if(stateObs.getResourcesPositions()!=null){
                gemas=stateObs.getResourcesPositions(stateObs.getAvatarPosition());
                ordenGemas=new OrdenGemas(stateObs,avatar,portal);
                ordenGemas.simulateAnnealing(elapsedTimer);
                isgemas=true;
                num_gemas_ant=gemas[0].size();
            }
                
	}
	
	/**
	 * return the best action to arrive faster to the closest portal
	 * @param stateObs Observation of the current state.
         * @param elapsedTimer Timer when the action returned is due.
	 * @return best	ACTION to arrive faster to the closest portal
	 */
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        
            
            avatar.pos =  new Vector2d(stateObs.getAvatarPosition().x / fescala.x, stateObs.getAvatarPosition().y / fescala.y);
            avatar.ori = stateObs.getAvatarOrientation(); 
            //nivel 2 y 1
            if(nivel_1y2){
                if(avatar.pos.x==posGemaAct.x && avatar.pos.y==posGemaAct.y){
                    ispath=false;
                    numGemas++;
                    posGemaAct= new Vector2d(-1,-1);
                }            

                if(numGemas<10 && !ispath && stateObs.getResourcesPositions()!=null){
                    if(stateObs.getResourcesPositions().length!=0){      
                       if(!ispath){
                           posGemaAct=new Vector2d(gemas[0].get(ordenGemas.getIndexSol()).position.x / fescala.x, gemas[0].get(ordenGemas.getIndexSol()).position.y / fescala.y);
                           pathFinding.Aestrela(avatar,posGemaAct);
                           ispath=true;
                           ordenGemas.indiceSol++;      
                       }

                    }                
                }


                if( (stateObs.getResourcesPositions()==null || numGemas==10) && !ispath ){
                   pathFinding.Aestrela(avatar,portal);
                   ispath=true;
                } 
            }  
            
            //nivel 3 y 4
            if(nivel_3y4){
                
                if(pathFinding.path.isEmpty()){
                    evitarEnemigos(stateObs);
                    ispath=true;
                }else if( compruebaCasilla(stateObs) && !isEscapando){
                    evitarEnemigos(stateObs);
                    ispath=true;
                }
            }
            
            //nivel 5
            if(nivel_5){
                //si el numero de gemas que hay a cambiado es que hemos cogido una.
                //si no hay es que la hemos cogido.
                if(stateObs.getResourcesPositions()==null){
                    num_gemas_ant=0;
                    numGemas++;
                }else if(num_gemas_ant!=stateObs.getResourcesPositions()[0].size() ){
                    
                    numGemas++;  
                    num_gemas_ant=stateObs.getResourcesPositions()[0].size();                   
                    
                }
                
                //si ha llegado a la gema de la solucion
                if(avatar.pos.x==posGemaAct.x && avatar.pos.y==posGemaAct.y){
                    
                    ispath=false;
                    ordenGemas.indiceSol++;
                    posGemaAct= new Vector2d(-1,-1);
                }
                
                if( compruebaCasilla(stateObs) && !isEscapando){
                    pathFinding.path.clear();
                    evitarEnemigos(stateObs);
                    isEscapando=true;
                    ispath=true;
                }

                if(numGemas<10 && !ispath && stateObs.getResourcesPositions()!=null && !isEscapando){
                    if(stateObs.getResourcesPositions().length!=0){      
                       if(!ispath && ordenGemas.getIndexSol()!=-2){
                           posGemaAct=new Vector2d(gemas[0].get(ordenGemas.getIndexSol()).position.x / fescala.x, gemas[0].get(ordenGemas.getIndexSol()).position.y / fescala.y);
                           pathFinding.Aestrela(avatar,posGemaAct);
                           ispath=true;                           
                       }

                    }                
                }
                

                if( (stateObs.getResourcesPositions()==null || numGemas>=10) && !isEscapando &&!ispath){
                   pathFinding.Aestrela(avatar,portal);
                   ispath=true;
                }
            }
            
            
            
            
            Types.ACTIONS act=Types.ACTIONS.ACTION_NIL;
            
            
            if(ispath && pathFinding.path.size()>0){
               act=pathFinding.path.get(0);
               pathFinding.path.remove(0); 
            }
            if(pathFinding.path.isEmpty()){
                ispath=false;
                isEscapando=false;
            }
            
            //System.out.println(act);
            return act; 
           
            
            
             
		
	}
        
        public boolean compruebaCasilla(StateObservation stateObs){
            enemigos=stateObs.getNPCPositions(avatar.pos)[0];
            ArrayList<Observation> enemigosCerca = new ArrayList<>();
            PathFinding pathFinding_aux = new PathFinding(stateObs);
            int distancia=0;
            boolean ispath=true;
            //miro los enemigos cerca.
            for(Observation enemigo : enemigos){
                Vector2d enemigoPos =new Vector2d(enemigo.position.x/fescala.x,enemigo.position.y/fescala.y);
                ispath=pathFinding_aux.Aestrela(avatar,enemigoPos);
                distancia=pathFinding_aux.path.size();
                if(ispath && distancia<=5){
                    return true; //hay enemigo
                }
            }
            return false;
        }
        
        public void evitarEnemigos(StateObservation stateObs){
            
            enemigos=stateObs.getNPCPositions(stateObs.getAvatarPosition())[0];
            ArrayList<Observation> enemigosCerca = new ArrayList<>();
            ArrayList<Double> distancias = new ArrayList<>();
            double distancia=0;
            boolean ispath=true;
            //miro los enemigos cerca.
            for(Observation enemigo : enemigos){
                Vector2d enemigoPos =new Vector2d(enemigo.position.x/fescala.x,enemigo.position.y/fescala.y);
                ispath=pathFinding.Aestrela(avatar,enemigoPos);
                distancia=Math.sqrt(Math.pow(enemigoPos.x - avatar.pos.x,2 ) + Math.pow(enemigoPos.y-avatar.pos.y ,2));;
                if(ispath && distancia<=5.1){
                    enemigosCerca.add(enemigo);
                    distancias.add(distancia);
                }
            }
            //si hay enemigos cerca tengo que hacer una ruta de escape.
            if(!enemigosCerca.isEmpty()){
                int[] x_arrNeig = new int[] {1, -1, 0, 0, 1, -1, 1, -1};
                int[] y_arrNeig = new int[] {0, 0, 1, -1, 1, -1, -1, 1};
                Vector2d enemigoPos;
                isEscapando=true;
                boolean muro;//muro
                ArrayList<Vector2d> casillasPosibles= new ArrayList<>();
                //miro que casillas son seguras
                while(casillasPosibles.isEmpty()){
                                                
                    for(int i=0; i<x_arrNeig.length; i++){
                        boolean d=true;//distancia
                        muro=false;
                        //miro si hay muro
                        for(Observation obs : mundo[(int)avatar.pos.x+x_arrNeig[i]][(int)avatar.pos.y+y_arrNeig[i]]){
                            if(obs.itype==0){//muro
                                muro=true;
                                break;
                            }
                        }
                        //miro si me alejo de los enemigos
                        int j=0;
                        for(Observation enemigo : enemigosCerca){
                            //Observation enemigo=enemigosCerca.get(0);                        
                            enemigoPos =new Vector2d(enemigo.position.x/fescala.x,enemigo.position.y/fescala.y);
                            Vector2d pos=new Vector2d(avatar.pos.x+x_arrNeig[i],avatar.pos.y+y_arrNeig[i]);
                            distancia= Math.sqrt(Math.pow(enemigoPos.x - pos.x,2 ) + Math.pow(enemigoPos.y-pos.y ,2));
                            if( distancia<=distancias.get(j) ){
                                d=false;
                                break;
                            }
                            j++;
                        }

                        if(d && !muro){
                            casillasPosibles.add(new Vector2d((avatar.pos.x+x_arrNeig[i]),(avatar.pos.y+y_arrNeig[i]) ) );
                        }
                    }
                    if(enemigosCerca.size()==1){
                        break;
                    }
                    if(casillasPosibles.isEmpty()){
                        enemigosCerca.remove(enemigosCerca.size()-1);
                    }
                }
                
                if(casillasPosibles.isEmpty()){
                    pathFinding.path.clear();
                    pathFinding.path.add(ACTIONS.ACTION_NIL);
                }else{
                    //hay que encontrar la mejor (la pue este a su orientacion)
                    Vector2d opt = new Vector2d(avatar.pos.x+avatar.ori.x,avatar.pos.y+avatar.ori.y);
                    boolean encontrada=false;
                    for(Vector2d posicion : casillasPosibles){
                        if(posicion.x==opt.x && posicion.x==opt.y){
                            encontrada=true;
                            break;
                        }
                    }
                        //si la mejor es posible la cogemos
                    if(encontrada){
                        pathFinding.Aestrela(avatar, opt);
                    }else{
                        //si no, cogemos la primera
                        pathFinding.Aestrela(avatar, casillasPosibles.get(0));
                    }

                }                          
            
                
            }else{
                isEscapando=false;
                int[] x_arrNeig = new int[] {0, -1, 1, 0};
                int[] y_arrNeig = new int[] {1, 0, 0, -1};
                // deambulamos aleatoriamente;
                Types.ACTIONS[] mov = new Types.ACTIONS[] {ACTIONS.ACTION_DOWN,ACTIONS.ACTION_LEFT,ACTIONS.ACTION_RIGHT,ACTIONS.ACTION_UP};
                Random r=new Random();
                int a = r.nextInt(x_arrNeig.length);
                //Vector2d pos=new Vector2d(avatar.pos.x+x_arrNeig[a],avatar.pos.y+y_arrNeig[a]);
                
                //miro si esta al lado de un muro si esta voy en direccion contraria              
                boolean muro=false;
                for(int i=0; i<x_arrNeig.length;i++){
                    for(Observation obs : mundo[(int)avatar.pos.x+x_arrNeig[i]][(int)avatar.pos.y+y_arrNeig[i]]){
                        if(obs.itype==0){//muro
                            muro=true;
                            break;
                        }
                    }
                    if(muro){
                        if(i%2==0){
                            //pos=new Vector2d(avatar.pos.x+x_arrNeig[(i+1)%x_arrNeig.length],avatar.pos.y+y_arrNeig[(i+1)%y_arrNeig.length]);
                            a=(i-1+mov.length)%mov.length;
                            break;
                        }else{
                            a=(i+1+mov.length)%mov.length;
                            //pos=new Vector2d(avatar.pos.x+x_arrNeig[(i-1)%x_arrNeig.length],avatar.pos.y+y_arrNeig[(i-1)%y_arrNeig.length]);
                            break;
                        }
                        
                    }
                }
                pathFinding.path.clear();
                pathFinding.path.add(mov[a]);
                //pathFinding.Aestrela(avatar,pos );
            }
            
        }
        
              
        
}

class OrdenGemas{
    ArrayList<Integer> solucion;
    ArrayList<Integer> gemas_descartadas;
    ArrayList<Integer> gemas_descartadas_prev;
    ArrayList<Integer> solucionPrev;
    ArrayList<Integer> distancias; //idea gemas que no valen
    HashMap<Double,ArrayList<Integer>> cache;//en la .x la distancia, en la .y la orientacion
    Avatar avatar;
    Vector2d portal;
    int distancia;
    int distanciaPrev;
    int indiceSol;    
    ArrayList<Observation>[] gemas;
    Vector2d fescala;
    PathFinding pathFinding;
    
    OrdenGemas(StateObservation stateObs, Avatar avatar, Vector2d portal){
        
        cache = new HashMap<>();
        this.avatar=avatar;
        this.portal=portal;
        //distancias=new ArrayList<ArrayList<Integer>>();
        distancias=new ArrayList<Integer>();
        solucion= new ArrayList<Integer>();
        solucionPrev= new ArrayList<Integer>();
        gemas_descartadas= new ArrayList<Integer>();
        gemas_descartadas_prev= new ArrayList<Integer>();
        pathFinding= new PathFinding(stateObs);
        fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length , 
                            stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);
        gemas = stateObs.getResourcesPositions(stateObs.getAvatarPosition());
        
        
        //creacion de distancias pos 0 va a ser avatar y 1 el portal
        //distancia de ir desde i a j
        int d;
        for(int j=0;j<gemas[0].size()+2;j++){
            if(0==j){
                distancias.add(0);   
            }else{
                d=this.getDistance(0, j);
                distancias.add(d);
            }
        }
            
        
        
        //gemas inaccesibles (desde avatar)
        ArrayList<Integer> gemasInaccesibles = new ArrayList<Integer>();
        for(int i=2;i<distancias.size();i++){
            if(distancias.get(i)==-1){
                gemasInaccesibles.add(i-2);
            }
        }       
        
        //creacion de solucion
       
        ArrayList<Integer> solucion_aux= new ArrayList<Integer>();
        for(int i=0;i<gemas[0].size();i++){
            solucion_aux.add(i);
        }
        //quitamos gemas inaccesibles
        for(int i=0;i<gemasInaccesibles.size();i++){
            solucion_aux.set(gemasInaccesibles.get(i), -1);
        }
        for(int i=0;i<solucion_aux.size();i++){
            if(solucion_aux.get(i)==-1){
                solucion_aux.remove(i);
                i--;
            }
        } 
        //quitamos gemas sobrantes
        Collections.shuffle(solucion_aux);
        for(int i=10;i<solucion_aux.size();i++){
            gemas_descartadas.add(solucion_aux.get(i));
            solucion_aux.set(i, -1);            
        }
        for(int i=0;i<solucion_aux.size();i++){
            if(solucion_aux.get(i)==-1){
                solucion_aux.remove(i);
                i--;
            }
        } 
        
        solucion.add(-1);//avatar
        solucion.addAll(solucion_aux);
        solucion.add(-2);//portal
        
        
        
        distancia=getDistance();
        indiceSol=1;
    }
    
    public int getIndexSol(){
        return solucion.get(indiceSol);
    }
    
    public void simulateAnnealing(ElapsedCpuTimer elapsedTimer){
            
        while(elapsedTimer.remainingTimeMillis()>20){
            swapGemas();
            if(distancia<=distanciaPrev){
                continue;
            }else{
                this.revertSwap();
                shuffle();
                if(distancia<=distanciaPrev){
                    continue;
                }else{
                    this.revertSwap();
                }
            }
        }
        
    }
    
    public void shuffle(){
        solucionPrev=(ArrayList<Integer>) solucion.clone();
        gemas_descartadas_prev = (ArrayList<Integer>) gemas_descartadas.clone();

        solucion.remove(0);
        solucion.remove(solucion.size()-1);

        ArrayList<Integer> solucion_aux= new ArrayList<Integer>();
        solucion_aux=(ArrayList<Integer>) solucion.clone();
        Collections.shuffle(solucion_aux);
        solucion.clear();
        solucion.add(-1);//avatar
        solucion.addAll(solucion_aux);
        solucion.add(-2);//portal
        distanciaPrev=distancia;
        distancia=this.getDistance();
    }
    
    public void swapGemas(){
        solucionPrev=(ArrayList<Integer>) solucion.clone();
        gemas_descartadas_prev = (ArrayList<Integer>) gemas_descartadas.clone();
        Random r=new Random();
        int a = r.nextInt(solucion.size()-2)+1;//cambiamos entre las gemas
        int b = r.nextInt(solucion.size()-2)+1;
        if(a!=b){
            int g1=solucion.get(a); //metemos en el saco de gemas descartadas
            int g2=solucion.get(b);
            
            gemas_descartadas.add(g1);
            int c =r.nextInt(gemas_descartadas.size());
            g1=gemas_descartadas.get(c);//cogemos dos gemas
            gemas_descartadas.remove(c);//eliminamos de gemas desc las gemas ya cogidas
            solucion.set(b, g1);
            
                        
            gemas_descartadas.add(g2);        
            int d =r.nextInt(gemas_descartadas.size());
            g2=gemas_descartadas.get(d);        
            gemas_descartadas.remove(d);
            solucion.set(a, g2);
        }else{
            int g1=solucion.get(a);
            gemas_descartadas.add(g1);
            int c =r.nextInt(gemas_descartadas.size());
            g1=gemas_descartadas.get(c);//cogemos dos gemas
            gemas_descartadas.remove(c);//eliminamos de gemas desc las gemas ya cogidas
            solucion.set(a, g1);
        }
        
        distanciaPrev=distancia;
        distancia=this.getDistance();
    }
    
    public void revertSwap(){
        solucion=(ArrayList<Integer>) solucionPrev.clone();
        gemas_descartadas=(ArrayList<Integer>) gemas_descartadas_prev.clone();
        distancia=distanciaPrev;
    }
    
    public int getDistance(int i, int j){
        //supongo i!=j
        //distancia desde i a j (igual que de ir de j a i)
        //distancias pos 0 va a ser avatar y 1 el portal
        int distance=0;
        boolean ispath=true;
        Avatar posAct=new Avatar();
        Vector2d posGemaAct;
        
        if(j==0){
            posGemaAct=new Vector2d(avatar.pos.x,avatar.pos.y);
        }else if(j==1){
            posGemaAct=new Vector2d(portal.x,portal.y);
        }else{
            posGemaAct=new Vector2d(gemas[0].get(j-2).position.x / fescala.x, gemas[0].get(j-2).position.y / fescala.y);
        }
        
        
        if(i==0){            
            posAct.pos=new Vector2d(avatar.pos.x,avatar.pos.y);
            posAct.ori=new Vector2d(avatar.ori.x,avatar.ori.y);
        }else if(i==1){
            posAct.pos=new Vector2d(portal.x,portal.y);
            posAct.ori=new Vector2d(1.0,0.0);
        }else{
            posAct.pos=new Vector2d(gemas[0].get(i-2).position.x / fescala.x, gemas[0].get(i-2).position.y / fescala.y);
            posAct.ori=new Vector2d(1.0,0.0);
        }
        
        ispath=pathFinding.Aestrela(posAct,posGemaAct );
        distance=pathFinding.path.size();
        double id=posAct.pos.x*100000000+posAct.pos.y*1000000+((posAct.ori.x+3)%3)*100000+((posAct.ori.y+3)%3)*10000+posGemaAct.x*100+posGemaAct.y;
        ArrayList<Integer> contenido= new ArrayList<>();
        contenido.add(distance);
        contenido.add((int)pathFinding.Current.estado.orientacion.x);
        contenido.add((int)pathFinding.Current.estado.orientacion.y);
        cache.put(id,contenido );
        
        if(ispath){
            return distance;
        }else{
            return -1;
        }
        
    }
    
    public int getDistance(){
        int distance=0;
        int d;
        boolean ispath=true;
        Avatar posAct=new Avatar();
        Vector2d posGemaAct;
        double id;
        
        posAct.pos=new Vector2d(avatar.pos.x,avatar.pos.y);
        posAct.ori=new Vector2d(avatar.ori.x,avatar.ori.y);
        posGemaAct=new Vector2d(gemas[0].get(solucion.get(1)).position.x / fescala.x, gemas[0].get(solucion.get(1)).position.y / fescala.y);
        
        id=posAct.pos.x*100000000+posAct.pos.y*1000000+((posAct.ori.x+3)%3)*100000+((posAct.ori.y+3)%3)*10000+posGemaAct.x*100+posGemaAct.y;
        if(cache.containsKey(id)){
            distance+=cache.get(id).get(0);
        }else{
            ispath=pathFinding.Aestrela(posAct,posGemaAct );
            d=pathFinding.path.size();
            distance+=d;
            ArrayList<Integer> contenido= new ArrayList<>();
            contenido.add(d);
            contenido.add((int)pathFinding.Current.estado.orientacion.x);
            contenido.add((int)pathFinding.Current.estado.orientacion.y);
            cache.put(id,contenido );
        }
        
        
        
        for(int i=2; i<solucion.size()-1;i++){
            if(cache.containsKey(id)){
                posAct.ori=new Vector2d(cache.get(id).get(1),cache.get(id).get(2));
            }else{
                posAct.ori=new Vector2d(pathFinding.Current.estado.orientacion.x,pathFinding.Current.estado.orientacion.y);
            }
            posAct.pos=new Vector2d(posGemaAct.x,posGemaAct.y);            
            posGemaAct=new Vector2d(gemas[0].get(solucion.get(i)).position.x / fescala.x, gemas[0].get(solucion.get(i)).position.y / fescala.y);
            id=posAct.pos.x*100000000+posAct.pos.y*1000000+((posAct.ori.x+3)%3)*100000+((posAct.ori.y+3)%3)*10000+posGemaAct.x*100+posGemaAct.y;
            if(cache.containsKey(id)){
                distance+=cache.get(id).get(0);
            }else{
                ispath=pathFinding.Aestrela(posAct,posGemaAct );
                d=pathFinding.path.size();
                distance+=d;
                ArrayList<Integer> contenido= new ArrayList<>();
                contenido.add(d);
                contenido.add((int)pathFinding.Current.estado.orientacion.x);
                contenido.add((int)pathFinding.Current.estado.orientacion.y);
                cache.put(id,contenido );
            }
        }
        
        posAct.pos=new Vector2d(posGemaAct.x,posGemaAct.y);
        if(cache.containsKey(id)){
                posAct.ori=new Vector2d(cache.get(id).get(1),cache.get(id).get(2));
        }else{
                posAct.ori=new Vector2d(pathFinding.Current.estado.orientacion.x,pathFinding.Current.estado.orientacion.y);
        }
        posGemaAct=new Vector2d(portal.x,portal.y);
        id=posAct.pos.x*100000000+posAct.pos.y*1000000+((posAct.ori.x+3)%3)*100000+((posAct.ori.y+3)%3)*10000+posGemaAct.x*100+posGemaAct.y;
        if(cache.containsKey(id)){
                distance+=cache.get(id).get(0);
        }else{
            ispath=pathFinding.Aestrela(posAct,posGemaAct );
            d=pathFinding.path.size();
            distance+=d;
            ArrayList<Integer> contenido= new ArrayList<>();
            contenido.add(d);
            contenido.add((int)pathFinding.Current.estado.orientacion.x);
            contenido.add((int)pathFinding.Current.estado.orientacion.y);
            cache.put(id,contenido );
        }
        
        return distance;
        
        
    }
    
    
}