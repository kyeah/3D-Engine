package parseTables;

import parseTables.*;

public class opFocal extends opCode
{
    private double value;
    public opFocal(double value)
    {
	this.value = value;
    }
    public double getValue()
    {
	return value;
    }
    public String toString()
    {
	return "Focal: "+value;
    }
}
