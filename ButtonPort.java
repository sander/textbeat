import processing.core.*;
import processing.serial.*;

class ButtonPort {
  final PApplet parent;
  final Serial port;
  
  boolean pressed = false;
  
  ButtonPort(final PApplet parent, final String portName) {
    this.parent = parent;
    port = new Serial(parent, portName, 19200);
  }
  
  /** Returns true iff bp.pressed has been updated. */
  boolean doStep() {
    int in = -1;
    boolean ns;
    boolean updated;
    while (port.available() > 0) {
      in = (int)port.read();
    }
    if (in == -1) {
      return false;
    }
    ns = in == 1;
    updated = ns != pressed;
    pressed = ns;
    return updated;
  }
}
