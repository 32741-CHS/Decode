package org.firstinspires.ftc.teamcode.Subsystems;


import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;

import org.firstinspires.ftc.teamcode.RobotContainer;

/** DriveTrain Subsystem */
public class DriveTrain extends SubsystemBase {

    // constants for Tetrix DC Motor
    //final double MAXRPM = 100.0;
    //final double MAXRPS = MAXRPM/60.0;
    //final double MAX_SPEED_TICKS_PER_SEC = MAXRPS * TICKS_PER_ROTATION;

    // motor speed ticks per revolution to m/s travel speed
    final double TICKS_PER_ROTATION = 280;        // encoder pulses per motor revolution
    final double WHEEL_DIA = 0.1;                   // wheel diameter in m
    final double GEAR_RATIO = 12.0;                 // drive gear ratio
    final double TICKSPS_TO_MPS = WHEEL_DIA * Math.PI / TICKS_PER_ROTATION / GEAR_RATIO;


    // create Mecanum drive and its motors
    private MotorEx leftBackDrive = null;
    private MotorEx rightBackDrive = null;


    /** Place code here to initialize subsystem */
    public DriveTrain() {

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        leftBackDrive  = new MotorEx(RobotContainer.ActiveOpMode.hardwareMap,"left_back_drive");
        rightBackDrive = new MotorEx(RobotContainer.ActiveOpMode.hardwareMap,"right_back_drive");

        // for tiny
        leftBackDrive.setInverted(true);
        rightBackDrive.setInverted(false);

        // set motor braking mode
        leftBackDrive.setZeroPowerBehavior(MotorEx.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(MotorEx.ZeroPowerBehavior.BRAKE);

        // distance per pulse in m
        // pi*d / pulse-per-revolution = 3.1415 *0.1m / 28 / 60 (gear ratio)
        final double DistancePerPulse = Math.PI * 0.1 / 28.0 / 60.0;
        leftBackDrive.setDistancePerPulse(DistancePerPulse);
        rightBackDrive.setDistancePerPulse(DistancePerPulse);
    }

    /** Method called periodically by the scheduler
     * Place any code here you wish to have run periodically */
    @Override
    public void periodic() {
    }

    /**
     * drive robot in field coordinates
     *
     * @param Vx - forward/backward velocity, range -1 to 1
     * @param Vy - left/right velocity, range -1 to 1
     * @param Omega - rotation velocity, range -1 to 1
     */
    public void FieldDrive(double Vx, double Vy, double Omega) {
        FieldDrive(Vx, Vy, Omega, 1.0);
    }

    /**
     * drive robot in field coordinates
     *
     * @param Vx - forward/backward velocity, range -1 to 1
     * @param Vy - left/right velocity, range -1 to 1
     * @param Omega - rotation velocity, range -1 to 1
     * @param powerFactor - scaling factor for power, range 0 to 1
     */
    public void FieldDrive(double Vx, double Vy, double Omega, double powerFactor) {

        // get angle of vector rotation angle
        // i.e. neg of gyro angle - in rad
        double rotAngRad = Math.toRadians(RobotContainer.gyro.getYawAngle());

        // rotate speed vector by negative of gyro angle
        double x = Vx * Math.cos(-rotAngRad) - Vy * Math.sin(-rotAngRad);
        double y = Vx * Math.sin(-rotAngRad) + Vy * Math.cos(-rotAngRad);

        // x,y now in robot coordinates - call robot drive
        RobotDrive(x, y, Omega, powerFactor);
    }

    /**
     * drive robot in robot coordinates
     *
     * @param Vx - forward/backward velocity, range -1 to 1
     * @param Vy - left/right velocity, range -1 to 1
     * @param Omega - rotation velocity, range -1 to 1
     */
    public void RobotDrive(double Vx, double Vy, double Omega) {
        RobotDrive(Vx, Vy, Omega, 1.0);
    }

    /**
     * drive robot in robot coordinates
     *
     * @param Vx - forward/backward velocity, range -1 to 1
     * @param Vy - left/right velocity, range -1 to 1
     * @param Omega - rotation velocity, range -1 to 1
     * @param powerFactor - scaling factor for power, range 0 to 1
     */
    public void RobotDrive(double Vx, double Vy, double Omega, double powerFactor) {
        // resolve individual wheel speeds
        double leftBackPower = Vx - Vy - Omega;
        double rightBackPower  = Vx + Vy - Omega;

        // Normalize the values so no wheel power exceeds 100%
        // This ensures that the robot maintains the desired motion.
        double max = Math.abs(leftBackPower);
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftBackPower /= max;
            rightBackPower  /= max;
        }

        leftBackDrive.set(leftBackPower);
        rightBackDrive.set(rightBackPower);
    }


}
