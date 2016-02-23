import processing.serial.*;
import ddf.minim.analysis.*;

final int w = 1440;
final int h = 900;
int debugHeight = 0;
final TextDisplay td = new TextDisplay(this, 0, 0, w, h);
LubDub ld = new LubDub("ea53284303a740b68702ceb6259ebf2a", new Handler());
final Analyzer analyzer = new Analyzer();
final int size = 16;

// State
int RR = 1200;
int RRavg = 1200;
int RRstd = 50;
boolean wasAdjusting = false;
SlidingBuffer<Integer> buffer = new SlidingBuffer<Integer>(size);
Analyzer.Result result;

void setup() {
  size(w, h);
  td.doSetup();
  td.doSetActive(true);
  td.doSetAdjusting(false);
}

void draw() {
  td.doDraw();
  if (result != null && debugHeight > 0)
    result.doDraw(this, 0, height - debugHeight, width, debugHeight);
}

void keyTyped() {
}

void keyPressed() {
  if (keyCode == 17 /* ctrl */) {
    debugHeight = 200;
  }
}

void keyReleased() {
  if (keyCode == 17 /* ctrl */) {
    fill(td.backgroundColor);
    noStroke();
    rect(0, height - debugHeight, width, debugHeight);
    debugHeight = 0;
  } else {
    td.doType();
  }
}

void mouseMoved() {
  td.doSetParameter1(float(mouseX) / width);
  td.doSetParameter2(float(mouseY) / height);
}

class Handler implements LubDubHandler {
  void handle(int RR) {
    RR = Math.round(RR / 1.024);
    print(" RR="); 
    print(RR); 
    RRavg = int((23 * RRavg + 1*RR)/24); 
    print(" AVG="); 
    print(RRavg);
    RRstd = int((15.0 * RRstd + abs(RR - RRavg)) / 16.0); 
    print(" STD="); 
    print(RRstd);//niet met kwadraat ivm outlyers
    println();

    td.doPulse(RR);
    buffer.add(RR);
    result = analyzer.calculate(buffer, size);
    td.doUpdateScore(result.score);

    td.doUpdateSteady(RRstd);
  }
}

