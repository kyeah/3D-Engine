JCC=javac
JFLAGS=-classpath ".."

.SUFFIXES: .java .class

.java.class:
	$(JCC) $(JFLAGS) $<

all: classes

classes: opCode.class opShape.class opBox.class opTorus.class opSphere.class \
	opLine.class SymTab.class opShape.class opTrans.class opMove.class \
	opScale.class opRotate.class opPush.class opPop.class \
	opBasename.class opSet.class opSetknobs.class \
	opGeneraterayfiles.class opShading.class opFocal.class \
	opDisplay.class opLight.class opAmbient.class \
	opConstants.class opSavecs.class opCamera.class opSaveknobs.class \
	opTween.class opFrames.class opVary.class opSave.class


clean:
	rm *class
