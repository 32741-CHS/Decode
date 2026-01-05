package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Flywheel PID Tuning")
public class FlywheelPIDTuning extends LinearOpMode {

    private DcMotorEx flywheelMotor;

    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize motor
        flywheelMotor = hardwareMap.get(DcMotorEx.class, "shootingMot");
        flywheelMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        telemetry.addLine("Ready to tune flywheel PID");
        telemetry.update();

        waitForStart();

        // PID constants
        double kP = 0.0;
        double kI = 0.0;
        double kD = 0.0;
        double kF = 0.0;

        final double MOTOR_CPR = 28.0; // encoder counts/rev
        final double MOTOR_PULLEY = 66.0;
        final double WHEEL_PULLEY = 54.0;
        final double MAX_RPM = 6000.0; // motor max

        // Set your target flywheel RPM
        double targetRPM = 6300; // Set Target RPM
        double targetTicksPerSec = targetRPM * (WHEEL_PULLEY / MOTOR_PULLEY) * (MOTOR_CPR / 60.0);

        // Rough initial kF
        kF = 1.0 / MAX_RPM * targetRPM;
        flywheelMotor.setVelocityPIDFCoefficients(kP, kI, kD, kF);

        telemetry.addData("Info", "Starting kF = %.3f", kF);
        telemetry.update();
        sleep(1000);

        // Incrementally tune kP
        boolean stable = false;
        ElapsedTime timer = new ElapsedTime();

        while (opModeIsActive() && !stable) {
            flywheelMotor.setVelocityPIDFCoefficients(kP, kI, kD, kF);

            double actualRPM = flywheelMotor.getVelocity() * 60.0 / MOTOR_CPR * MOTOR_PULLEY / WHEEL_PULLEY;

            telemetry.addData("Target RPM", targetRPM);
            telemetry.addData("Actual RPM", actualRPM);
            telemetry.addData("kP", kP);
            telemetry.addData("kF", kF);
            telemetry.update();

            if (Math.abs(actualRPM - targetRPM) < 50) {
                stable = true;
            } else if (timer.milliseconds() > 500) {
                kP += 0.001; // increment slowly
                timer.reset();
            }

            sleep(20);
        }

        telemetry.addData("Tuning Complete", "kF = %.3f | kP = %.3f", kF, kP);
        telemetry.update();

        // Keep spinning motor to test
        while (opModeIsActive()) {
            flywheelMotor.setVelocity(targetTicksPerSec);
            telemetry.addData("RPM", flywheelMotor.getVelocity() * 60.0 / MOTOR_CPR * MOTOR_PULLEY / WHEEL_PULLEY);
            telemetry.update();
            sleep(50);
        }
    }
}

