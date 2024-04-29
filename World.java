import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.LinkedList;
import java.util.Map;

abstract public class World {

  private double livingReward = -0.04;
  protected ArrayList<State> states;
  protected HashMap<State,Double> terminals;
  protected long seed = 2026875034;
  protected Random random = new Random( seed );

  public World() { }

  abstract public ArrayList<Action> A( State s );

  abstract public Percept act( State s, Action a );

  public Action getRandomAction( State s ) {
    ArrayList<Action> actions = A( s );
    return actions.get( random.nextInt( actions.size() ) );
  }

  public State getRandomState() {
    State s = states.get( random.nextInt( states.size() ) );
    while ( isTerminal( s ) ) {
      s = states.get( random.nextInt( states.size() ) );
    } // while
    return s;
  }

  public boolean isTerminal( State s ) {
    return terminals.containsKey( s );
  }

  // Used in QLearner.learn to implement the \epsilon-greedy strategy.
  // This isn't great, but it's probably better than QLearner having
  // a separate random-number generator.

  public double nextRandomDouble() {
    return random.nextDouble();
  }

  abstract public LinkedList<Transition> P(State s, Action a );

  public double R( State s ) {
    Double r = terminals.get( s );
    if ( r != null )
      return r.doubleValue();
    else
      return livingReward;
  }

  public ArrayList<State> S() {
    return states;
  }

  public void setLivingReward( double livingReward ) {
    this.livingReward = livingReward;
  }

  public void setSeed( long seed ) {
    this.seed = seed;
    random = new Random( seed );
  }

  public ArrayList<State> getStates(){
    return states;
  }

  public double getLivingReward(){
    return livingReward;
  }

  public HashMap<State,Double> getTerminals(){
    return terminals;
  }

} // World class
