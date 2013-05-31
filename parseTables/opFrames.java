package parseTables;

import parseTables.*;

public class opFrames extends opCode
{
    private int num;
    public opFrames(int num)
    {
	this.num = num;
    }
    public int getNum()
    {
	return num;
    }
    public String toString()
    {
	return "Frames: "+num;
    }
}
