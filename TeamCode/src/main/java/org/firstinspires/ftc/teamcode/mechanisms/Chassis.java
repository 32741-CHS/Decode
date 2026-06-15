package org.firstinspires.ftc.teamcode.mechanisms;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Chassis {
    private DcMotor flDrive, frDrive, brDrive, blDrive;
    private IMU imu;


    public Chassis() {
        flDrive = hardwareMap.get(DcMotor.class, "flDrive");
        frDrive = hardwareMap.get(DcMotor.class, "frDrive");
        brDrive = hardwareMap.get(DcMotor.class, "brDrive");
        blDrive = hardwareMap.get(DcMotor.class, "blDrive");

        flDrive.setDirection(DcMotor.Direction.REVERSE);
        blDrive.setDirection(DcMotor.Direction.REVERSE);
        frDrive.setDirection(DcMotor.Direction.FORWARD);
        brDrive.setDirection(DcMotor.Direction.FORWARD);

        imu = hardwareMap.get(IMU.class, "imu");
        IMU.Parameters imuParameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
        ));
        imu.initialize(imuParameters);
    }

    public void drive_robot(double y, double x, double rx) {
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double flPower = (y + x + rx) / denominator;
        double blPower = (y - x + rx) / denominator;
        double frPower = (y - x - rx) / denominator;
        double brPower = (y + x - rx) / denominator;

        blDrive.setPower(flPower);
        blDrive.setPower(blPower);
        frDrive.setPower(frPower);
        brDrive.setPower(brPower);
    }

    public void drive_field(double y, double x, double rx) {
        double chassisHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        double rotX = x * Math.cos(-chassisHeading) - y * Math.sin(-chassisHeading);
        double rotY = x * Math.sin(-chassisHeading) + y * Math.cos(-chassisHeading);

        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
        double flPower = (rotY + rotX + rx) / denominator;
        double frPower = (rotY - rotX + rx) / denominator;
        double brPower = (rotY - rotX - rx) / denominator;
        double blPower = (rotY + rotX - rx) / denominator;

        blDrive.setPower(flPower);
        blDrive.setPower(blPower);
        frDrive.setPower(frPower);
        brDrive.setPower(brPower);
    }

}
