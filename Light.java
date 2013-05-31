import java.io.*;
import java.util.*;
import java.awt.*;

public class Light {

    String name;
    private double x, y, z, r, g, b;

    public Light(String name, double x, double y, double z, double r, double g, double b){
	this.name = name;
	this.x = x;
	this.y = y;
	this.z = z;
	this.r = r;
	this.g = g;
	this.b = b;
	
    }

    public String getName(){
	return name;
    }

    public double getX(){
	return x;
    }

    public double getY(){
	return y;
    }

    public double getZ(){
	return z;
    }
    
    public double getRed(){
	return r;
    }

    public double getGreen(){
	return g;
    }
    
    public double getBlue(){
	return b;
    }

}