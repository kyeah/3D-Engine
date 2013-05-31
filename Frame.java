/*========== Frame.java ==========
  Wrapper class for java's built in BufferedImage class.
  Allows use of java's DrawLine and image saving methods

  =========================*/

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;

public class Frame {

    public static final int XRES = 500;
    public static final int YRES = 500;
    public static final int COLOR_VALUE = 255;

    private int maxx, maxy, maxcolor; 
    private BufferedImage bi;
    private double[][] zBuffer;
    private double[] view = {0,0,-1};
    private Light[] lights;
    private double ambientR, ambientG, ambientB;
    private String interp;

    public Frame() {
	maxx = XRES;
	maxy = YRES;
	maxcolor = COLOR_VALUE;
	bi = new BufferedImage(maxx,maxy,BufferedImage.TYPE_BYTE_INDEXED);
	
	ambientR = ambientG = ambientB = 1;

	interp = "wireframe";
	lights = new Light[5];

	zBuffer = new double[maxx][maxy];
	clearBuffer();
    }

    public void clearScreen() {
	bi = new BufferedImage(maxx,maxy,BufferedImage.TYPE_BYTE_INDEXED);
	clearBuffer();
    }	

    public void clearBuffer(){
	for(int i=0; i < maxx; i++)
	    for(int j=0; j < maxy; j++)
		zBuffer[i][j] = Double.NEGATIVE_INFINITY;
    }

    public void addAmbient(double r, double g, double b){
	ambientR = r;
	ambientG = g;
	ambientB = b;
    }

    public void addLight(String name, double x, double y, double z, double r, double g, double b){
	for(int i=0; i < 5; i++)
	    if(lights[i] == null)
		lights[i] = new Light(name, x, y, z, r, g, b);
	
    }

    public void setInterp(String type){
    	interp = type;
    }

    public Color calculateShade(EdgeMatrix pm, int i, int r, int g, int b){

	double iDiffuse, iSpec, iNet, iR, iG, iB;
       	iR = iG = iB = 0;
	
	if ( interp.equals("flat") || interp.equals("gouraud") || interp.equals("phong") ){

	    iDiffuse = pm.calculateDiffuse( i, lights[0], interp ); //If multiple lights, need to find max iDiffuse out of them
	    
	    iSpec = pm.calculateSpecular( i, lights[0], view, interp );
	    iNet = iDiffuse + iSpec;

	    iR = lights[0].getRed() * iNet;
	    iG = lights[0].getGreen() * iNet;
	    iB = lights[0].getBlue() * iNet;
		    
	}
 
	else if ( interp.equals("disco") ) {
	    r = (r+30) % 255;
	    g = (g+50) % 255;
	    b = (b+70) % 255;
	    return new Color(r,g,b);
	}
	else{ 
	    throw new IllegalArgumentException("Shading method not found.");
	}

	//Add ambient
	iR += ambientR;
	iG += ambientG;
	iB += ambientB;
	
	r *= iR;
	g *= iG;
	b *= iB;

	    
	if(r > 255) r=255;
	if(g > 255) g=255;
	if(b > 255) b=255;
	if(r < 0) r=0;
	if(g < 0) g=0;
	if(b < 0) b=0;

	return new Color(r,g,b);
    
    }

