/*
 * Action.java
 * Copyright (c) 2019 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

public abstract class Action extends Object implements Comparable<Action> {
  public Action() { }
  abstract protected Object clone() throws CloneNotSupportedException;
  abstract public int compareTo( Action a );
  abstract public boolean equals( Object o );
  abstract public int hashCode();
  abstract public String toString();
} // Action class
