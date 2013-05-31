all: subs mdl.class

subs:
	$(MAKE) -C parseTables
	$(MAKE) -C parser

mdl.class: mdl.java MDLReader.class
	javac -classpath "." mdl.java

MDLReader.class: MDLReader.java Matrix.class EdgeMatrix.class Frame.class VaryNode.class Light.class
	javac -cp "." MDLReader.java

Frame.class: Frame.java EdgeMatrix.class Light.class
	javac -cp "."  Frame.java

EdgeMatrix.class: EdgeMatrix.java Matrix.class
	javac -cp "." EdgeMatrix.java

Matrix.class: Matrix.java
	javac -cp "." Matrix.java

VaryNode.class: VaryNode.java
	javac -cp "." VaryNode.java

Light.class: Light.java 
	javac -cp "." Light.java

clean:
	$(MAKE) -C parseTables clean
	$(MAKE) -C parser clean
	rm *.class