package parseTables;

import parseTables.*;

public class opShading extends opCode
{
    private String name;
    public opShading(String name)
    {
	this.name = name;
    }
    public String getType()
    {
	return name;
    }
    public String toString()
    {
	return "Shading: "+name;
    }
}
