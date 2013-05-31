package parseTables;

import parseTables.*;

public class opSet extends opCode
{
    private String name;
    private double value;

    public opSet(String name, double value)
    {
	this.name = name;
	this.value = value;
    }
    public String getKnob()
    {
	return name;
    }
    public String getName()
    {
	return name;
    }
    public double getValue()
    {
	return value;
    }
    public String toString()
    {
	return "Set: "+name+" - "+value;
    }
}
