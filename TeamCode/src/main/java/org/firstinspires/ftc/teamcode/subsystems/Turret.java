package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.teamcode.configs.TickRates;
import org.firstinspires.ftc.teamcode.configs.RobotHardware;

// encoder resets on init so center = 0 degrees.
public class Turret {
    private final double GEAR_RATIO = 1;
    private final DcMotor turret;
    private double desiredBearing = 0;
    public double currentBearing = 0;

    // IN RADIANS!!
    private final double MAX_BEARING = Math.toRadians(45);
    private final double MIN_BEARING = Math.toRadians(-45); // Added missing variable
    // Constant that dictates how fast motor move based on err
    private final double ROTATIONAL_SPEED = 0.1;
    // ↓↓↓ This is the thing that is dependent on the motor to convert to degrees
    private final double TICKS_PER_REVOLUTION = TickRates.GOBILDA_5203_312RPM * GEAR_RATIO; // We do not know with the gear ratio do we...


    public Turret(RobotHardware hw) {
        turret = hw.turret;
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void update(){ // Called continuously in the main loop first
        this.currentBearing = getCurrentBearing();
        double error = desiredBearing - currentBearing;
        double power = error * ROTATIONAL_SPEED;
        power = Range.clip(power, -1, 1); // Fancy Ai thing. Limits.
        if (Math.abs(error) < Math.toRadians(1.0)) {
            turret.setPower(0);
        } else {
            turret.setPower(power);
        }
    }

    public void pointTurret(double radians) {
        // Enforce the min and max soft limits safely
        this.desiredBearing = Range.clip(radians, MIN_BEARING, MAX_BEARING);

    }
    private double getCurrentBearing() {
         double motorRevolutions = turret.getCurrentPosition() / TICKS_PER_REVOLUTION;
         // 1 full revolution = 2 * PI radians
         return motorRevolutions * (2.0 * Math.PI);
        }

        // Getters for telemetry or external loops
        public double getCurrentBearingRadians() { return currentBearing; }
        public double getDesiredBearingRadians() { return desiredBearing; }
    }
