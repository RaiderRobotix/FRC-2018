package frc.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SerialPort.Port;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.VictorSP;

public final class Drivebase {

	private static final Drivebase m_instance = new Drivebase();

	private final VictorSP m_leftDrives = new VictorSP(Constants.LEFT_DRIVES_PWM);
	private final VictorSP m_rightDrives = new VictorSP(Constants.RIGHT_DRIVES_PWM);

	private final Servo m_rightBrake = new Servo(Constants.RIGHT_BRAKE_PWM);
	private final Servo m_leftBrake = new Servo(Constants.LEFT_BRAKE_PWM);

	private final Encoder m_leftEncoder = new Encoder(Constants.LEFT_ENCODER_PWM_A, Constants.LEFT_ENCODER_PWM_B,
			Constants.LEFT_ENCODER_INVERTED);
	// private final Encoder m_rightEncoder = new Encoder(Constants.RIGHT_ENCODER_PWM_A, Constants.RIGHT_ENCODER_PWM_B,
	// Constants.RIGHT_ENCODER_INVERTED);

	private final AHRS m_navX = new AHRS(Port.kMXP);

	private boolean m_brakesOn;
	private boolean m_drivingStep = false;
	private double m_headingYaw = 0.0;

	private Drivebase() {

		m_leftEncoder.setDistancePerPulse(Constants.INCHES_PER_COUNT);
		// m_rightEncoder.setDistancePerPulse(Constants.INCHES_PER_COUNT);

	}

	public static Drivebase get() {
		return m_instance;
	}

	public void speed(double speed) {
		speed(speed, speed);
	}

	public void speed(double leftSpeed, double rightSpeed) {
		m_leftDrives.set(leftSpeed * (Constants.RIGHT_DRIVE_MOTORS_INVERTED ? -1.0 : 1.0));
		m_rightDrives.set(rightSpeed * (Constants.LEFT_DRIVE_MOTORS_INVERTED ? -1.0 : 1.0));
	}

	public void brakes(boolean brakesOn) {
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

	public boolean brakes() {
		return m_brakesOn;
	}

	public double leftBrake() {
		return m_leftBrake.get();
	}

	public double rightBrake() {
		return m_rightBrake.get();
	}

	public double leftEncoderDistance() {
		return m_leftEncoder.getDistance();
	}

	// public double getRightEncoderDistance() {
	// return m_rightEncoder.getDistance();
	// }

	public double averageEncoderDistance() {
		// return (getLeftEncoderDistance() + getRightEncoderDistance()) / 2.0;
		// TODO: fix
		return leftEncoderDistance();
	}

	/**
	 * Have the robot turn to a specific angle at a specified speed. Uses the sign of the angle
	 * 
	 * @param angle
	 *                  - The angle (in degrees) you would like to turn the robot to.
	 * @param speed
	 *                  - The speed at which you would like the robot to turn.
	 * @return True, when complete.
	 */
	public boolean turnToAngle(double angle, double speed) {
		if (!m_drivingStep) {
			brakes(false);
			resetSensors();
			m_drivingStep = true;
		} else {
			speed = Math.copySign(speed, angle);
			speed(speed, -speed);
			if (Math.abs(getGyroAngle() - angle) < Constants.TURN_TOLERANCE) {
				m_drivingStep = false;
				speed(0.0);
			}
		}

		return (!m_drivingStep);
	}

	/**
	 * Have the robot drive to a specific distance at a specified speed. Uses the sign of distance, not the speed
	 * 
	 * @param distance
	 *                     - The distance (in inches) you would like the robot to drive to.
	 * @param speed
	 *                     - The speed at which you would like the robot to drive.
	 * @return True, when complete.
	 */
	public boolean driveStraight(double distance, double speed, boolean slowDown) {
		if (!m_drivingStep) {
			brakes(false);
			resetSensors();
			m_drivingStep = true;
		} else {
			speed = Math.copySign(speed, distance);
			double leftSpeed = speed;
			double rightSpeed = speed;
			if (Math.abs(averageEncoderDistance() - distance) <
			// Keeping expected and observed values
			// within the same absolute value
			// because if they differ in sign,
			// that's more than a coding problem
					Constants.DRIVE_STRAIGHT_DISTANCE_TOLERANCE) {
				speed(0.0);
				m_drivingStep = false;
			} else if (slowDown) { // If within slow range, set to slow speed
				if (averageEncoderDistance() >= distance - Constants.DRIVE_STRAIGHT_SLOW_RANGE)
					setToSlowSpeed(speed > 0.0);
				else if (averageEncoderDistance() >= distance + Constants.DRIVE_STRAIGHT_DISTANCE_TOLERANCE)
					setToSlowSpeed(speed < 0.0);
			} else if (Math.abs(getGyroAngle()) > Constants.VEER_TOLERANCE) {
				// Adjust speeds for in case of veering
				if (getGyroAngle() > 0) { // Too far clockwise
					if (distance > 0)
						leftSpeed -= Constants.DRIVE_SPEED_CORRECTION;
					else
						rightSpeed += Constants.DRIVE_SPEED_CORRECTION;
				} else { // Too far anti-clockwise
					if (distance > 0)
						rightSpeed -= Constants.DRIVE_SPEED_CORRECTION;
					else
						leftSpeed += Constants.DRIVE_SPEED_CORRECTION;
				}
				speed(leftSpeed, rightSpeed);
			}
		}
		return (!m_drivingStep);
	}

	public boolean driveStraight(double distance, double speed) {
		return this.driveStraight(distance, speed, true);
	}

	public boolean driveStraightFast(double distance, double speed) {
		return this.driveStraight(distance, speed, false);
	}

	/**
	 * Slow speeds for auto-driving functions.
	 * 
	 * @param slowDown
	 *                     - True if running forward.
	 */
	public void setToSlowSpeed(boolean slowDown) {
		if (slowDown) {
			speed(Constants.SLOW_SPEED_STRONG, Constants.SLOW_SPEED_WEAK);
		} else {
			speed(-Constants.SLOW_SPEED_WEAK, -Constants.SLOW_SPEED_STRONG);
		}
	}

	public double getGyroAngle() {
		return m_navX.getAngle() - m_headingYaw;
	}

	public void resetSensors() {
		// Put all resets for sensors in the drivebase here.
		m_headingYaw = m_navX.getAngle();
		m_leftEncoder.reset();
		// m_rightEncoder.reset();

	}
}
