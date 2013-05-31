package parseTables;

import parseTables.*;

public class opSave extends opCode
{
    private String name;
    public opSave(String name)
    {
	this.name = name;
    }
    public String getName()
    {
	return name;
    }
    public String toString()
    {
	return "Save: "+name;
    }
}
