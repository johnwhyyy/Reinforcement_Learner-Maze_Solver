/*
 * State.java
 * Copyright (c) 2017 Mark Maloof.  All Rights Reserved.  See LICENSE.
 */

import java.util.Comparator;

public abstract class State extends Object implements Comparable<State> {
  public State() { }
  public State( State s ) { this(); }
  abstract protected Object clone() throws CloneNotSupportedException;
  abstract public int compareTo( State s );
  abstract public boolean equals( Object o );
  abstract public int hashCode();
  abstract public String toString();
} // State class
