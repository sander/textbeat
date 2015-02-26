import processing.core.*;

final class Cursor {
  final int t1 = 200;
  final int t2 = 500;
  final State state;
  final int width;
  final int height;
  final int x;
  final int y;
  final int fg;
  final int bg;
  final int t0;

  enum State { 
    ACTIVE, INACTIVE, ADJUSTING
  };

  Cursor(State s, int x, int y, int width, int height, int fg, int bg, int t0) {
    this.state = s;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.fg = fg;
    this.bg = bg;
    this.t0 = t0;
  }

  void doDraw(PApplet parent, int c) {
    doDraw(parent, c, width);
  }

  void doDraw(PApplet parent, int c, float r) {
    parent.noStroke();
    //parent.stroke(0);
    parent.fill(bg);
    parent.rect(x, y, width, height);
    parent.fill(c);
    parent.ellipse(x + (float)width / 2, y + (float)height / 2, r, r);
  }

  void doDraw(PApplet parent) {
    doDraw(parent, fg);
  }

  void doDrawFaded(PApplet parent, int t) {
    if (state == State.ACTIVE) {
      doDrawFadedActive(parent, t);
    } else if (state == State.INACTIVE) {
      doDrawFadedInactive(parent, t);
    } else if (state == State.ADJUSTING) {
      doDrawRotatingAdjusting(parent, t);
    }
  }

  private void doDrawFadedActive(PApplet parent, int t) {
    final int steps = t - t0;
    final int thres1 = t1;
    final int thres2 = t2;
    final float f = parent.constrain(
    (steps < thres1)
      ? (float)(steps) / thres1
      : (steps < thres2)
      ? (float)(steps - thres1) / (thres2 - thres1)
      : 1, 0, 1);
    //final int c = parent.lerpColor(fg, bg, f);
    final int c = fg;
    final float r = parent.lerp(width, (float)width / 2, f);
    doDraw(parent, c, r);
  }

  private void doDrawFadedInactive(PApplet parent, int t) {
    parent.noStroke();
    parent.fill(bg);
    parent.rect(x, y, width, height);
    parent.fill(fg);
    parent.rect(x, y, 2, height);
  }
  
  private void doDrawRotatingAdjusting(PApplet parent, int t) {
    float angle = parent.lerp(0, parent.TWO_PI, (float)t / 500);
    parent.noStroke();
    parent.fill(bg);
    parent.rect(x, y, width, height);
    parent.fill(fg);
    parent.pushMatrix();
    parent.translate(x + (float)width / 2, y + (float)height / 2);
    parent.rotate(angle);
    parent.ellipse((float)width/3, 0, 4, 4);
    parent.popMatrix();
    parent.println("drawing adjusting" + angle + ", " + fg);
  }

  void doClear(PApplet parent) {
    doDraw(parent, bg);
  }

  Cursor place(int x, int y) {
    return new Cursor(state, x, y, width, height, fg, bg, t0);
  }

  Cursor move(int dx, int dy) { 
    return place(x + dx, y + dy);
  }

  Cursor move(float dx, float dy) {
    return move((int)dx, (int)dy);
  }

  Cursor newLine(int x, int dy) {
    return place(x, y + dy);
  }

  Cursor pulsate(int t) {
    return new Cursor(state, x, y, width, height, fg, bg, t);
  }

  Cursor recolor(int c) {
    return new Cursor(state, x, y, width, height, c, bg, t0);
  }

  Cursor inactive(int t) {
    return new Cursor(State.INACTIVE, x, y, width, height, fg, bg, t);
  }

  Cursor active(int t) {
    return new Cursor(State.ACTIVE, x, y, width, height, fg, bg, t - t2);
  }
  
  Cursor adjust(int t) {
    return new Cursor(State.ADJUSTING, x, y, width, height, fg, bg, t - t2);
  }
}
