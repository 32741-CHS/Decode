package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcontroller.external.samples.ConceptScanServo;

@TeleOp
public class servo extends OpMode {
    public ConceptScanServo servo;
    public Servo servo2;

    @Override
    public void init() {
        servo2 = hardwareMap.get(Servo.class, "servo");
        servo2 = hardwareMap.get(ConceptScanServo.class, "servo");
//        servo = hardwareMap.get(CRServo.class, "servo");
    }

    @Override
    public void loop() {
        if(gamepad1.a){
            servo2.setPosition(0);
//            servo.setPower(1);
        }
        if(gamepad1.b){
            servo2.setPosition(1);
//            servo.setPower(-1);
        }
//        servo.setPower(0);
    }
}