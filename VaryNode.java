public class VaryNode {
    
    private double value;
    private String name;

    public VaryNode(double v, String n) {
	
	value = v;
	name = n;
    }

    public double getValue() {
	return value;
    }
    
    public String getName() {
	return name;
    }

    public void setValue( double v ) {
	value = v;
    }

}