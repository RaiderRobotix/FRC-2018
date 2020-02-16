package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

final class OI {

	// ===== Joysticks =====
	private final Joystick m_leftStick = new Joystick(Constants.LEFT_JOYSTICK_PORT);
	private final Joystick m_rightStick = new Joystick(Constants.RIGHT_JOYSTICK_PORT);

	public static final OI oi = new OI();

	double getLeftY() {
		double yval = -m_leftStick.getY();
		return Math.abs(yval) < Constants.JOYSTICK_DEADBAND ? 0.0 : yval;
	}

	double getRightY() {
		double yval = -m_rightStick.getY();
		return Math.abs(yval) < Constants.JOYSTICK_DEADBAND ? 0.0 : yval;
	}
}
