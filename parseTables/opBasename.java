package parseTables;

import parseTables.*;

public class opBasename extends opCode
{
    private String name;
    public opBasename(String name)
    {
	this.name = name;
    }
    public String getName()
    {
	return name;
    }
    public String toString()
    {
	return "Basename: "+name;
    }
}
