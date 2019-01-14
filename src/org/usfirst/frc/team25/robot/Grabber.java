package org.usfirst.frc.team25.robot;

import edu.wpi.first.wpilibj.Solenoid;

public class Grabber {
	public static Grabber m_instance;

	private Solenoid m_grabber;
	boolean m_isOpen = false;
	
	public Grabber() {
		m_grabber = new Solenoid(Constants.GRABBER_SOLENOID);
	}
	
	public static Grabber getInstance() {
		if (m_instance == null) {
			m_instance = new Grabber();
		}
		return m_instance;
	}
	
	public void grab()
	{
		m_grabber.set(true);
		m_isOpen = false;
	}
	
	public void release() {
		m_grabber.set(false);
		m_isOpen = true;
	}
	
	public boolean isOpen() {
		return m_isOpen;
	}
	
}
