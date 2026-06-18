package org.firstinspires.ftc.teamcode.mechanisms;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@Config
public class Turret {
    private DcMotorEx turret;
    public static double kP = 0.001;
    public static double kD = 0.000;
    private double goalXOffset; // calibrate based on camera mount point
    private double lastError = 0;
    public static double angleTolerance = 0.2;
    public static double MAX_POWER = 0.8;
    public static double targetHeading = 0.0;
    private final ElapsedTime timer = new ElapsedTime();

    public void init(HardwareMap hardwareMap) {
        turret = hardwareMap.get(DcMotorEx.class, "turret");
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void resetTimer() {timer.reset();}

    public void slewTo(double desiredHeading) {
        targetHeading = desiredHeading;
    }

    public void update() {
        double deltaTime = timer.seconds();
        timer.reset();

        double error = goalXOffset - targetHeading;
        double pTerm = error * kP;

        double dTerm = 0;
        if (deltaTime > 0) {
            dTerm = ((error - lastError) / deltaTime) * kD;
        }

        double power = 0;
        if (Math.abs(error) < angleTolerance) {
            power = 0;
        } else {
            power = Range.clip(pTerm * dTerm, -MAX_POWER, MAX_POWER);
        }

        turret.setPower(power);
        lastError = error;
    }
}
