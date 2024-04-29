import java.util.Objects;
import java.util.Comparator;

public class GridAction extends Action implements Comparable<Action> {
  enum actionDirection {
    EAST, WEST, NORTH, SOUTH;
  } 

  private actionDirection direction;

  /* 
  constructs a GridAction from a in {"north", "south", "east", "west" } 
  */
  public GridAction( String a ) {
    switch (a) {
      case "north":
        this.direction = actionDirection.NORTH;
        break;
      case "south":
        this.direction = actionDirection.SOUTH;
        break;
      case "east":
        this.direction = actionDirection.EAST;
        break;
      case "west":
        this.direction = actionDirection.WEST;
        break;
      default:
        throw new IllegalArgumentException("Invalid action: " + a);
    }
  }

  public actionDirection getDirection() {
    return direction;
  } 


  @Override
    protected Object clone() throws CloneNotSupportedException {
        return new GridAction(this.direction.toString().toLowerCase());
    }

    @Override
    public int compareTo(Action a) {
      if (a instanceof GridAction) {
        GridAction otherAction = (GridAction) a;
        return this.direction.compareTo(otherAction.getDirection());
      }
      throw new IllegalArgumentException("Cannot compare GridAction with " + a.getClass().getSimpleName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GridAction other = (GridAction) o;
        return direction == other.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction);
    }

    @Override
    public String toString() {
       return direction.toString().toLowerCase();
    }
    

} // GridAction class
