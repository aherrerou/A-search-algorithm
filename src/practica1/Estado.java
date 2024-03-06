/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica1;
import practica1.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Luis
 */
public class Estado {
    private List<Estado> listaFronteraVecino;
    //coste actual
    int g=0;
    int x;
    int y;
    float funcion;
    float h;
    int fin;
    int ta;
    Estado padre=null;
    Estado (int x, int y, int fin, int tamaño, Estado padre) {  
        this.x=x;
        this.fin=fin;
        this.y=y;
        this.padre=padre;
        this.ta=tamaño-2;

        setG(padre.getG()+1);
        this.setFuncion();
    }
    Estado (int x, int y, int fin, int tamaño) {  
        this.fin=fin;
        this.ta=tamaño-2;
        this.x=x;
        this.y=y;
        setG(0);
        this.setFuncion();
    }
    public int getG() {
        return g;
    }

    public Estado getPadre() {
        return padre;
    }
    
    public void setPadre(Estado e) {
        this.padre=e;
    }
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

   
    public void setG(int g) {
        this.g = 1+g;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }


    public void setFuncion() {
        setH(getX(),getY());
//        System.out.print("*****"+getG()+"+"+getH()+"=FNC:");
        this.funcion = this.getG()+this.getH();
//        System.out.println(funcion);
    }

    public float getFuncion() {
        return funcion;
    }

    public void setH(int x, int y) { 
        ta=20;
        this.h= (float) distManhattan(x,y);
    }
    
    public float getH() {
        return this.h;
    }
    
    public float distManhattan(int x, int y) {
        float aux1=ta-y;
        float aux2=fin-x;
        //System.out.println(x+","+y+" "+ta+","+fin);
        
        return Math.abs(aux1)+Math.abs(aux2);
        //EUCLIDEAN return (float) ((Math.sqrt((Math.abs(aux1)*Math.abs(aux1))+(Math.abs(aux2)*Math.abs(aux2)))));
        //return ha;
    }
    public List<Estado> getPosibles(){
        return listaFronteraVecino;
    }
    
    public boolean contiene(List<Estado> lista) {
        for (Estado item : lista) {   
            if (item.getX()==getX()&&item.getY()==getY())
                return true;
        }        
        return false;
    }
    
    public void setPosibles(Estado e, int[][] m, List li, int t){
        listaFronteraVecino = new ArrayList<Estado>();
        Estado hijo;

        if ((m[e.x+1][e.y]==0)&&(!(new Estado(x+1, y,fin,ta, e).contiene(li)))) {
            Estado aux=new Estado(x+1, y,fin,ta, e);
            listaFronteraVecino.add(aux);
        }
        if ((m[e.x-1][e.y]==0)&&(!(new Estado(x-1, y,fin,ta, e).contiene(li)))) {
            Estado aux=new Estado(x-1, y,fin,ta, e);
            listaFronteraVecino.add(aux);
        }
        if ((m[e.x][e.y-1]==0)&&(!(new Estado(x, y-1,fin,ta, e).contiene(li)))) {
            e.setFuncion();
            listaFronteraVecino.add(new Estado(x, y-1,fin,ta, e));
        }
        if ((m[e.x][e.y+1]==0)&&(!(new Estado(x, y+1,fin,ta, e).contiene(li)))) {
            Estado aux=new Estado(x, y+1,fin,ta, e);
            listaFronteraVecino.add(aux);
        }
    }
    void setPadre(float n) {
    }  
}
