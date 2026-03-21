package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="nate opMode")
public class  adam extends OpMode {

    @Override
   public void init() {

    }

    @Override
    public void loop() {
        int shayshay = 9;
        int mochi = 3;
        int wise_tree = shayshay + mochi;

        telemetry.addData("number", wise_tree);
    }

}

