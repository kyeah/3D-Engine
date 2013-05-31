package parseTables;

import parseTables.*;

public class opConstants extends opCode
{

    private String name;
    private double[] ambient;
    private double[] diffuse;
    private double[] specular;
    private double[] intensities;

    public opConstants(String name, double[] ambient, double[] diffuse,
		       double[] specular, double[] intensities)
    {
	this.name = name;
	this.ambient=ambient;
	this.diffuse=diffuse;
	this.specular=specular;
	this.intensities = intensities;
    }
    public String getName() {return name;}
    public double[] getAmbient() {return ambient;}
    public double[] getDiffuse() {return diffuse;}
    public double[] getSpecular() {return specular;}
    public double[] getIntensities() {return intensities;}

    public String toString()
    {
	return "Constants: Name - "+name+" Ambient - "+triple(ambient)+
	    " diffuse - "+triple(diffuse)+" specular - "+triple(specular)+
	    " intensities - "+triple(intensities);
    }
}
