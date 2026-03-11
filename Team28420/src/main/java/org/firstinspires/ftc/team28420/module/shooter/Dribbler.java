package org.firstinspires.ftc.team28420.module.shooter;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.team28420.util.Config;

public class Dribbler {
    private final DcMotorEx dribblerMotor;

    public Dribbler(HardwareMap hMap) {
        dribblerMotor = hMap.get(DcMotorEx.class, "dribbler");
    }
    public void setVelocityCoefficient(float k) {
        dribblerMotor.setVelocity(Config.ShooterConf.DRIBBLER_VELOCITY * k);
    }
}
