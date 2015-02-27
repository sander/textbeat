import ddf.minim.analysis.*;
import java.util.Arrays;
import processing.core.*;

final class Analyzer {
  final int skipFirst = 1;
  final int fontSize = 16;

  final static class Result {
    final float[] RRbuffer;
    final float RRbufferAvg;
    final float RRbufferMax;

    final float[] spectrum;
    final float spectrumMax;

    final float score;

    Result(float[] RRbuffer, float RRbufferAvg, float RRbufferMax, float[] spectrum, float spectrumMax, float score) {
      this.RRbuffer = RRbuffer;
      this.RRbufferAvg = RRbufferAvg;
      this.RRbufferMax = RRbufferMax;
      this.spectrum = spectrum;
      this.spectrumMax = spectrumMax;
      this.score = score;
    }

    void doDraw(PApplet app, int left, int top, int width, int height) {
      app.pushMatrix();
      app.translate(left, top);

      app.fill(255);
      app.noStroke();
      app.rect(0, 0, width, height);

      app.stroke(255);
      app.fill(0);

      int n = spectrum.length;
      for (int i = 0; i < n; i++) {
        float x = (float)i * width / n;
        float h = spectrum[i] / spectrumMax * (height / 2 - 30);
        float y = height - h;
        float w = (float)width / n;
        app.rect(x, y, w, h);
      }

      n = spectrum.length;
      for (int i = 0; i < RRbuffer.length; i++) {
        float x = (float)i * width / n;
        float h = (float)RRbuffer[i] / RRbufferMax * (height / 2 - 30);
        float y = height / 2 - h;
        float w = (float)width / n;
        app.rect(x, y, w, h);
      }

      app.stroke(0);
      app.line(0, height / 2 - 0.5f, width, height / 2 - 0.5f);
      app.line(0, height - 1, width, height - 1);
      app.textAlign(app.LEFT, app.BOTTOM);
      app.textSize(18);
      app.text("RR (time)", 4, 30);
      app.text("Amplitude (frequency)", 4, height / 2 + 30);
      app.textAlign(app.RIGHT, app.BOTTOM);
      app.text("Score: " + score, width - 4, height / 2 + 30);

      app.popMatrix();
    }
  }

  /** a > b are the fft maxima */
  final float score(final float a, final float b) {
    if (b == 0) return 0f;
    final float factor = .9f;
    float x = (float)Math.log(a / b);
    System.out.println(x);
    return Math.min(1.0f, Math.max(0f, x / 1f));
  }

  final Result calculate(final SlidingBuffer<Integer> RR, final int size) {
    float[] RRbuffer = new float[size];
    float RRbufferAvg = 0f;
    float RRbufferMax = 0f;
    float rate = 0f;
    int i = 0;
    FFT fft;
    float[] spectrum = new float[size];
    float spectrumMax = 0f;
    float spectrumSecondMax = 0f;
    float s;

    for (int j = RR.length; j < size; j++)
      RRbuffer[i++] = 0f;

    for (int val : RR) {
      RRbuffer[i++] = val;
      RRbufferAvg += (float)val / size;
      RRbufferMax = Math.max(RRbufferMax, val);
    }

    rate = (float)1000/RRbufferAvg;
    fft = new FFT(size, rate);
    fft.forward(RRbuffer);

    spectrumMax = 0;
    for (i = skipFirst; i < fft.specSize(); i++) {
      float val = fft.getBand(i);
      if (val > spectrumMax) {
        spectrumSecondMax = spectrumMax;
        spectrumMax = val;
      } else if (val > spectrumSecondMax) {
        spectrumSecondMax = val;
      }
      spectrum[i] = val;
    }

    s = score(spectrumMax, spectrumSecondMax);
    
    return new Result(RRbuffer, RRbufferAvg, RRbufferMax, spectrum, spectrumMax, s);
  }
}
