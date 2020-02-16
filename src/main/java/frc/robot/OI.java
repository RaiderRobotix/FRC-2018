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
		m_drives.speed(getLeftY(), getRightY());
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
}
