package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;

public class Intake {

    private final RobotHardware hw;

    public Intake(RobotHardware hw) {
        this.hw = hw;
        hw.intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    // power > 0 = out
    // power < 0 = in
    public void setPower(double power) {

        hw.intake.setPower(power);
    }

    public void stop() {

        hw.intake.setPower(0);
    }
}
