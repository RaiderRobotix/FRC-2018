package org.usfirst.frc.team25.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Solenoid;

public final class Hanger {
	private static Hanger m_instance;

	private final WPI_TalonSRX m_leftHanger;
	private final WPI_TalonSRX m_rightHanger;
	private final Solenoid m_deployer;
	private boolean m_isDeployed = false;

	private Hanger() {
		m_leftHanger = new WPI_TalonSRX(Constants.LEFT_HANGER_CAN_ADDRESS);
		m_rightHanger = new WPI_TalonSRX(Constants.RIGHT_HANGER_CAN_ADDRESS);
		m_deployer = new Solenoid (Constants.PCM_CAN_ADDRESS, Constants.DEPLOYER_SOLENOID);
	}

	public static Hanger getInstance() {
		if (m_instance == null) {
			m_instance = new Hanger();
		}
		return m_instance;
	}

	public void setSpeed(double speed) {
		m_leftHanger.set(speed * (Constants.LEFT_HANGER_MOTOR_INVERTED ? -1.0 : 1.0));
		m_rightHanger.set(speed * (Constants.RIGHT_HANGER_MOTOR_INVERTED ? -1.0 : 1.0));
	}
	
	public void deployHanger() {
		m_deployer.set(true);
		m_isDeployed = true;
	}

	public boolean isDeployed(){
		return m_isDeployed;
	}
}
