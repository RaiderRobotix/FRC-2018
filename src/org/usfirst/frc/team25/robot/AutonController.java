package org.usfirst.frc.team25.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;

public final class AutonController {

	private static AutonController m_instance;

	private int m_step;
	private final Drivebase m_drives;
	private final Elevator m_elevator;
	private final Grabber m_grabber;
	private final Timer m_timer;
	private final Joystick m_switchBox;

	private AutonController() {
		m_drives = Drivebase.getInstance();
		m_elevator = Elevator.getInstance();
		m_grabber = Grabber.getInstance();
		m_switchBox = new Joystick(Constants.SWITCH_BOX_PORT);
		m_timer = new Timer();
		m_step = 0;
	}

	public static AutonController getInstance() {
		if (m_instance == null) {
			m_instance = new AutonController();
		}
		return m_instance;
	}

	public void resetStep() {
		m_step = 0;
	}

	public void reset() {
		m_timer.start();
		m_timer.reset();
	}

	public void nextStep() {
		this.reset();
		m_step++;
	}

	public void doNothing() {
		m_drives.setSpeed(0.0);
		m_elevator.setSpeed(0.0);
	}

	/**
	 * Drives straight to the switch and drops the cube if the switch is the
	 * proper color.
	 * 
	 * This works for both the right and left sides.
	 * 
	 * If the switch is not the right color, back up and lower the mast to
	 * prevent accidentally dropping the cube during the transition from auto ->
	 * teleop
	 * 
	 * @param dropCube
	 */

	public void goStraight() {
		if (m_step == 0) {
			m_drives.resetSensors();
			this.nextStep();
		} else if (m_step == 1) {
			m_grabber.grab();
			if (m_timer.get() > 0.25) {
				this.nextStep();
			}
		} else if (m_step == 2) {
			m_elevator.straighten();
			this.nextStep();
		} else if (m_step == 3) {
			if (m_drives.driveStraightFast(120.0, 0.35)) {
				this.nextStep();
			}
		} else {
			this.doNothing();
		}
	}

