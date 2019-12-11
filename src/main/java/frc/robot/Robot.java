package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;

final class Robot extends TimedRobot {

	public static void main(String... args) {
		RobotBase.startRobot(Robot::new);
	}

	// ===== ROBOT MECHANISMS =====
	private final OI m_OI = OI.m_instance;
	private final Drivebase m_drives = Drivebase.m_instance;

	@Override
	public void teleopPeriodic() {
		m_OI.teleop();
	}

}