    /*======== public void drawPolygons() ==========
      Inputs:  EdgeMatrix pm
      Color c 
      Returns: 
      
      Go through the point matrix as if it were a polygon matrix
      Call drawline in batches of 3s to create triangles.
      Call scanLine in batches of 3s to shade in triangles.
 
      04/16/12 22:05:02
      jdyrlandweaver
      ====================*/
    public void drawPolygons(EdgeMatrix pm, Color c) {

	int r, g, b;
	Color shade, shade0, shade1, shade2;

	r = c.getRed();
	g = c.getGreen();
	b = c.getBlue();

	if ( pm.getLastCol() < 3 ) 
	    return;
	
	for (int i=0; i < pm.getLastCol() - 2; i+=3)  {

	    if ( pm.calculateDot( i, view, "flat" ) > 0 ) {
		
		//Shaded
		if( interp.equals("gouraud") || interp.equals("phong") ){
		    shade0 = calculateShade(pm, i, r, g, b);
		    shade1 = calculateShade(pm, i+1, r, g, b);
		    shade2 = calculateShade(pm, i+2, r, g, b);

		    drawLine( (int)pm.getX(i), (int)pm.getY(i), (int)pm.getZ(i),
			      (int)pm.getX(i+1), (int)pm.getY(i+1), (int)pm.getZ(i+1), shade0, shade1);
		    drawLine( (int)pm.getX(i+1), (int)pm.getY(i+1), (int)pm.getZ(i+1),
			      (int)pm.getX(i+2), (int)pm.getY(i+2), (int)pm.getZ(i+2), shade1, shade2);
		    drawLine( (int)pm.getX(i+2), (int)pm.getY(i+2), (int)pm.getZ(i+2),
			      (int)pm.getX(i), (int)pm.getY(i), (int)pm.getZ(i), shade2, shade0);

		    scanLine( (int)pm.getX(i), (int)pm.getY(i), (int)pm.getZ(i),
		    	      (int)pm.getX(i+1), (int)pm.getY(i+1), (int)pm.getZ(i+1),
		    	      (int)pm.getX(i+2), (int)pm.getY(i+2), (int)pm.getZ(i+2), shade0, shade1, shade2);
		    		    
		    
		    /*
		    Color redsss = new Color(255, 0, 0);
		    drawLine( (int)pm.getX(i), (int)pm.getY(i), (int)pm.getZ(i),
			      (int)pm.getX(i+1), (int)pm.getY(i+1), (int)pm.getZ(i+1), redsss, redsss);
		    drawLine( (int)pm.getX(i+1), (int)pm.getY(i+1), (int)pm.getZ(i+1),
			      (int)pm.getX(i+2), (int)pm.getY(i+2), (int)pm.getZ(i+2), redsss, redsss);
		    drawLine( (int)pm.getX(i+2), (int)pm.getY(i+2), (int)pm.getZ(i+2),
			      (int)pm.getX(i), (int)pm.getY(i), (int)pm.getZ(i), redsss, redsss);
		    */		
		    continue;
		}
		
		else if( interp.equals("flat") || interp.equals("disco") ){
		    shade = calculateShade(pm, i, r, g, b);
		    
		    if(interp.equals("disco")){
			r = shade.getRed();
			g = shade.getGreen();
			b = shade.getBlue();
		    }
		    
		    scanLine( (int)pm.getX(i), (int)pm.getY(i), (int)pm.getZ(i),
			      (int)pm.getX(i+1), (int)pm.getY(i+1), (int)pm.getZ(i+1),
			      (int)pm.getX(i+2), (int)pm.getY(i+2), (int)pm.getZ(i+2), shade, shade, shade);
		}
		
		//Wireframe
		else{
		    shade = c;
		}

		drawLine( (int)pm.getX(i), (int)pm.getY(i), (int)pm.getZ(i),
			  (int)pm.getX(i+1), (int)pm.getY(i+1), (int)pm.getZ(i+1), shade, shade);
		drawLine( (int)pm.getX(i+1), (int)pm.getY(i+1), (int)pm.getZ(i+1),
			  (int)pm.getX(i+2), (int)pm.getY(i+2), (int)pm.getZ(i+2), shade, shade);
		drawLine( (int)pm.getX(i+2), (int)pm.getY(i+2), (int)pm.getZ(i+2),
			  (int)pm.getX(i), (int)pm.getY(i), (int)pm.getZ(i), shade, shade);

	    }
	    
	}  
    }
 
    public EdgeMatrix sortPoints( int x0, int y0, int z0,
				  int x1, int y1, int z1,
				  int x2, int y2, int z2){

	EdgeMatrix mat = new EdgeMatrix(3);
	int yMin;
	yMin = (int)Math.min( Math.min(y0, y1), y2);

	if(yMin == y0){
	    mat.addPoint(x0, y0, z0);
	    if(y1 < y2){
		mat.addPoint(x1, y1, z1);
		mat.addPoint(x2, y2, z2);
	    }
	    else{
		mat.addPoint(x2, y2, z2);
		mat.addPoint(x1, y1, z1);
	    }
	}

	else if(yMin == y1){
	    mat.addPoint(x1, y1, z1);
	    if(y0 < y2){
		mat.addPoint(x0, y0, z0);
		mat.addPoint(x2, y2, z2);
	    }
	    else{
		mat.addPoint(x2, y2, z2);
		mat.addPoint(x0, y0, z0);
	    }
	}
	else{
	    mat.addPoint(x2, y2, z2);
	    if(y1 < y0){
		mat.addPoint(x1, y1, z1);
		mat.addPoint(x0, y0, z0);
	    }
	    else{
		mat.addPoint(x0, y0, z0);
		mat.addPoint(x1, y1, z1);
	    }
	}	    

	return mat;
    }

