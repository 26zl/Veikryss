package com.g15.veikryss;

import javafx.scene.Group;
import javafx.scene.shape.Shape;

public abstract class Figur extends Shape {
    //instansvariabler
    protected double xPos;
    protected double yPos;

    //konstruktør
    public Figur(double xPos, double yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    /**
     *
     * @return gruppe som representerer figur
     */
    public abstract Group getFigur();


    /**
     *
     * @param xPos
     */
    public void setXPos(double xPos){
        this.xPos = xPos;
    }

    /**
     *
     * @param yPos
     */
    public void setYPos(double yPos){
        this.yPos = yPos;
    }

    /**
     *
     * @return
     */
    public double getXPos() {
        return xPos;
    }

    /**
     *
     * @return
     */
    public double getRadius() {
        return 0.0;
    }

    /**
     *
     * @return
     */
    public double getYPos() {
        return yPos;
    }
}