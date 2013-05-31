package parseTables;

public class opScale extends opTrans
{
    
    private double[] t;

    public opScale(double[] t, String knob)
    {
	this.t = t;
	this.knob = knob;
    }

    public String toString()
    {
	return "Scale: "+"t - "+triple(t)+
	    " Knob - "+knob;
    }
    public double[] getValues(){return t;}
    public String getKnob(){return knob;}

}


