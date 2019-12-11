package frc.robot;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.VictorSP;

final class Drivebase {

	static final Drivebase m_instance = new Drivebase();

	private final VictorSP m_leftDrives = new VictorSP(Constants.LEFT_DRIVES_PWM);
	private final VictorSP m_rightDrives = new VictorSP(Constants.RIGHT_DRIVES_PWM);

	private final Servo m_rightBrake = new Servo(Constants.RIGHT_BRAKE_PWM);
	private final Servo m_leftBrake = new Servo(Constants.LEFT_BRAKE_PWM);

	private boolean m_brakesOn;

	void speed(double speed) {
		speed(speed, speed);
	}

	void speed(double leftSpeed, double rightSpeed) {
		m_leftDrives.set(leftSpeed * (Constants.RIGHT_DRIVE_MOTORS_INVERTED ? -1.0 : 1.0));
		m_rightDrives.set(rightSpeed * (Constants.LEFT_DRIVE_MOTORS_INVERTED ? -1.0 : 1.0));
	}

	void brakes(boolean brakesOn) {
		if (brakesOn) {
			m_brakesOn = true;
			m_leftBrake.set(Constants.LEFT_BRAKES_ON);
			m_rightBrake.set(Constants.RIGHT_BRAKES_ON);
		} else {
			m_brakesOn = false;
			m_leftBrake.set(Constants.LEFT_BRAKES_OFF);
			m_rightBrake.set(Constants.RIGHT_BRAKES_OFF);
		}

	}

	boolean brakes() {
		return m_brakesOn;
	}

	double leftBrake() {
		return m_leftBrake.get();
	}

	double rightBrake() {
		return m_rightBrake.get();
	}

}
