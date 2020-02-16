package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;

final class Robot extends TimedRobot {

	public static void main(String... args) {
		RobotBase.startRobot(Robot::new);
	}

	@Override
	public void teleopPeriodic() {
		OI.m_instance.teleop();
	}

}
