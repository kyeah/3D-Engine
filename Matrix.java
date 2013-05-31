 /*========== Matrix.java ==========
  Matrix will hold a 2-d array of doubles and have a default size of 4x4.
  Handles basic matrix maintenence and math.
  Creates transformation matrices for translations, rotations, and scaling.
=========================*/

import java.io.*;
import java.util.*;

public class Matrix {

    public static final int DEFAULT_SIZE = 4;
    protected double[][] m;

    /*===========Constructors================
      Default constructor creates a 4x4 matrix
      Second constructor creates a 4xN matrix
    */
    public Matrix() {
	m = new double[DEFAULT_SIZE][DEFAULT_SIZE];
    }
    public Matrix(int c) {
	m = new double[DEFAULT_SIZE][c];
    }


    /*======== public double calculateDot() ==========

      Calculates the dot product of the surface normal to
      triangle points[i], points[i+1], points[i+2] and a 
      view vector.      

      Returns:  The dot product of 'view' and the surface normal of i.
      ====================*/
    public double calculateDot( int i, double[] view, String interp ) {
	
	double[] normal;
	double vx, vy, vz;
	double dot;

	if( interp.equals("flat") )
	    normal = calculateNormal( i );
	else
	    normal = calculateVertexNormal( i );
	
	normalize(normal);

	//set up view vector
	vx = view[0];
	vy = view[1];
	vz = view[2];
	normalize(view);

	//calculate dot product
	dot = normal[0] * vx + normal[1] * vy + normal[2] * vz;

	return dot;
    }
    
    /*====== public double calculateDot() =======
    
      Calculates the dot product A x B.

      Returns: A double representing A x B.
      =========================*/
    public double calculateDot( double[] A, double[] B ) {
	return A[0] * B[0] + A[1] * B[1] + A[2] * B[2];
    }

    /*======== public double area() =========
    
      Calculates the surface area of the triangle 
      identified by points[i], points[i+1], and points[i+2].

      Returns: The surface area of the polygon.
      =========================*/
    public double area( int i ){
	double[] normal = calculateNormal( i );
	double magnitude = Math.sqrt( Math.pow(normal[0], 2) +
				      Math.pow(normal[1], 2) +
				      Math.pow(normal[2], 2) );

	return magnitude * 0.5;
    }

    /*====== public void normalize() ======
    
      Normalizes the given vector.

      Returns:
      ===================*/
      public void normalize( double[] vector ){
	double magnitude = Math.sqrt( Math.pow(vector[0], 2) +
				      Math.pow(vector[1], 2) +
				      Math.pow(vector[2], 2) );

	for ( int i=0; i<3;i++ )
	    vector[i] /= magnitude;
	
    }

    /*========     public double[] calculateNormal() ==========
     
      Calculates the normal vector to the polygon identified by i.

      Returns:  A double array of size 3 representing the 
                cross product of <ax, ay, az> and <bx, by, bz>
		
		====================*/
    public double[] calculateNormal( int i ){

	double ax, ay, az, bx, by, bz;
	double[] normal = new double[3];
	
	ax = m[0][ i + 1 ] - m[0][ i ];
	ay = m[1][ i + 1 ] - m[1][ i ];
	az = m[2][ i + 1 ] - m[2][ i ];

	bx = m[0][ i ] - m[0][ i + 2 ];
	by = m[1][ i ] - m[1][ i + 2 ];
	bz = m[2][ i ] - m[2][ i + 2 ];

	normal[0] = ay * bz - az * by;
	normal[1] = az * bx - ax * bz;
	normal[2] = ax * by - ay * bx;

	return normal;
    }

    /*====== public double[] calculateVertexNormal() ======

      Calculates the normal vector to the vertex identified by index.

      Returns: A double array of size 3 representing the average 
               normal vector of all polygons that share the vertex.
	       
	       ======================*/
    public double[] calculateVertexNormal( int index ){

	double[] average, normal, baseNormal;
	double x, y, z; 
	int numPolys;
	ArrayList polygons;

	average = new double[3];	
	polygons = new ArrayList(100);

	x = m[0][index];
	y = m[1][index];
	z = m[2][index];

	baseNormal = calculateNormal(index - (index % 3) );
	average[0] = baseNormal[0];
	average[1] = baseNormal[1];
	average[2] = baseNormal[2];
	numPolys = 1;
	
	// Go through each polygon in the matrix
	for(int i=0; i < m[0].length - 2; i = i+3 ){
	    
	    // Determine whether the polygon shares the vertex
	    for(int j=i; j < i+3; j++)
		if( j != index &&
		    m[0][j] == x &&
		    m[1][j] == y &&
		    m[2][j] == z ) 
		    {
			normal = calculateNormal(i);
			
			/* Adds the polygon normal to the average if it makes a
			   smooth angle with the base polygon (angle < ~85 degrees)

			   ***For more functionality, use += normal[0] * (area(i) / baseArea) 
			      to weigh polygon normals based on area (not needed for the uniform triangle polygons currently used)
			*/
			if( calculateDot( baseNormal, normal ) > 0.1 ){
			    average[0] += normal[0];
			    average[1] += normal[1];
			    average[2] += normal[2];
			    numPolys++;
			}
			
			break;
		    
		    }
	}

	// Calculates the average normal vector components
	for(int i=0; i<3; i++)
	    average[i] = average[i] / numPolys;

	return average;
    }

