package src_manzanares_lemus_alejandro;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Pair;
import tools.Vector2d;

import java.util.*;

import static java.util.Collections.*;
import static ontology.Types.*;

enum Nivel{
  DS, DC, RS, RC, RD
}

public class Agent extends AbstractPlayer {
  private Nivel estado = Nivel.RD;

  private Vector2d fescala;
  private Vector2d portal;

  private ArrayList<ACTIONS> ruta;
  private boolean ruta_completa = false;
  private ArrayList<Node> cerrados = new ArrayList<Node>();
  private ArrayList<Node> abiertos = new ArrayList<Node>();
  private ArrayList<Observation>[][] obv;
  private ArrayList<ACTIONS> acciones;
  private long tiempo = 0;

  private ArrayList<Vector2d> gemas;
  private int gemas_recogidas = 0;
  private boolean gema_encontrada = false;
  private ArrayList<ArrayList<Double>> matriz_distancias = new ArrayList<>();

  private ArrayList<ArrayList<Integer>> mapa_de_calor = new ArrayList<>();
  private Vector2d avatar;
  private int peligro_actual;
  private int mejor_peligro;

  private boolean interrupcion;

  public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
    fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length ,
            stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);

    if(estado == Nivel.DS){
      ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());

      portal = posiciones[0].get(0).position;
      portal.x = Math.floor(portal.x / fescala.x);
      portal.y = Math.floor(portal.y / fescala.y);

      System.out.print("Mundo: ");
      System.out.print(stateObs.getObservationGrid().length);
      System.out.print(" x ");
      System.out.println(stateObs.getObservationGrid()[0].length);

      Vector2d pos = new Vector2d(stateObs.getAvatarPosition().x / fescala.x, stateObs.getAvatarPosition().y / fescala.y);
      Vector2d ori = new Vector2d(stateObs.getAvatarOrientation());
      Node padre = new Node(1, ori, pos, portal, new ArrayList<ACTIONS>());
      abiertos.add(padre);

      //ruta = new ArrayList<ACTIONS>();
      obv = stateObs.getObservationGrid();
      acciones = stateObs.getAvailableActions();
      ruta = A_estrella(portal,stateObs,elapsedTimer);

    } else if(estado == Nivel.DC){
      ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());

      portal = posiciones[0].get(0).position;
      portal.x = Math.floor(portal.x / fescala.x);
      portal.y = Math.floor(portal.y / fescala.y);

      System.out.print("Mundo: ");
      System.out.print(stateObs.getObservationGrid().length);
      System.out.print(" x ");
      System.out.println(stateObs.getObservationGrid()[0].length);

      Vector2d pos = new Vector2d(stateObs.getAvatarPosition().x / fescala.x, stateObs.getAvatarPosition().y / fescala.y);
      Vector2d ori = new Vector2d(stateObs.getAvatarOrientation());
      Node padre = new Node(1, ori, pos, portal, new ArrayList<ACTIONS>());
      abiertos.add(padre);

      ruta = new ArrayList<ACTIONS>();
      obv = stateObs.getObservationGrid();
      acciones = stateObs.getAvailableActions();
      gemas = new ArrayList<Vector2d>();
      matriz_distancias = new ArrayList<>();

      calcular_distancias_gemas(stateObs,elapsedTimer);

    } else if(estado == Nivel.RS || estado == Nivel.RC){

      crear_mapa_calor(stateObs,elapsedTimer);

    } else if(estado == Nivel.RD){
      ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());

      portal = posiciones[0].get(0).position;
      portal.x = Math.floor(portal.x / fescala.x);
      portal.y = Math.floor(portal.y / fescala.y);

      System.out.print("Mundo: ");
      System.out.print(stateObs.getObservationGrid().length);
      System.out.print(" x ");
      System.out.println(stateObs.getObservationGrid()[0].length);

      Vector2d pos = new Vector2d(stateObs.getAvatarPosition().x / fescala.x, stateObs.getAvatarPosition().y / fescala.y);
      Vector2d ori = new Vector2d(stateObs.getAvatarOrientation());
      Node padre = new Node(1, ori, pos, portal, new ArrayList<ACTIONS>());
      abiertos.add(padre);

      ruta = new ArrayList<ACTIONS>();
      obv = stateObs.getObservationGrid();
      acciones = stateObs.getAvailableActions();
      gemas = new ArrayList<Vector2d>();
      matriz_distancias = new ArrayList<>();

      calcular_distancias_gemas(stateObs,elapsedTimer);

      interrupcion = false;
    }

    System.out.println("Tiempo del constructor: " + elapsedTimer.elapsedMillis());

  }

  public void init(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){

  }

  public boolean misma_orientacion_accion(Vector2d orientacion, ACTIONS accion){
    if(accion == ACTIONS.ACTION_RIGHT && orientacion.x == 1.0){
      return true;
    } else if (accion == ACTIONS.ACTION_LEFT && orientacion.x == -1.0){
      return true;
    } else if (accion == ACTIONS.ACTION_UP && orientacion.y == -1.0){
      return true;
    } else if (accion == ACTIONS.ACTION_DOWN && orientacion.y == 1.0){
      return true;
    } else {
      return false;
    }
  }

  public ArrayList<ACTIONS> A_estrella (Vector2d destino, StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
    ArrayList<ACTIONS> path = new ArrayList<ACTIONS>();
    Node actual;
    boolean encontrado = false;
    boolean salir = false;
    boolean objetivo_alcanzado = false;
    int tipo_dest = (obv[(int) destino.x][(int) destino.y]).get(0).itype;

    System.out.println("(" + stateObs.getAvatarPosition().x/fescala.x + ","
            + stateObs.getAvatarPosition().y/fescala.y + ")"
            + " -> " + "(" + destino.x + "," + destino.y + ")");

    do{
      actual = abiertos.get(0);
      cerrados.add(actual);
      abiertos.remove(0);

      Iterator<ACTIONS> it_acc = acciones.iterator();
      while(it_acc.hasNext()){
        ACTIONS accion = it_acc.next();
        Vector2d new_pos = new Vector2d(actual.getPosicion());
        Vector2d new_ori = new Vector2d(actual.getOrientacion());

        if (accion != ACTIONS.ACTION_USE){

          if (accion == ACTIONS.ACTION_RIGHT) {
            new_pos.x = new_pos.x + 1;
          } else if (accion == ACTIONS.ACTION_LEFT) {
            new_pos.x = new_pos.x - 1;
          } else if (accion == ACTIONS.ACTION_UP) {
            new_pos.y = new_pos.y - 1;
          } else if (accion == ACTIONS.ACTION_DOWN) {
            new_pos.y = new_pos.y + 1;
          }

          int tipo;
          if((obv[(int) new_pos.x][(int) new_pos.y]).size() > 0){
            tipo = (obv[(int) new_pos.x][(int) new_pos.y]).get(0).itype;
          } else {
            tipo = 2;
          }

          if(tipo != 0) {
            ArrayList<ACTIONS> camino_actual = new ArrayList<ACTIONS>(actual.getAccion());
            camino_actual.add(accion);

            if(!misma_orientacion_accion(new_ori,accion)){
              camino_actual.add(accion);
            }

            if (accion == ACTIONS.ACTION_RIGHT) {
              new_ori.x = 1;
              new_ori.y = 0;
            } else if (accion == ACTIONS.ACTION_LEFT) {
              new_ori.x = -1;
              new_ori.y = 0;
            } else if (accion == ACTIONS.ACTION_UP) {
              new_ori.x = 0;
              new_ori.y = -1;
            } else if (accion == ACTIONS.ACTION_DOWN) {
              new_ori.x = 0;
              new_ori.y = 1;
            }

            Node hijo = new Node(tipo, new_ori, new_pos, destino, camino_actual);
            encontrado = false;
            salir = false;

            Iterator<Node> it_cerrados = cerrados.iterator();
            while(it_cerrados.hasNext() && !encontrado){
              Node i = it_cerrados.next();
              if(hijo.equals(i))
                encontrado = true;
            }

            if(!encontrado){
              Iterator<Node> it_abiertos = abiertos.iterator();
              while(it_abiertos.hasNext() && !salir){
                Node j = it_abiertos.next();
                if(hijo.equals(j) && hijo.getCoste_camino() < j.getCoste_camino()){
                  j.update(hijo);
                  salir = true;
                }
              }

              if (!salir) {
                abiertos.add(hijo);
              }
            }
          }
        }
      }
      sort(abiertos);

    }while((actual.getPosicion().x != destino.x || actual.getPosicion().y != destino.y) && elapsedTimer.remainingTimeMillis() > 10);

    tiempo += elapsedTimer.elapsedMillis();

    if(actual.getPosicion().x == destino.x && actual.getPosicion().y == destino.y){
      ruta_completa = true;
    }

    if(ruta_completa){
      path = new ArrayList<ACTIONS>(actual.getAccion());
      ruta_completa = false;
    } else {
      path = new ArrayList<ACTIONS>();
      path.add(ACTIONS.ACTION_NIL);
    }

    return path;
  }

  public double A_estrella_pos (Node padre, Vector2d destino, StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
    abiertos.clear();
    cerrados.clear();
    abiertos.add(padre);
    ArrayList<ACTIONS> path = new ArrayList<ACTIONS>();
    Node actual;
    boolean encontrado = false;
    boolean salir = false;

    do{
      actual = abiertos.get(0);
      cerrados.add(actual);
      abiertos.remove(0);

      Iterator<ACTIONS> it_acc = acciones.iterator();
      while(it_acc.hasNext()){
        ACTIONS accion = it_acc.next();
        Vector2d new_pos = new Vector2d(actual.getPosicion());
        Vector2d new_ori = new Vector2d(actual.getOrientacion());

        if (accion != ACTIONS.ACTION_USE){

          if (accion == ACTIONS.ACTION_RIGHT) {
            new_pos.x = new_pos.x + 1;
          } else if (accion == ACTIONS.ACTION_LEFT) {
            new_pos.x = new_pos.x - 1;
          } else if (accion == ACTIONS.ACTION_UP) {
            new_pos.y = new_pos.y - 1;
          } else if (accion == ACTIONS.ACTION_DOWN) {
            new_pos.y = new_pos.y + 1;
          }

          int tipo;
          if((obv[(int) new_pos.x][(int) new_pos.y]).size() > 0){
            tipo = (obv[(int) new_pos.x][(int) new_pos.y]).get(0).itype;
          } else {
            tipo = 2;
          }

          if(tipo != 0) {
            ArrayList<ACTIONS> camino_actual = new ArrayList<ACTIONS>(actual.getAccion());
            camino_actual.add(accion);

            Node hijo = new Node(tipo, new_ori, new_pos, destino, camino_actual);
            encontrado = false;
            salir = false;

            Iterator<Node> it_cerrados = cerrados.iterator();
            while(it_cerrados.hasNext() && !encontrado){
              Node i = it_cerrados.next();
              if(hijo.equals(i))
                encontrado = true;
            }

            if(!encontrado){
              Iterator<Node> it_abiertos = abiertos.iterator();
              while(it_abiertos.hasNext() && !salir){
                Node j = it_abiertos.next();
                if(hijo.equals(j) && hijo.getCoste_camino() < j.getCoste_camino()){
                  j.update(hijo);
                  salir = true;
                }
              }

              if (!salir) {
                abiertos.add(hijo);
              }
            }
          }
        }
      }
      sort(abiertos);

    }while((actual.getPosicion().x != destino.x || actual.getPosicion().y != destino.y));

    return actual.getCoste_total();


  }

  public void calcular_posicion_gemas(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
    ArrayList<Observation>[] g = stateObs.getResourcesPositions(stateObs.getAvatarPosition());
    gemas.add(new Vector2d(stateObs.getAvatarPosition().x/fescala.x,stateObs.getAvatarPosition().y/fescala.y));
    for(int i = 0; i < g[0].size(); i++){
      gemas.add(new Vector2d(g[0].get(i).position.x / fescala.x ,g[0].get(i).position.y/fescala.x));
    }

  }

  public void calcular_distancias_gemas(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
    calcular_posicion_gemas(stateObs,elapsedTimer);

    for(int i = 0; i < gemas.size(); i++){
      matriz_distancias.add(new ArrayList<>());
    }

    for(int i = 0; i < gemas.size(); i++){
      for(int j = 0; j < gemas.size(); j++){
        if(i != j){
          Node gema = new Node(6, new Vector2d(1,0), gemas.get(i), gemas.get(j), new ArrayList<ACTIONS>());
          matriz_distancias.get(i).add(A_estrella_pos(gema, gemas.get(j), stateObs, elapsedTimer));
        } else {
          matriz_distancias.get(i).add(0.0);
        }
      }
    }

    ArrayList<Gem> posibles = new ArrayList<>();
    ArrayList<Gem> explorados = new ArrayList<>();

    for(int i = 1; i < matriz_distancias.get(0).size(); i++){
      ArrayList<Integer> index = new ArrayList<>();
      index.add(0);
      index.add(i);
      Gem gema_actual = new Gem(index,matriz_distancias.get(0).get(i));
      posibles.add(gema_actual);
    }
    sort(posibles);
    Gem actual = posibles.get(0);;
    posibles.remove(0);

    while(actual.getGem_size() != 11){
      explorados.add(actual);

      Gem hijo_mejor = new Gem(actual);
      boolean primero = true;

      for(int i = 1; i < gemas.size(); i++){

        if(!actual.getGems().contains(i)){
          ArrayList<Integer> indices_actual = new ArrayList<>(actual.getGems());
          indices_actual.add(i);
          Gem hijo = new Gem(indices_actual, actual.getPeso()+matriz_distancias.get(actual.getGems().get(actual.getGem_size()-1)).get(i));

          if(primero){
            hijo_mejor = new Gem(hijo);
            posibles.add(hijo);
            primero = false;
          }else {

            if(hijo.getPeso() < hijo_mejor.getPeso()){
              posibles.add(hijo);
              hijo_mejor =new Gem(hijo);
            }

          }
        }
      }

      sort(posibles);
      actual = posibles.get(0);
      posibles.remove(0);
    }


    ArrayList<Vector2d> gemas_def = new ArrayList<>();

    for(int i = 1; i < actual.getGem_size(); i++){
      gemas_def.add(gemas.get(actual.getGems().get(i)));
    }
    gemas_def.add(new Vector2d(portal.x,portal.y));

    gemas.clear();
    gemas = new ArrayList<>(gemas_def);
  }

  public void crear_mapa_calor(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
    ArrayList<Observation> [] enemigos = stateObs.getNPCPositions();
    ArrayList<Observation> [][] muros = stateObs.getObservationGrid();
    Vector2d limites = new Vector2d(stateObs.getObservationGrid().length,stateObs.getObservationGrid()[0].length);

    for(int i = 0; i < stateObs.getObservationGrid()[0].length; i++){
      mapa_de_calor.add(new ArrayList<>());
      for(int j = 0; j < stateObs.getObservationGrid().length; j++){
        mapa_de_calor.get(i).add(0);
      }
    }

    for(int i = 0; i < stateObs.getObservationGrid()[0].length; i++){
      for(int j = 0; j < stateObs.getObservationGrid().length; j++){
        if(stateObs.getObservationGrid()[j][i].size() > 0){
          if(stateObs.getObservationGrid()[j][i].get(0).itype == 0){
            mapa_de_calor.get(i).set(j,mapa_de_calor.get(i).get(j)+9);
            if(j - 1 > -1){
              mapa_de_calor.get(i).set(j-1,mapa_de_calor.get(i).get(j-1)+1);
            }
            if(j + 1 < limites.x){
              mapa_de_calor.get(i).set(j+1,mapa_de_calor.get(i).get(j+1)+1);
            }
            if(i -1 > -1){
              mapa_de_calor.get(i-1).set(j,mapa_de_calor.get(i-1).get(j)+1);
            }
            if(i + 1 < limites.y){
              mapa_de_calor.get(i+1).set(j,mapa_de_calor.get(i+1).get(j)+1);
            }
          }
        }
      }
    }

    for(int e = 0; e < enemigos[0].size(); e++){
      Vector2d pos_enemigo = enemigos[0].get(e).position;
      pos_enemigo.x = pos_enemigo.x / fescala.x;
      pos_enemigo.y = pos_enemigo.y / fescala.y;

      for(int i = (int)(pos_enemigo.x-3); i <= pos_enemigo.x+3; i++){
        for (int j = (int)(pos_enemigo.y-3); j <= pos_enemigo.y+3; j++){
          if(i > 0 && i < (int) limites.x && j > 0 && j < (int) limites.y) {
            if(i == (int)(pos_enemigo.x-3) || j == (int)(pos_enemigo.y-3) || i == (int)(pos_enemigo.x+3) || j == (int)(pos_enemigo.y+3)){
              mapa_de_calor.get(j).set(i,mapa_de_calor.get(j).get(i)+5);
            }else if(i == (int)(pos_enemigo.x-2) || j == (int)(pos_enemigo.y-2) || i == (int)(pos_enemigo.x+2) || j == (int)(pos_enemigo.y+2)){
              mapa_de_calor.get(j).set(i,mapa_de_calor.get(j).get(i)+6);
            } else if (i == (int)pos_enemigo.x && j == (int)(pos_enemigo.y)){
              mapa_de_calor.get(j).set(i,mapa_de_calor.get(j).get(i)+8);
            } else {
              mapa_de_calor.get(j).set(i,mapa_de_calor.get(j).get(i)+7);
            }
          }
        }
      }
    }
  }

  public boolean avatar_en_limite(ACTIONS accion,StateObservation stateObs){
    boolean en_limite = false;
    Vector2d avatar = stateObs.getAvatarPosition();
    avatar.x = avatar.x / fescala.x;
    avatar.y = avatar.y / fescala.y;
    Vector2d limites = new Vector2d(stateObs.getObservationGrid().length,stateObs.getObservationGrid()[0].length);

    if(accion == ACTIONS.ACTION_RIGHT){
      if(avatar.x+1 < (limites.x -1)){
        en_limite = true;
      }
    } else if(accion == ACTIONS.ACTION_LEFT){
      if(avatar.x-1 > 0){
        en_limite = true;
      }
    } else if(accion == ACTIONS.ACTION_UP){
      if(avatar.y-1 > 0){
        en_limite = true;
      }
    } else if(accion == ACTIONS.ACTION_DOWN){
      if(avatar.y+1 < (limites.y-1)){
        en_limite = true;
      }
    }
    return en_limite;
  }

  public ACTIONS act( StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
    if(estado == Nivel.DS){

      if(!ruta_completa && ruta.size() == 0){
        ruta = A_estrella(portal,stateObs,elapsedTimer);
        System.out.println("Tiempo de A*: " + tiempo + "ms.");
      }

      ACTIONS accion = ruta.get(0);
      System.out.println(ruta.get(0));
      ruta.remove(0);
      System.out.println("Tiempo act: " + elapsedTimer.elapsedMillis());
      return accion;

    } else if (estado == Nivel.DC){

      avatar = stateObs.getAvatarPosition();
      avatar.x = avatar.x / fescala.x;
      avatar.y = avatar.y / fescala.y;

      if(avatar.x == gemas.get(gemas_recogidas).x && avatar.y == gemas.get(gemas_recogidas).y){
          gemas_recogidas++;
      }

      if(!ruta_completa && ruta.size() == 0){
        abiertos.clear();
        cerrados.clear();
        Vector2d pos = new Vector2d(stateObs.getAvatarPosition().x / fescala.x, stateObs.getAvatarPosition().y / fescala.y);
        Vector2d ori = new Vector2d(stateObs.getAvatarOrientation());
        Node padre = new Node(1, ori, pos, portal, new ArrayList<ACTIONS>());
        abiertos.add(padre);
        if(gemas_recogidas < 10){
          ruta = A_estrella(gemas.get(gemas_recogidas),stateObs,elapsedTimer);
        } else {
          ruta = A_estrella(portal,stateObs,elapsedTimer);
        }

      }


      ACTIONS accion = ruta.get(0);
      System.out.println(accion);
      ruta.remove(0);
      return accion;

    } else if (estado == Nivel.RS || estado == Nivel.RC) {
      mapa_de_calor.clear();
      crear_mapa_calor(stateObs,elapsedTimer);

      ACTIONS accion = ACTIONS.ACTION_NIL;

      avatar = stateObs.getAvatarPosition();
      avatar.x = avatar.x / fescala.x;
      avatar.y = avatar.y / fescala.y;

      peligro_actual = mapa_de_calor.get((int) avatar.y).get((int) avatar.x);
      mejor_peligro = peligro_actual;

      if(peligro_actual > 0){

        if(misma_orientacion_accion(stateObs.getAvatarOrientation(),ACTIONS.ACTION_RIGHT)){
          if(peligro_actual > mapa_de_calor.get((int) avatar.y).get((int)(avatar.x+1)) && avatar_en_limite(ACTIONS.ACTION_RIGHT, stateObs)){
            mejor_peligro = mapa_de_calor.get((int) avatar.y).get((int)(avatar.x+1));
            accion = ACTIONS.ACTION_RIGHT;
          }
        } else if(misma_orientacion_accion(stateObs.getAvatarOrientation(),ACTIONS.ACTION_LEFT)){
          if(peligro_actual > mapa_de_calor.get((int) avatar.y).get((int)(avatar.x-1)) && avatar_en_limite(ACTIONS.ACTION_LEFT, stateObs)){
            mejor_peligro = mapa_de_calor.get((int) avatar.y).get((int)(avatar.x-1));
            accion = ACTIONS.ACTION_LEFT;
          }
        } else if(misma_orientacion_accion(stateObs.getAvatarOrientation(),ACTIONS.ACTION_UP)){
          if(peligro_actual > mapa_de_calor.get((int) avatar.y-1).get((int)(avatar.x)) && avatar_en_limite(ACTIONS.ACTION_UP, stateObs)){
            mejor_peligro = mapa_de_calor.get((int) avatar.y-1).get((int)(avatar.x));
            accion = ACTIONS.ACTION_UP;
          }
        } else if(misma_orientacion_accion(stateObs.getAvatarOrientation(),ACTIONS.ACTION_DOWN)){
          if(peligro_actual > mapa_de_calor.get((int) avatar.y+1).get((int)(avatar.x)) && avatar_en_limite(ACTIONS.ACTION_DOWN, stateObs)){
            mejor_peligro = mapa_de_calor.get((int) avatar.y+1).get((int)(avatar.x));
            accion = ACTIONS.ACTION_DOWN;
          }
        }

        if(mejor_peligro > mapa_de_calor.get((int) avatar.y).get((int)(avatar.x+1)) && avatar_en_limite(ACTIONS.ACTION_RIGHT, stateObs)){
          mejor_peligro = mapa_de_calor.get((int) avatar.y).get((int)(avatar.x+1));
          accion = ACTIONS.ACTION_RIGHT;

        }
        if(mejor_peligro > mapa_de_calor.get((int) avatar.y).get((int)(avatar.x-1)) && avatar_en_limite(ACTIONS.ACTION_LEFT, stateObs)){
          mejor_peligro = mapa_de_calor.get((int) avatar.y).get((int)(avatar.x-1));
          accion = ACTIONS.ACTION_LEFT;

        }
        if(mejor_peligro > mapa_de_calor.get((int) (avatar.y+1)).get((int)avatar.x) && avatar_en_limite(ACTIONS.ACTION_DOWN, stateObs)){
          mejor_peligro = mapa_de_calor.get((int) avatar.y+1).get((int)(avatar.x));
          accion = ACTIONS.ACTION_DOWN;

        }
        if(mejor_peligro > mapa_de_calor.get((int) (avatar.y-1)).get((int)avatar.x) && avatar_en_limite(ACTIONS.ACTION_UP, stateObs)){
          mejor_peligro = mapa_de_calor.get((int) avatar.y-1).get((int)(avatar.x));
          accion = ACTIONS.ACTION_UP;

        }
      } else {
        accion = ACTIONS.ACTION_NIL;
      }
      System.out.println(accion);
      return accion;

    } else if(estado == Nivel.RD){
      mapa_de_calor.clear();
      crear_mapa_calor(stateObs,elapsedTimer);

      ACTIONS accion = ACTIONS.ACTION_NIL;
      avatar = stateObs.getAvatarPosition();
      avatar.x = avatar.x / fescala.x;
      avatar.y = avatar.y / fescala.y;
      peligro_actual = mapa_de_calor.get((int) avatar.y).get((int) avatar.x);
      mejor_peligro = peligro_actual;

      if(peligro_actual > 2){
        interrupcion = true;
        if(misma_orientacion_accion(stateObs.getAvatarOrientation(),ACTIONS.ACTION_RIGHT)){
          if(peligro_actual > mapa_de_calor.get((int) avatar.y).get((int)(avatar.x+1)) && avatar_en_limite(ACTIONS.ACTION_RIGHT, stateObs)){
            mejor_peligro = mapa_de_calor.get((int) avatar.y).get((int)(avatar.x+1));
            accion = ACTIONS.ACTION_RIGHT;
          }
        } else if(misma_orientacion_accion(stateObs.getAvatarOrientation(),ACTIONS.ACTION_LEFT)){
          if(peligro_actual > mapa_de_calor.get((int) avatar.y).get((int)(avatar.x-1)) && avatar_en_limite(ACTIONS.ACTION_LEFT, stateObs)){
            mejor_peligro = mapa_de_calor.get((int) avatar.y).get((int)(avatar.x-1));
            accion = ACTIONS.ACTION_LEFT;
          }
        } else if(misma_orientacion_accion(stateObs.getAvatarOrientation(),ACTIONS.ACTION_UP)){
          if(peligro_actual > mapa_de_calor.get((int) avatar.y-1).get((int)(avatar.x)) && avatar_en_limite(ACTIONS.ACTION_UP, stateObs)){
            mejor_peligro = mapa_de_calor.get((int) avatar.y-1).get((int)(avatar.x));
            accion = ACTIONS.ACTION_UP;
          }
        } else if(misma_orientacion_accion(stateObs.getAvatarOrientation(),ACTIONS.ACTION_DOWN)){
          if(peligro_actual > mapa_de_calor.get((int) avatar.y+1).get((int)(avatar.x)) && avatar_en_limite(ACTIONS.ACTION_DOWN, stateObs)){
            mejor_peligro = mapa_de_calor.get((int) avatar.y+1).get((int)(avatar.x));
            accion = ACTIONS.ACTION_DOWN;
          }
        }

        if(mejor_peligro > mapa_de_calor.get((int) avatar.y).get((int)(avatar.x+1)) && avatar_en_limite(ACTIONS.ACTION_RIGHT, stateObs)){
          mejor_peligro = mapa_de_calor.get((int) avatar.y).get((int)(avatar.x+1));
          accion = ACTIONS.ACTION_RIGHT;
        }
        if(mejor_peligro > mapa_de_calor.get((int) avatar.y).get((int)(avatar.x-1)) && avatar_en_limite(ACTIONS.ACTION_LEFT, stateObs)){
          mejor_peligro = mapa_de_calor.get((int) avatar.y).get((int)(avatar.x-1));
          accion = ACTIONS.ACTION_LEFT;
        }
        if(mejor_peligro > mapa_de_calor.get((int) (avatar.y+1)).get((int)avatar.x) && avatar_en_limite(ACTIONS.ACTION_DOWN, stateObs)){
          mejor_peligro = mapa_de_calor.get((int) avatar.y+1).get((int)(avatar.x));
          accion = ACTIONS.ACTION_DOWN;
        }
        if(mejor_peligro > mapa_de_calor.get((int) (avatar.y-1)).get((int)avatar.x) && avatar_en_limite(ACTIONS.ACTION_UP, stateObs)){
          mejor_peligro = mapa_de_calor.get((int) avatar.y-1).get((int)(avatar.x));
          accion = ACTIONS.ACTION_UP;
        }
        System.out.println(accion);
        return accion;
      } else {

        if(interrupcion){
          interrupcion = false;
          ruta_completa = false;
          ruta.clear();
        }

        if(avatar.x == gemas.get(gemas_recogidas).x && avatar.y == gemas.get(gemas_recogidas).y){
          gemas_recogidas++;
        }

        if(!ruta_completa && ruta.size() == 0){
          abiertos.clear();
          cerrados.clear();
          Vector2d pos = new Vector2d(stateObs.getAvatarPosition().x / fescala.x, stateObs.getAvatarPosition().y / fescala.y);
          Vector2d ori = new Vector2d(stateObs.getAvatarOrientation());
          Node padre = new Node(1, ori, pos, portal, new ArrayList<ACTIONS>());
          abiertos.add(padre);
          if(gemas_recogidas < 10){
            ruta = A_estrella(gemas.get(gemas_recogidas),stateObs,elapsedTimer);
            if(gema_encontrada){
              gemas_recogidas++;
              gema_encontrada = false;
            }
          } else {
            ruta = A_estrella(portal,stateObs,elapsedTimer);
          }

        }

        accion = ruta.get(0);
        ruta.remove(0);
        System.out.println(accion);
        return accion;
      }
    }
    return ACTIONS.ACTION_NIL;
  }
}