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
  final int x;
  final int y;
  final int width;
  final int height;

  // State
  Cursor cur;
  int textColor;
  float score;

  TextDisplay(PApplet parent, int x, int y, int width, int height) {
    this.parent = parent;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;

    font = parent.createFont(fontName, fontSize);
    backgroundColor = parent.color(254, 247, 226);
    foregroundColor = parent.color(143, 162, 162);
    intenseColor = parent.color(220, 57, 0);
  }

  void doSetup() {
    parent.pushMatrix();
    parent.translate(x, y);

    parent.noStroke();
    parent.fill(backgroundColor);
    parent.rect(0, 0, width, height);
    parent.fill(foregroundColor);
    parent.textFont(font);
    parent.textAlign(parent.LEFT, parent.TOP);

    final int curWidth = 25;//(int)parent.textWidth(' ');
    final int curHeight = 25;//(int)(fontSize * 1.4);
    cur = new Cursor(Cursor.State.INACTIVE, margin[3], margin[0], curWidth, curHeight, foregroundColor, backgroundColor, 250);

    //cur.doDraw(parent);

    parent.popMatrix();

    textColor = foregroundColor;
  }

  void doDraw() {
    int t = parent.millis();
    parent.pushMatrix();
    parent.translate(x, y);
    cur.doDrawFaded(parent, t);
    parent.popMatrix();
  }

  void doType() {
    parent.pushMatrix();
    parent.translate(x, y);
    if (parent.key == '\n') {
      doDrawReturn();
    } else {
      doDraw(parent.key);
    }
    parent.popMatrix();
  }

  void doDraw(char c) {
    float w = parent.textWidth(c);

    cur.doClear(parent);
    if (!canAddWidth(w))
      doBreakLine();

    /*
    // Extra highlight
    if (score < 0.2) {
      parent.fill(255);
      parent.rect(cur.x, cur.y, cur.width, cur.height);
    }
    */

    parent.fill(textColor);
    parent.text(c, cur.x, cur.y);

    cur = cur.move(w, 0);
    if (!canAddWidth(cur.width))
      doBreakLine();
  }

  void doDrawReturn() {
    cur.doClear(parent);
    doBreakLine();
    cur.doDrawFaded(parent, parent.millis());
  }

  boolean canAddWidth(float w) {
    return cur.x + w <= width - margin[1] - margin[3];
  }

  void doBreakLine() {
    cur = cur.newLine(margin[3], (int)(1.4 * fontSize));
  }

  void doSetParameter1(float v) {
    //cur = cur.pulsate((int)(parent.lerp(100, 2000, v)));
  }

  void doSetParameter2(float v) {
    /*
    textColor = parent.lerpColor(foregroundColor, intenseColor, v);
     cur = cur.recolor(textColor);
     */
  }

  void doPulse(int RR) {
    cur = cur.pulsate(parent.millis());
  }

  void doUpdateSteady(int RRstd) {
    /*
    float f = parent.constrain(parent.map(RRstd, 100, 200, 0, 1), 0, 1);
    textColor = parent.lerpColor(intenseColor, foregroundColor, f);
    cur = cur.recolor(textColor);
    */
  }
  
  void doUpdateScore(float score) {
    this.score = score;
    textColor = parent.lerpColor(intenseColor, foregroundColor, score);
    cur = cur.recolor(textColor);
  }

  void doSetActive(boolean active) {
    if (active) {
      cur = cur.active(parent.millis());
    } else {
      cur = cur.inactive(parent.millis());
    }
  }

  void doSetAdjusting(boolean adjusting) {
    if (adjusting) {
      parent.println("adjusting!!");
      cur = cur.adjust(parent.millis());
    } else {
      parent.println("not adjusting!!");
      cur = cur.active(parent.millis());
    }
  }
}