	public void driveStraightTest(double distance) {
		if (m_step == 0) {
			if (m_drives.driveStraightFast(distance, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 1) {
			if (m_drives.turnToAngle(45.0, 0.5)) {
				this.nextStep();
			}
		} else {
			this.doNothing();
		}

	}

	public void turnInPlace(double angle) {
		if (m_step == 0) {
			if (m_drives.turnToAngle(angle, 0.5)) {
				this.nextStep();
			}
		} else {
			this.doNothing();
		}

	}

	public void driveStraightToSwitch_v1(boolean dropCube) {
		System.out.println("Auton step = " + m_step);
		if (m_step == 0) {
			m_drives.resetSensors();
			this.nextStep();
		} else if (m_step == 1) {
			m_grabber.grab();
			if (m_timer.get() > 0.25) {
				this.nextStep();
			}
		} else if (m_step == 2) {
			m_elevator.straighten();
			this.nextStep();
		} else if (m_step == 3) {
			if (m_drives.getAverageEncoderDistance() > 30) {
				m_elevator.goToHeight(40);
			}
			if (m_drives.driveStraight(102, 0.75)) { // TODO: add expiration
														// time
				this.nextStep();
			}
		} else if (m_step == 4) { // Ensure elevator is at proper height
			if (m_elevator.goToHeight(40)) {
				this.nextStep();
			}
		} else if (m_step == 5) {
			if (dropCube) {
				m_grabber.release();
				this.nextStep();
			} else {
				if (m_drives.driveStraight(-28.0, 0.5)) {
					this.nextStep();
				}
			}
		} else if (m_step == 6 && !dropCube) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else {
			this.doNothing();
		}
	}

	public void switchThenScale(boolean leftSwitch, boolean leftScale) {
		if (m_step == 0) {
			m_drives.resetSensors();
			this.nextStep();
		} else if (m_step == 1) {
			m_grabber.grab();
			if (m_timer.get() > 0.15) {
				this.nextStep();
			}
		} else if (m_step == 2) {
			m_elevator.straighten();
			this.nextStep();
		} else if (m_step >= 3) {
			if (leftSwitch && !leftScale) {
				this.centerToLeftSwitch(true);
			} else if (!leftSwitch && leftScale) {
				this.centerToRightSwitch(false);
			} else if (leftSwitch && leftScale) {
				this.leftSwitchLeftScale();
			} else if (!leftSwitch && !leftScale) {
				this.rightSwitchRightScale(leftSwitch);
			} else {
				this.doNothing();
			}
		}
	}

	public void leftSwitchLeftScale() {
		if (m_step == 3) {
			if (m_drives.driveStraightFast(12, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 4) {
			if (m_drives.turnToAngle(-45.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 5) {
			if (m_drives.driveStraightFast(72.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 6) {
			if (m_drives.turnToAngle(43.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 7) {
			m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET);
			if (m_drives.getAverageEncoderDistance() > 26.0) {
				m_grabber.release();
			}
			if (m_drives.driveStraightFast(32.0, 0.5) || m_timer.get() > 3.0) {
				this.nextStep();
			}
		} else if (m_step == 8) { // END SCORING FIRST CUBE
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				if (m_timer.get() >= 0.1) {
					this.nextStep();
				}
			}
		} else if (m_step == 9) { // Placeholder, used to back up 3 inches
			if (m_timer.get() > 0.1) {
				this.nextStep();
			}
		} else if (m_step == 10) {
			if (m_drives.turnToAngle(71.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 11) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 12) {
			if (m_drives.driveStraightFast(42.0, 0.75)) {
				m_grabber.grab();
				this.nextStep();
			}
		} else if (m_step == 13) {
			if (m_timer.get() >= 0.2) {
				this.nextStep();
			}
		} else if (m_step == 14) {
			if (m_drives.driveStraightFast(-120, 0.75)) { // TODO get values
				this.nextStep();
			}
		} else if (m_step == 15) {
			if (m_drives.turnToAngle(75.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 16) {
			if (m_drives.driveStraightFast(-200.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 17) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SCALE_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 18) {
			if (m_drives.turnToAngle(-75.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 19) {
			if (m_drives.driveStraightFast(36.0, 0.3)) {
				m_grabber.release();
				this.nextStep();
			}
		} else if (m_step == 20) {
			if (m_drives.driveStraightFast(-24.0, 0.3)) {
				this.nextStep();
			}
		} else if (m_step == 21) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();

			}
		} else {
			this.doNothing();
		}
	}

	public void rightSwitchRightScale(boolean leftSwitch) {
		if (m_step == 3) {
			if (m_drives.driveStraightFast(12, 0.75)) {

				this.nextStep();
			}
		} else if (m_step == 4) {
			double turnDirection = leftSwitch ? -1.0 : 1.0;
			if (m_drives.turnToAngle(turnDirection * 45.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 5) {
			if (m_drives.driveStraightFast(66.0, 0.75)) { // Was 72
				this.nextStep();
			}
		} else if (m_step == 6) {
			double turnDirection = leftSwitch ? 1.0 : -1.0;
			if (m_drives.turnToAngle(turnDirection * 43.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 7) {
			m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET);
			if (m_drives.driveStraightFast(28.0, 0.4) || m_timer.get() > 3.0) {
				m_grabber.release();
				this.nextStep();
			}
		} else if (m_step == 8) { // END SCORING FIRST CUBE
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				if (m_timer.get() >= 0.5) {
					this.nextStep();
				}
			}
		} else if (m_step == 9) { // Placeholder for if we need to move back
			if (m_timer.get() > 0.25) {
				this.nextStep();

			}
		} else if (m_step == 10) {
			double turnDirection = leftSwitch ? 1.0 : -1.0;
			double turnAngle = leftSwitch ? 70.0 : 76.0; // Used to be 68
			if (m_drives.turnToAngle(turnDirection * turnAngle, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 11) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 12) {
			if (m_drives.driveStraightFast(48.0, 0.5)) {
				m_grabber.grab();
				this.nextStep();
			}
		} else if (m_step == 13) {
			if (m_timer.get() >= 0.2) {
				this.nextStep();
			}
		} else if (m_step == 14) {
			if (m_drives.driveStraightFast(-120, 0.75)) { // TODO get values
				this.nextStep();
			}
		} else if (m_step == 15) {
			if (m_drives.turnToAngle(-75.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 16) {
			if (m_timer.get() > 0.25) {
				m_drives.resetSensors();
				this.nextStep();
			}
		} else if (m_step == 17) {
			if (m_drives.driveStraightFast(-200.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 18) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SCALE_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 19) {
			if (m_drives.turnToAngle(-75.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 20) {
			if (m_drives.driveStraightFast(36.0, 0.3)) {
				m_grabber.release();
				this.nextStep();
			}
		} else if (m_step == 21) {
			if (m_drives.driveStraightFast(-24.0, 0.3)) {
				this.nextStep();
			}
		} else if (m_step == 22) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else {
			this.doNothing();
		}
	}

	public void centerDriveToSwitch(boolean leftSwitch) {
		System.out.println("Auton step = " + m_step);
		if (m_step == 0) {
			m_drives.resetSensors();
			this.nextStep();
		} else if (m_step == 1) {
			m_grabber.grab();
			if (m_timer.get() > 0.25) {
				this.nextStep();
			}
		} else if (m_step == 2) {
			m_elevator.straighten();
			this.nextStep();
		} else if (m_step >= 3) {
			if (leftSwitch) {
				this.centerToLeftSwitch(true);
			} else {
				this.centerToRightSwitch(false);
			}

		} else {
			this.doNothing();
		}

	}

	public void switchFrontAndBack(boolean leftSwitch, boolean leftScale) {
		System.out.println("Auton step = " + m_step);
		if (m_step == 0) {
			m_drives.resetSensors();
			this.nextStep();
		} else if (m_step == 1) {
			m_grabber.grab();
			if (m_timer.get() > 0.25) {
				this.nextStep();
			}
		} else if (m_step == 2) {
			m_elevator.straighten();
			this.nextStep();
		} else if (m_step >= 3) {
			if (leftSwitch) {
				this.centerToLeftSwitch(leftSwitch);
			} else if (!leftSwitch && leftScale) {
				this.rightSwitchFrontAndBack();
			} else {
				this.centerToRightSwitch(leftSwitch);
			}

		} else {
			this.doNothing();
		}

	}

	public void rightSwitchFrontAndBack() {
		if (m_step == 3) {
			if (m_drives.driveStraightFast(12, 0.75)) {

				this.nextStep();
			}
		} else if (m_step == 4) {
			if (m_drives.turnToAngle(45.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 5) {
			if (m_drives.driveStraightFast(66.0, 0.75)) { // Was 72
				this.nextStep();
			}
		} else if (m_step == 6) {
			if (m_drives.turnToAngle(-43.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 7) {
			m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET);
			if (m_drives.driveStraightFast(28.0, 0.4) || m_timer.get() > 3.0) {
				m_grabber.release();
				this.nextStep();
			}
		} else if (m_step == 8) { // END SCORING FIRST CUBE
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				if (m_timer.get() >= 0.5) {
					this.nextStep();
				}
			}
		} else if (m_step == 9) {
			if (m_drives.turnToAngle(-75.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 10) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 11) {
			if (m_drives.driveStraightFast(-48.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 12) {
			if (m_timer.get() > 0.2) {
				if (m_drives.turnToAngle(-75.0, 0.5)) {
					this.nextStep();
				}
			}
		} else if (m_step == 13)

		{
			if (m_timer.get() > 0.2) {
				if (m_drives.driveStraightFast(-90.0, 0.75)) {
					this.nextStep();
				}
			}
		} else if (m_step == 14)

		{
			if (m_drives.turnToAngle(-30.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 15)

		{
			if (m_timer.get() > 0.2) {
				if (m_drives.driveStraightFast(-48.0, 0.75)) {
					this.nextStep();
				}
			}
		} else if (m_step == 16)

		{
			if (m_drives.turnToAngle(40.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 17) {
			if (m_timer.get() > 0.2) {
				if (m_drives.driveStraightFast(20.0, 0.75)) {
					m_grabber.grab();
					this.nextStep();
				}
			}
		} else if (m_step == 18) {
			if (m_timer.get() > 0.5) {
				if (m_drives.driveStraightFast(-6.0, 0.75)) {
					this.nextStep();
				}
			}
		} else if (m_step == 19) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 20) {
			if (m_drives.driveStraightFast(14.0, 0.75)) {
				m_grabber.release();
				this.nextStep();
			}
		} else if (m_step == 21) {
			if (m_timer.get() > 0.1) {
				if (m_drives.turnToAngle(-30.0, 0.5)) {
					this.nextStep();
				}
			}
		} else if (m_step == 22) {
			if (m_drives.driveStraightFast(-36.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 23) {
			if (m_drives.turnToAngle(30.0, 0.5)) {
				this.nextStep();
			}
		} else {
			this.doNothing();
		}
	}

	public void leftSwitchFrontAndBack() {
		if (m_step == 3) {
			if (m_drives.driveStraightFast(12, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 4) {
			if (m_drives.turnToAngle(-45.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 5) {
			if (m_drives.driveStraightFast(72.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 6) {
			if (m_drives.turnToAngle(43.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 7) {
			m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET);
			if (m_drives.driveStraightFast(32.0, 0.4) || m_timer.get() > 3.0) {
				m_grabber.release();
				this.nextStep();
			}
		} else if (m_step == 8) { // END SCORING FIRST CUBE
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				if (m_timer.get() >= 0.5) {
					this.nextStep();
				}
			}
		} else if (m_step == 9) {
			if (m_drives.turnToAngle(75.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 10) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 11) {
			if (m_drives.driveStraightFast(-50.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 12) {
			if (m_timer.get() > 0.2) {
				if (m_drives.turnToAngle(75.0, 0.5)) {
					this.nextStep();
				}
			}
		} else if (m_step == 13) {
			if (m_timer.get() > 0.2) {
				if (m_drives.driveStraightFast(-90.0, 0.75)) {
					this.nextStep();
				}
			}
		} else if (m_step == 14) {
			if (m_drives.turnToAngle(30.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 15) {
			if (m_timer.get() > 0.2) {
				if (m_drives.driveStraightFast(-48.0, 0.75)) {
					this.nextStep();
				}
			}
		} else if (m_step == 16) {
			if (m_drives.turnToAngle(-35.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 17) {
			if (m_drives.driveStraightFast(20.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 18) {
			if (m_timer.get() > 0.1) {
				m_grabber.grab();
				this.nextStep();
			}
		} else if (m_step == 19) {
			if (m_drives.driveStraightFast(8.0, 0.75)) {
				m_grabber.release();
				this.nextStep();
			}
		} else if (m_step == 20) {
			if (m_drives.turnToAngle(30.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 21) {
			if (m_drives.driveStraightFast(-36.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 22) {
			if (m_drives.turnToAngle(-30.0, 0.5)) {
				this.nextStep();
			}
		} else {
			this.nextStep();
		}
	}

	public void centerToLeftSwitch(boolean leftSwitch) {
		if (m_step == 3) {
			if (m_drives.driveStraightFast(12, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 4) {
			double turnDirection = leftSwitch ? -1.0 : 1.0;
			if (m_drives.turnToAngle(turnDirection * 45.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 5) {
			if (m_drives.driveStraightFast(72.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 6) {
			double turnDirection = leftSwitch ? 1.0 : -1.0;
			if (m_drives.turnToAngle(turnDirection * 43.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 7) {
			m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET);
			if (m_drives.driveStraightFast(28.0, 0.4) || m_timer.get() > 1.0) {
				m_grabber.release();
				this.nextStep();
			}
		} else if (m_step == 8) { // END SCORING FIRST CUBE
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				if (m_timer.get() >= 0.5) {
					this.nextStep();
				}
			}
		} else if (m_step == 9) { // Placeholder, used to back up 3 inches
			if (m_timer.get() > 0.1) {
				this.nextStep();
			}
		} else if (m_step == 10) {
			double turnDirection = leftSwitch ? 1.0 : -1.0;
			double turnAngle = leftSwitch ? 83.0 : 68;
			if (m_drives.turnToAngle(turnDirection * turnAngle, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 11) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 12) {
			if (m_drives.driveStraightFast(42.0, 0.5)) {
				m_grabber.grab();
				this.nextStep();
			}
		} else if (m_step == 13) {
			if (m_timer.get() >= 0.2) {
				this.nextStep();
			}
		} else if (m_step == 14) {
			if (m_drives.getAverageEncoderDistance() < -12.0) {
				m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET);
			}
			if (m_drives.driveStraightFast(-36.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 15) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 16) {
			double turnDirection = leftSwitch ? -1.0 : 1.0;
			m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET);
			if (m_drives.turnToAngle(turnDirection * 70.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 17) {
			if (m_timer.get() > 0.1) {
				if (m_drives.driveStraightFast(4.0, 0.5) || m_timer.get() > 3.0) {
					this.nextStep();
				}
			}
		} else if (m_step == 18) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 19) {
			m_grabber.release();
			this.nextStep();
		} else if (m_step == 20) {
			if (m_timer.get() > 1.0) {
				if (m_drives.driveStraightFast(-24.0, 0.4)) {
					this.nextStep();
				}
			}
		} else if (m_step == 21) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else {
			this.doNothing();
		}
	}

	public void centerToRightSwitch(boolean leftSwitch) {
		if (m_step == 3) {
			if (m_drives.driveStraightFast(12, 0.75)) {

				this.nextStep();
			}
		} else if (m_step == 4) {
			double turnDirection = leftSwitch ? -1.0 : 1.0;
			if (m_drives.turnToAngle(turnDirection * 45.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 5) {
			if (m_drives.driveStraightFast(66.0, 0.75)) { // Was 72
				this.nextStep();
			}
		} else if (m_step == 6) {
			double turnDirection = leftSwitch ? 1.0 : -1.0;
			if (m_drives.turnToAngle(turnDirection * 43.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 7) {
			m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET);
			if (m_drives.driveStraightFast(28.0, 0.4) || m_timer.get() > 1.0) {
				m_grabber.release();
				this.nextStep();
			}
		} else if (m_step == 8) { // END SCORING FIRST CUBE
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				if (m_timer.get() >= 0.5) {
					this.nextStep();
				}
			}
		} else if (m_step == 9) { // Placeholder for if we need to move back
			if (m_timer.get() > 0.25) {
				this.nextStep();

			}
		} else if (m_step == 10) {
			double turnDirection = leftSwitch ? 1.0 : -1.0;
			double turnAngle = leftSwitch ? 70.0 : 78.0; // Used to be 68
			if (m_drives.turnToAngle(turnDirection * turnAngle, 0.45)) {
				this.nextStep();
			}
		} else if (m_step == 11) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 12) {
			if (m_drives.driveStraightFast(48.0, 0.5)) {
				m_grabber.grab();
				this.nextStep();
			}
		} else if (m_step == 13) {
			if (m_timer.get() >= 0.3) {
				this.nextStep();
			}
		} else if (m_step == 14) {
			if (m_drives.getAverageEncoderDistance() < -12.0) {
				m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET);
			}
			if (m_drives.driveStraightFast(-42.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 15) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 16) {
			double turnDirection = leftSwitch ? -1.0 : 1.0;
			m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET);
			if (m_drives.turnToAngle(turnDirection * 70.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 17) {
			if (m_timer.get() > 0.1) {
				if (m_drives.driveStraightFast(3.0, 0.5) || m_timer.get() > 3.0) { // TODO
																					// edit
																					// distance?
					this.nextStep();
				}
			}
		} else if (m_step == 18) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 19) {
			m_grabber.release();
			this.nextStep();
		} else if (m_step == 20) {
			if (m_timer.get() > 1.0) {
				if (m_drives.driveStraightFast(-24.0, 0.4)) {
					this.nextStep();
				}
			}
		} else if (m_step == 21) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else {
			this.nextStep();
		}
	}

	public void rightSide(boolean switchLeft, boolean scaleLeft) {
		System.out.println("Auton step = " + m_step);
		if (m_step == 0) {
			m_drives.resetSensors();
			this.nextStep();
		} else if (m_step == 1) {
			m_grabber.grab();
			if (m_timer.get() > 0.25) {
				this.nextStep();
			}
		} else if (m_step == 2) {
			m_elevator.straighten();
			this.nextStep();
		} else if (m_step >= 3) {
			if (!scaleLeft) {
				this.rightSideScale();
			} else if (!switchLeft) {
				this.rightSideSwitch();
			} else {
				this.rightStartToLeft();
			}
		}
	}

	public void rightSideScalePriority(boolean scaleLeft) {
		System.out.println("Currently in: Auton 7 Start");
		System.out.println("Auton step = " + m_step);
		if (m_step == 0) {
			m_drives.resetSensors();
			this.nextStep();
		} else if (m_step == 1) {
			m_grabber.grab();
			if (m_timer.get() > 0.25) {
				this.nextStep();
			}
		} else if (m_step == 2) {
			m_elevator.straighten();
			this.nextStep();
		} else if (m_step >= 3) {
			if (!scaleLeft) {
				this.rightSideScale();
			} else {
				this.rightStartToLeft();
			}
		}
	}

	public void rightSideScaleOnlyPriority(boolean switchLeft, boolean scaleLeft) {
		if (m_step == 0) {
			m_drives.resetSensors();
			this.nextStep();
		} else if (m_step == 1) {
			m_grabber.grab();
			if (m_timer.get() > 0.25) {
				this.nextStep();
			}
		} else if (m_step == 2) {
			m_elevator.straighten();
			this.nextStep();
		} else if (m_step >= 3) {
			if (!scaleLeft) {
				this.rightSideScale();
			} else if (!switchLeft) {
				this.rightSideSwitch();
			} else {
				this.rightToLeftNoScale();
			}
		}
	}

	public void rightSideSwitchPriority(boolean switchLeft, boolean scaleLeft) {
		System.out.println("Auton step = " + m_step);
		if (m_step == 0) {
			m_drives.resetSensors();
			this.nextStep();
		} else if (m_step == 1) {
			m_grabber.grab();
			if (m_timer.get() > 0.25) {
				this.nextStep();
			}
		} else if (m_step == 2) {
			m_elevator.straighten();
			this.nextStep();
		} else if (m_step >= 3) {
			if (!switchLeft) {
				this.rightSideSwitch();
			} else if (!scaleLeft) {
				this.rightSideScale();
			} else {
				this.rightStartToLeft();
			}
		}
	}

	public void rightSideScale() {
		System.out.println("Currently In: Right Side Scale");
		System.out.println("Auton step = " + m_step);
		if (m_step == 3) {
			if (m_timer.get() > 0.2) {
				if (m_elevator.goToHeight(8.0)) {
					this.nextStep();
				}
			}
		} else if (m_step == 4) {
			if (m_drives.driveStraightFast(262, 1)) {
				this.nextStep();
			}
		} else if (m_step == 5) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SCALE_PRESET) || m_timer.get() > 5.0) {
				this.nextStep();
			}
		} else if (m_step == 6) {
			m_elevator.goToHeight(Constants.ELEVATOR_SCALE_PRESET);
			if (m_drives.turnToAngle(-77.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 7) {
			m_elevator.goToHeight(Constants.ELEVATOR_SCALE_PRESET);
			if (m_drives.driveStraightFast(6.0, 0.35)) {
				m_grabber.release();
				this.nextStep();
			}
		} else if (m_step == 8) {
			if (m_timer.get() > 0.75) {
				if (m_drives.driveStraightFast(-24.0, 0.30)) {
					this.nextStep();
				}
			}
		} else if (m_step == 9) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else {
			this.doNothing();
		}
	}

	public void rightSideSwitch() {
		if (m_step == 3) {
			if (m_drives.getAverageEncoderDistance() > 110.0) {
				m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET);
			}
			if (m_drives.driveStraightFast(138.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 4) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 5) {
			m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET);
			if (m_drives.turnToAngle(-75.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 6) {
			m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET);
			if (m_drives.driveStraightFast(22.0, 0.4)) {
				this.nextStep();
			}
		} else if (m_step == 7) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				m_grabber.release();
				this.nextStep();
			}
		} else if (m_step == 8) {
			if (m_timer.get() > 0.25) {
				if (m_drives.driveStraightFast(-8.0, 0.5)) {
					this.nextStep();
				}
			}
		} else if (m_step == 9) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 10) {
			if (m_drives.turnToAngle(-75.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 11) {
			if (m_drives.driveStraightFast(-36.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 12)

		{
			if (m_drives.turnToAngle(-30.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 13)

		{
			if (m_timer.get() > 0.2) {
				if (m_drives.driveStraightFast(-42.0, 0.75)) {
					this.nextStep();
				}
			}
		} else if (m_step == 14)

		{
			if (m_drives.turnToAngle(40.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 15) {
			if (m_timer.get() > 0.2) {
				if (m_drives.driveStraightFast(20.0, 0.75)) {
					m_grabber.grab();
					this.nextStep();
				}
			}
		} else if (m_step == 16) {
			if (m_timer.get() > 0.5) {
				if (m_drives.driveStraightFast(-6.0, 0.75)) {
					this.nextStep();
				}
			}
		} else if (m_step == 17) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 18) {
			if (m_drives.driveStraightFast(14.0, 0.75)) {
				m_grabber.release();
				this.nextStep();
			}
		} else if (m_step == 19) {
			if (m_timer.get() > 0.1) {
				if (m_drives.turnToAngle(-30.0, 0.5)) {
					this.nextStep();
				}
			}
		} else if (m_step == 20) {
			if (m_drives.driveStraightFast(-36.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 21) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 22) {
			if (m_drives.turnToAngle(30.0, 0.5)) {
				this.nextStep();
			}
		} else {
			this.doNothing();
		}
	}

	public void leftToRightNoScale() {
		if (m_step == 3) {
			if (m_drives.driveStraightFast(206.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 4) {
			if (m_drives.turnToAngle(70.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 5) {
			m_drives.resetSensors();
			if (m_timer.get() >= 0.25) {
				this.nextStep();
			}
		} else if (m_step == 6) {
			m_elevator.goToHeight(5.0);
			if (m_drives.driveStraightFast(108.0, 0.75)) {
				this.nextStep();
			}
		} else {
			this.doNothing();
		}
	}

	public void rightToLeftNoScale() {
		if (m_step == 3) {
			if (m_drives.driveStraightFast(206.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 4) {
			if (m_drives.turnToAngle(-70.0, 0.5)) {
				this.nextStep();
			}
		}

		else if (m_step == 5)

		{
			m_drives.resetSensors();
			if (m_timer.get() >= 0.25) {
				this.nextStep();
			}
		} else if (m_step == 6)

		{
			m_elevator.goToHeight(5.0);
			if (m_drives.driveStraightFast(108.0, 0.75)) {
				this.nextStep();
			}
		} /*
			 * else if (m_step == 7)
			 * 
			 * { if (m_drives.turnToAngle(75.0, 0.5)) { this.nextStep(); } }
			 */ else

		{
			this.doNothing();
		}

	}

	public void rightStartToLeft() {
		System.out.println("Auton step = " + m_step);
		System.out.println("Currently In: Right Start to Left Scale");
		if (m_step == 3) {
			if (m_drives.driveStraightFast(218.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 4) {
			if (m_drives.turnToAngle(-73.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 5) {
			m_drives.resetSensors();
			if (m_timer.get() >= 0.25) {
				this.nextStep();
			}
		} else if (m_step == 6) {
			m_elevator.goToHeight(5.0);
			if (m_drives.driveStraightFast(190.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 7) {
			if (m_drives.turnToAngle(78.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 8) {
			m_drives.resetSensors();
			if (m_elevator.goToHeight(Constants.ELEVATOR_SCALE_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 9) {
			m_elevator.goToHeight(Constants.ELEVATOR_SCALE_PRESET);
			if (m_drives.driveStraightFast(40.0, 0.35)) {
				m_grabber.release();
				this.nextStep();
			}
		} else if (m_step == 10) {
			if (m_timer.get() > 0.5) {
				if (m_drives.driveStraightFast(-36.0, 0.35)) {
					this.nextStep();
				}
			}
		} else if (m_step == 11) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else {
			this.doNothing();
		}
	}

	public void leftSideSwitchPriority(boolean switchLeft, boolean scaleLeft) {
		System.out.println("Auton step = " + m_step);
		if (m_step == 0) {
			m_drives.resetSensors();
			this.nextStep();
		} else if (m_step == 1) {
			m_grabber.grab();
			if (m_timer.get() > 0.25) {
				this.nextStep();
			}
		} else if (m_step == 2) {
			if (switchLeft || scaleLeft) {
				m_elevator.straighten();
				this.nextStep();
			}
		} else if (m_step >= 3) {
			if (switchLeft) {
				this.leftSwitchBack();
			} else if (scaleLeft) {
				this.leftSideScale();
			} else {
				this.leftSideBackboard();
			}
		}
	}

	public void leftSide(boolean switchLeft, boolean scaleLeft) {
		if (m_step == 0) {
			m_drives.resetSensors();
			this.nextStep();
		} else if (m_step == 1) {
			m_grabber.grab();
			if (m_timer.get() > 0.25) {
				this.nextStep();
			}
		} else if (m_step == 2) {
			m_elevator.straighten();
			this.nextStep();
		} else if (m_step >= 3) {
			if (scaleLeft) {
				this.leftSideScale();
			} else if (switchLeft) {
				this.leftSwitchBack();
			} else {
				this.leftToRightNoScale();
			}
		}
	}
	
	public void leftSideSimple(boolean switchLeft, boolean scaleLeft) {
		if (m_step == 0) {
			m_drives.resetSensors();
			this.nextStep();
		} else if (m_step == 1) {
			m_grabber.grab();
			if (m_timer.get() > 0.25) {
				this.nextStep();
			}
		} else if (m_step == 2) {
			m_elevator.straighten();
			this.nextStep();
		} else if (m_step >= 3) {
			if (scaleLeft) {
				this.leftSideScale();
				//this.leftToRightNoScale();
			} else if (switchLeft) {
				this.leftSideSwitch(); 
				//this.leftToRightNoScale();
			} else {
				this.leftToRightNoScale();
			}
		}
	}

	public void leftSideScalePriority(boolean scaleLeft) {
		System.out.println("Auton step = " + m_step);
		if (m_step == 0) {
			m_drives.resetSensors();
			this.nextStep();
		} else if (m_step == 1) {
			m_grabber.grab();
			if (m_timer.get() > 0.25) {
				this.nextStep();
			}
		} else if (m_step == 2) {
			m_elevator.straighten();
			this.nextStep();
		} else if (m_step >= 3) {
			if (scaleLeft) {
				this.leftSideScale();
			} else {
				this.leftSideBackboard();
			}
		}
	}

	public void leftSidePlayoffs(boolean switchLeft, boolean scaleLeft, boolean farSwitchLeft) {
		System.out.println("Auton step = " + m_step);
		if (m_step == 0) {
			m_drives.resetSensors();
			this.nextStep();
		} else if (m_step == 1) {
			m_grabber.grab();
			if (m_timer.get() > 0.25) {
				this.nextStep();
			}
		} else if (m_step == 2) {
			m_elevator.straighten();
			this.nextStep();
		} else if (m_step >= 3) {
			if ((switchLeft && scaleLeft && farSwitchLeft) || (!switchLeft && scaleLeft && !farSwitchLeft)) {
				this.leftSideScale();
			} else if (!switchLeft && !scaleLeft && !farSwitchLeft) {
				this.crossToRightScale();
			} else if (switchLeft && !scaleLeft && farSwitchLeft) {
				this.leftSwitchBack();
			} else {
				this.doNothing();
			}
		}
	}

	public void leftSwitchBack() {
		if (m_step == 3) {
			if (m_drives.getAverageEncoderDistance() > 110.0) {
				m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET);
			}
			if (m_drives.driveStraightFast(138.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 4) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 5) {
			if (m_drives.turnToAngle(75.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 6) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				if (m_drives.driveStraightFast(26.0, 0.5)) {
					this.nextStep();
				}
			}
		} else if (m_step == 7) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				m_grabber.release();
				this.nextStep();
			}
		} else if (m_step == 8) {
			if (m_drives.driveStraightFast(-8.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 9) {
			if (m_drives.turnToAngle(75.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 10) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 11) {
			if (m_drives.driveStraightFast(-36.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 12) {
			if (m_drives.turnToAngle(30.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 13) {
			if (m_drives.driveStraightFast(-42.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 14) {
			if (m_drives.turnToAngle(-40.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 15) {
			if (m_drives.driveStraightFast(20.0, 0.75)) {
				m_grabber.grab();
				this.nextStep();
			}
		} else if (m_step == 16) {
			if (m_timer.get() > 0.2) {
				if (m_drives.driveStraightFast(-6.0, 0.75)) {
					this.nextStep();
				}
			}
		} else if (m_step == 17) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 18) {
			if (m_drives.driveStraightFast(14.0, 0.75)) {
				m_grabber.release();
				this.nextStep();
			}
		} else if (m_step == 19) {
			if (m_timer.get() > 0.25) {
				if (m_drives.turnToAngle(30.0, 0.5)) {
					this.nextStep();
				}
			}
		} else if (m_step == 20) {
			if (m_drives.driveStraightFast(-36.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 21) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 22) {
			if (m_drives.turnToAngle(-30.0, 0.5)) {
				this.nextStep();
			}
		} /*
			 * else if (m_step == 21) { if (m_drives.driveStraightFast(-30.0,
			 * 0.75)) { this.nextStep(); } } else if (m_step == 22) { if
			 * (m_drives.turnToAngle(-43.0, 0.5)) { this.nextStep(); } } else if
			 * (m_step == 23) { if (m_drives.driveStraightFast(30.0, 0.75)) {
			 * m_grabber.grab(); this.nextStep(); } } else if (m_step == 24) {
			 * if (m_timer.get() > 0.25) { if
			 * (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
			 * this.nextStep(); } } } else if (m_step == 25) { if
			 * (m_drives.driveStraightFast(10.0, 0.5)) { m_grabber.release();
			 * this.nextStep(); } } else if (m_step == 26) { if (m_timer.get() >
			 * 0.25) { if (m_drives.driveStraightFast(-24.0, 0.5)) {
			 * this.nextStep(); } } } else if (m_step == 27) { if
			 * (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
			 * this.nextStep(); } }
			 */ else {
			this.nextStep();
		}
	}

	public void crossToRightScale() {
		if (m_step == 3) {
			if (m_drives.driveStraightFast(12.0, 0.35)) {
				this.nextStep();
			}
		} else if (m_step == 4) {
			if (m_drives.turnToAngle(80.0, 0.25)) {
				this.nextStep();
			}
		} else if (m_step == 5) {
			if (m_drives.driveStraightFast(180.0, 0.35)) {
				this.nextStep();
			}
		} else if (m_step == 6) {
			if (m_drives.turnToAngle(-80.0, 0.25)) {
				this.nextStep();
			}
		} else if (m_step == 7) {
			if (m_drives.driveStraightFast(240, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 8) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SCALE_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 9) {
			if (m_drives.turnToAngle(-75.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 10) {
			if (m_drives.driveStraightFast(6.0, 0.35)) {
				m_grabber.release();
				this.nextStep();
			}
		} else if (m_step == 11) {
			if (m_timer.get() > 0.5) {
				if (m_drives.driveStraightFast(-24.0, 0.35)) {
					this.nextStep();
				}
			}
		} else if (m_step == 12) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else {
			this.doNothing();
		}
	}

	public void leftSideBackboard() {
		System.out.println("Auton step = " + m_step);
		if (m_step == 3) {
			m_drives.resetSensors();
			this.nextStep();
		} else if (m_step == 4) {
			m_grabber.grab();
			if (m_timer.get() > 0.25) {
				this.nextStep();
			}
		} else if (m_step == 5) {
			if (m_drives.driveStraightFast(270.0, 0.8)) { // was 284
				this.nextStep();
			}
		} else if (m_step == 6) {
			if (m_timer.get() > 0.2)
				if (m_drives.turnToAngle(77.0, 0.4)) { // was 75
					this.nextStep();
				}
		} else if (m_step == 7) {
			m_drives.resetSensors();
			if (m_timer.get() >= 0.25) {
				if (m_elevator.goToHeight(5.0)) {
					this.nextStep();
				}
			}
		} else if (m_step == 8) {
			if (m_drives.driveStraightFast(154.0, 0.5)) { // was 120
				this.nextStep();
			}
		} else if (m_step == 9) {
			m_elevator.straighten();
			if (m_timer.get() > 0.25) {
				this.nextStep();
			}
		} else if (m_step == 10) {
			if (m_drives.turnToAngle(20.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 11) {
			if (m_elevator.goToHeight(94.0)) {
				this.nextStep();
			}
		} else if (m_step == 12) {
			if (m_drives.turnToAngle(-48.0, 0.35)) { // was -25 this.nextStep();
			}
		} else if (m_step == 13) {
			m_grabber.release();
			if (m_timer.get() > 0.25) {
				this.nextStep();
			}
		} else if (m_step == 14) {
			if (m_drives.turnToAngle(28.0, 0.35)) {
				this.nextStep();
			}
		} else if (m_step == 15) {
			if (m_drives.getAverageEncoderDistance() < -18.0) {
				m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET);
			}
			if (m_drives.driveStraightFast(-72.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 16) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 17) {
			if (m_drives.turnToAngle(75.0, 0.5)) {
				this.nextStep();
			}
		} else {
			this.doNothing();
		}
	}

	public void leftSideScale() {
		System.out.println("Currently In: Left Side Scale");
		System.out.println("Auton step = " + m_step);
		if (m_step == 3) {
			if (m_timer.get() > 0.2) {
				if (m_elevator.goToHeight(8.0)) {
					this.nextStep();
				}
			}
		} else if (m_step == 4) {
			if (m_drives.driveStraightFast(308.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 5) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SCALE_PRESET) || m_timer.get() > 5.0) {
				this.nextStep();
			}
		} else if (m_step == 6) {
			m_elevator.goToHeight(Constants.ELEVATOR_SCALE_PRESET);
			if (m_drives.turnToAngle(75.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 7) {
			m_elevator.goToHeight(Constants.ELEVATOR_SCALE_PRESET);
			if (m_drives.driveStraightFast(6.0, 0.35) && m_timer.get() > 1.15 ) {
				m_grabber.release();
				this.nextStep();
			}
		} else if (m_step == 8) {
			if (m_timer.get() > 0.75) {
				if (m_drives.driveStraightFast(-24.0, 0.30)) {
					this.nextStep();
				}
			}
		} else if (m_step == 9) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else {
			this.doNothing();
		}
	}

	public void leftSideSwitch() {
		if (m_step == 3) {
			if (m_drives.getAverageEncoderDistance() > 110.0) {
				m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET);
			}
			if (m_drives.driveStraightFast(146.0, 1 )) {
				this.nextStep();
			}
		} else if (m_step == 4) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 5) {
			if (m_drives.turnToAngle(80.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 6) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				if (m_drives.driveStraightFast(22.0, 0.4)) {
					this.nextStep();
				}
			}
		} else if (m_step == 7) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				m_grabber.release();
				this.nextStep();
			}
		} else if (m_step == 8) {
			if (m_drives.driveStraightFast(-24.0, 0.5) || m_timer.get() > 1 ) {
				this.nextStep();
			}
		} else if (m_step == 9) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else {
			this.doNothing();
		}
	}

	public void leftStartToRight() {
		if (m_step == 3) {
			if (m_drives.driveStraightFast(206.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 4) {
			if (m_drives.turnToAngle(73.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 5) {
			m_drives.resetSensors();
			if (m_timer.get() >= 0.25) {
				this.nextStep();
			}
		} else if (m_step == 6) {
			m_elevator.goToHeight(5.0);
			if (m_drives.driveStraightFast(184.0, 0.75)) {
				this.nextStep();
			}
		} else if (m_step == 7) {
			if (m_drives.turnToAngle(-75.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 8) {
			m_drives.resetSensors();
			if (m_elevator.goToHeight(Constants.ELEVATOR_SCALE_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 9) {
			m_elevator.goToHeight(Constants.ELEVATOR_SCALE_PRESET);
			if (m_drives.driveStraightFast(60.0, 0.5)) {
				m_grabber.release();
				this.nextStep();
			}
		} else if (m_step == 10) {
			if (m_timer.get() > 0.5) {
				if (m_drives.driveStraightFast(-36.0, 0.35)) {
					this.nextStep();
				}
			}
		} else if (m_step == 11) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else {
			this.doNothing();
		}
	}

	public void exchange(boolean leftSide) {
		System.out.println("Auton step = " + m_step);
		if (m_step == 0) {
			m_drives.resetSensors();
			this.nextStep();
		} else if (m_step == 1) {
			m_grabber.grab();
			if (m_timer.get() > 0.25) {
				this.nextStep();
			}
		} else if (m_step == 2) {
			m_elevator.straighten();
			this.nextStep();
		} else if (m_step == 3) {
			if (m_drives.driveStraightFast(-42.0, 0.6)) {
				this.nextStep();
			}
		} else if (m_step == 4) {
			if (m_timer.get() > 0.25) {
				this.nextStep();
			}
		} else if (m_step == 5) {
			if (m_drives.turnToAngle(-45.0, 0.4)) {
				this.nextStep();
			}
		} else if (m_step == 6) {
			if (m_timer.get() > 0.25) {
				this.nextStep();
			}
		} else if (m_step == 7) {
			if (m_drives.driveStraightFast(29.0, 0.5) || m_timer.get() > 3.0) {
				this.nextStep();
			}
		} else if (m_step == 8) {
			m_grabber.release();
			if (m_timer.get() > 1.0) {
				this.nextStep();
			}
		} else if (m_step == 9) {
			if (m_drives.driveStraightFast(-85.0, 0.6)) {
				this.nextStep();
			}
		} else if (m_step == 10) {
			if (m_drives.turnToAngle(-75.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 11) {
			if (m_drives.driveStraightFast(24.0, 0.6)) {
				this.nextStep();
			}
		} else if (m_step == 12) {
			if (m_timer.get() > 0.2) {
				m_grabber.grab();
			}
			if (m_timer.get() > 0.3) {
				this.nextStep();
			}
		} else if (m_step == 13) {
			if (m_drives.driveStraightFast(-42.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 14) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_SWITCH_PRESET)) {
				this.nextStep();
			}
		} else if (m_step == 15) {
			if (m_drives.turnToAngle(-75.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 16) {
			if (m_drives.getAverageEncoderDistance() > 8.0) {
				m_grabber.release();
			}
			if (m_drives.driveStraightFast(12.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 17) {
			if (m_timer.get() > 1.0) {
				this.nextStep();
			}
		} else if (m_step == 18) {
			if (m_drives.driveStraightFast(-24.0, 0.5)) {
				this.nextStep();
			}
		} else if (m_step == 19) {
			if (m_elevator.goToHeight(Constants.ELEVATOR_DOWN_PRESET)) {
				this.nextStep();
			}
		} else {
			this.doNothing();
		}
	}

	/**
	 * @param sideLength
	 * 	- the sideLength of the box, in inches
	 */
	public void practiceBox(double sideLength) {
		double speed = 0.75;
		double turnSpeed = 0.75;
		double angle = 90;
		boolean goNext = true;
		System.out.println("Auton step = " + m_step + "\nSpeed" + speed);
		switch (m_step) {
		case 0: m_drives.resetSensors();
			break;
		case 1: goNext = m_drives.driveStraight(sideLength, speed);
			break;
		case 2: goNext = m_drives.turnToAngle(angle, turnSpeed);
			break; 
		case 3: goNext = m_drives.driveStraight(sideLength, speed);
			break;
		case 4: goNext = m_drives.turnToAngle(angle, turnSpeed);
			break; 
		case 5: goNext = m_drives.driveStraight(sideLength, speed);
			break;
		case 6: goNext = m_drives.turnToAngle(angle, turnSpeed);
			break; 
		case 7: goNext = m_drives.driveStraight(sideLength, speed);
			break;
		case 8: goNext = m_drives.turnToAngle(angle, turnSpeed);
			break; 
		default: this.doNothing();
		}
		if (goNext) this.nextStep();
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
			return m_switchBox.getRawButton(5);
		case 2:
			return m_switchBox.getRawButton(12);
		case 3:
			return m_switchBox.getRawButton(7);
		case 4:
			return m_switchBox.getRawButton(11);
		case 5:
			return m_switchBox.getRawButton(6);
		case 6:
			return m_switchBox.getRawButton(8);
		default:
			return false;
		}
	}

}
