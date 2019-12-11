package frc.robot;

interface Constants {

	// Auto-Driving Constants

	// PWMs (Control)
	int RIGHT_BRAKE_PWM = 1;
	int RIGHT_DRIVES_PWM = 0;
	int LEFT_BRAKE_PWM = 7;
	int LEFT_DRIVES_PWM = 8;

	// Brake Positions
	double RIGHT_BRAKES_ON = 0.18;
	double RIGHT_BRAKES_OFF = 1.0;
	double LEFT_BRAKES_ON = 0.63;
	double LEFT_BRAKES_OFF = 0.20;

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

	// Button Constants
	int OPERATOR_OVERRIDE_BUTTON = 7;

	// motors (probably already correct) and encoder
	boolean RIGHT_DRIVE_MOTORS_INVERTED = false;
	boolean LEFT_DRIVE_MOTORS_INVERTED = true;
	
	
}