    /*======   =======

      ==========================*/
    public double[] calculateLightVect( int i, Light light ){
	double Lx = m[0][i] - light.getX();
	double Ly = m[1][i] - light.getY();
	double Lz = m[2][i] - light.getZ();
	    
	double[] Lvect = { Lx, Ly, Lz };

	return Lvect;
    }

    public double[] calculateProjection( int i, Light light, String interp ){
	
	double NdL;
	double[] normal;

	NdL= calculateDiffuse( i, light, interp );

	if( interp.equals("flat") )
	    normal = calculateNormal( i );
	else
	    normal = calculateVertexNormal( i );

	normalize(normal);

	normal[0] *= NdL;
	normal[1] *= NdL;
	normal[2] *= NdL;

	return normal;

    }

    public double calculateDiffuse( int i, Light light, String interp ){
	double[] Lvect = calculateLightVect( i, light );
	normalize( Lvect );
	
	return calculateDot( i, Lvect, interp );
    }

    public double calculateSpecular( int i, Light light, double[] view, String interp ){

	//Calculate y component of light vector, L
	double L;
	double[] Lvect = calculateLightVect( i, light );
	normalize( Lvect );
	L = Lvect[1];

	//Find Reflection vector
	double[] projection = calculateProjection( i, light, interp ); 	
	projection[0] = 2 * projection[0];
	projection[1] = 2 * projection[1] - L;
	projection[2] = 2 * projection[2];

	//Return dot product of reflection and view vectors
	return calculateDot(projection, view);
    }

        /*===========grow================
      Increase the number of columns in a matrix by 10
      You can change the growth factor as you see fit
    */
    public void grow() {

	double[][] n = new double[m.length][m[0].length + 10];
	for (int r=0; r<m.length; r++)
	    for (int c=0; c<m[r].length; c++)
		n[r][c] = m[r][c];
	
	m = n;
    }

    /*======== public void clear() ==========
      Inputs:  
      Returns: 
      Sets every entry in the matrix to 0
      ====================*/
    public void clear() {

	for (int i=0; i<m.length; i++) 
	    for (int j=0; j<m[i].length; j++) 
		m[i][j] = 0;
    }		

    /*===========ident================
      Turns this matrix into the indentity matrix
      You may assume the calling Matrix is square
    */
    public void ident() {
	
	for (int i=0; i<m.length; i++) {
	    for (int j=0; j<m[i].length; j++) {
		
		if (i==j)
		    m[i][j] = 1;
		else
		    m[i][j] = 0;
	    }
	}
    }

    /*===========scalarMult================
      Inputs:  double x
      
      multiply each element of the calling matrix by x
    */
    public void scalarMult( int s ) {

	for (int i=0; i<m.length; i++) 
	    for (int j=0; j<m[i].length; j++) 
		m[i][j] = m[i][j] * s;
    }		

    /*===========matrixMult================
      Multply matrix n by the calling matrix, modify
      the calling matrix to store the result.
      
      eg.
      In the call a.matrixMult(n), n will remain the same
      and a will now be the product of n * a
    */
    public void matrixMult( Matrix n ) {

	double[][] tmp = new double[4][1];

	for (int c=0; c<m[0].length; c++) {
	    for (int r=0; r<4; r++) 
		tmp[r][0] = m[r][c];

	    for (int r=0; r<4; r++)
		m[r][c] = n.m[r][0] * tmp[0][0] +
		    n.m[r][1] * tmp[1][0] +
		    n.m[r][2] * tmp[2][0] +
		    n.m[r][3] * tmp[3][0];
	}
    }
   
    /*===========copy================
      Create and return new matrix that is a duplicate 
      of the calling matrix
    */
    public Matrix copy() {

	Matrix n = new Matrix( m[0].length );
	for (int r=0; r<m.length; r++)
	    for (int c=0; c<m[r].length; c++)
		n.m[r][c] = m[r][c];

	return n;
    }

