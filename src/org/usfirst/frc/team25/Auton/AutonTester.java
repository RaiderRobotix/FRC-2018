package org.usfirst.frc.team25.Auton;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.usfirst.frc.team25.robot.*;

import com.google.gson.Gson;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
/**
 * Parses JSON file into an {@link AutonMode} array
 * Method "auton" actually executes the chosen mode
 * @author Varun Chari
 *
 */
final class AutonTester {

	//Hardware
	private final Drivebase drives = Drivebase.getInstance();
	private final Timer timer = new Timer();
	private final Joystick switchBox = new Joystick(Constants.SWITCH_BOX_PORT);
	
	/**
	 * Array of autonomous modes,  with indices corresponding 
	 * to the order read from file
	 */
	//private AutonMode[] autons;
	private AutonSet autons;
	private int step = 0;
	public int autonChosen = 0;

	/**
	 * Reads from the json file containing Auton 
	 * instructions and uses it to populate an array
	 */
	private AutonTester() {
		try (Scanner in = new Scanner(new FileInputStream("auton.json"))) {
			StringBuilder bd = new StringBuilder(400);
			while (in.hasNextLine())
				bd.append(in.nextLine());
			autons = new Gson().fromJson(bd.toString(), AutonSet.class);
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't find auton.JSON");
			e.printStackTrace();
		}
	}


	/**
	 * 
	 * @param autonChosen the AutonMode that would be liked to be executed.
	 * Should be the return state of the switchbox
	 * The {@link Robot} class must read the switchBox and pass it
	 */
	public void auton() {
		boolean proceed = true;
		if ( step < autons.get(autonChosen).size() ) {
			Move current = autons.get(autonChosen).get(step);
			switch (current.getOpt()) {
			case DRIVE:
				proceed = drives.driveStraight(((Move.Drive)current).distance, ((Move.Drive)current).speed);
				break;
			case STOP:
				this.doNothing();
				break;
			case RESET:
				this.reset();
				drives.resetSensors();
				break;
			case TURN:
				drives.turnToAngle(((Move.Turn)current).angle, ((Move.Turn)current).speed);
				break;
			case WAIT: 
				proceed = timer.get() >= ((Move.Wait)current).time;
				break;
			case GOTO:
				step = ((Move.Goto) current).step - 1; 
				// Substract 1 cuz it will be incremented
				autonChosen = ((Move.Goto) current).mode;
			default:
				break;
			}
			if (proceed) this.nextStep();
		}
	}

	public void reset() {
		step = 0;
	}

	public void nextStep() {
		timer.start();
		timer.reset();
		step++;
	}

	public void doNothing() {
		drives.setSpeed(0.0);
	}

	/**
	 * @return Autonomous mode chosen by switch-box. (in between 0 & 63)
	 */
	public short getAutonChosen() {
		short ret = 0;
		for (int i = 1; i <= 6; i++)
			if (getSwitch(i)) ret += 1 << i - 1 ;
		return ret;
	}

	/**
	 * @param n
	 *            - (int) Switch to check if is flipped on switch-box.
	 * @return True, if flipped
	 */
	public boolean getSwitch(int n) {
		switch (n) {
		case 1:
			return switchBox.getRawButton(5);
		case 2:
			return switchBox.getRawButton(12);
		case 3:
			return switchBox.getRawButton(7);
		case 4:
			return switchBox.getRawButton(11);
		case 5:
			return switchBox.getRawButton(6);
		case 6:
			return switchBox.getRawButton(8);
		default:
			return false;
		}
	}

}
