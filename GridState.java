import java.util.Objects;
import java.util.Comparator;
import java.util.ArrayList;

public class GridState extends State implements Comparable<State> {
  private int x;
  private int y;

  public GridState(int x, int y) {
      this.x = x;
      this.y = y;
  }

  public int getX() {
      return x;
  }

  public int getY() {
      return y;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
      return new GridState(this.x, this.y);
  }

  @Override
  public int compareTo(State s) {
      if (s instanceof GridState) {
          GridState otherState = (GridState) s;
          if (this.x == otherState.getX() && this.y == otherState.getY()) {
              return 0;
          }
          return -1;
      }
      throw new IllegalArgumentException("Cannot compare GridState with " + s.getClass().getSimpleName());
  }

  @Override
  public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      GridState gridState = (GridState) o;
      return x == gridState.x && y == gridState.y;
  }

  @Override
  public int hashCode() {
      return Objects.hash(x, y);
  }

  @Override
  public String toString() {
      return "(" + x + ", " + y + ")";
  }
} // GridState class
