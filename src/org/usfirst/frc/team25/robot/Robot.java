package org.usfirst.frc.team25.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
//import edu.wpi.first.wpilibj.PowerDistributionPanel;

public final class Robot extends IterativeRobot { 
	// TODO include all important libraries

	// ===== ROBOT MECHANISMS =====
	private final AutonController m_autonController = AutonController.getInstance();
	private final OI m_OI = OI.getInstance();
	private final Drivebase m_drives = Drivebase.getInstance();
	private final Elevator m_elevator = Elevator.getInstance();
	private final Compressor m_compressor = new Compressor(Constants.PCM_CAN_ADDRESS);
	// private final PowerDistributionPanel m_pdp = new PowerDistributionPanel(Constants.PDP_CAN_ADDRESS);

	// ===== SELECTED AUTONOMOUS MODE ===== 
	private final int m_autonChosen = m_autonController.getAutonChosen();

	public Robot() {

		// TODO: Delete ALL autonomous modes (and old files)stored on the RoboRio
		// for(File i : new
		// File(Constants.AUTON_DATA_RIO_DIRECTORY).listFiles())
		// i.delete();
		
		// ===== RESETS =====
		m_drives.resetSensors();
		m_elevator.resetEncoder();
		m_compressor.setClosedLoopControl(true);
		
	}

	private void update() {
		
		System.out.print("Elevator Height: " + m_elevator.getHeight() 
				+ "\nLeft Encoder: " + m_drives.getLeftEncoderDistance() 
				//+ "\nRight Encoder: " + m_drives.getRightEncoderDistance())
				+ "\nAngle: " + m_drives.getGyroAngle()
				//+ "\nLeft brake: " + m_drives.getLeftBrake()
				//+ "\nRight brake: " + m_drives.getRightBrake()
				);
		
		if (this.isDisabled() || this.isAutonomous())
			System.out.printf("Auton Chosen: %d\n", m_autonChosen);

	}

	@Override
	public void autonomousInit() {
		m_drives.brakesOff();
		m_autonController.resetStep();
		m_drives.resetSensors();
		m_elevator.resetEncoder();
	}

	@Override
	public void autonomousPeriodic() {
		final String gamePositions = 
				DriverStation.getInstance()
				.getGameSpecificMessage();
				
		if (gamePositions.length() > 0 ) { 
			/*Game specific message, returns an empty
			 * string (not null) to signal no value
			 */
			final boolean switchLeft = gamePositions.charAt(0) == 'L';
			final boolean scaleLeft = gamePositions.charAt(1) == 'L';
			final boolean farSwitchLeft = gamePositions.charAt(2) == 'L';
			
			switch (m_autonChosen) {
			case 0:
				m_autonController.doNothing();
				break;
			case 1: // Left Switch
				m_autonController.driveStraightToSwitch_v1(switchLeft);
				break;
			case 2: // Right Switch
				m_autonController.driveStraightToSwitch_v1(!switchLeft);
				break;
			case 3: // Start middle, go to correct switch
				m_autonController.centerDriveToSwitch(switchLeft);
				break;
			case 4: // Right side, scale > switch > other scale
				m_autonController.rightSide(switchLeft, scaleLeft);
				break;
			case 5: // Left side, switch > scale > other scale
				m_autonController.leftSideSwitchPriority(switchLeft, scaleLeft);
				break;
			case 6: // Exchange
				m_autonController.exchange(switchLeft);
				break;
			case 7: // Right side, scale only
				m_autonController.rightSideScalePriority(scaleLeft);
				break;
			case 8: // Left side, scale only
				m_autonController.leftSideScalePriority(scaleLeft);
				break;
			case 9: // Right side, scale > switch > neither
				m_autonController.rightSideScaleOnlyPriority(switchLeft, scaleLeft);
				break;
			case 10:
				m_autonController.switchThenScale(switchLeft, scaleLeft);
				break;
			case 11:
				m_autonController.switchFrontAndBack(switchLeft, scaleLeft);
				break;
			case 12:
				m_autonController.goStraight();
				break;
			case 13:
				m_autonController.leftSidePlayoffs(switchLeft, scaleLeft, farSwitchLeft);
				break;
			case 14:
				m_autonController.rightSideSwitchPriority(switchLeft, scaleLeft);
				break;
			case 15:
				m_autonController.leftSide(switchLeft, scaleLeft);
				break;
			case 16:
				m_autonController.turnInPlace(45.0);
			case 17:
				m_autonController.driveStraightTest(50.0);
			case 18:
				m_autonController.leftSideSimple(switchLeft, scaleLeft);
			case 19:
				m_autonController.practiceBox(15*12);
				break;
			}
		}
		
		update();
	}

	@Override
	public void teleopInit() {
		m_drives.brakesOff();
		m_drives.resetSensors();
		m_elevator.resetEncoder();
	}

	@Override
	public void teleopPeriodic() {
		m_OI.teleop();
		update();
		if (m_elevator.getHeight() < 0.0)
			m_elevator.resetEncoder();
	}

	@Override
	public void disabledPeriodic() {
		update();
	}

	@Override
	public void testPeriodic() {
		m_drives.brakesOff();
		update();
	}

}
