import java.util.HashMap;
import java.util.Set;
import java.util.ArrayList;

public class PolicyIteration {

  private int iterations;
  private double gamma = 0.9;
  private HashMap<State,Double> u;  // utility function
  private HashMap<State,Action> pi; // policy

  public PolicyIteration() { 
    u = new HashMap<State,Double>();
    pi = new HashMap<State,Action>();
    iterations = 0;
  }

  public Action pi( State s ) {
    if ( pi == null )
      return null;
    else
      return pi.get( s );
  }

  public void solve(World world) throws Exception {
		boolean unchanged;
		initializePolicy(world);

		do {
      // Policy Evaluation
			unchanged = true;
      u = policyEvaluation(world);

			// Policy Improvement
			for (State s : world.getStates()) {
        // Get the best action and associated utility for the current state
				Action bestAction = pi.get(s);

        double maxUtility = Double.NEGATIVE_INFINITY;
        Action maxAction = null;
				for (Action a : world.A(s)) {
					double expectedUtility = 0.0;
					for (Transition t : world.P(s, a)) {
            /* 
            // If the next state is terminal, the expected utility is the utility of the terminal state
            if (world.isTerminal(t.getState())) {
              expectedUtility += t.getProbability() * world.getTerminals().get(t.getState());
              continue;
            }
            */
            //Bellman update equation for non-terminal states
						expectedUtility += t.getProbability() * u.get( t.getState());
					}
					if (expectedUtility > maxUtility) {
						maxUtility = expectedUtility;
            maxAction = a;
					}
				}
        if (maxAction!=null && !maxAction.equals(bestAction)) {
          unchanged = false;
          pi.put(s, maxAction);
        }
			}
			iterations++;
		} while (!unchanged);
  
    // Set policy to null for terminal states
    for (State s: world.getTerminals().keySet()) {
      pi.put(s, null);
    }
	}

  private void initializePolicy(World world) {
    for (State state : world.getStates()) {
      if (world.isTerminal(state)) {
        u.put(state, world.getTerminals().get(state)); 
        continue;
      }
      pi.put(state, world.getRandomAction(state)); // Randomly initialize policy
      u.put(state, 0.0);  // Initialize utility as 0
    }
  }

  private HashMap<State, Double> policyEvaluation(World world) {
    HashMap<State, Double> uPrime = new HashMap<State, Double>();
    for (State s : world.getStates()) {
      if (world.isTerminal(s)) {
        uPrime.put(s, world.getTerminals().get(s));
      }
      else{
        double actionUtility = 0.0;
        //Bellman update equation
        for (Transition t : world.P(s,  pi.get(s))) {
          actionUtility += t.getProbability() * u.get(t.getState());
        }
        actionUtility =  world.R(s) + gamma * actionUtility;
        uPrime.put(s, actionUtility);
      }
    }
    return uPrime;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append( iterations + " iterations\n" );
    sb.append( "u = " + u );
    sb.append( "\npi = " + pi );
    return sb.toString();
  }

  /**
   * java PolicyIteration rnGrid.lay
   */

  public static void main( String args[] ) {
    try {
      World world = new GridWorld( "rnGrid.lay" );
      System.out.println( world );
      //world.setLivingReward( -0.01 ); // play around with this
      PolicyIteration solution = new PolicyIteration();
      solution.solve( world );
      System.out.println( solution );
      GridWorld gridWorld = (GridWorld) world; 
      System.out.println( "Has contradiction: "+ gridWorld.hasContradictions(solution.pi) );
    } // try
    catch ( Exception e ) {
      System.out.println( e.getMessage() );
      e.printStackTrace();
    } // catch
  }

} // PolicyIteration class
