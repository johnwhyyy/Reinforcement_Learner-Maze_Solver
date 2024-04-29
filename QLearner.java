import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class QLearner {

  private int episodes;
  private int iterations;
  private double alpha = 0.7;
  private double epsilon = 0.2; // play around with epsilon
  private double gamma = 0.9;
  private double theta = 10E-5; // threshold of changes for convergence
  private HashMap<State, HashMap<Action,Double>> q;
  private HashMap<State,Action> pi;

  public QLearner() { 
    q = new HashMap<State, HashMap<Action,Double>>();
    pi = new HashMap<State,Action>();
    episodes = 0;
    iterations = 0;
  }

  public QLearner( double epsilon ) {
    this.epsilon = epsilon;
    q = new HashMap<State, HashMap<Action,Double>>();
    pi = new HashMap<State,Action>();
    episodes = 0;
    iterations = 0;
  }

  public void learn( World world ) throws Exception {
    initializeQ( world );
    boolean converged = false;
    int episodeIteration = 0;

    while(!converged){
      State s = world.getRandomState();
      double delta = 0.0; //delta is the maximum change in q-value in an episode
      while ( !world.isTerminal( s ) ) {
        Action a = epsilonGreedy( s, world );
        Percept percept = world.act( s, a );
        State nextState = percept.getState();
        double oldQ = q.get( s ).get( a );
        double maxQ = Double.NEGATIVE_INFINITY;
        // If the next state is terminal, the q-value is 0
        if (world.isTerminal(nextState)) {
          maxQ = world.R(nextState); 
        }
        else{
          for ( Action nextAction : world.A( nextState ) ) {
            double nextQValue = q.get( nextState ).get( nextAction );
            if ( nextQValue > maxQ ) {
              maxQ = nextQValue;
            } 
          }
        }
        double newQ = oldQ + alpha * ( percept.getReward() + gamma * maxQ - oldQ );
        double change = Math.abs(oldQ - newQ);
        // Update delta to find maximum change in q-value in an episode
        if (change > delta) {
          delta = change;
        }
        q.get( s ).put( a, newQ );// Update q-value
        s = nextState;
        iterations++;
        episodeIteration++;
      }
      if (delta < theta && episodeIteration > q.size()) {
        converged = true;
      }
      episodes++;
    }
    derivePi( world );
  }

  private void derivePi( World world ) {
    for ( State s : world.S() ) {
      Action bestAction = null;
      double bestQ = Double.NEGATIVE_INFINITY;
      // If the state is terminal, the policy is null
      if ( world.isTerminal( s ) ) {
        pi.put( s, null );
        continue;
      }
      for ( Action a : world.A( s ) ) {
        double qValue = q.get( s ).get( a );
        if ( qValue > bestQ ) {
          bestQ = qValue;
          bestAction = a;
        } 
      }
      if (bestAction != null) {
        pi.put( s, bestAction );
      }
    }
  }

  private Action epsilonGreedy( State s, World world ) {
    if ( world.nextRandomDouble() < epsilon ) {
      return world.getRandomAction( s );
    } 
    else {
      return maxQAction( s, world );
    } 
  }

  private Action maxQAction( State s, World world ) {
    Action bestAction = null;
    double bestQ = Double.NEGATIVE_INFINITY;
    for ( Action a : world.A( s ) ) {
      double qValue = q.get( s ).get( a );
      if ( qValue > bestQ) {
        bestQ = qValue;
        bestAction = a;
      } 
    }
    return bestAction;
  }

  private void initializeQ( World world ) {
    for ( State s : world.S() ) {
      HashMap<Action,Double> qValues = new HashMap<Action,Double>();
      // If the state is terminal, the q-value is 0
      if ( world.isTerminal( s ) ) {
        qValues.put(null, 0.0);
      }
      // Otherwise, initialize q-values to random values
      else {
        for ( Action a : world.A( s ) ) {
          qValues.put( a, world.nextRandomDouble());
        } 
      }
      q.put(s, qValues);
    } 
  }

  public Action pi( State s ) {
    if ( pi == null )
      return null;
    else
      return pi.get( s );
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append( episodes + " episodes\n" );
    sb.append( iterations + " iterations\n" );
    sb.append( "q = " + q );
    sb.append( "\npi = " + pi );
    return sb.toString();
  }

  public String toPolicyString() {
    StringBuilder sb = new StringBuilder();
    for ( State s : pi.keySet() ) {
      sb.append( s + " : " + pi.get( s ) + "\n" );
    }
    return sb.toString();
  }
  /**
   * java QLearner rnGrid.lay
   */

  public static void main( String args[] ) {
    try {
      World world = new GridWorld( "tinyGrid.lay");
      // world.setLivingReward( -0.01 ); // play around with this
      QLearner qlearner = new QLearner( 0.4 ); // play around with epsilon
      qlearner.learn( world );
      System.out.println( qlearner );
      System.out.println( qlearner.toPolicyString() );
      GridWorld gridWorld = (GridWorld) world; 
      System.out.println( "Has contradiction: "+ gridWorld.hasContradictions(qlearner.pi) );
    } // try
    catch ( Exception e ) {
      System.out.println( e.getMessage() );
      e.printStackTrace();
    } // catch
  }

} // QLearner class
