package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="Murali Opmode")
public class Murali extends LinearOpMode {
    @Override
    public void runOpMode() {


        int declaration = 9;
        int MurMur = 12;
        int tiger = 67;
        int Williamsburg = 89;
        Williamsburg = tiger + MurMur + declaration;
        telemetry .addData("Letter", declaration);
    }
}