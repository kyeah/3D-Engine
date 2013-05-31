import java.io.*;
import java.util.*;

public class EdgeMatrix extends Matrix {
    
    private int lastCol;

    public EdgeMatrix() {
	super();
	lastCol = 0;
    }

    public EdgeMatrix( int c ) {
	super( c );
	lastCol = 0;
    }

    /*======== public void addSphere() ==========
 
      Adds all the polygons required to make a 
      sphere with center (cx, cy, cz) and radius r.

      Returns:
      ====================*/
    public void addSphere( double cx, double cy, double cz, double r ) { 

	double step = 0.05;
	EdgeMatrix points = new EdgeMatrix();
	
	double x, y, z;
	int index;
	int numSteps = (int)(1 / step);

	int longStart, latStart, longtStop, latStop, lat, longt;
	longStart = 0;
	latStart = 0;
	longtStop = numSteps;
	latStop = numSteps;

	// Generate Points
	points.generateSphere( cx, cy, cz, r, step );

	// Draw Polygons
	for ( lat = latStart; lat < latStop; lat++ ) {
	    for ( longt = longStart; longt < longtStop; longt++ ) { 

		index = lat * numSteps + longt;  
		
		// Non-edge case
		if ( lat != numSteps - 1 && longt != numSteps - 1 ) {
	
		    addPolygon( points.getX( index ),
				points.getY( index ),
				points.getZ( index ),
				points.getX( index + 1 ),
				points.getY( index + 1 ),
				points.getZ( index + 1 ),
				points.getX( index + numSteps ),
				points.getY( index + numSteps ),
				points.getZ( index + numSteps ));
		    addPolygon( points.getX( index + 1 ),
				points.getY( index + 1 ),
				points.getZ( index + 1 ),
				points.getX( index + 1 + numSteps ),
				points.getY( index + 1 + numSteps ),
				points.getZ( index + 1 + numSteps ),
				points.getX( index + numSteps ),
				points.getY( index + numSteps ),
				points.getZ( index + numSteps ) );	
		} // End of non edge case
      
		// Latitudinal edge case
		else if ( lat == numSteps - 1 ) {
		    if  ( longt != numSteps -1 ) {
			addPolygon( points.getX( index ),
				     points.getY( index ),
				     points.getZ( index ),
				     points.getX( index + 1 ),
				     points.getY( index + 1 ),
				     points.getZ( index + 1 ),
				     points.getX( index % numSteps ),
				     points.getY( index % numSteps ),
				     points.getZ( index % numSteps ) );
			addPolygon( points.getX( index + 1 ),
				     points.getY( index + 1 ),
				     points.getZ( index + 1 ),
				     points.getX((index+1) % numSteps),
				     points.getY((index+1) % numSteps),
				     points.getZ((index+1) % numSteps),
				     points.getX( index % numSteps ),
				     points.getY( index % numSteps ),
				     points.getZ( index % numSteps ) );
		    }
		    else {
			addPolygon( points.getX( index ),
				     points.getY( index ),
				     points.getZ( index ),
				     (points.getX( 0 )) - ( 2 * r ),
				     points.getY( 0 ),
				     points.getZ( 0 ),
				     points.getX( index % numSteps ),
				     points.getY( index % numSteps ),
				     points.getZ( index % numSteps ));	
		    }
		} // End of latitudinal edge case
		
		// Longitudinal edge case
		else {
		    addPolygon( points.getX( index ),
				 points.getY( index ),
				 points.getZ( index ),
				 (points.getX(index + 1 )) - ( 2 * r ),
				 points.getY( index + 1 ),
				 points.getZ( index + 1 ),
				 points.getX( index + numSteps ),
				 points.getY( index + numSteps ),
				 points.getZ( index + numSteps ) );	
		} //end longitudinal edge case
		
	    } //end for long
	}// end for lat
    }

