package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class kVTunner extends OpMode {
    Flywheel flywheel = new Flywheel();

    public double kV = 0.01;
    public double kS = 0.17;
    public double goalRPM = 5000;
    double[] increments = {0.000001, 0.00001, 0.0001, 0.001, 0.01};

    int incrementIdx = 4;

    @Override
    public void init() {
        flywheel.init(hardwareMap);
    }

    @Override
    public void loop() {
        if (gamepad1.dpadRightWasPressed() && incrementIdx< 4) {
            incrementIdx++;
        } else if (gamepad1.dpadLeftWasPressed() && incrementIdx > 0) {
            incrementIdx--;
        }

        if(gamepad1.a) {
            goalRPM = 5000 ;
        }

        if(gamepad1.b) {
            goalRPM = 4000;
        }

        double currentStep =increments[incrementIdx];
        if (gamepad1.dpadUpWasPressed()) { kV += currentStep;  }
        if (gamepad1.dpadDownWasPressed()) { kV -= currentStep;  }


        double power = (kV * goalRPM) + kS;
        flywheel.setMotorPower(power);

        telemetry.addData("Step","%.6f",currentStep);
        telemetry.addData("kV","%.6f", kV);
        telemetry.addData("RPm",flywheel.getRPM());
        telemetry.addData("Ticks per sec",flywheel.getTicksPerSec());

    }
}