    /*======== public void drawPolygons() ==========
      Inputs:  int x0, y0, z0
               int x1, y1, z1
               int x2, y2, z2
	       Color c
      Returns: 
      
      Determine the min, mid, max y values and corresponding x values.
      Fill in the triangle using horizontal lines from top to bottom.
 
      ====================*/
    public void scanLine( int x0, int y0, int z0,
			  int x1, int y1, int z1,
			  int x2, int y2, int z2, Color c0, Color c1, Color c2){

	EdgeMatrix points;
	int xMid, xTop, 
	    y, yMid, yMax, 
	    zMid, zTop;
	double x, rowEnd, z, zEnd, 
	    slopeBM, slopeBT, slopeMT, 
	    zBT, zBM, zMT;
	
	double red, green, blue,
	    redMid, greenMid, blueMid,
	    redTop, greenTop, blueTop,
	    redEnd, greenEnd, blueEnd, 
	    redBT, redBM, redMT, 
	    greenBT, greenBM, greenMT, 
	    blueBT, blueBM, blueMT;
	Color cMin, cMid, cMax;

	//Instantialize to get compiler out of your patootie
	xMid = xTop = y = yMid = zMid = zTop = 0;
	rowEnd = x = z = zEnd = 0.0;
	cMin = null;
	cMid = null;
	cMax = null;

	/* SORT POINTS */
	points = sortPoints(x0, y0, z0, 
			    x1, y1, z1, 
			    x2, y2, z2);

	x = rowEnd = (int)points.getX(0);
	xMid = (int)points.getX(1);
	xTop = (int)points.getX(2);

	y = (int)points.getY(0);
	yMid = (int)points.getY(1);
	yMax = (int)points.getY(2);
	
	z = zEnd = (int)points.getZ(0);
	zMid = (int)points.getZ(1);
	zTop = (int)points.getZ(2);
	/* END SORTING */

	
	/* SORT COLORS */
	if( x == x0 && y == y0 && z == z0){
	    cMin = c0;
	    c0 = null;
	}
	else if( x == x1 && y == y1 && z == z1){
	    cMin = c1;
	    c1 = null;
	}
	else{
	    cMin = c2;
	    c2 = null;
	}

	if( c0 != null && xMid == x0 && yMid == y0 && zMid == z0){
	    cMid = c0;
	    c0 = null;
	}
	else if( c1 != null && xMid == x1 && yMid == y1 && zMid == z1){
	    cMid = c1;
	    c1 = null;
	}
	else if( c2 != null && xMid == x2 && yMid == y2 && zMid == z2){
	    cMid = c2;
	    c2 = null;
	}
	
	if( c0 != null && xTop == x0 && yMax == y0 && zTop == z0)
	    cMax = c0;
	else if( c1 != null && xTop == x1 && yMax == y1 && zTop == z1)
	    cMax = c1;
	else if( c2 != null && xTop == x2 && yMax == y2 && zTop == z2)
	    cMax = c2;

	red = redEnd = cMin.getRed();
	redMid = cMid.getRed();
	redTop = cMax.getRed();

	green = greenEnd = cMin.getGreen();
	greenMid = cMid.getGreen();
	greenTop = cMax.getGreen();

	blue = blueEnd = cMin.getBlue();
	blueMid = cMid.getBlue();
	blueTop = cMax.getBlue();
	
	/* END COLOR SORT */




      
	slopeBT = (xTop - x) / (yMax - y);
	zBT = (zTop - z) / (yMax - y);
	
	redBT = (redTop - red) / (yMax - y);
	greenBT = (greenTop - green) / (yMax - y);
	blueBT = (blueTop - blue) / (yMax - y);
	

	// Scanline upper portion of triangle
	if(yMid != y){

	    // Find slope and inverse it to find change in x per increment in y direction
	    slopeBM = (xMid - x) / (yMid - y);
	    zBM = (zMid - z) / (yMid - y);
	    
	    redBM = (redMid - red) / (yMax - y);
	    greenBM = (greenMid - green) / (yMax - y);
	    blueBM = (blueMid - blue) / (yMax - y);
	 
	    // Scanline
	    for(y = y+1; y < yMid; y++){
		
		x += slopeBT;
		rowEnd += slopeBM;
		
		z += zBT;
		zEnd += zBM;
	
		red += redBT;
		redEnd += redBM;

		green += greenBT;
		greenEnd += greenBM;

		blue += blueBT;
		blueEnd += blueBM;

		c0 = new Color((int)red, (int)green, (int)blue);
		c1 = new Color((int)redEnd, (int)greenEnd, (int)blueEnd);

		drawLine((int)x, y, (int)z,
			 (int)rowEnd, y, (int)zEnd, c0, c1);
		
	    }
	}
	
	// Scanline lower portion of triangle
	if(yMax != yMid){
	    slopeMT = (xTop - (double)xMid) / (yMax - yMid);
	    zMT = (zTop - (double)zMid) / (yMax - yMid);

	    redMT = (redTop - (double)redMid) / (yMax - yMid);
	    greenMT = (greenTop - (double)greenMid) / (yMax - yMid);
	    blueMT = (blueTop - (double)blueMid) / (yMax - yMid);

	    rowEnd = xMid - slopeMT;
	    zEnd = zMid - zMT;
	    
	    redEnd = redMid - redMT;
	    greenEnd = greenMid - greenMT;
	    blueEnd = blueMid - blueMT;

	    for(y = yMid; y < yMax; y++){
		x += slopeBT;
		rowEnd += slopeMT;

		z += zBT;
		zEnd += zMT;

		red += redBT;
		redEnd += redMT;

		green += greenBT;
		greenEnd += greenMT;

		blue += blueBT;
		blueEnd += blueMT;

		c0 = new Color((int)red, (int)green, (int)blue);
		c1 = new Color((int)redEnd, (int)greenEnd, (int)blueEnd);

		drawLine((int)x, y, (int)z,
			 (int)rowEnd, y, (int)zEnd, c0, c1);    
		
	    }
	}
	
    }
    
