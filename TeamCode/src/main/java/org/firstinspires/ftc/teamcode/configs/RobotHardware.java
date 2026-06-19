package org.firstinspires.ftc.teamcode.configs;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

// all hardware goes HERE!!! if wiring changes, only this file needs updating.
public class RobotHardware {

    public DcMotor frontLeft, frontRight, backLeft, backRight;

    public DcMotor lazySusan;
    public DcMotor flywheel;
    public DcMotor feederMotor;
    public DcMotor intake;

    public IMU imu;

    public WebcamName vision;

    public void init(HardwareMap hardwareMap) {
        // drive motors
        frontLeft  = hardwareMap.get(DcMotor.class, "flDrive");
        frontRight = hardwareMap.get(DcMotor.class, "frDrive");
        backLeft   = hardwareMap.get(DcMotor.class, "blDrive");
        backRight  = hardwareMap.get(DcMotor.class, "brDrive");

        // turret
        lazySusan = hardwareMap.get(DcMotor.class, "turret");

        // shooter
        flywheel = hardwareMap.get(DcMotor.class, "flywheel");

        feederMotor = hardwareMap.get(DcMotor.class, "feederMotor");

        // intake
        intake = hardwareMap.get(DcMotor.class, "intake");

        // imu (change orientation based on final pos, but lowk have no clue where itll be)
        imu = hardwareMap.get(IMU.class, "imu");

        vision = hardwareMap.get(WebcamName.class, "webcam");
    }
}
