package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.configs.TickRates.GOBILDA_5203_6000RPM;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;

@Configurable
public class Shooter {
    private final DcMotorEx flywheel;
    private final DcMotor feeder;

public static double STEP_AMOUNT = 1;
    private static double targetFeederPower;
    public static double desiredFlywheelRPS = 13;
    public static double desiredFeederPower = 0.7;

    private static double MAX_FLYWHEEL_RPS = 100;

    public static boolean forceFeed = false;
    public static double kP = 6.5;
    public static double kF = 14;

    public static double FLYWHEEL_ERROR_TOLERANCE = 1;

    public static boolean canSpinFlywheel = true;

    private boolean isFlywheelSpinning;

    public Shooter(RobotHardware hw) {
        flywheel = hw.flywheel;
        flywheel.setDirection(DcMotorSimple.Direction.REVERSE);
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,
                new PIDFCoefficients(kP, 0, 0, kF));

        feeder = hw.feeder;
        feeder.setDirection(DcMotorSimple.Direction.FORWARD);
        feeder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public double getFlywheelRPS() {
        return flywheel.getVelocity() / GOBILDA_5203_6000RPM;
    }

    public double getFeederPower() {
        return feeder.getPower();
    }

    public void speedUpFlywheel() {
        desiredFlywheelRPS = Math.min(desiredFlywheelRPS + STEP_AMOUNT, MAX_FLYWHEEL_RPS);
    }
    public void slowDownFlywheel() {
        desiredFlywheelRPS = Math.max(desiredFlywheelRPS - STEP_AMOUNT, 0.0);
    }

    public double getFlywheelErrorRPS() {
        return getFlywheelRPS() - desiredFlywheelRPS;
    }

    public void toggleFlywheel() {
        canSpinFlywheel = !canSpinFlywheel;
    }

    public void feed() {
        targetFeederPower = desiredFeederPower;
    }
    public void enableForceFeed() {forceFeed = true;}

    public void update() {
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,
                new PIDFCoefficients(kP, 0, 0, kF));

        flywheel.setVelocity(canSpinFlywheel ? desiredFlywheelRPS * GOBILDA_5203_6000RPM : 0);

        if (Math.abs(getFlywheelErrorRPS()) <= FLYWHEEL_ERROR_TOLERANCE || forceFeed) {
            feeder.setPower(targetFeederPower);
        }

        targetFeederPower = 0; forceFeed = false;
    }
}
