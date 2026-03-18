package org.firstinspires.ftc.teamcode.TeleOP;

import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Tools.LimelightController;

@TeleOp(name = "Riley Is tuff, Ryan C Is BUNS", group = "Test")
public class Test2026 extends LinearOpMode {
    
    // Core components
    private final ElapsedTime runtime = new ElapsedTime();
    private MecanumDrive drive;
    private GamepadEx driveGamepad1;
    private LimelightController limelightController;

    private PIDController headingPID;
    private double targetHeading = 0;

    // Use MotorEx to access encoders
    private MotorEx frontLeft, frontRight, backLeft, backRight;

    private static final double Kp = 0.015;
    private static final double Ki = 0.0;
    private static final double Kd = 0.001;

    // Robot physical constants for encoder-based heading
    private static final double TRACK_WIDTH = 16.3; // Distance between left and right wheels in inches
    private static final double WHEEL_DIAMETER = 4.0;
    private static final double TICKS_PER_REV = 537.7;
    private static final double TICKS_PER_INCH = TICKS_PER_REV / (WHEEL_DIAMETER * Math.PI);

    public void initialize() {
        // Initialize motors using MotorEx
        frontLeft = new MotorEx(hardwareMap, "front_left");
        frontRight = new MotorEx(hardwareMap, "front_right");
        backLeft = new MotorEx(hardwareMap, "back_left");
        backRight = new MotorEx(hardwareMap, "back_right");
        
        // Reset encoders
        frontLeft.resetEncoder();
        frontRight.resetEncoder();
        backLeft.resetEncoder();
        backRight.resetEncoder();

        // Initialize the drive base
        drive = new MecanumDrive(frontLeft, frontRight, backLeft, backRight);
        
        // Initialize Gamepad wrapper
        driveGamepad1 = new GamepadEx(gamepad1);
        
        // Initialize PID Controller
        headingPID = new PIDController(Kp, Ki, Kd);
        headingPID.setTolerance(1.5); 

        // Initialize Limelight
        limelightController = new LimelightController(hardwareMap, telemetry);
        
        telemetry.addData("Status", "Hardware Initialized (Encoder Heading)");
    }

    /**
     * Estimates heading using wheel encoders.
     * Heading = (RightDistance - LeftDistance) / TrackWidth
     */
    private double getHeading() {
        // MotorEx.getDistance() uses the distance per pulse if set, 
        // otherwise we manually calculate it from ticks.
        double leftDist = (frontLeft.getCurrentPosition() + backLeft.getCurrentPosition()) / (2.0 * TICKS_PER_INCH);
        double rightDist = (frontRight.getCurrentPosition() + backRight.getCurrentPosition()) / (2.0 * TICKS_PER_INCH);
        
        // Calculate heading in radians then convert to degrees
        double headingRad = (rightDist - leftDist) / TRACK_WIDTH;
        return Math.toDegrees(headingRad);
    }

    @Override
    public void runOpMode() {
        initialize();
        telemetry.update();

        waitForStart();
        runtime.reset();
        
        // Start with the current heading as the target
        targetHeading = getHeading();

        while (opModeIsActive()) {
            if (runtime.milliseconds() > 119000) {
                drive.stop();
                break;
            }

            double currentHeading = getHeading();

            // Press A to reset the target heading and encoders
            if (gamepad1.a) {
                frontLeft.resetEncoder();
                frontRight.resetEncoder();
                backLeft.resetEncoder();
                backRight.resetEncoder();
                targetHeading = 0;
                headingPID.reset();
            }

            // Calculate shortest-path heading error (Angle Wrapping)
            double error = targetHeading - currentHeading;
            while (error > 180)  error -= 360;
            while (error <= -180) error += 360;

            // PID calculates turn correction based on wrapped error
            double turnCorrection = headingPID.calculate(-error, 0);
            turnCorrection = Range.clip(turnCorrection, -0.5, 0.5);

            // Limelight Tracking: ID 24 (or whatever is in range)
            // You can change 19 to the ID you see in telemetry
            boolean tracking = limelightController.track(20, drive);

            if (!tracking) {
                drive.driveRobotCentric(
                        -driveGamepad1.getLeftX(),
                        -driveGamepad1.getLeftY(),
                        -turnCorrection 
                );
            }

            // Telemetry output
            telemetry.addData("Visible IDs", limelightController.getVisibleIDs());
            telemetry.addData("Encoder Heading", "%.1f deg", currentHeading);
            telemetry.addData("Target Heading", "%.1f deg", targetHeading);
            telemetry.addData("Correction", "%.2f", turnCorrection);
            telemetry.addData("Limelight Tracking", tracking);
            telemetry.update();
        }
        limelightController.stop();
    }
}
