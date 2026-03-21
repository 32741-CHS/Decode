package org.firstinspires.ftc.robotcontroller;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="Proctor 0pMode")
public class proctor extends OpMode {
    @Override
    public void init() {

    }

    @Override
    public void loop() {

        int sketchy_van_guy = 10;
        int barcode_scanner = 55;

        int pop_corn = sketchy_van_guy + barcode_scanner;

        telemetry.addData("Number",pop_corn);

    }
}
