package org.firstinspires.ftc.teamcode.NonOpModes.depreciated;

import static org.firstinspires.ftc.teamcode.NonOpModes.colorsensing.ColorSensingFunctions.colorDetection;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.unknown;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Util.Enum.Balls;
import org.firstinspires.ftc.teamcode.positioning.odometry.FieldOrientedDriving;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name="ScooperTest")

public class SCooperTest extends LinearOpMode {
    private GoBildaPinpointDriver odomhub;
    private DcMotorEx Scooper;
    private DcMotor BR;
    private DcMotor BL;
    private DcMotor FL;
    private DcMotor FR;
    private Servo DrumServo;
    private Servo FiringPinServo;




    @Override
    public void runOpMode() {
        double[] drumlocations = {.27,.6,.92};
        Balls[] drumBallColors = {unknown, unknown, unknown};
        double targetdrumangle = 0;
        double targetfiringpinangle = 1;
        int targetdrumslot = 0;

        ElapsedTime timer = new ElapsedTime();

        DrumServo = hardwareMap.get(Servo.class, "DrumServo");
        FiringPinServo = hardwareMap.get(Servo.class, "FiringPinServo");

        odomhub = hardwareMap.get(GoBildaPinpointDriver.class,"odomhub");

        Scooper = hardwareMap.get(DcMotorEx.class,"Scooper");
        Scooper.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Scooper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        BR = hardwareMap.get(DcMotor.class, "BR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        FL.setDirection(DcMotor.Direction.REVERSE); //so I don't have to think about
        BL.setDirection(DcMotor.Direction.REVERSE); //inverting later
        FR.setDirection(DcMotor.Direction.FORWARD); //should generally do whenever motors
        BR.setDirection(DcMotor.Direction.FORWARD);

        NormalizedColorSensor colorSensor1 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor1");
        NormalizedColorSensor colorSensor2 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor2");

        odomhub.initialize();
        odomhub.resetPosAndIMU();   // resets encoders and IMU

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {


            double leftstickinputy = gamepad1.left_stick_y; // Forward/backward negative because it's naturally inverted
            double leftstickinputx = gamepad1.left_stick_x; // side to side
            double targetturn  = gamepad1.right_stick_x/2; // Turning

            double currentrelativeheading = odomhub.getHeading(AngleUnit.RADIANS);
            //Calls FieldOrientedDriving function and sets motor power
            double[] motorpowerarray = FieldOrientedDriving.fieldOrientedMath(leftstickinputy, leftstickinputx, targetturn, currentrelativeheading);


            double BRmotorpower = motorpowerarray[0];
            double BLmotorpower = motorpowerarray[1];
            double FRmotorpower = motorpowerarray[2];
            double FLmotorpower = motorpowerarray[3];


            odomhub.update();
            //assigns power to each motor based on gamepad inputs
            BR.setPower(BRmotorpower);
            BL.setPower(BLmotorpower);
            FR.setPower(FRmotorpower);
            FL.setPower(FLmotorpower);

            if (gamepad1.a){
                targetdrumslot = 0;
            }
            Balls loadedcolor = colorDetection(colorSensor1, colorSensor2);
            if (gamepad1.left_bumper){
                Scooper.setVelocity(999, AngleUnit.RADIANS);
            }
            else if (gamepad1.right_bumper){
                Scooper.setVelocity(-999, AngleUnit.RADIANS);
                if (loadedcolor != unknown && targetdrumslot < 3 && timer.milliseconds() > 500){
                    timer.reset();
                    drumBallColors[targetdrumslot] = loadedcolor;
                    telemetry.addLine("ball Detected");
                    targetdrumslot++;
                }
            }
            else Scooper.setVelocity(0, AngleUnit.RADIANS);

            targetdrumslot = Math.min(targetdrumslot,2);//clamps the target to 3

            targetdrumangle = drumlocations[targetdrumslot];
            DrumServo.setPosition(targetdrumangle);
            FiringPinServo.setPosition(targetfiringpinangle);

            telemetry.addData("loaded balls",drumBallColors[0].name());
            telemetry.addData("loaded balls",drumBallColors[1].name());
            telemetry.addData("loaded balls",drumBallColors[2].name());
            telemetry.addData("selected slot",targetdrumslot);

            telemetry.update();
        }
    }
}
