package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Muchen extends LinearOpMode {
    @Override
    public void runOpMode() {

        int wierdthing = 60845;
        int anotherthingy = 409095;

        int bothwierdthings = wierdthing + anotherthingy;

        telemetry.addData("hi this is a thing", bothwierdthings);

    }

}
