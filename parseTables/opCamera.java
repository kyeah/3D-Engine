package parseTables;

import parseTables.*;

public class opCamera extends opCode
{

    private double[] eye;
    private double[] aim;

    public opCamera(double[] eye, double[] aim)
    {
	this.eye = eye; this.aim = aim;
    }
    public double[] getEye()
    {
	return eye;
    }
    public double[] getAim()
    {
	return aim;
    }
    public String toString()
    {
	return "Camera: Eye - "+triple(eye)+
	    " Aim - "+triple(aim);
    }
}
