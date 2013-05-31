package parseTables;

import parseTables.*;

public class opSavecs extends opCode
{
    private String name;
    public opSavecs(String name)
    {
	this.name = name;
    }
    public String getName()
    {
	return name;
    }
    public String toString()
    {
	return "SaveCS: "+name;
    }
}
