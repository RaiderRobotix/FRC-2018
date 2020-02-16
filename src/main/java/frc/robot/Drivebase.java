package frc.robot;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.VictorSP;

final class Drivebase {

	int RIGHT_FRONT_DRIVES_PWM = 1;
	int RIGHT_BACK_DRIVES_PWM = 2;
	int LEFT_FRONT_DRIVES_PWM = 7;
	int LEFT_BACK_DRIVES_PWM = 8;

	private final VictorSP leftFront = new VictorSP(LEFT_FRONT_DRIVES_PWM);
	private final VictorSP leftBack = new VictorSP(LEFT_BACK_DRIVES_PWM);

	private final VictorSP rightFront = new VictorSP(RIGHT_FRONT_DRIVES_PWM);
	private final VictorSP rightBack = new VictorSP(RIGHT_BACK_DRIVES_PWM);

	SpeedControllerGroup left = new SpeedControllerGroup(leftFront, leftBack);
	SpeedControllerGroup right = new SpeedControllerGroup(rightFront, rightBack);

	public static final Drivebase drives = new Drivebase();

	{
		left.setInverted(Constants.LEFT_DRIVE_MOTORS_INVERTED);
		right.setInverted(Constants.RIGHT_DRIVE_MOTORS_INVERTED);
	}

	void speed(double speed) {
		speed(speed, speed);
	}

	void speed(double leftSpeed, double rightSpeed) {
		left.set(leftSpeed);
		right.set(rightSpeed);
	}

}
