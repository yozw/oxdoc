package oxdoc.util;

public class Stopwatch {
  private final long startTime;

  public Stopwatch() {
    startTime = System.nanoTime();
  }

  public long elapsed() {
    return System.nanoTime() - startTime;
  }

  public long elapsedMsec() {
    return Math.round(elapsed() / 1000000.0);
  }
}