    /*======== public void drawLines() ==========
      Inputs:  PointMatrix pm
      Color c 
      Returns: 
      calls drawLine so that it draws all the lines within PointMatrix pm
      ====================*/
    public void drawLines(EdgeMatrix pm, Color c) {
	
	for (int i=0; i < pm.getLastCol() - 1; i+=2) 
	    drawLine( (int)pm.getX(i), (int)pm.getY(i),
		      (int)pm.getX(i+1), (int)pm.getY(i+1), c);
    }	


    /*======== public void drawLine() ==========
      Inputs:  int x0
      int y0
      int x1
      int y1
      Color c 
      Returns: 
      Wrapper for java's built in drawLine routine
      ====================*/
    public void drawLine(int x0, int y0, 
			 int x1, int y1, Color c) {
	Graphics2D g = bi.createGraphics();
	g.setColor(c);
	g.drawLine(x0,y0,x1,y1);
    }	
   
    /*========= public boolean inBounds(int x, int y) =========
      Inputs: int x, int y
      Returns: true if the point is within frame boundaries; false otherwise.

      ====================*/
    public boolean inBounds(int x, int y){
	return 	x < maxx && y < maxy && x >= 0 && y >= 0;
	
    }

    /*======== public void drawLine() ==========
      Inputs:  int x0
      int y0
      int z0
      int x1
      int y1
      int z1
      Color c 
      Returns: 
      
      per-Pixel drawLine method incorporating z-Buffering.
      ====================*/

