package parseTables;

public class opTorus extends opShape
{
    
    private double[] center;
    private double R,r;

    public opTorus(double[] center, double R, double r, String cs, String constants)
    {
	this.center = center;
	this.r=r;
	this.R=R;
	this.cs=cs;
	this.constants=constants;
    }

    public String toString()
    {
	return "Torus: "+"Center - "+triple(center)+
	    " R - "+R+" r - "+r+" cs - "+cs+" Contsants - "+constants;
    }
    public double[] getCenter(){return center;}
    public double getR(){return R;}
    public double getr(){return r;}
    
}


