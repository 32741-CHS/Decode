package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Config
@TeleOp(name = "PID Straight Drive")
public class drive_train extends LinearOpMode {
    private DcMotor leftMotor, rightMotor;
    private BNO055IMU imu;

    // PID Constants - SET THESE BEFORE RUNNING
    public static double Kp = 0.025;   // Proportional
    public static double Ki = 0.001;   // Integral
    public static double Kd = 0.005;   // Derivative

    // PID variables
    private double integralSum = 0;
    private double previousError = 0;
    private double targetHeading = 0;

    @Override
    public void runOpMode() {
        // Initialize motors
        leftMotor = hardwareMap.get(DcMotor.class, "leftMotor");
        rightMotor = hardwareMap.get(DcMotor.class, "rightMotor");

        // SET THESE CORRECTLY FOR YOUR ROBOT!
        leftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        rightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Initialize IMU
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        telemetry.addData("Status", "Ready!");
        telemetry.addData("Controls", "Push forward > 30% for auto-straight");
        telemetry.addData("Right stick", "Manual turning");
        telemetry.update();

        waitForStart();

        // Set target to current heading
        targetHeading = getHeading();
        resetPID();

        while (opModeIsActive()) {
            // Get joystick inputs
            double forwardPower = -gamepad1.left_stick_y;
            double turnInput = gamepad1.right_stick_x;

            // Determine mode
            boolean isManualTurning = Math.abs(turnInput) > 0.1;
            boolean shouldDriveStraight = forwardPower > 0.3 && !isManualTurning;

            double correction = 0;

            if (isManualTurning) {
                // Manual turning mode
                correction = turnInput * 0.7;
                targetHeading = getHeading();  // Reset target
                resetPID();                     // Reset PID terms
                telemetry.addData("Mode", "MANUAL TURN");
            }
            else if (shouldDriveStraight) {
                // PID straight driving mode
                correction = calculatePIDCorrection();
                telemetry.addData("Mode", "PID STRAIGHT");
            }
            else {
                // Normal drive mode
                telemetry.addData("Mode", "NORMAL");
                resetPID(); // Reset when not using PID
            }

            // Calculate motor powers
            double leftPower = forwardPower + correction;
            double rightPower = forwardPower - correction;

            // Limit powers to [-1, 1]
            double maxPower = Math.max(Math.abs(leftPower), Math.abs(rightPower));
            if (maxPower > 1.0) {
                leftPower /= maxPower;
                rightPower /= maxPower;
            }

            // Apply to motors
            leftMotor.setPower(leftPower);
            rightMotor.setPower(rightPower);

            // Display info
            updateTelemetry(forwardPower, turnInput, correction, leftPower, rightPower);
        }

        // Stop motors
        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }

    private double calculatePIDCorrection() {
        double currentHeading = getHeading();
        double error = calculateAngleError(targetHeading, currentHeading);

        // P: Proportional term
        double proportional = Kp * error;

        // I: Integral term
        integralSum += error;
        // Anti-windup
        double maxIntegral = 0.5 / Ki;
        integralSum = Math.max(Math.min(integralSum, maxIntegral), -maxIntegral);
        double integral = Ki * integralSum;

        // D: Derivative term
        double derivative = Kd * (error - previousError);
        previousError = error;

        // Combine PID terms
        double output = proportional + integral + derivative;

        // Limit output
        output = Math.max(Math.min(output, 0.3), -0.3);

        return output;
    }

    private double calculateAngleError(double target, double current) {
        double error = target - current;

        // Normalize error to shortest path (-180 to 180)
        while (error > 180) error -= 360;
        while (error < -180) error += 360;

        return error;
    }

    private void resetPID() {
        integralSum = 0;
        previousError = 0;
    }

    private double getHeading() {
        return imu.getAngularOrientation().firstAngle;
    }

    private void updateTelemetry(double forward, double turn, double correction,
                                 double leftPower, double rightPower) {
        telemetry.addLine("=== CONTROLS ===");
        telemetry.addData("Left Stick (Drive)", "%.2f", forward);
        telemetry.addData("Right Stick (Turn)", "%.2f", turn);

        telemetry.addLine();
        telemetry.addLine("=== ROBOT STATUS ===");
        telemetry.addData("Heading", "%.1f°", getHeading());
        telemetry.addData("Target", "%.1f°", targetHeading);
        telemetry.addData("Correction", "%.3f", correction);

        telemetry.addLine();
        telemetry.addLine("=== MOTOR POWERS ===");
        telemetry.addData("Left", "%.2f", leftPower);
        telemetry.addData("Right", "%.2f", rightPower);

        telemetry.addLine();
        telemetry.addLine("=== PID VALUES ===");
        telemetry.addData("Kp", "%.4f", Kp);
        telemetry.addData("Ki", "%.5f", Ki);
        telemetry.addData("Kd", "%.4f", Kd);

        telemetry.update();
    }
}