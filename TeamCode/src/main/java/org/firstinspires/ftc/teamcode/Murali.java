package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="Murali Opmode")
public class Murali extends OpMode {

    @Override
    public void init() {

    }

    @Override
    public void loop() {


        int declaration = 9;
        int MurMur = 12;
        int tiger = 67;
        int Williamsburg = 89;
        Williamsburg = tiger + MurMur + declaration;
        telemetry.addData("Letter", declaration);
        }
}