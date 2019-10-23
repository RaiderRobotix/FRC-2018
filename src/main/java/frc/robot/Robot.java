package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;

public final class Robot extends TimedRobot {

	// ===== ROBOT MECHANISMS =====
	private final OI m_OI = OI.getInstance();
	private final Drivebase m_drives = Drivebase.get();

	public Robot() {
		m_drives.resetSensors();
	}

	private void update() {
		System.out.print("\nLeft Encoder: " + m_drives.leftEncoderDistance() + "\nAngle: " + m_drives.getGyroAngle());
	}

	@Override
	public void autonomousInit() {
		m_drives.brakes(false);
		m_drives.resetSensors();
	}

	@Override
	public void autonomousPeriodic() {
		update();
	}

	@Override
	public void teleopInit() {
		m_drives.brakes(false);
		m_drives.resetSensors();
	}

	@Override
	public void teleopPeriodic() {
		m_OI.teleop();
		update();
	}

	@Override
	public void disabledPeriodic() {
		update();
	}

	@Override
	public void testPeriodic() {
		m_drives.brakes(false);
		update();
	}

}
