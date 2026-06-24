package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.configs.TickRates.GOBILDA_5203_312RPM;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;

@Configurable
public class Turret {

    private final DcMotor turret;
    private final static double MOTOR_TO_TURRET_RATIO = 65.5 / 290;

    public static double desiredAngle;

    public static double kD, kV;
    public static double kS = 0.057;
    public static double kP = 0.15;

    private ElapsedTime timer = new ElapsedTime();
    private double lastError = 0;

    public static double MAX_POWER = 0.4;
    private double power = 0;

    public static double ANGLE_TOLERANCE = 0.2;

    public static double MIN_ANGLE = -44;
    public static double MAX_ANGLE = 20;

    public Turret(RobotHardware hw) {
        turret = hw.turret;
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public double getCurrentAngle() {
        int ticks = turret.getCurrentPosition();
        return ticks / GOBILDA_5203_312RPM * MOTOR_TO_TURRET_RATIO * 360;
    }

    public double getErrorAngle() {
        return desiredAngle - getCurrentAngle();
    }

    public void resetTimer() {
        timer.reset();
    }

    public void resetTurretEncoder() {
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);}

    public void update() {
        double deltaTime = timer.seconds();
        double error = getErrorAngle();

        double sTerm = Math.copySign(kS, error);
        double vTerm = error / deltaTime * kV;

        double pTerm = error * kP;

        double dTerm = 0;
        if (deltaTime > 0) {
            dTerm = ((error - lastError) / deltaTime) * kD;
        }

        double combinedTerm = sTerm + vTerm + pTerm + dTerm;

        if (Math.abs(error) < ANGLE_TOLERANCE) {
            power = 0;
        } else {
            power = Range.clip(combinedTerm, -MAX_POWER, MAX_POWER);
        }

        if (power > 0 && getCurrentAngle() >= MAX_ANGLE) {power = 0;}
        else if (power < 0 && getCurrentAngle() <= MIN_ANGLE) {power = 0;}

        turret.setPower(power);
        lastError = error; timer.reset();
    }

}
