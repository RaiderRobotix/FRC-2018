package frc.robot;

interface Constants {

	// Auton Information
	double TIRE_CIRCUMFERENCE = 29.898;
	double COUNTS_PER_REVOLUTION = 128;
	double GEAR_RATIO = 0.0714286; // (Driver: Encoder
									// Gear, Driven: Wheel
									// Gear)	

	// Joysticks
	int LEFT_JOYSTICK_PORT = 0;
	int RIGHT_JOYSTICK_PORT = 1;
	int OPERATOR_JOYSTICK_PORT = 2;
	int SWITCH_BOX_PORT = 3;
	double JOYSTICK_DEADBAND = 0.2;
	
}
