Java 3D Graphics Engine and Animator
======================================

A 3D graphics engine built from scratch in Java. Able to read scripts written in Jon-Alf Dyrland-Weaver's motion description language and generate animated gifs.

#### FEATURES

* Scanline
* Z-Buffering
* Flat Shading
* Gouraud Shading

Usage
==========

Compile and run a script

     make
     java mdl <mdl filename>

The animation frames will be placed into animations/<mdl filename>/
from there, you can generate a gif:
     
     animate -delay 3 ./* (suggested delay)

### Provided Scripts
* flatAnim - Flat shading test
* gouraudAnim - Gouraud shading test
* gouraudtorus - Torus test
* discoAnim - Flat shading test with random colors for each polygon

* animtest - general test
* hypnotic
* test

BUGS
=======
Gouraud:
  shading works for black and white, with black spot at the sphere's rear end.
  shading is not uniform for colored objects; transitions in layers.
  
KY 2012
