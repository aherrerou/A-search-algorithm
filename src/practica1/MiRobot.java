/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package practica1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;
import simbad.sim.*;
import javax.vecmath.Vector3d;
import javax.vecmath.Matrix3d;

/**
 *
 * @author mireia
 */
public class MiRobot extends Agent{

        //Variables utilizadas para el controlador difuso
        RangeSensorBelt sonars;
        FuzzyController controller;
        
        //Variables generales
        int mundo[][]; //Datos del entorno
        int origen; //Punto de partida del robot. Será la columna 1 y esta fila
        int destino; //Punto de destino del robot. Será la columna tamaño-1 y esta fila
        char camino[][]; //Camino que debe seguir el robot. Será el resultado del A*
        int expandidos[][]; //Orden de los nodos expandidos. Será el resultado del A*
        int tamaño; //Tamaño del mundo
        int ta=20;
        

        public MiRobot(Vector3d position, String name, Practica1 practica1) {
            super(position, name);

            //Prepara las variables
            tamaño = practica1.tamaño_mundo;            
            mundo = new int[tamaño][tamaño];
            camino = new char[tamaño][tamaño];
            expandidos = new int[tamaño][tamaño];
            origen = practica1.origen;
            destino = practica1.destino;
            mundo = practica1.mundo;
            
            //Inicializa las variables camino y expandidos donde el A* debe incluir el resultado
            for(int i=0;i<tamaño;i++)
                for(int j=0;j<tamaño;j++){
                    camino[i][j] = '.';
                    expandidos[i][j] = -1;
                }

            // Añade sonars
            sonars = RobotFactory.addSonarBeltSensor(this); // de 0 a 1.5m
        }


        public List<Estado> ordenar(List<Estado> l){
            for(int i=0;i<(l.size()-1);i++){
                for(int j=i+1;j<l.size();j++){
                    if(l.get(i).getFuncion()>l.get(j).getFuncion()){
                        //Intercambiamos valores
                        Estado variableauxiliar=l.get(i);
                        l.set(i,l.get(j));
                        
                        l.set(j,variableauxiliar);
                    }
                }
            }
            
            return l;
        }
        
        boolean encontrado=false;
        //Calcula el A*
        public int AEstrella(){
            System.out.println("Solución del A*");            
            System.out.println("Camino");
            
            Estado hijo;
            Estado ori = new Estado(origen, 1, destino,ta);
            
            List<Estado> listaInterior = new ArrayList<Estado>();
            List<Estado> listaFrontera = new ArrayList<Estado>();//visitado

            int cont=0;
            //listaFrontera.add(ori.proxima(m,s))
            ori.setPosibles(ori, mundo,listaInterior, ta);
            
            listaFrontera.add(ori); 
            //listaFrontera=ori.getPosibles();
            while (!listaFrontera.isEmpty())
            {
                listaFrontera=ordenar(listaFrontera);
                Estado actual=listaFrontera.get(0);


                //for (int i=0; i<listaFrontera.size(); i++){ 
                    if ((actual.getX()==(destino))&&(actual.getY()==(ta-2)))
                    {
                        Estado aux2 = actual;
                        while (aux2.getPadre()!=null) {
                            camino[aux2.getX()][aux2.getY()]='X';
                            aux2=aux2.getPadre();
                        }
                        encontrado=true;
                        break;
                    }
                //}
                
                if (encontrado)
                {
                    break;
                }
            

                //nodoactual

                listaFrontera.remove(actual);
                listaInterior.add(actual);
                
                
                //expandidos[actual.getX()][actual.getY()]=(int)actual.distManhattan(actual.getX(), actual.getY());
                //expandidos[actual.getX()][actual.getY()]=(int)actual.getFuncion();
                expandidos[actual.getX()][actual.getY()]=cont;
                cont++;

                //desde aqui no se si esta bien
                actual.setPosibles(actual, mundo,listaInterior, ta);
                for (int i = 0; i < actual.getPosibles().size(); i++)
                {
                    hijo = actual.getPosibles().get(i); //para cada hijo de actual
                    
                    if (!hijo.contiene(listaFrontera)) {
                        hijo.setG(actual.getG());
                        hijo.setH(hijo.getX(),hijo.getY());
                        
                        hijo.setFuncion();//hijo.getG()+hijo.getH()
                        
                        hijo.setPadre(actual);
                        listaFrontera.add(hijo);                        
                    }
                    else if (hijo.getG() < actual.getG()) 
                    {
                        hijo.setPadre(actual);
                        hijo.setG(hijo.getPadre().getG()+1);
                        hijo.setFuncion();//hijo.getG()+hijo.getH()
                    }
                }
            }
            
            if (encontrado)
            {
                camino[origen][1]='X';
                camino[destino][tamaño-2]='X';
                for (int i=0; i<tamaño; i++){
                    for(int j=0;j<tamaño; j++)
                    {
                        System.out.print(" "+camino[i][j]);
                    }
                    System.out.println();
                }

                System.out.println("Nodos explorados");
                for (int i=0; i<tamaño; i++){
                    for(int j=0;j<tamaño; j++)
                    {
                        if ((expandidos[i][j]<10)&&(expandidos[i][j]>=0))
                            System.out.print(" ");
                        if (expandidos[i][j]<100)
                            System.out.print(" ");

                        System.out.print(" "+expandidos[i][j]);
                    }
                    System.out.println();
                }
            }
            else {
                System.err.println("No hay solucion");
            }
            return 0;
        }

