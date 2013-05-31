package parseTables;

import parseTables.*;

public class opSaveknobs extends opCode
{
    private String name;
    public opSaveknobs(String name)
    {
	this.name = name;
    }
    public String getName()
    {
	return name;
    }
    public String toString()
    {
	return "Saveknobs: "+name;
    }
}
