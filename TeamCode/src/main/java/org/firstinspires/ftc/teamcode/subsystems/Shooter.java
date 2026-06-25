package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.configs.TickRates.GOBILDA_5203_6000RPM;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;

@Configurable
public class Shooter {
    private final DcMotorEx flywheel;
    private final DcMotor feeder;

    public static double desiredFlywheelRPS = 13;
    public static double desiredFeederPower = 0.7;

    public static boolean forceFeed = false;
    public static double flywheelKP = 6.5;
    // theoretical kf = 32767 / (6000/60 * 112) = 2.93
    // tune this up slightly if flywheel runs below target
    public static double flywheelKF = 2.93;

    public static double FLYWHEEL_ERROR_TOLERANCE = 1;
    public static boolean canSpinFlywheel = false;

    // feeder runs for a set duration instead of one frame
    private ElapsedTime feedTimer = new ElapsedTime();
    private boolean isFeeding = false;
    public static double FEED_TIME_SECONDS = 0.4;

    public Shooter(RobotHardware hw) {
        flywheel = hw.flywheel;
        flywheel.setDirection(DcMotorSimple.Direction.REVERSE);
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

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
        desiredFlywheelRPS = Math.min(desiredFlywheelRPS + 1, 100);
    }
    public void slowDownFlywheel() {
        desiredFlywheelRPS = Math.max(desiredFlywheelRPS - 1, 0);
    }

    public double getFlywheelErrorRPS() {
        return getFlywheelRPS() - desiredFlywheelRPS;
    }

    public void toggleFlywheel() {
        canSpinFlywheel = !canSpinFlywheel;
    }

    public void feed() {
        if (!isFeeding) {
            isFeeding = true;
            feedTimer.reset();
        }
    }

    // use the ballistics lookup table to set flywheel speed from distance
    public void setTargetFromDistance(double distanceMeters) {
        desiredFlywheelRPS = Ballistics.getFlywheelRPS(distanceMeters);
    }

    public void update() {
        // apply pidf every frame so @Configurable changes work
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,
                new PIDFCoefficients(flywheelKP, 0, 0, flywheelKF));

        flywheel.setVelocity(canSpinFlywheel ? desiredFlywheelRPS * GOBILDA_5203_6000RPM : 0);

        // timed feeder
        if (isFeeding && feedTimer.seconds() < FEED_TIME_SECONDS) {
            boolean atSpeed = Math.abs(getFlywheelErrorRPS()) <= FLYWHEEL_ERROR_TOLERANCE;
            if (atSpeed || forceFeed) {
                feeder.setPower(desiredFeederPower);
            } else {
                feeder.setPower(0);
            }
        } else {
            feeder.setPower(0);
            isFeeding = false;
            forceFeed = false;
        }
    }
}
