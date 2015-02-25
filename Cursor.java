import processing.core.*;

final class Cursor {
  final int width;
  final int height;
  final int x;
  final int y;
  final int fg;
  final int bg;
  final int ival;

  Cursor(int x, int y, int width, int height, int fg, int bg, int ival) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.fg = fg;
    this.bg = bg;
    this.ival = ival;
  }

  void doDraw(PApplet parent, int c) {
    parent.noStroke();
    parent.fill(c);
    parent.rect(x, y, width, height);
  }

  void doDraw(PApplet parent) {
    doDraw(parent, fg);
  }

  void doDrawFaded(PApplet parent, int t) {
    final int steps = t % ival;
    final int thres2 = (int)(0.5 * ival);
    final int thres1 = (thres2 > 200) ? 200 : (int)((float)(thres2) * 0.25);
    final int c =
      (steps < thres1)
      ? parent.lerpColor(fg, bg, (float)(steps) / thres1)
        : (steps < thres2)
          ? parent.lerpColor(fg, bg, (float)(steps - thres1) / (thres2 - thres1))
            : bg;
    doDraw(parent, c);
  }

  void doClear(PApplet parent) {
    doDraw(parent, bg);
  }

  Cursor place(int x, int y) {
    return new Cursor(x, y, width, height, fg, bg, ival);
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

  Cursor pulsate(int ms) {
    return new Cursor(x, y, width, height, fg, bg, ms);
  }

  Cursor recolor(int c) {
    return new Cursor(x, y, width, height, c, bg, ival);
  }
}
