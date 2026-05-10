package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.hardware.bosch.BNO055IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@Autonomous(name = "Thrower Drive Turn", group = "Autonomous")
public class ThrowerDriveTurnAuto extends LinearOpMode {

    private DcMotor leftMotor;
    private DcMotor rightMotor;
    private DcMotor throwerMotor;
    private BNO055IMU imu;
    private Orientation lastAngles;
    private double globalAngle;

    @Override
    public void runOpMode() {
        leftMotor = hardwareMap.dcMotor.get("left_motor");
        rightMotor = hardwareMap.dcMotor.get("right_motor");
        throwerMotor = hardwareMap.dcMotor.get("thrower_motor");
        imu = hardwareMap.get(BNO055IMU.class, "imu");

        leftMotor.setDirection(DcMotor.Direction.REVERSE);
        rightMotor.setDirection(DcMotor.Direction.FORWARD);

        leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        throwerMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json";
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";

        imu.initialize(parameters);

        while (opModeInInit() && !imu.isGyroCalibrated()) {
            sleep(50);
        }

        resetAngle();

        waitForStart();

        if (opModeIsActive()) {
            throwerMotor.setPower(1.0);
            sleep(8000);
            throwerMotor.setPower(0);

            leftMotor.setPower(0.6);
            rightMotor.setPower(0.6);
            sleep(3000);

            leftMotor.setPower(0);
            rightMotor.setPower(0);
            sleep(500);

            gyroTurn(0.4, 90);
        }
    }

    private void gyroTurn(double speed, double targetAngle) {
        resetAngle();

        if (targetAngle > 0) {
            leftMotor.setPower(speed);
            rightMotor.setPower(-speed);
        } else {
            leftMotor.setPower(-speed);
            rightMotor.setPower(speed);
        }

        while (opModeIsActive() && Math.abs(getAngle()) < Math.abs(targetAngle)) {
        }

        leftMotor.setPower(0);
        rightMotor.setPower(0);
        resetAngle();
    }

    private void resetAngle() {
        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        globalAngle = 0;
    }

    private double getAngle() {
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;

        if (deltaAngle < -180) deltaAngle += 360;
        else if (deltaAngle > 180) deltaAngle -= 360;

        globalAngle += deltaAngle;
        lastAngles = angles;
        return globalAngle;
    }
}