package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;

public class Feeder {
    private static final double FEED_POWER = 1;

    private final DcMotor motor;

    public Feeder(RobotHardware hw) {
        motor = hw.feeder;
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    public void start() {

        motor.setPower(FEED_POWER);
    }

    public void stop() {

        motor.setPower(0);
    }

    public boolean isRunning() {

        return motor.getPower() > 0;
    }
}
