package parseTables;

import parseTables.*;

public class opSetknobs extends opCode
{
    private double value;

    public opSetknobs(double value)
    {
	this.value = value;
    }
    public double getValue()
    {
	return value;
    }
    public String toString()
    {
	return "Setknobs: "+value;
    }
}
