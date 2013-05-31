package parseTables;

import parseTables.*;

public class opTween extends opCode
{
    private int start,stop;
    private String k1,k2;

    public opTween(int start, int stop, String k1, String k2)
    {
	this.start=start; this.stop=stop;
	this.k1=k1; this.k2=k2;
    }
    public int getStart(){ return start;}
    public int getStop(){ return stop;}
    public String getK1(){return k1;}
    public String getK2(){return k2;}
    public String toString()
    {
	return "Tween: "+start+" to "+stop+
	    " k1 - "+k1+" k2 - "+k2;
    }
}
