/*========== MDLReader.java ==========
  MDLReader objects minimally contain an ArrayList<opCode> containing
  the opCodes generated when an mdl file is run through the java created
  lexer/parser, as well as the associated SymTab (Symbol Table).

  The provided methods are a constructor, and methods to print out the
  entries in the symbol table and command ArrayList.

  Your job is to go through each entry in opCodes and perform
  the required action from the list below:

  frames: set numFrames for animation

  basename: set baseName for animation

  vary: manipluate knob values between two given frames
        over a specified interval

  push: push a new origin matrix onto the origin stack

  pop: remove the top matrix on the origin stack

  move/scale/rotate: create a transformation matrix 
                     based on the provided values, then 
		     multiply the current top of the
		     origins stack by it.

  box/sphere/torus: create a solid object based on the
                    provided values. Store that in a 
		    temporary matrix, multiply it by the
		    current top of the origins stack, then
		    call draw_polygons.

  line: create a line based on the provided values. Store 
        that in a temporary matrix, multiply it by the
	current top of the origins stack, then call draw_lines.

  save: save the current screen with the provided filename

  =========================*/

import java.util.*;
import java.io.*;
import java.awt.Color;

import parser.*;
import parseTables.*;

public class  MDLReader {

    ArrayList<opCode> opcodes;
    SymTab symbols;
    Set<String> symKeys;
    Stack<Matrix> origins;
    EdgeMatrix tmp;
    Frame f;
    int numFrames;
    String baseName;

    public MDLReader(ArrayList<opCode> o, SymTab s) {

	opcodes = o;
	symbols = s;
	symKeys = s.keySet();
	numFrames = 1;
	baseName = "frame";

	tmp = new EdgeMatrix();
	f = new Frame();
	Matrix m = new Matrix(4);
	m.ident();
	origins = new Stack<Matrix>();
	origins.push(m);
    }

    public void printCommands() {
	
	Iterator i = opcodes.iterator();

	while (i.hasNext()) {
	    System.out.println(i.next());
	}
    }

    public void printSymbols() {

	Iterator i;

	i = symKeys.iterator();
	System.out.println("Symbol Table:");

	while (i.hasNext()) {
	    String key = (String)i.next();
	    Object value=symbols.get(key);
	    System.out.println(""+key+"="+value);
	}
    }


    /*======== public void firstPass()) ==========
      Inputs:   
      Returns: 

      Checks the op ArrayList for any animation commands
      (frames, basename, vary)
      
      Should set num_frames and basename if the frames 
      or basename commands are present
      
      If vary is found, but frames is not, the entire
      program should exit.
      
      If frames is found, but basename is not, set name
      to some default value, and print out a message
      with the name being used.

      05/17/12 09:54:22
      jdyrlandweaver
      ====================*/
    public void firstPass() {
	Iterator i = opcodes.iterator();
	opCode oc;
	boolean vary = false;

	//Iterate through the op ArrayList
	while(i.hasNext()){
	    oc = (opCode)i.next();
   
	    if(oc instanceof opFrames)
		numFrames = ((opFrames)oc).getNum();
	
	    else if(oc instanceof opBasename)
		baseName = ((opBasename)oc).getName();

	    else if(vary != true && oc instanceof opVary)
		vary = true;
	}

	if(numFrames > 1 && baseName.length() == 0){
		baseName = "new_anim";
		System.out.println("Basename not found. Using 'new_anim'.");
	}
	
	if(numFrames == 1 && vary == true)
	    throw new RuntimeException("Vary command found with no frames.");
	

    }


    /*======== public LinkedList<VaryNode>[] secondPass()) ==========
      Inputs:   
      Returns: An array of Linked Lists of VaryNodes

      In order to set the knobs for animation, we need to keep
      a seaprate value for each knob for each frame. We can do
      this by using an array of linked lists. Each array index
      will correspond to a frame (eg. knobs[0] would be the first
      frame, knobs[2] would be the 3rd frame and so on).
      
      Each index should contain a linked list of VaryNodes, each
      node contains a knob name and a value (see VaryNode.java)

      Go through the opcode ArrayList, and when you find vary, go 
      from knobs[0] to knobs[frames-1] and add (or modify) the
      vary_node corresponding to the given knob with the
      appropirate value. 

      05/17/12 09:55:29
      jdyrlandweaver
      ====================*/
    public LinkedList<VaryNode>[] secondPass() {

	LinkedList<VaryNode>[] knobs;
	VaryNode vnode;
	opCode oc;
	int index, ans, sframe, eframe, dframe;
	double step, sval, eval, dval;
	String name;
	Iterator i;
	
	knobs = (LinkedList<VaryNode>[]) new LinkedList[numFrames];
	i = opcodes.iterator();

	// Instantiate each LinkedList in knobs
	for(int j=0; j<numFrames; j++)
	    knobs[j] = new LinkedList<VaryNode>();

	// Iterator through the op ArrayList

	while(i.hasNext()){
	    oc = (opCode)i.next();
	    
	    // Enter next pass if next op is not VARY
	    if (!(oc instanceof opVary))
		continue;

	    // Get VARY values
	    name = ((opVary)oc).getKnob();
	    sval = ((opVary)oc).getStartval();
	    eval = ((opVary)oc).getEndval();
	    sframe = ((opVary)oc).getStartframe();
	    eframe = ((opVary)oc).getEndframe();
	    
	    // Calculate the step
	    dframe = eframe - sframe;
	    dval = eval - sval;
	    step = dval / dframe;

	    // Modify the knob's existing vary node if it exists.
	    index = -1;
	    ans = 0;
	    for(VaryNode n: knobs[0]){
		if(n.getName().equals(name)){
		    index = ans;
		    break;
		}
		ans++;
	    }
	    //knobs[0].indexOf((opVary)oc);

	    if(index >= 0){		
		for(int j=sframe; j <= eframe; j++){
		    vnode = knobs[j].get(index);
		    vnode.setValue(sval + ((j - sframe) * step));
		}
		
		continue;
	    }

	    /* Add varyNodes to each linkedlist if the knob has not yet been varied.
	       All frames outside of the frame interval will receive sval or eval. */
	    for(int j=0; j<numFrames; j++){
		
		if(j < sframe)
		    vnode = new VaryNode(sval, name);
		else if (j > eframe)
		    vnode = new VaryNode(eval, name);
		else
		    vnode = new VaryNode(sval + ((j - sframe) * step), name);
		
		knobs[j].add(vnode);
	    }
	}
	
	
	return knobs;
    }