    public void drawLine(int x0, int y0, int z0,
			 int x1, int y1, int z1, Color c0, Color c1){

	int x, y, dx, dy, d;
	double z, dz, zBuf, dr, dg, db, red, green, blue;

	Graphics2D g = bi.createGraphics();
	g.setColor(c0);
	
	x = x0;
	y = y0;
	z = z0;

	if(inBounds(x,y))
	    zBuf = zBuffer[x][y];
	else 
	    zBuf = Double.POSITIVE_INFINITY;

	//swap points so we're always drawing left to right
	if ( x0 > x1 ) {
	    x = x1;
	    y = y1;
	    z = z1;
	    x1 = x0;
	    y1 = y0;
	    z1 = z0;

	    red = c1.getRed();
	    green = c1.getGreen();
	    blue = c1.getBlue();
	    
	    dr = c0.getRed() - red;
	    dg = c0.getGreen() - green;
	    db = c0.getBlue() - blue;
	}
	else{
	    red = c0.getRed();
	    green = c0.getGreen();
	    blue = c0.getBlue();
	    
	    dr = c1.getRed() - red;
	    dg = c1.getGreen() - green;
	    db = c1.getBlue() - blue;
	}

	dx = x1 -x;
	dy = y1 - y;
	dz = z1 - z;
	
	//positive slope: Octants 1, 2 (5 and 6)
	if ( dy > 0 ) {

	    //slope < 1: Octant 1 (5)
	    if ( dx > dy ) {
		d = 2 * dy - dx;
  
		while ( x < x1 ) {
		    
		    if(!c0.equals(c1))
			g.setColor(new Color((int)red, (int)green, (int)blue));

		    //Check Z value and draw pixel if it is in view
		    if( inBounds(x,y) ){
			zBuf = zBuffer[x][y];
			
			if ( z > zBuf ){
			    g.drawLine(x, y, x, y);
			    zBuffer[x][y] = z;
			}
		    }
		    
		    //Find next pixel
		    if ( d < 0 ) {
			x = x + 1;
			d = d + dy;
			
		    }
		    else {
			x = x + 1;
			y = y + 1;
			d = d + dy - dx;
			
		    }
		    
		    z += dz / dx;
		    
		    red += dr / dx;
		    green += dg / dx;
		    blue += db / dx;

		    if(red>255) red=255;
		    if(green>255) green=255;
		    if(blue>255) blue=255;
		    if(red<0) red=0;
		    if(green<0) green=0;
		    if(blue<0) blue=0;
		    
		}
	    }
	    
	    //slope > 1: Octant 2 (6)
	    else {
		d = dy - 2 * dx;
		while ( y < y1 ) {
		    
		    if(!c0.equals(c1))
			g.setColor(new Color((int)red, (int)green, (int)blue));

		    if( inBounds(x,y) ){
			zBuf = zBuffer[x][y];
			
			if( z > zBuf ){
			    g.drawLine(x, y, x, y);
			    zBuffer[x][y] = z;
			}
		    }
		    
		    if ( d > 0 ) {
			y = y + 1;
			d = d - dx;
		    }
		    else {
			y = y + 1;
			x = x + 1;
			d = d + dy - dx;
		    }
		    
		    z += dz / dy;
		    
		    red += dr / dy;
		    green += dg / dy;
		    blue += db / dy;

		    if(red>255) red=255;
		    if(green>255) green=255;
		    if(blue>255) blue=255;
		    if(red<0) red=0;
		    if(green<0) green=0;
		    if(blue<0) blue=0;
		}
	    }
	}
	
	//negative slope: Octants 7, 8 (3 and 4)
	else { 
	    
	    //slope > -1: Octant 8 (4)
	    if ( dx > Math.abs(dy) ) {
		d = 2 * dy + dx;
		
		while ( x < x1 ) {
		    
		    if(!c0.equals(c1))
			g.setColor(new Color((int)red, (int)green, (int)blue));

		    if( inBounds(x,y) ){
			zBuf = zBuffer[x][y];
			
			if( z > zBuf ){
			    g.drawLine(x, y, x, y);
			    zBuffer[x][y] = z;
			}
		    }
		    
		    if ( d > 0 ) {
			x = x + 1;
			d = d + dy;
			
		    }
		    else {
			x = x + 1;
			y = y - 1;
			d = d + dy + dx;

		    }

		    z += dz / dx;
		
		    red += dr / dx;
		    green += dg / dx;
		    blue += db / dx;

		    if(red>255) red=255;
		    if(green>255) green=255;
		    if(blue>255) blue=255;
		    if(red<0) red=0;
		    if(green<0) green=0;
		    if(blue<0) blue=0;
		}
	    }

	    //slope < -1: Octant 7 (3)
	    else {
		
		d = dy + 2 * dx;
		while ( y > y1 ) {
		    
		    if(!c0.equals(c1))
			g.setColor(new Color((int)red, (int)green, (int)blue));
		    

		    if( inBounds(x,y) ){
			zBuf = zBuffer[x][y];
			
			if( z > zBuf ){
			    g.drawLine(x, y, x, y);
			    zBuffer[x][y] = z;
			}
		    }
		    
		    if ( d < 0 ) {
			y = y - 1;
			d = d + dx;
		    }
		    else {
			y = y - 1;
			x = x + 1;
			d = d + dy + dx;
		    }

		    z -= dz / dy;

		    red -= dr / dy;
		    green -= dg / dy;
		    blue -= db / dy;

		    if(red>255) red=255;
		    if(green>255) green=255;
		    if(blue>255) blue=255;
		    if(red<0) red=0;
		    if(green<0) green=0;
		    if(blue<0) blue=0;
		}
	    }
	}                        

    }

    /*======== public void save() ==========
      Inputs:  String filename 
      Returns: 
      saves the bufferedImage as a png file with the given filename
      ====================*/
    public void save(String filename) {
	try {
	    File fn = new File(filename);
	    ImageIO.write(bi,"png",fn);
	}
	catch (IOException e) {}
    }

}