    /*======== public void addTorus() ==========

      Adds all the polygons required to make a 
      torus with center (cx, cy, cz) and radii r1 and r2.

      Returns:
      ====================*/
    public void addTorus( double cx, double cy, double cz, double r1, double r2 ) {

	double step = 0.025;
	EdgeMatrix points = new EdgeMatrix();
	
	int index, botR, botL, topR;
	int numSteps = (int)(1 / step);
	int longStart, latStart, longStop, latStop;
	longStart = 0;
	latStart = 0;
	longStop = numSteps;
	latStop = numSteps;

	// Generate points on the torus
	points.generateTorus( cx, cy, cz, r1, r2, step );

	// Draw polygons
	for ( int longt = longStart; longt < longStop; longt++ ) 
	    for ( int lat = latStart; lat < latStop; lat++ ) {
		
		index = lat * numSteps + longt;
		
		//------Fill in the Empty Strip------//
		if( longt == longStop - 1 ){
		    
		    if ( lat == numSteps - 1 ) {
		    topR = longt;
		    botL = index - longt;
		    botR = 0;
		    }
		    
		    else {
			topR = index + numSteps;
			botL = lat * numSteps;
			botR = topR - longt;
		    }
		}
		
		//------Fill in all other points------//
		else {
		    
		    if ( lat == numSteps - 1 ) {
			topR = longt;
			botL = index + 1;
			botR = topR + 1;
		    }
		    
		    else {
			topR = index + numSteps;
			botL = index + 1;
			botR = topR + 1;
		    }
		}
		    
		//------Add Polygons-----//

		addPolygon(points.getX(index), 
			   points.getY(index), 
			   points.getZ(index),
			   points.getX(botL), 
			   points.getY(botL), 
			   points.getZ(botL),
			   points.getX(botR), 
			   points.getY(botR), 
			   points.getZ(botR));
		
		addPolygon(points.getX(topR), 
			   points.getY(topR), 
			   points.getZ(topR),
			   points.getX(index), 
			   points.getY(index), 
			   points.getZ(index),
			   points.getX(botR), 
			   points.getY(botR), 
			   points.getZ(botR));

	    }

	/*
	double step = 0.025;
	EdgeMatrix points = new EdgeMatrix();
	
	double x, y, z;
	int index;
	int numSteps = (int)(1 / step);
	int longStart, latStart, longtStop, latStop, lat, longt;
	longStart = 0;
	latStart = 0;
	longtStop = numSteps;
	latStop = numSteps;

	// Generate torus points
	points.generateTorus( cx, cy, cz, r1, r2, step );

	// Draw polygons
	for ( lat = latStart; lat < latStop; lat++ ) {
	    for ( longt = longStart; longt < longtStop; longt++ ) {

		index = lat * numSteps + longt;

		if ( lat != numSteps - 1 && longt != numSteps - 1 ) {
	
		    addPolygon( points.getX( index ),
				points.getY( index ),
				points.getZ( index ),
				points.getX( index + 1 ),
				points.getY( index + 1 ),
				points.getZ( index + 1 ),
			        points.getX( index + numSteps ),
				points.getY( index + numSteps ),
				points.getZ( index + numSteps ) );
		    addPolygon( points.getX( index + 1 ),
				 points.getY( index + 1 ),
				 points.getZ( index + 1 ),
				 points.getX( index + 1 + numSteps ),
				 points.getY( index + 1 + numSteps ),
				 points.getZ( index + 1 + numSteps ),
				 points.getX( index + numSteps ), 
				 points.getY( index + numSteps ),
				 points.getZ( index + numSteps ) );	
		    
		} //end of non edge cases

		else if ( lat == numSteps - 1 ) {
		    if  ( longt != numSteps -1 ) {
			addPolygon( points.getX( index ),
				     points.getY( index ),
				     points.getZ( index ),
				     points.getX( index + 1 ),
				     points.getY( index + 1 ),
				     points.getZ( index + 1 ),
				     points.getX( index % numSteps ),
				     points.getY( index % numSteps ),
				     points.getZ( index % numSteps ) );
			addPolygon( points.getX( index + 1 ),
				     points.getY( index + 1 ),
				     points.getZ( index + 1 ),
				     points.getX((index+1) % numSteps),
				     points.getY((index+1) % numSteps),
				     points.getZ((index+1) % numSteps),
				     points.getX( index % numSteps ),
				     points.getY( index % numSteps ),
				     points.getZ( index % numSteps ) );
		    }
		    else { 
			addPolygon( points.getX( index ),
				     points.getY( index ),
				     points.getZ( index ),
				     points.getX(index + 1 - numSteps),
				     points.getY(index + 1 - numSteps),
				     points.getZ(index + 1 - numSteps),
				     points.getX( index % numSteps ),
				     points.getY( index % numSteps ),
				     points.getZ( index % numSteps ) );
			addPolygon( points.getX(index + 1 - numSteps),
				     points.getY(index + 1 - numSteps),
				     points.getZ(index + 1 - numSteps),
				     points.getX( 0 ),
				     points.getY( 0 ),
				     points.getZ( 0 ),
				     points.getX( index % numSteps ),
				     points.getY( index % numSteps ),
				     points.getZ( index % numSteps ) );
		    }
		} //end latitude edge
		else {
		    addPolygon( points.getX( index ),
				 points.getY( index ),
				 points.getZ( index ),
				 points.getX( index + 1 - numSteps ),
				 points.getY( index + 1 - numSteps ),
				 points.getZ( index + 1 - numSteps ),
				 points.getX( index + numSteps ),
				 points.getY( index + numSteps ),
				 points.getZ( index + numSteps ) );	
		    addPolygon( points.getX( index + 1 - numSteps ),
				 points.getY( index + 1 - numSteps ),
				 points.getZ( index + 1 - numSteps ),
				 points.getX( index + 1 ),
				 points.getY( index + 1 ),
				 points.getZ( index + 1 ),
				 points.getX( index ), 
				 points.getY( index ),
				 points.getZ( index ) );	
		} //end longitude edge (south pole)
	    } //end for longt
	} //end for lat

	*/
    }

