package org.usfirst.frc.team25.Auton;

public class AutonMode extends java.util.ArrayList<Move> {

	/**
	 * Appease the serialization gods
	 */
	private static final long serialVersionUID = -9054678334744895380L;
	
	public final String name;
	
	public AutonMode(String name, Iterable<Move> moves) {
		this.name = new String(name);
		for (Move m : moves) this.add(m);
	}
	
	public AutonMode(String name, Move... moves) {
		super(java.util.Arrays.asList(moves));
		this.name = new String(name);
		
	}
		
}
