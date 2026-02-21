package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Spindex;
import org.firstinspires.ftc.teamcode.subsystems.Turret;

@TeleOp

public class configTester extends OpMode {

    private Intake intake;
    private Spindex spindex;
    SparkFunOTOS otos;
    private Turret turret;

    private Drivetrain drive;

    public void init(){
        intake = new Intake(hardwareMap);
        spindex = new Spindex (hardwareMap);
        turret = new Turret(hardwareMap, "blue",90);
        drive = new Drivetrain (hardwareMap);
    }
    public void loop(){
        if (gamepad1.a){
            drive.setBackLeft(0.25);
        } else {
            drive.setBackLeft(0);
        }
        if (gamepad1.b){
            drive.setBackRight(0.25);
        } else {
            drive.setBackRight(0);
        }
        if (gamepad1.x){
            drive.setFrontRight(0.25);
        } else {
            drive.setFrontRight(0);
        }
        if (gamepad1.y){
            drive.setFrontLeft(0.25);
        } else {
            drive.setFrontLeft(0);
        }
        if (gamepad1.left_trigger>0.25){
            spindex.setSpindexPower(0.5);
        }else{
            spindex.setSpindexPower(0);
        }
        if (gamepad1.right_trigger>0.25){
            turret.setFlyWheelSpeed(-900);
        }
        else{
            turret.setFlyWheelSpeed(0);
        }
        if (gamepad1.left_bumper){
            intake.setIntakePower(0.4);
        }
        else{
            intake.setIntakePower(0);
        }
        telemetry.addData("spindex position", spindex.getPosition());


    }

}
