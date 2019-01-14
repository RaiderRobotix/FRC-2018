package org.usfirst.frc.team25.Auton;

public interface Move {

	/**
	 * Used to determine what type of Move this is. Helps enforce type safety, and
	 * also keeps ya'll from parsing random Strings as arguments. When you implement
	 * your own move, you must add in your own arguments. See default implementations
	 * for examples
	 */
	public static enum Option { // Feel free to add more
		RESET, DRIVE, TURN, WAIT, STOP, GOTO
	}
	
	public Option getOpt();
	
	public static Reset reset(double distance, double speed) {
		return new Reset();
	}
	
	public static Drive drive(double distance, double speed) {
		return new Drive(distance,speed);
	}

	public static Wait wait(double time) {
		return new Wait(time);
	}

	public static Turn turn(double angle, double speed) {
		return new Turn(angle,speed);
	}
	
	public static Stop stop() {
		return new Stop();
	}
	
	public static Goto gotoo(int mode, int step) {
		return new Goto(mode,step);
	}
	
 	public static class Reset implements Move {
		public final Option opt = Option.RESET;

		public Option getOpt() {
			return opt;
		}
	}

	public static class Drive implements Move {
		public final Option opt = Option.DRIVE;
		public double speed, distance;

		public Drive(double speed, double distance) {
			this.speed = speed;
			this.distance = distance;
		}

		public Option getOpt() {
			return opt;
		}
	}

	public static class Turn implements Move {
		public final Option opt = Option.TURN;

		public double speed, angle;

		public Turn(double angle, double speed) {
			this.speed = speed;
			this.angle = angle;
		}

		public Option getOpt() {
			return opt;
		}
	}

	public static class Wait implements Move {
		public final Option opt = Option.WAIT;
		public double time;

		public Wait(double timeUntil) {
			this.time = timeUntil;
		}

		public Option getOpt() {
			return opt;
		}
	}

	public static class Stop implements Move { //Stupid to have this, but whatever
		public final Option opt = Option.STOP;

		public Option getOpt() {
			return opt;
		}
	}

	public static class Goto implements Move {
		public final Option opt = Option.GOTO;
		
		public int step, mode;
		
		public Goto(int step, int mode) {
			this.step = step;
			this.mode = mode;
		}

		public Option getOpt() {
			return opt;
		}
	}
}
