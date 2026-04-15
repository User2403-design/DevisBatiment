/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;

/**
 *
 * @author chloe
 */
public class Point {
 private float x;
 private float y ;
 private float xmax;
 private float ymax ;
 private float xmin;
 private float ymin ;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getXmax() {
        return xmax;
    }

    public void setXmax(float xmax) {
        this.xmax = xmax;
    }

    public float getYmax() {
        return ymax;
    }

    public void setYmax(float ymax) {
        this.ymax = ymax;
    }

    public float getXmin() {
        return xmin;
    }

    public void setXmin(float xmin) {
        this.xmin = xmin;
    }

    public float getYmin() {
        return ymin;
    }

    public void setYmin(float ymin) {
        this.ymin = ymin;
    }
 
 public Point ( float x, float y, float xmin, float xmax, float ymin, float ymax){
 this.x = x;
 this.y= y;
 this.ymax = ymax;
 this.ymin = ymin;
 this.xmax = xmax;
 this.xmin = xmin;

 }
 
 
}
