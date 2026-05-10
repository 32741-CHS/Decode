package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Flywheel {
    private DcMotorEx m1;

    private double gearRatio = 1;

    private double encoderCPM = 20;

     private double kV ,kS ,kP;

     public void init(HardwareMap hwMap) {
         m1 = hwMap.get(DcMotorEx.class,"anas");
         m1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
         m1.setDirection(DcMotorSimple.Direction.REVERSE);
     }

     public void setMotorPower(double Power) {
         m1.setPower(Power);
     }
     public double getTicksPerSec() {
         return m1.getVelocity();
     }

     public  double getRPM() {
         return ((getTicksPerSec()/encoderCPM) * 60) / gearRatio;
     }

}