    /*======== public void addBox() ==========

      Adds all the polygons necessary to make the
      rectangular prism with upper-left-front corner
      (x, y, z) and dimensions width, depth and height.

      Returns:
      ====================*/
    public void addBox( double x, double y, double z, 
			double width, double height, double depth ) {
	
	double x1, y1, z1;
	
	x1 = x + width;
	y1 = y + height;
	z1 = z - depth;

	// Front
	addPolygon( x, y1, z,
		    x, y, z,
		    x1, y1, z);
	addPolygon( x1, y1, z, 
		    x, y, z,
		    x1, y, z);
	// Back
	addPolygon( x1, y1, z1,
		    x1, y, z1,
		    x, y1, z1);
	addPolygon( x, y1, z1,
		    x1, y, z1,
		    x, y, z1);

	// Top
	addPolygon( x, y, z,
		    x, y, z1,
		    x1, y, z);
	addPolygon( x1, y, z, 
		    x, y, z1,
		    x1, y, z1);	

	// Bottom
	addPolygon( x, y1, z1,
		    x, y1, z,
		    x1, y1, z1);
	addPolygon( x1, y1, z1, 
		    x, y1, z,
		    x1, y1, z);
	
	
	// Left
	addPolygon( x, y1, z1,
		    x, y, z1,
		    x, y1, z);
	addPolygon( x, y1, z, 
		    x, y, z1,
		    x, y, z);
	// Right
	addPolygon( x1, y1, z,
		    x1, y, z,
		    x1, y1, z1);
	addPolygon( x1, y1, z1, 
		     x1, y, z,
		     x1, y, z1);

    }

