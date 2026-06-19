package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;

public class Intake {

    private final DcMotor intake;

    private static double targetPower;
    private static double desiredPower; //TODO find this

    public Intake(RobotHardware hw) {
        intake = hw.intake;
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        intake.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void eat() {
        targetPower = desiredPower;
    }

    public double getPower() {
        return intake.getPower();
    }

    public void update() {
        intake.setPower(targetPower);
        targetPower = 0;
    }
}
