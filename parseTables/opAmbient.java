package parseTables;

import parseTables.*;

public class opAmbient extends opCode
{

    private double[] rgb;

    public opAmbient(double[] rgb)
    {
	this.rgb = rgb;
    }
    public double[] getRgb()
    {
	return rgb;
    }
    public String toString()
    {
	return "Ambient: RGB - "+rgb[0]+" "+rgb[1]+" "+rgb[2];
    }
}