        //Función utilizada para la parte de lógica difusa donde se le indica el siguiente punto al que debe ir el robot.
        //Busca cual es el punto más cercano.

        public Point3d puntoMasCercano(Point3d posicion){
            int inicio;
            Point3d punto = new Point3d(posicion);
            double distancia;
            double cerca = 100;

            inicio = (int) (tamaño-(posicion.z+(tamaño/2)));
            
            for(int i=0; i<tamaño; i++)
                for(int j=inicio+1; j<tamaño; j++){
                    if(camino[i][j]=='X'){
                        distancia = Math.abs(posicion.x+(tamaño/2)-i) + Math.abs(tamaño-(posicion.z+(tamaño/2))-j);
                        if(distancia < cerca){
                            punto.x=i;
                            punto.z=j;
                            cerca = distancia;
                        }
                    }
                }

            return punto;
        }

        /** This method is called by the simulator engine on reset. */
    @Override
        public void initBehavior() {

            System.out.println("Entra en initBehavior");
            //Calcula A*
            int a = AEstrella();

            if(a!=0){
                System.err.println("Error en el A*");
            }else{
                // init controller
                controller = new FuzzyController();
            }
        }

        /** This method is call cyclically (20 times per second)  by the simulator engine. */
    @Override
        public void performBehavior() {

            double angulo;
            int giro;

            //Ponemos las lecturas de los sonares al controlador difuso
            //System.out.println("Fuzzy Controller Input:");
            float[] sonar = new float[9];
            for(int i=0; i<9; i++){
                if(sonars.getMeasurement(i)==Float.POSITIVE_INFINITY){
                    sonar[i] = sonars.getMaxRange();
                } else {
                    sonar[i] = (float) sonars.getMeasurement(i);
                }
                //System.out.println("    > S"+ i +": " + sonar[i]);
            }

     
            //Calcula ángulo del robot
            Transform3D rotTrans = new Transform3D();
            this.rotationGroup.getTransform(rotTrans); //Obtiene la transformada de rotación

            //Debe calcular el ángulo a partir de la matriz de transformación
            //Nos quedamos con la matriz 3x3 superior
            Matrix3d m1 = new Matrix3d();
            rotTrans.get(m1);

            //Calcula el ángulo sobre el eje y
            angulo = -java.lang.Math.asin(m1.getElement(2,0));

            if(angulo<0.0)
                angulo += 2*Math.PI;
            assert(angulo>=0.0 && angulo<=2*Math.PI);

            //Calcula la dirección
            if(m1.getElement(0, 0)<0)
                angulo = -angulo;
            angulo = angulo*180/Math.PI;            
            if(angulo<0 && angulo>-90)
                angulo += 180;
            if(angulo<-270 && angulo>-360)
                angulo += 180+360;


            //Calcula el siguiente punto al que debe ir del A*
            Point3d coord = new Point3d();
            this.getCoords(coord);

            Point3d punto = puntoMasCercano(coord);
            coord.x = coord.x+(tamaño/2);
            coord.z = tamaño-(coord.z+tamaño/2);
            coord.x = (int)coord.x;
            coord.z = (int)coord.z;
            
            
            //Calcula distancia y ángulo del vector, creado desde el punto que se encuentra el robot,
            //hasta el punt que se desea ir
            double distan = Math.sqrt(Math.pow(coord.z-punto.z, 2)+Math.pow(coord.x-punto.x, 2));    
            double phi= Math.atan2((punto.z-coord.z),(punto.x - coord.x));
            phi = phi*180/Math.PI;
            
            //Calcula el giro que debe realizar el robot. Este valor es el que se le pasa al controlador difuso.
            double rot = phi-angulo;
            if(rot<-180)
                rot += 360;
            if(rot>180)
                rot -=360;
            
            //System.out.println("Angulo de giro: "+rot);
            
            //Ejecuto el controlador
            controller.step(sonar, rot);

            //Obtengo las velocidades calculadas y las aplico al robot
            setTranslationalVelocity(controller.getVel());
            setRotationalVelocity(controller.getRot());

            //Para mostrar los valores del controlador
            //System.out.println("Fuzzy Controller Output:");
            //System.out.println("    >vel: "+ controller.getVel());
            //System.out.println("    >rot: "+ controller.getRot());
        }
}