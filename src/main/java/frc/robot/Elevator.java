package frc.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;

public final class Elevator {

	private enum ElevatorRange {
		AT_TARGET, UP_FAR_FROM_TARGET, UP_SCALED_RANGE, UP_NEAR_TARGET, DOWN_FAR_FROM_TARGET, DOWN_NEAR_TARGET
	}

	private static Elevator m_instance;

	private final Spark m_leftElevator = new Spark(Constants.LEFT_ELEVATOR_PWM);
	private final Spark m_rightElevator = new Spark(Constants.RIGHT_ELEVATOR_PWM);
	private final Solenoid m_leftTilt = new Solenoid(Constants.PCM_CAN_ADDRESS, Constants.LEFT_TILT_SOLENOID);
	private final Solenoid m_rightTilt = new Solenoid(Constants.PCM_CAN_ADDRESS, Constants.RIGHT_TILT_SOLENOID);
	private final Encoder m_encoder = new Encoder(Constants.ELEVATOR_ENCODER_PWM_A, Constants.ELEVATOR_ENCODER_PWM_B,
			Constants.ELEVATOR_ENCODER_INVERTED);

	private Elevator() {

		m_leftTilt.set(false);
		m_rightTilt.set(false);

		// TODO: Set encoder distance per pulse
		m_encoder.setDistancePerPulse(Constants.ELEVATOR_INCHES_PER_COUNT);
	}

	public static Elevator getInstance() {
		if (m_instance == null) {
			m_instance = new Elevator();
		}
		return m_instance;
	}

	public boolean encoderValueWithinRange(double targetHeight, double allowedDeviation) {
		return this.getHeight() >= (targetHeight - allowedDeviation)
				&& this.getHeight() <= (targetHeight + allowedDeviation);
	}

	public void setSpeed(double speed) {
		m_leftElevator.set(speed * (Constants.LEFT_ELEVATOR_INVERTED ? -1.0 : 1.0));
		m_rightElevator.set(speed * (Constants.RIGHT_ELEVATOR_INVERTED ? -1.0 : 1.0));
	}

	/**
	 * @return The height of the mast, in inches.
	 */
	public double getHeight() {
		return m_encoder.getDistance() > Constants.ELEVATOR_DOUBLE_HEIGHT_THRESHOLD
				? 2 * m_encoder.getDistance() - Constants.ELEVATOR_DOUBLE_HEIGHT_THRESHOLD
					: m_encoder.getDistance();
	}

	/**
	 * Function to go to desired preset height based on 2011 mast code.
	 * 
	 * @param targetHeight
	 *                         - The target encoder value to go to, height in inches
	 * 
	 * @return true, when complete and stopped; false when mast is still moving
	 */
	public boolean goToHeight(double targetHeight) {
		double currentHeight = this.getHeight();
		boolean movingUp = currentHeight < targetHeight;
		boolean movingDown = currentHeight > targetHeight;
		double positionDelta = 0.0;
		ElevatorRange elevatorRange = ElevatorRange.AT_TARGET;

		// If the elevator needs to change heights, figure out how far away from
		// the target it is
		boolean positionChange = !this.encoderValueWithinRange(targetHeight, Constants.ALLOWED_ELEVATOR_DEVIATION);
		if (positionChange) {
			if (movingUp) {
				positionDelta = targetHeight - currentHeight;

				if (positionDelta > Constants.ELEVATOR_UP_SCALED_RANGE_START) {
					elevatorRange = ElevatorRange.UP_FAR_FROM_TARGET;
				} else if (positionDelta > Constants.ELEVATOR_UP_SCALED_RANGE_END) {
					elevatorRange = ElevatorRange.UP_SCALED_RANGE;
				} else {
					elevatorRange = ElevatorRange.UP_NEAR_TARGET;
				}
			} else if (movingDown) {
				positionDelta = currentHeight - targetHeight;

				if (positionDelta < 16.0) {
					elevatorRange = ElevatorRange.DOWN_NEAR_TARGET;
				} else {
					elevatorRange = ElevatorRange.DOWN_FAR_FROM_TARGET;
				}
			}
		} else {
			elevatorRange = ElevatorRange.AT_TARGET;
		}

		// Set the elevator speed based on its distance from target
		if (elevatorRange == ElevatorRange.AT_TARGET
				|| (movingDown && currentHeight <= Constants.ELEVATOR_LOWER_LIMIT + 1.0)
				|| (movingUp && currentHeight >= Constants.ELEVATOR_UPPER_LIMIT)) {
			this.setSpeed(0.0);
			if (this.getHeight() < Constants.ELEVATOR_LOWER_LIMIT + 1.0) {
				this.resetEncoder();
			}
			return true;
		} else if (movingDown && (currentHeight > (targetHeight + Constants.ALLOWED_ELEVATOR_DEVIATION + 1.0))) {
			if (elevatorRange == ElevatorRange.DOWN_NEAR_TARGET) {
				this.setSpeed(Constants.ELEVATOR_DOWN_SPEED_NEAR_TARGET);
			} else if (elevatorRange == ElevatorRange.DOWN_FAR_FROM_TARGET) {
				this.setSpeed(Constants.ELEVATOR_DOWN_SPEED);
			}
		} else if (movingUp && (currentHeight < (targetHeight - Constants.ALLOWED_ELEVATOR_DEVIATION))) {
			if (elevatorRange == ElevatorRange.UP_FAR_FROM_TARGET) {
				this.setSpeed(0.8);
			} else if (elevatorRange == ElevatorRange.UP_SCALED_RANGE) {
				double scaledSpeed = 0.5 + (0.3 * (positionDelta - Constants.ELEVATOR_UP_SCALED_RANGE_END)
						/ (Constants.ELEVATOR_UP_SCALED_RANGE_START - Constants.ELEVATOR_UP_SCALED_RANGE_END));
				this.setSpeed(scaledSpeed);
			} else if (elevatorRange == ElevatorRange.UP_NEAR_TARGET) {
				this.setSpeed(0.5);
			}
		}
		return false;
	}

	public void tilt() {
		m_leftTilt.set(false);
		m_rightTilt.set(false);
	}

	public void straighten() {
		m_leftTilt.set(true);
		m_rightTilt.set(true);

	}

	public void resetEncoder() {
		m_encoder.reset();
	}
}
