// What the Q Learner perceives after taking action A in state S.

public class Percept extends Object {

  private State s;
  private Double r; // reward

  public Percept(State s, Double r) {
    this.s = s;
    this.r = r;
  }

  public State getState() {
    return s;
  }

  public Double getReward() {
    return r;
  }

} // Percept class