    /*======== public void addPolygon() ==========

      Adds the points (x0, y0, z0), (x1, y1, z1)
      and (x2, y2, z2 ) to the EdgeMatrix.

      Returns:
      ====================*/
    public void addPolygon(double x0, double y0, double z0, 
			   double x1, double y1, double z1,
			   double x2, double y2, double z2) {

	addPoint(x0, y0, z0);
	addPoint(x1, y1, z1);
	addPoint(x2, y2, z2);
    }

    
    /*======== public void addSphereMesh() ==========

      Adds all the edges required to make a wire frame mesh
      for a sphere with center (cx, cy) and radius r.

      Returns:
      ====================*/
    public void addSphereMesh( double cx, double cy, double r ) {

	double step = 0.025;
	EdgeMatrix points = new EdgeMatrix();
	
	double x, y, z;
	int index;
	int numSteps = (int)(1 / step);
	int longStart, latStart, longStop, latStop;
	longStart = 0;
	latStart = 0;
	longStop = numSteps;
	latStop = numSteps;

	points.generateSphere( cx, cy, 0, r, step );

	// Add one longitudinal circle per inner loop
	for ( int lat = latStart; lat < latStop; lat++ ) 
	    for ( int longt = longStart; longt < longStop; longt++ ) {

		index = lat * numSteps + longt;
		
		if ( longt < numSteps - 1 ) {
		    addEdge( points.getX( index ),
			     points.getY( index ),
			     points.getZ( index ),
			     
			     points.getX( index + 1 ),
			     points.getY( index + 1 ),
			     points.getZ( index + 1 ) );
		}
		else {
		    if ( lat == numSteps - 1 ) {
			x = points.getX( 0 ) - ( 2 * r );
			y = points.getY( 0 );
			z = points.getZ( 0 );
		    }
		    else {
			x = points.getX( index + 1) - ( 2 * r );
			y = points.getY( index + 1 );
			z = points.getZ( index + 1 );
		    }
		    addEdge( points.getX( index ),
			     points.getY( index ),
			     points.getZ( index ),
			     x, y, z );
		}
	    } //end longitude

	// Add one latitudinal circle per inner loop
	for ( int longt = longStart; longt < longStop; longt++ )
	    for ( int lat = latStart; lat < latStop; lat++ )  {

		index = lat * numSteps + longt;

		if ( lat == numSteps - 1 ){
		    addEdge( points.getX( index ),
			     points.getY( index ),
			     points.getZ( index ),
			     points.getX( index % numSteps ),
			     points.getY( index % numSteps ),
			     points.getZ( index % numSteps ) ); 
		}
		else
		    addEdge( points.getX( index ),
			     points.getY( index ),
			     points.getZ( index ),
			     points.getX( index + numSteps ),
			     points.getY( index + numSteps ),
			     points.getZ( index + numSteps ) );
	    } //end latitude

    }
    
    /*======== public void generateSphere() ==========

      Generates all the points along the surface of a 
      sphere with center (cx, cy, cz) and radius r.

      Adds these points to the point matrix.

      Returns: 
      ====================*/
    public void generateSphere( double cx, double cy, double cz, double r, double step ) {
	
	double x, y, z;

	for ( double rotation = 0; rotation <= 1; rotation+= step )
	    for ( double circle = 0; circle <= 1; circle+= step ) {

		x = r * Math.cos( Math.PI * circle ) + cx;
		y = r * Math.sin( Math.PI * circle ) * 
		    Math.cos( 2 * Math.PI * rotation ) + cy;
		z = r * Math.sin( Math.PI * circle ) * 
		    Math.sin( 2 * Math.PI * rotation ) + cz;
		
		addPoint(x, y, z);
	    }
    }