    public void printKnobs() {

	Iterator i;
	int c = 0;

	i = symKeys.iterator();
	System.out.println("Knob List:");
	System.out.println( "ID\tNAME\tVALUE\n" );

	while (i.hasNext()) {
	    String key = (String)i.next();
	    Object value=symbols.get(key);
	    System.out.printf( "%d\t%s\t%6.2f\n", c++, key, value );
	}
    }

    public void processKnobs() {
	
	String s, name;
	double val;
	BufferedReader in;

	try {
	    in = new BufferedReader( new InputStreamReader( System.in ));
	    do {
		
		printKnobs();
		System.out.print("Enter knob name to modify (or exit): ");
		name = in.readLine();
		
		if ( !symKeys.contains( name ) && 
		     !name.equalsIgnoreCase( "exit" ) )

		    System.out.println( "Invalid name" );

		else if ( !name.equalsIgnoreCase( "exit") ) {
		    System.out.print("Enter new value: ");

		    s = in.readLine();
		    val = Double.parseDouble( s );
		    
		    symbols.setValue( name, val );		    
		}

	    } while ( !name.equalsIgnoreCase( "exit" )) ;
	} //end try
	catch ( IOException e ) {}
    }	
	   
	

    /*======== public void process()) ==========
      Inputs:   
      Returns: 

      Insert your interpreting code here

      you can use instanceof to check waht kind of op
      you are looking at:
      if ( oc instanceof opPush ) ...
	  
      you will need to typecast in order to get the
      operation specific data values

      If frames is not present in the source (and therefore 
      num_frames is 1, then process_knobs should be called.
      
      If frames is present, the enitre op array must be
      applied frames time. At the end of each frame iteration
      save the current screen to a file named the
      provided basename plus a numeric string such that the
      files will be listed in order, then clear the screen and
      reset any other data structures that need it.
      
      Important note: you cannot just name your files in 
      regular sequence, like pic0, pic1, pic2, pic3... if that
      is done, then pic1, pic10, pic11... will come before pic2
      and so on. In order to keep things clear, add leading 0s
      to the numeric portion of the name. If you use String.format
      (look it up online), you can use "%0xd" for this purpose. 
      It will add at most x 0s in front of a number, if needed, 
      so if used correctly, and x = 4, you would get numbers 
      like 0001, 0002, 0011, 0487

      04/23/12 09:52:32
      jdyrlandweaver
      ====================*/
    public void process() {
	
	int currFrame;
	double knobVal, xval, yval, zval;
	String resume = "no";
	LinkedList<VaryNode>[] knobs = null;

	firstPass();

	if ( numFrames == 1 ) 
	    processKnobs();
	else
	    knobs = secondPass();

	Iterator i;
	opCode oc;
	Matrix m = new Matrix();
	currFrame = 0;
	
	while(currFrame < numFrames){
	    i = opcodes.iterator(); 
	    origins = new Stack<Matrix>();	    
	    m.ident();
	    origins.push(m);
	    
	    if(knobs != null)
		for (VaryNode vnode: knobs[currFrame])
		    symbols.setValue( vnode.getName(), vnode.getValue() );
	    
	    while (i.hasNext()) {
		
		oc = (opCode)i.next();
		String command = oc.getClass().getName();
		
		if ( oc instanceof opAmbient ){
		    double[] rgb = ((opAmbient)oc).getRgb();
		    f.addAmbient(rgb[0], rgb[1], rgb[2]);
		}

		else if ( oc instanceof opLight ){
		    double[] loc = ((opLight)oc).getLocation();
		    double[] rgb = ((opLight)oc).getRgb();

		    f.addLight( ((opLight)oc).getName(),
				loc[0], loc[1], loc[2],
			       rgb[0], rgb[1], rgb[2] );
		}

		else if ( oc instanceof opShading ){
		    f.setInterp( ((opShading)oc).getType() );
		}

		else if ( oc instanceof opSet ){
		    symbols.setValue( ((opSet)oc).getName(),
				      ((opSet)oc).getValue() );
		}

		else if ( oc instanceof opSetknobs ){
		    for ( String s : symKeys )
			symbols.setValue( s, ((opSetknobs)oc).getValue());
		}

		else if ( oc instanceof opPush ) {
		
		    m = origins.peek().copy();
		    origins.push( m );
		}
	    
		else if ( oc instanceof opPop ) {
		    origins.pop();
		}
	    
		else if ( oc instanceof opSphere ) {
		
		    tmp.addSphere( ((opSphere)oc).getCenter()[0],
				   ((opSphere)oc).getCenter()[1],
				   ((opSphere)oc).getCenter()[2],
				   ((opSphere)oc).getR());

		    tmp.matrixMult( origins.peek() );
		    f.drawPolygons( tmp, new Color( 255, 255, 255 ) );
		    //f.drawPolygons( tmp, new Color( 100, 149, 237 ) );
		    tmp.clear();
		}

		else if ( oc instanceof opTorus ) {
		
		    tmp.addTorus( ((opTorus)oc).getCenter()[0],
				  ((opTorus)oc).getCenter()[1],
				  ((opTorus)oc).getCenter()[2],
				  ((opTorus)oc).getr(), 
				  ((opTorus)oc).getR());
		    tmp.matrixMult( origins.peek() );
		    f.drawPolygons( tmp, new Color( 255, 255, 255 ) );
//f.drawPolygons( tmp, new Color( 100, 149, 237 ) );
		    tmp.clear();
		}

		else if ( oc instanceof opBox ) {
		
		    tmp.addBox( ((opBox)oc).getP1()[0],
				((opBox)oc).getP1()[1],
				((opBox)oc).getP1()[2],
				((opBox)oc).getP2()[0],
				((opBox)oc).getP2()[1],
				((opBox)oc).getP2()[2] );

		    tmp.matrixMult( origins.peek() );
		    f.drawPolygons( tmp, new Color( 255, 255, 255 ) );
		    //		    f.drawPolygons( tmp, new Color( 100, 149, 237 ) );
		    tmp.clear();
		}

		else if ( oc instanceof opMove ) {

		    Matrix t = new Matrix(4);

		    xval = ((opMove)oc).getValues()[0];
		    yval = ((opMove)oc).getValues()[1];
		    zval = ((opMove)oc).getValues()[2];

		    if ( ((opTrans)oc).getKnob() != null ) {
			knobVal = symbols.getValue(((opTrans)oc).getKnob());
			xval = xval * knobVal;
			yval = yval * knobVal;
			zval = zval * knobVal;
		    }

		    t.makeTranslate( xval, yval, zval );
		
		    t.matrixMult( origins.peek() );
		    origins.pop();
		    origins.push( t );
		}	
		else if ( oc instanceof opScale ) {

		    Matrix t = new Matrix(4);

		    xval = ((opScale)oc).getValues()[0];
		    yval = ((opScale)oc).getValues()[1];
		    zval = ((opScale)oc).getValues()[2];

		    if ( ((opTrans)oc).getKnob() != null ) {
			knobVal = symbols.getValue(((opTrans)oc).getKnob());
			xval = xval * knobVal;
			yval = yval * knobVal;
			zval = zval * knobVal;
		    }

		    t.makeScale( xval, yval, zval );

		    t.matrixMult( origins.peek() );
		    origins.pop();
		    origins.push( t );
		}	

		else if ( oc instanceof opRotate ) {
		
		    double angle = ((opRotate)oc).getDeg() * (Math.PI / 180);
		    char axis = ((opRotate)oc).getAxis();
		    Matrix t = new Matrix(4);
		
		    if ( ((opTrans)oc).getKnob() != null ) {
	
			knobVal = symbols.getValue(((opTrans)oc).getKnob());
			angle = angle * knobVal;
		    }

		    if ( axis == 'x' )
			t.makeRotX( angle );
		    else if ( axis == 'y' )
			t.makeRotY( angle );
		    else
			t.makeRotZ( angle );
				
		
		    t.matrixMult( origins.peek() );
		    origins.pop();
		    origins.push( t );
		}

		else if ( oc instanceof opSave ) {
		    f.save( ((opSave)oc).getName() );
		}
		
	    }

	    if ( numFrames > 1 ){
		new File(System.getProperty("user.dir") + "/animations/" + baseName).mkdir();
		f.save( "animations/" + baseName + "/" + baseName + String.format("%04d",currFrame) );
		f.clearScreen();
		System.out.println("Drew Frame " + (currFrame + 1) + "/" + numFrames);
	    }
	
	    currFrame++;
	    
	}//end loop

    }
}
