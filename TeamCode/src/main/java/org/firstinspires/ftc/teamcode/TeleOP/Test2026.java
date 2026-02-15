package org.firstinspires.ftc.teamcode.TeleOp;

import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import android.graphics.Color;

@TeleOp
public class Test2026 extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private MecanumDrive drive;
    private MotorEx frontLeft;
    private MotorEx frontRight;
    private MotorEx backLeft;
    private MotorEx backRight;
    private GamepadEx driveGamepad1;
    private GamepadEx driveGamepad2;
    private IMU imu;
    //PID Poopy
    private PIDController headingPID;
    private double targetHeading = 0;
    // Tune later
    private double kP = 0.02;
    private double kI = 0.0;
    private double kD = 0.002;

    public void initialise() throws InterruptedException {
        drive = new MecanumDrive(frontLeft, frontRight, backLeft, backRight);
        imu = hardwareMap.get(IMU.class, "imu");
        headingPID = new PIDController(kP, kI, kD);
        headingPID.setTolerance(1.5, 5); // 1.5 deg error, 5 deg/sec derivative

    }
    private double getHeading() {
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        return orientation.getYaw(AngleUnit.DEGREES);
    }
    
    @Override
    public void runOpMode() throws InterruptedException {
        initialise();

        waitForStart();

        if (isStopRequested()) return;

        runtime.reset();

        while (opModeIsActive()) {
            if (runtime.milliseconds() > 119000) {
                drive.stop();
                break;
            }
            double speed = 0.8;
            double currentHeading = getHeading();
            if (gamepad1.a) {
                targetHeading = currentHeading;
                headingPID.reset();
            }
            double turnCorrection = headingPID.calculate(currentHeading, targetHeading);
            drive.driveRobotCentric(
                    -driveGamepad1.getLeftX() * speed,
                    -driveGamepad1.getLeftY() * speed,
                    turnCorrection
            );

        }
        }
    }
}