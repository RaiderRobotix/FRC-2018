package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

final class OI {

	// ===== Robot Mechanisms =====
	private final Drivebase m_drives = Drivebase.m_instance;
	// private final Climber m_climber;

	// ===== Joysticks =====
	private final Joystick m_leftStick = new Joystick(Constants.LEFT_JOYSTICK_PORT);
	private final Joystick m_rightStick = new Joystick(Constants.RIGHT_JOYSTICK_PORT);
	private final Joystick m_operatorStick = new Joystick(Constants.OPERATOR_JOYSTICK_PORT);

	// ===== Automatic Constants =====
	boolean m_presetDone = true;

	static final OI m_instance = new OI();

	void teleop() {

		// =========== DRIVES ===========
		if (getLeftTrigger()) {
			m_drives.brakes();
		} else if (getLeftButton(2)) {
			m_drives.brakes(false);
		}

		if (!m_drives.brakes()) {
			m_drives.speed(getLeftY(), getRightY());
		} else {
			m_drives.speed(0.0);
		}
	}

	double getLeftY() {
		double yval = -m_leftStick.getY();
		return Math.abs(yval) < Constants.JOYSTICK_DEADBAND ? 0.0 : yval;
	}

	double getRightY() {
		double yval = -m_rightStick.getY();
		return Math.abs(yval) < Constants.JOYSTICK_DEADBAND ? 0.0 : yval;
	}

	double getOperatorY() {
		double yval = -m_operatorStick.getY();
		return Math.abs(yval) < Constants.JOYSTICK_DEADBAND ? 0.0 : yval;
	}

	boolean getOperatorTrigger() {
		return m_operatorStick.getTrigger();
	}

	boolean getOperatorButton(int btn) {
		return m_operatorStick.getRawButton(btn);
	}

	boolean getRightTrigger() {
		return m_rightStick.getTrigger();
	}

	boolean getLeftTrigger() {
		return m_leftStick.getTrigger();
	}

	boolean getRightButton(int btn) {
		return m_rightStick.getRawButton(btn);
	}

	boolean getLeftButton(int btn) {
		return m_leftStick.getRawButton(btn);
	}
}
