import processing.core.*;

class TextDisplay {
  final PApplet parent;
  final String fontName = "FiraMonoOT-Bold";
  final int fontSize = 18;
  final PFont font;
  final int[] margin = {
    12, 12, 12, 12
  };
  final int backgroundColor;
  final int foregroundColor;
  final int intenseColor;

  // State
  Cursor cur;
  int textColor;

  TextDisplay(PApplet parent) {
    this.parent = parent;
    
    font = parent.createFont(fontName, fontSize);
    backgroundColor = parent.color(255, 255, 255);
    foregroundColor = parent.color(150);
    intenseColor = parent.color(255, 0, 0);
  }

  void doSetup() {
    parent.background(backgroundColor);
    parent.size(800, 600);
    parent.fill(foregroundColor);
    parent.noStroke();
    parent.textFont(font);
    parent.textAlign(parent.LEFT, parent.TOP);

    cur = new Cursor(margin[3], margin[0], (int)parent.textWidth(' '), (int)(fontSize * 1.4), foregroundColor, backgroundColor, 250);
    cur.doDraw(parent);
    
    textColor = foregroundColor;
  }

  void doDraw() {
    int t = parent.millis();
    cur.doDrawFaded(parent, t);
  }

  void doType() {
    if (parent.key == '\n') {
      doDrawReturn();
    } else {
      doDraw(parent.key);
    }
  }

  void doDraw(char c) {
    float w = parent.textWidth(c);

    cur.doClear(parent);
    if (!canAddWidth(w))
      doBreakLine();
    parent.fill(textColor);
    parent.text(c, cur.x, cur.y);
    cur = cur.move(w, 0);
    if (!canAddWidth(cur.width))
      doBreakLine();
  }

  void doDrawReturn() {
    cur.doClear(parent);
    doBreakLine();
    cur.doDraw(parent);
  }

  boolean canAddWidth(float w) {
    return cur.x + w <= parent.width - margin[1] - margin[3];
  }

  void doBreakLine() {
    cur = cur.newLine(margin[3], (int)(1.4 * fontSize));
  }

  void doSetParameter1(float v) {
    //cur = cur.pulsate((int)(parent.lerp(100, 2000, v)));
  }
  
  void doSetRR(int RR) {
    cur = cur.pulsate(RR);
  }

  void doSetParameter2(float v) {
    textColor = parent.lerpColor(foregroundColor, intenseColor, v);
    cur = cur.recolor(textColor);
  }
}