    /*======== public void addTorusMesh() ==========

      Adds all the edges required to make a wireframe mesh
      for a torus with center (cx, cy) and radii r1 and r2.

      Returns:
      ====================*/
    public void addTorusMesh( double cx, double cy, double r1, double r2 ) {
	double step = 0.05;
	EdgeMatrix points = new EdgeMatrix();
	
	int index;
	int numSteps = (int)(1 / step);
	int longStart, latStart, longStop, latStop;
	longStart = 0;
	latStart = 0;
	longStop = numSteps;
	latStop = numSteps;

	points.generateTorus( cx, cy, 0, r1, r2, step );
	
	// Add one longitudinal circle per loop	
	for ( int lat = latStart; lat < latStop; lat++ ) 
	    for ( int longt = longStart; longt < longStop; longt++ ) {

		index = lat * numSteps + longt;
		
		if ( longt < numSteps - 1 ) {
		    addEdge( points.getX( index ),
			     points.getY( index ),
			     points.getZ( index ),
			     points.getX( index + 1 ),
			     points.getY( index + 1 ),
			     points.getZ( index + 1 ) );
		}
		
		else {
		    addEdge( points.getX( index ),
			     points.getY( index ),
			     points.getZ( index ),
			     points.getX( index + 1 - numSteps ),
			     points.getY( index + 1 - numSteps ),
			     points.getZ( index + 1 - numSteps ) );
		}
	    } //end longitude
	
	
	// Add one latitudinal circle per loop
	for ( int longt = longStart; longt < longStop; longt++ )
	    for ( int lat = latStart; lat < latStop; lat++ )  {

		index = lat * numSteps + longt;

		if ( lat == numSteps - 1 ){
		    addEdge( points.getX( index ),
			     points.getY( index ),
			     points.getZ( index ),
			     points.getX( index % numSteps ),
			     points.getY( index % numSteps ),
			     points.getZ( index % numSteps ) ); 
		}
		else
		    addEdge( points.getX( index ),
			     points.getY( index ),
			     points.getZ( index ),
			     points.getX( index + numSteps ),
			     points.getY( index + numSteps ),
			     points.getZ( index + numSteps ) );
	    } //end latitude
	
    }

    /*======== public void generateTorus() ==========

      Generates all the points along the surface of a 
      torus with center (cx, cy, cz) and radii r1 and r2.

      Adds these points to the point matrix.

      Returns:
      ====================*/
    public void generateTorus( double cx, double cy, double cz, double r1, double r2, double step ) {
	double x, y, z;

	// Draws circles rotated around the y-axis of the torus.
	for ( double rotation = 0; rotation <= 1; rotation+= step )
	    for ( double circle = 0; circle <= 1; circle+= step ) {

		x = Math.cos( 2 * Math.PI * rotation ) *
		    ( r1 * Math.cos( 2 * Math.PI * circle ) + r2 ) + cx;
		y = r1 * Math.sin( 2 * Math.PI * circle ) + cy;
		z = Math.sin( 2 * Math.PI * rotation ) *
		    ( r1 * Math.cos( 2 * Math.PI * circle ) + r2 ) + cz;

		addPoint(x, y, z);
	    }
	
    }

    /*======== public void addBoxMesh() ==========

      Adds all the edges required to make a wire frame mesh
      for a rectagular prism whose upper-left corner is
      (x, y, z) with width, height and depth dimensions.
      
      Returns:
      ====================*/
    public void addBoxMesh( double x, double y, double z, double width, double height, double depth ) {

	double x2, y2, z2;
		
	x2 = x + width;
	y2 = y + height;
	z2 = z - depth;
	
	// Front
	addEdge( x, y, z,
		 x2, y, z);
	addEdge( x2, y, z,
		 x2, y2, z);
	addEdge( x2, y2, z,
		 x, y2, z);
	addEdge( x, y2, z,
		 x, y, z);

	// Back
	addEdge( x, y, z2,
		 x2, y, z2);
	addEdge( x2, y, z2,
		 x2, y2, z2);
	addEdge( x2, y2, z2,
		 x, y2, z2);
	addEdge( x, y2, z2,
		 x, y, z2);
	
	// Top
	addEdge( x, y, z,
		 x, y, z2);
	addEdge( x2, y, z,
		 x2, y, z2);
	
	// Bottom
	addEdge( x2, y2, z,
		 x2, y2, z2);
	addEdge( x, y2, z,
		 x, y2, z2);
	    
    }

    /*======== public static double distance() ==========

      Calculates the distance between two points in double precision.
      
      Returns: The distance between (x0, y0) and (x1, y1)
      ====================*/
    public static double distance(double x0, double y0, 
				  double x1, double y1) {
	return Math.sqrt( (x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0) );
    }
	   

