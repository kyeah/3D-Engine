package parseTables;

public class opRotate extends opTrans
{
    
    double deg;
    char axis;

    public opRotate(char axis, double deg, String knob)
    {
	this.axis = axis;
	this.deg = deg;
	this.knob = knob;
    }

    public String toString()
    {
	return "Rotate: Axis - "+axis+" Deg - "+deg;
    }
    public char getAxis(){return axis;}
    public double getDeg(){return deg;}
    public String getKnob(){return knob;}

}


