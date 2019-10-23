package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

public final class OI {

	// ===== Robot Mechanisms =====
	private final Drivebase m_drives = Drivebase.get();
	// private final Climber m_climber;

	// ===== Joysticks =====
	private final Joystick m_leftStick = new Joystick(Constants.LEFT_JOYSTICK_PORT);
	private final Joystick m_rightStick = new Joystick(Constants.RIGHT_JOYSTICK_PORT);
	private final Joystick m_operatorStick = new Joystick(Constants.OPERATOR_JOYSTICK_PORT);

	// ===== Automatic Constants =====
	public boolean m_presetDone = true;

	private static final OI m_instance = new OI();

	public static OI getInstance() {
		return m_instance;
	}

	private OI() {
	}

	public void teleop() {
		// =========== RESETS ===========
		if (getRightButton(8)) {
			m_drives.resetSensors();
		}

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
