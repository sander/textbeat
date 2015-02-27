import processing.serial.*;
import ddf.minim.analysis.*;

final int w = 800;
final int h = 600;
int debugHeight = 0;
final TextDisplay td = new TextDisplay(this, 0, 0, w, h);
final RRport myport = new RRport(this);
final RRparser myparser = new RRparser();
final ButtonPort btn = new ButtonPort(this, "/dev/tty.usbmodem1411");
final Analyzer analyzer = new Analyzer();
final int size = 32;

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
    println("typed");
    td.doType();
    println("  handled");
  }
}

void mouseMoved() {
  td.doSetParameter1(float(mouseX) / width);
  td.doSetParameter2(float(mouseY) / height);
}

void serialEvent(Serial p) {
  if (btn != null && p == btn.port) {
    if (btn.doStep()) {
      td.doSetActive(btn.pressed);
    }
  } else {
    if (myport == null) return;
    myport.step();
    myparser.step();
    
    if (myparser.adjusting != wasAdjusting && btn.pressed) {
      td.doSetAdjusting(wasAdjusting = myparser.adjusting);
    }

    if (myparser.event()) {
      RR = myparser.val; 
      print(" RR="); 
      print(RR); 
      RRavg = int((23 * RRavg + 1*RR)/24); 
      print(" AVG="); 
      print(RRavg);
      RRstd = int((15.0 * RRstd + abs(RR - RRavg)) / 16.0); 
      print(" STD="); 
      print(RRstd);//niet met kwadraat ivm outlyers
      println();

      if (btn.pressed) {
        /*
        if (myparser.adjusting != wasAdjusting) {
          td.doSetAdjusting(false);
        }
        */
        td.doPulse(RR);
        buffer.add(RR);
        result = analyzer.calculate(buffer, size);
        td.doUpdateScore(result.score);
      }
      td.doUpdateSteady(RRstd);

      //float BTopt = 15.00; //assumed resonant breathing rate in seconds
      //int advice = int(BTopt*1000/RRavg); //idem in heart beats
      //fill(30); stroke(0); rect(0,height-16,16,2*16);
      //fill(150); text(Integer.toString(advice),0, height);
    }
  }
}
