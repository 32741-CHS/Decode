package org.firstinspires.ftc.teamcode;

import androidx.annotation.Nullable;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class kPTunner extends OpMode {

    Flywheel flywheel = new Flywheel();

    public double kV = 0.01;
    public double kS = 0.17;
    public double kP =0.0;

    public double goalRPM = 5000;
    double[] increments = {0.000001, 0.00001, 0.0001, 0.001, 0.01};

    int incrementIdx = 2;

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
        if (gamepad1.dpadUpWasPressed()) { kP += currentStep;  }
        if (gamepad1.dpadDownWasPressed()) { kP -= currentStep;  }


        double feedForward = (kV * goalRPM) + kS;
        double error = goalRPM - flywheel.getRPM();
        double feedback = error * kP;


        flywheel.setMotorPower(feedForward + feedback);

        telemetry.addData("Step","%.6f",currentStep);
        telemetry.addData("kP","%.6f", kP);
        telemetry.addData("Error",error);
        telemetry.addData("RPm",flywheel.getRPM());
        telemetry.addData("Ticks per sec",flywheel.getTicksPerSec());

    }
}
