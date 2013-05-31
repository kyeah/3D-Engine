package parseTables;

public class opCode
{
    protected String triple(double[] s)
    {
	if (s==null)
	    return "";
	else
	    return ""+s[0]+","+s[1]+","+s[2];
    }

    public String toString()
    {
	return "GENERIC OPCODE";
    }

}

