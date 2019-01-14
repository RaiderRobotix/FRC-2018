package org.usfirst.frc.team25.robot;

import edu.wpi.first.wpilibj.Joystick;

public final class OI {

	// ===== Robot Mechanisms =====
	private final Drivebase m_drives;
	private final Elevator m_elevator;
	private final Grabber m_grabber;
	// private final Climber m_climber;

	// ===== Joysticks =====
	private final Joystick m_leftStick;
	private final Joystick m_rightStick;
	private final Joystick m_operatorStick;

	// ===== Automatic Constants =====
	public boolean m_presetDone = true;
	private double m_liftHeight;

	private static OI m_instance;

	public static OI getInstance() {
		if (m_instance == null) {
			m_instance = new OI();
		}
		return m_instance;
	}

	private OI() {
		m_drives = Drivebase.getInstance();
		m_elevator = Elevator.getInstance();
		m_grabber = Grabber.getInstance();
		// m_climber = Climber.getInstance();

		m_leftStick = new Joystick(Constants.LEFT_JOYSTICK_PORT);
		m_rightStick = new Joystick(Constants.RIGHT_JOYSTICK_PORT);
		m_operatorStick = new Joystick(Constants.OPERATOR_JOYSTICK_PORT);
	}

	public void teleop() {
		// =========== RESETS ===========
		if (getRightButton(8)) {
			m_drives.resetSensors();
		}

		if (getOperatorButton(8)) {
			m_elevator.resetEncoder();
		}

		// =========== DRIVES ===========
		if (getLeftTrigger()) {
			m_drives.brakesOn();
		} else if (getLeftButton(2)) {
			m_drives.brakesOff();
		}

		if (!m_drives.brakesAreOn()) {
			m_drives.setSpeed(getLeftY(), getRightY());
		} else {
			m_drives.setSpeed(0.0);
		}

		// // ========== ELEVATOR ==========
		/*
		 * if (getOperatorButton(3)) {
		 * m_elevator.setSpeed(Constants.ELEVATOR_MANUAL_DOWN_RATE); } else if
		 * (getOperatorButton(5)) {
		 * m_elevator.setSpeed(Constants.ELEVATOR_MANUAL_UP_RATE); } else {
		 * m_elevator.setSpeed(0); }
		 */
		if (this.getOperatorY() > Constants.JOYSTICK_DEADBAND
				&& (m_elevator.getHeight() > Constants.ELEVATOR_LOWER_LIMIT
						|| getOperatorButton(Constants.OPERATOR_OVERRIDE_BUTTON))) {
			m_presetDone = true;
			m_elevator.setSpeed(this.getOperatorY() * Constants.ELEVATOR_MANUAL_DOWN_RATE * -1.0);
		} else if (this.getOperatorY() < -1.0 * Constants.JOYSTICK_DEADBAND
				&& (m_elevator.getHeight() < Constants.ELEVATOR_UPPER_LIMIT
						|| getOperatorButton(Constants.OPERATOR_OVERRIDE_BUTTON))) {
			m_presetDone = true;
			m_elevator.setSpeed(this.getOperatorY() * Constants.ELEVATOR_MANUAL_UP_RATE * -1.0);
		} else if (getOperatorButton(12)) { // Switch height preset
			m_presetDone = false;
			m_liftHeight = 40.0;
		} else if (getOperatorButton(9)) { // Scale height
			m_presetDone = false;
			m_liftHeight = 72.0;
		} else if (getOperatorButton(11)) { // Bottom
			m_presetDone = false;
			m_liftHeight = 1.0;
		} else if (!m_presetDone) { // Moving Automatically
			m_presetDone = m_elevator.goToHeight(m_liftHeight);
		} else {
			m_elevator.setSpeed(0.0);
		}

		// // =========== Tilt ===========
		// if (getOperatorButton(Constants.OPERATOR_OVERRIDE_BUTTON)
		// || m_elevator.encoderValueWithinRange(Constants.ELEVATOR_LOWER_LIMIT,
		// Constants.ELEVATOR_SLOW_RANGE)) {
		if (getOperatorButton(5)) {
			// Straighten
			m_elevator.straighten();
		} else if (getOperatorButton(3) && m_elevator.getHeight() < 30.0) {
			// Tilt
			m_elevator.tilt();
		}
		// }
		//
		// ========== Grabber ==========
		if (getRightButton(11) || (getOperatorButton(Constants.OPERATOR_OVERRIDE_BUTTON) && getOperatorTrigger())) {
			m_grabber.grab();
		} else if (getOperatorButton(2)) {
			m_grabber.release();
		}
		//
		// // ========== Climber=========
		// if (getOperatorButton(4)) {
		// m_climber.setSpeed(-1.0);
		// } else if (getOperatorButton(6)) {
		// m_climber.setSpeed(1.0);
		// } else {
		// m_climber.setSpeed(0.0);
		// }
	}

	public double getLeftY() {
		double yval = -m_leftStick.getY();
		return Math.abs(yval) < Constants.JOYSTICK_DEADBAND ? 0.0 : yval;
	}

	public double getRightY() {
		double yval = -m_rightStick.getY();
		return Math.abs(yval) < Constants.JOYSTICK_DEADBAND ? 0.0 : yval;
	}

	public double getOperatorY() {
		double yval = -m_operatorStick.getY();
		return Math.abs(yval) < Constants.JOYSTICK_DEADBAND ? 0.0 : yval;
	}

	public boolean getOperatorTrigger() {
		return m_operatorStick.getTrigger();
	}

	public boolean getOperatorButton(int btn) {
		return m_operatorStick.getRawButton(btn);
	}

	public boolean getRightTrigger() {
		return m_rightStick.getTrigger();
	}

	public boolean getLeftTrigger() {
		return m_leftStick.getTrigger();
	}

	public boolean getRightButton(int btn) {
		return m_rightStick.getRawButton(btn);
	}

	public boolean getLeftButton(int btn) {
		return m_leftStick.getRawButton(btn);
	}
}