    /*======== public void addCircle() ==========

      Generates the edges required to make a circle 
      centered at (cx, cy) with radius r.

      Returns:
      ====================*/
    public void addCircle(double cx, double cy, double r) {
	
	double x0, y0, x, y, step;
	
	step = 0.01;
	x0 = r + cx;
	y0 = cy;

	for( double t=step; t <= 1; t+= step) {
	    
	    x = r * Math.cos( 2 * Math.PI * t) + cx;
	    y = r * Math.sin( 2 * Math.PI * t) + cy;

	    addEdge(x0, y0, 0, x, y, 0);
	    x0 = x;
	    y0 = y;
	}

	// Closes the circle
	addEdge(x0, y0, 0, r + cx, cy, 0);
    }


    /*======== public void addCurve() ==========
      
      Generates the edges required to create a curve
      and adds them to the edge matrix.

      Returns:
      ====================*/
    /*
    public void addCurve( double x0, double y0, 
			  double x1, double y1, 
			  double x2, double y2, 
			  double x3, double y3, int type ) {
	
	EdgeMatrix xcoefs = new EdgeMatrix(1);
	EdgeMatrix ycoefs = new EdgeMatrix(1);

	// Lower step values generate more precise curves.
	double step = 0.01;

	double x, y, z, ax, ay, bx, by, cx, cy, dx, dy;
	
	// Generates the coefficients.
	if ( type == Parser.HERMITE_MODE ) {
	    xcoefs.generateHermiteCoefs(x0, x1, x2, x3);
	    ycoefs.generateHermiteCoefs(y0, y1, y2, y3);
	}
	else {
	    xcoefs.generateBezierCoefs(x0, x1, x2, x3);
	    ycoefs.generateBezierCoefs(y0, y1, y2, y3);
	}

	// Fills the coefficient arrays.
	ax = xcoefs.getX(0);
	bx = xcoefs.getY(0);
	cx = xcoefs.getZ(0);
	dx = xcoefs.getD(0);

	ay = ycoefs.getX(0);
	by = ycoefs.getY(0);
	cy = ycoefs.getZ(0);
	dy = ycoefs.getD(0);

	double startx = x0;
	double starty = y0;

	/* Generates each point on the curve and adds it to the edge matrix.
	for (double t = step; t <= 1; t+= step ) {
	    
	    x = ax * t * t * t + bx * t * t + cx * t + dx;
	    y = ay * t * t * t + by * t * t + cy * t + dy;

	    addEdge( startx, starty, 0, x, y, 0 );
	    startx = x;
	    starty = y;
	}
    }
    */	    
    /*======== public void addPoint() ==========

      Adds (x, y, z) to the edge matrix.

      Returns:
      ====================*/
    public void addPoint(double x, double y, double z) {

	if ( lastCol == m[0].length ) 
	    grow();
	
	m[0][lastCol] = x;
	m[1][lastCol] = y;
	m[2][lastCol] = z;
	m[3][lastCol] = 1;
	lastCol++;
    }

    /*======== public void addEdge() ==========
      
      adds the line connecting (x0, y0, z0) and 
      (x1, y1, z1) to the edge matrix.
      
      Returns:
      ====================*/
    public void addEdge(double x0, double y0, double z0, 
			double x1, double y1, double z1) {

	addPoint(x0, y0, z0);
	addPoint(x1, y1, z1);
    }



    /*=================
         ACCESSORS
	 ==============*/

    public int getLastCol() {
	return lastCol;
    }
    public double getX(int c) {
	return m[0][c];
    }
    public double getY(int c) {
	return m[1][c];
    }
    public double getZ(int c) {
	return m[2][c];
    }
    public double getD(int c) {
	return m[3][c];
    }
    
    public void clear() {
	super.clear();
	lastCol = 0;
    }


    /*====== public EdgeMatrix copy() ======

      Duplicates the EdgeMatrix.

      Returns: A copy of the calling EdgeMatrix.
      =====================*/
    public EdgeMatrix copy() {
	
	EdgeMatrix n = new EdgeMatrix( m[0].length );
	
	for (int r=0; r<m.length; r++)
	    for (int c=0; c<m[r].length; c++)
		n.m[r][c] = m[r][c];
	
	n.lastCol = lastCol;
	return n;
    }

}