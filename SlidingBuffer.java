import java.util.Iterator;

final class SlidingBuffer<T> implements Iterable<T> {
  private T[] buffer;
  private int head;
  int length;

  @SuppressWarnings("unchecked")
  SlidingBuffer(int n) {
    buffer = (T[]) new Object[n];
  }

  void add(T v) {
    buffer[(head + length) % buffer.length] = v;
    if (length < buffer.length)
      length++;
    else
      head = (head + 1) % buffer.length;
  }

  final T get(final int i) {
    return buffer[(head + i) % buffer.length];
  }

  public final Iterator<T> iterator() {
    return new Iterator<T>() {
      int i = 0;
      public boolean hasNext() { return i < length; }
      public T next() { return get(i++); }
      public void remove() {}
    };
  }

  public String toString() {
    String s = "SlidingBuffer (head=" + head + ",length=" + length + ") [ ";
    for (T v : this) s += v.toString() + " ";
    s += "]";
    return s;
  }

  public static void main(String[] args) {
    SlidingBuffer<String> b = new SlidingBuffer<String>(3);
    String[] words = {"aap", "noot", "mies", "wim", "zus"};
    for (String s : words) {
      b.add(s);
      System.out.println(b);
    }

    SlidingBuffer<Integer> b2 = new SlidingBuffer<Integer>(2);
    for (int i = 0; i < 5; i++) {
      b2.add(i);
      System.out.println(b2);
    }
  }
}
