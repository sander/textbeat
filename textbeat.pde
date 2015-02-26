import processing.serial.*;

final int w = 800;
final int h = 600;
final int debugHeight = 0;
final TextDisplay td = new TextDisplay(this, 0, 0, w, h - debugHeight);
final RRport myport = new RRport(this);
final RRparser myparser = new RRparser();
final ButtonPort btn = new ButtonPort(this, "/dev/tty.usbmodem1411");

// State
int RR = 1200;
int RRavg = 1200;
int RRstd = 50;
boolean wasAdjusting = false;

void setup() {
  size(w, h);
  td.doSetup();
}

void draw() {
  td.doDraw();
}

void keyTyped() {
  td.doType();
}

void mouseMoved() {
  td.doSetParameter1(float(mouseX) / width);
  td.doSetParameter2(float(mouseY) / height);
}

void serialEvent(Serial p) {
  if (btn != null && p == btn.port) {
    if (btn.doStep()) {
      td.doSetActive(btn.pressed);
      println("update: " + btn.pressed);
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
      }
      td.doUpdateSteady(RRstd);

      //float BTopt = 15.00; //assumed resonant breathing rate in seconds
      //int advice = int(BTopt*1000/RRavg); //idem in heart beats
      //fill(30); stroke(0); rect(0,height-16,16,2*16);
      //fill(150); text(Integer.toString(advice),0, height);
    }
  }
}
