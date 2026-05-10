package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
public class ftc_decode2026 extends OpMode {
    private DcMotor frontright , backright , thrower_right , thrower_left;
    double throttle;
    double endGamestart;
    double spin;
    boolean isEndgame;
    @Override
    public void init() {
        backright = hardwareMap.get(DcMotor.class,"DriveL");
        frontright = hardwareMap.get(DcMotor.class, "DriveR");
        thrower_right = hardwareMap.get(DcMotor.class,"liftR");
        thrower_left = hardwareMap.get(DcMotor.class,"liftL");

        frontright.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backright.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        thrower_left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        thrower_right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        frontright.setDirection(DcMotorSimple.Direction.REVERSE);
        backright.setDirection(DcMotorSimple.Direction.FORWARD);
        thrower_right.setDirection(DcMotorSimple.Direction.REVERSE);
        thrower_left.setDirection(DcMotorSimple.Direction.FORWARD);

        frontright.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backright.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }
    @Override
    public void start() {
        endGamestart = getRuntime() + 90;
    }


    @Override
    public void loop() {
        spin = gamepad1.left_stick_x;
        throttle = -gamepad1.left_stick_y;

        double leftPower = throttle + spin ;
        double rightPower = throttle - spin;
        double throwerpower = 3     ;
        double largest = Math.max(Math.abs(leftPower) ,Math.abs(rightPower));

        if(largest > 1.0){
            leftPower /= largest;
            rightPower /= largest;
        }
        frontright.setPower(leftPower);
        backright.setPower(rightPower);

        if(gamepad1.a){
            thrower_left.setPower(throwerpower);
            thrower_right.setPower(throwerpower);   
        }
        else{
            thrower_left.setPower(0);
            thrower_right.setPower(0);
        }



        if (endGamestart >= getRuntime() && !isEndgame) {
            // end game chek
            gamepad1.rumble(1000);
            gamepad1.rumbleBlips(3);
            isEndgame = true;
        }
    }


}
