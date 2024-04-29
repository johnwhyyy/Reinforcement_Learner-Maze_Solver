import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class GridWorld extends World {

  private char[][] grid;
  private int x; // Number of columns
  private int y; // Number of rows

  public GridWorld() {
    super();
  }

  public GridWorld(String filename) throws FileNotFoundException { 
		try (Scanner scanner = new Scanner(new File(filename))) {
			StringBuilder gridString = new StringBuilder();
			while (scanner.hasNextLine()) {
				gridString.append(scanner.nextLine()).append("\n");
			}

			String[] lines = gridString.toString().split("\n");
      x = lines[0].length();
			y = lines.length;
			grid = new char[x][y]; 
			states = new ArrayList<State>(); 
      terminals = new HashMap<State, Double>();

			for (int i = 0; i < x; i++) { 
				for (int j = 0; j < y; j++) { 
					grid[i][j] = lines[j].charAt(i); 
					if (grid[i][j] == '+') {
						GridState terminalState = new GridState(i, j);
						terminals.put(terminalState, 1.0);
						states.add(terminalState); 
					} 
          else if (grid[i][j] == '-') {
						GridState terminalState = new GridState(i, j);
						terminals.put(terminalState, -1.0);
						states.add(terminalState); 
          }
          else if (grid[i][j] != '%') { 
						states.add(new GridState(i, j)); 
					}
				}
			}
		}
	}

  @Override
    public ArrayList<Action> A( State s ) {
      if (!(s instanceof GridState)) {
        throw new IllegalArgumentException("A GridState object expected.");
      }
      ArrayList<Action> actionsList = new ArrayList<>();
      // If the state is terminal, return an empty list
      if (isTerminal(s)){
        return actionsList;
      }
      GridState gridState = (GridState) s;

      // Check each direction and add action if it's not a wall
      if (canMoveTo(gridState.getX()+1, gridState.getY())){
        actionsList.add(new GridAction("east"));
      }
      if (canMoveTo(gridState.getX()-1, gridState.getY())){
        actionsList.add(new GridAction("west"));
      }
      if (canMoveTo(gridState.getX(), gridState.getY()+1)){
        actionsList.add(new GridAction("north"));
      }

      if (canMoveTo(gridState.getX(), gridState.getY()-1)){
        actionsList.add(new GridAction("south"));
      } 
      return actionsList;
    }

    private boolean canMoveTo(int x, int y) {
      // Check boundaries
      if (x < 0 || x > this.x || y < 0 || y > this.y ) {
        return false;
      }
      // Check wall
      return grid[x][y] != '%';
    }

    /*
    PolicyIteration directly uses the transition model, but QLearner does
    not.  Like A(State), P should generate a probabilistic action even
    if it makes the agent bump into a wall.  In this case, the agent's
    state will not change.
    */
    @Override
    public LinkedList<Transition> P(State s, Action a ){
      LinkedList<Transition> transitions = new LinkedList<>();
      if (a == null) {
        return transitions;
      }
      GridState gridState = (GridState) s;
      GridAction gridAction = (GridAction) a;

      GridState eastState = new GridState(gridState.getX()+1, gridState.getY());
      GridState westState = new GridState(gridState.getX()-1, gridState.getY());
      GridState northState = new GridState(gridState.getX(), gridState.getY()+1);
      GridState southState = new GridState(gridState.getX(), gridState.getY()-1);

      double probCorrectDirection = 0.8;
      double probleftRight = 0.1;

      switch (gridAction.getDirection()) {
        case EAST:
          if (canMoveTo(gridState.getX() + 1, gridState.getY())) {
            transitions.add(new Transition(eastState, probCorrectDirection));
          } 
          else {
            transitions.add(new Transition(gridState, probCorrectDirection));
          }

          // Check for deviations to north and south
          if (canMoveTo(gridState.getX(), gridState.getY()+1)) {
            transitions.add(new Transition(northState, probleftRight));
          } 
          else {
            transitions.add(new Transition(gridState, probleftRight));
          }
          if (canMoveTo(gridState.getX(), gridState.getY()-1)) {
            transitions.add(new Transition(southState, probleftRight));
          } 
          else {
            transitions.add(new Transition(gridState, probleftRight));
          }
          break;
        case WEST:
          if (canMoveTo(gridState.getX()-1, gridState.getY())) {
            transitions.add(new Transition(westState, probCorrectDirection));
          } 
          else {
            transitions.add(new Transition(gridState, probCorrectDirection));
          }
          // Check for deviations to north and south
          if (canMoveTo(gridState.getX(), gridState.getY()+1)) {
            transitions.add(new Transition(northState, probleftRight));
          } 
          else {
            transitions.add(new Transition(gridState, probleftRight));
          }
          if (canMoveTo(gridState.getX(), gridState.getY()-1)) {
            transitions.add(new Transition(southState, probleftRight));
          } else {
            transitions.add(new Transition(gridState, probleftRight));
          }
          break;
        case NORTH:
          if (canMoveTo(gridState.getX(), gridState.getY()+1)) {
            transitions.add(new Transition(northState, probCorrectDirection));
          } else {
            transitions.add(new Transition(gridState, probCorrectDirection));
          }
          // Check for deviations to east and west
          if (canMoveTo(gridState.getX()+1, gridState.getY())) {
            transitions.add(new Transition(eastState, probleftRight));
          } else {
            transitions.add(new Transition(gridState, probleftRight));
          }
          if (canMoveTo(gridState.getX()-1, gridState.getY())) {
            transitions.add(new Transition(westState, probleftRight));
          } else {
            transitions.add(new Transition(gridState, probleftRight));
          }
          break;
        case SOUTH:
          if (canMoveTo(gridState.getX(), gridState.getY()-1)) {
            transitions.add(new Transition(southState, probCorrectDirection));
          } else {
            transitions.add(new Transition(gridState, probCorrectDirection));
          }
          // Check for deviations to east and west
          if (canMoveTo(gridState.getX()+1, gridState.getY())) {
            transitions.add(new Transition(eastState, probleftRight));
          } else {
            transitions.add(new Transition(gridState, probleftRight));
          }
          if (canMoveTo(gridState.getX()-1, gridState.getY() )) {
            transitions.add(new Transition(westState, probleftRight));
          } else {
            transitions.add(new Transition(gridState, probleftRight));
          }
          break;
      }
      return transitions;
    }

    @Override
    public Percept act(State s, Action a) {
      if (!(s instanceof GridState)) {
        throw new IllegalArgumentException("A GridState object expected.");
      }
      if (!(a instanceof GridAction)) {
        throw new IllegalArgumentException("A GridAction object expected.");
      }

      GridState newState = null;
      LinkedList<Transition> possibleTransitions = P(s, a);
      double randomChoice = Math.random(); // Random number between 0 and 1, this act as a random threshold
      double cumulativeProbability = 0.0;

      //Randomly choose a transition based on the probability
      for (Transition transition : possibleTransitions) {
        cumulativeProbability += transition.getProbability();
        if (randomChoice < cumulativeProbability) {
          newState = (GridState) transition.getState();
          break;
        }
      }

      if (isTerminal(newState)) {
        return new Percept(newState, terminals.get(newState));
      } 
      else {
        return new Percept(newState, -0.04);
      }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Grid Layout:\n");
        for (int j = 0; j < y; j++) {
            for (int i = 0; i < x; i++) {
                sb.append(grid[i][j]);
            }
            sb.append("\n");
        }
        sb.append("\nStates: ");
        if (!states.isEmpty()) {
            states.forEach(state -> sb.append(state.toString()).append(", "));
            sb.delete(sb.length() - 2, sb.length()); // Removes the last comma and space
        } else {
            sb.append("No states");
        }
        sb.append("\n\nTerminals: ");
        if (!terminals.isEmpty()) {
            terminals.forEach((state, reward) -> sb.append("{").append(state).append(" = ").append(reward).append("}, "));
            sb.delete(sb.length() - 2, sb.length()); // Removes the last comma and space
        } else {
            sb.append("No terminals");
        }
        return sb.toString();
    }

    public boolean hasContradictions(HashMap<State, Action> pi) {
      boolean hasContradiction = false;
      int contradictionCount = 0;
      for (Map.Entry<State, Action> entry : pi.entrySet()) {
        GridState state = (GridState) entry.getKey();
        GridAction action = (GridAction) entry.getValue();
        if(isTerminal(state)){
          continue;
        }

        // Get the state that this action points to
        GridState adjacentState = (GridState) getAdjacentState(state, action);
        if (adjacentState != null && pi.containsKey(adjacentState) && !adjacentState.equals(state)) {
          // Check if the adjacent state has an action pointing back to the original state
          GridAction reverseAction = (GridAction) pi.get(adjacentState);
          if (reverseAction != null && getAdjacentState(adjacentState, reverseAction).equals(state)) {
            System.out.println("Contradiction found between " + state + " and " + adjacentState);
            System.out.println("Action from " + state + " to " + adjacentState + ": " + action);
            System.out.println("Action from " + adjacentState + " to " + state + ": " + reverseAction+"\n");
            hasContradiction = true; // There's a contradiction
            contradictionCount++;
          }
        }
    }
    if (hasContradiction) {
      System.out.println("Total contradictions found: " + contradictionCount/2);
    }
    return hasContradiction; // No contradictions found
  }

  // Helper method to get the adjacent state based on the action
  private State getAdjacentState(GridState state, GridAction action) {
    switch (action.getDirection()) {
      case NORTH:
          return new GridState(state.getX(), state.getY()+1);
      case SOUTH:
          return new GridState(state.getX(), state.getY()-1);
      case EAST:
          return new GridState(state.getX()+1, state.getY());
      case WEST:
          return new GridState(state.getX()-1, state.getX());
    }
    return null;
  }


    public static void main(String[] args) {
      try {
        GridWorld world = new GridWorld("rnGrid.lay");
        System.out.println(world.toString());

        State initialState = new GridState(1, 1);

        // Check available actions from the initial state
        ArrayList<Action> actions = world.A(initialState);
        System.out.println("Available actions from initial state: "+actions);
        
        System.out.println("");

        LinkedList<Transition> transitions = world.P(initialState, actions.get(0)); 
        System.out.println( transitions );
        System.out.println("");

        // Simulate an action and its outcome
        if (!actions.isEmpty()) {
            Action selectedAction = actions.get(0); // Assume we take the first available action
            System.out.println("Selected Action: " + ((GridAction) selectedAction).getDirection());

            // Perform the action and get the percept
            Percept outcome = world.act(initialState, selectedAction);
            GridState resultingState = (GridState) outcome.getState();
            double reward = outcome.getReward();

            System.out.println("Resulting State: (" + resultingState.getX() + ", " + resultingState.getY() + ")");
            System.out.println("Reward: " + reward);
        }

        System.out.println("Terminal Test for non-terminal: "+ world.isTerminal(new GridState(1, 1)));
        System.out.println("Terminal Test for terminal: "+ world.isTerminal(new GridState(4, 3)));

    } catch (FileNotFoundException e) {
        System.out.println("Error: File not found. " + e.getMessage());
    } catch (Exception e) {
        System.out.println("An error occurred: " + e.getMessage());
        e.printStackTrace();
    }
  }
} // GridWorld class
