package parseTables;

public class opMesh extends opShape
{
    
    private String filename;

    public opMesh(String filename,String cs, String constants)
    {
	this.filename=filename;
	this.cs = cs;
	this.constants=constants;
    }

    public String toString()
    {
	return "Mesh: "+filename+"  cs - "+cs+" Contsants - "+constants;
    }
    public String getFilename(){return filename;}

}


