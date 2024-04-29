/**
 * A transition is a state-probability pair
 */

public class Transition extends Object {

  private State s;
  private Double p;

  public Transition(State s, Double p) {
    this.s = s;
    this.p = p;
  }

  public State getState() {
    return s;
  }

  public Double getProbability() {
    return p;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("<");
    sb.append(s+","+p);
    sb.append(">");
    return sb.toString();
  }

} // Transition class