    /*===========toString================
      Crate a readable String representation of the 
      calling matrix.
    */
    public String toString() {

	String s = "";
	for (int i=0; i<m.length; i++) {
	    for (int j=0; j<m[i].length; j++)
		s = s + m[i][j] + " ";
	    s = s + "\n";
	}
	return s;
    }

    /*===========MakeTranslate================
      Turns the calling matrix into the appropriate
      translation matrix using x, y, and z as the translation
      offsets.
    */
    public void makeTranslate(double x, double y, double z) {

	ident();
	m[0][3] = x;
	m[1][3] = y;
	m[2][3] = z;
    }
    
    /*===========MakeScale================
      Turns the calling matrix into the appropriate scale
      matrix using x, y and z as the scale factors.
    */
    public void makeScale(double x, double y, double z) {

	ident();
	m[0][0] = x;
	m[1][1] = y;
	m[2][2] = z;
    }

    /*=========== MakeRotX ================
      Turns the calling matrix into the appropriate rotation
      matrix using theta as the angle of rotation and X
      as the axis of rotation.
    */
    public void makeRotX(double theta) {
	
	ident();
	m[1][1] = Math.cos( theta );
	m[1][2] = -1 * Math.sin( theta );
	m[2][1] = Math.sin( theta );
	m[2][2] = Math.cos( theta );
    }

    /*=========== MakeRotY ================
      Turns the calling matrix into the appropriate rotation
      matrix using theta as the angle of rotation and Y
      as the axis of rotation.
    */
    public void makeRotY(double theta) {

	ident();
	m[0][0] = Math.cos( theta );
	m[0][2] = -1 * Math.sin( theta );
	m[2][0] = Math.sin( theta );
	m[2][2] = Math.cos( theta );
    }

    /*=========== MakeRotZ ================
      Turns the calling matrix into the appropriate rotation
      matrix using theta as the angle of rotation and axis
      as the axis of rotation.
    */
    public void makeRotZ(double theta) {

	ident();
	m[0][0] = Math.cos( theta );
	m[0][1] = -1 * Math.sin( theta );
	m[1][0] = Math.sin( theta );
	m[1][1] = Math.cos( theta );
    }
    
    /*======== public void makeHermite()) ==========
      Inputs:   
      Returns: 
      
      Turn the calling matrix into a hermite coeficient
      generating matrix

      03/09/12 18:15:58
      jonalf
      ====================*/
    public void makeHermite() {
	
	ident();
	m[0][0] = 2;
	m[0][1] = -2;
	m[0][2] = 1;
	m[0][3] = 1;

	m[1][0] = -3;
	m[1][1] = 3;
	m[1][2] = -2;
	m[1][3] = -1;

	m[3][0] = 1;
	m[3][3] = 0;
    }

    /*======== public void makeBezier()) ==========
      Inputs:   
      Returns: 

      Turn the calling matrix into a bezier coeficient
      generating matrix

      03/09/12 18:15:00
      jonalf
      ====================*/
    public void makeBezier() {
	
	ident();
	m[0][0] = -1;
	m[0][1] = 3;
	m[0][2] = -3;
	m[0][3] = 1;

	m[1][0] = 3;
	m[1][1] = -6;
	m[1][2] = 3;
	m[1][3] = 0;

	m[2][0] = -3;
	m[2][1] = 3;
	m[2][2] = 0;

	m[3][0] = 1;
	m[3][3] = 0;
    }

    /*======== public void generateHermiteCoefs() ==========
      Inputs:  double p1
               double p2
	       double p3
	       double p4 
      Returns: 
      
      Turns the calling matrix into a matrix that provides the 
      coefiecients required to generate a Hermite curve given 
      the values of the 4 parameter coordinates.

      03/09/12 18:17:16
      jonalf
      ====================*/
    public void generateHermiteCoefs(double p1, double p2, 
				     double p3, double p4) {

	Matrix mult = new Matrix(4);

	m[0][0] = p1;
	m[1][0] = p3;
	m[2][0] = p2 - p1;
	m[3][0] = p4 - p3;

	mult.makeHermite();
	matrixMult( mult );
    }

    /*======== public void generateBezierCoefs() ==========
      Inputs:  double p1
               double p2
	       double p3
	       double p4 
      Returns: 
      
      Turns the calling matrix into a matrix that provides the 
      coefiecients required to generate a Bezier curve given 
      the values of the 4 parameter coordinates.

      03/09/12 18:17:16
      jonalf
      ====================*/
    public void generateBezierCoefs(double p1, double p2, 
				    double p3, double p4) {

	Matrix mult = new Matrix(4);

	m[0][0] = p1;
	m[1][0] = p2;
	m[2][0] = p3;
	m[3][0] = p4;

	mult.makeBezier();
	matrixMult( mult );
    } 


}
