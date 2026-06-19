package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.configs.RobotHardware;

// flywheel shooter. 5203 series 6000rpm motor.
// on/off toggle + speed levels via dpad.
// these speed values are EXAMPLES — tune at practice (issue #9).
public class Flywheel {

    private final RobotHardware hw;

    // speed levels as fractions of max power
    // TODO: tune these at practice for actual shooting distances
    private static final double[] SPEED_LEVELS = {0.0, 0.25, 0.50, 0.75, 1.0};

    private int speedLevel = 0;
    private boolean running = false;

    public Flywheel(RobotHardware hw) {
        this.hw = hw;

        hw.flywheel.setDirection(DcMotorSimple.Direction.FORWARD);
        hw.flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    // toggle flywheel on/off
    public void setRunning(boolean on) {
        running = on;
        updatePower();
    }

    public boolean isRunning() {
        return running;
    }

    // cycle speed up (dpad up)
    public void speedUp() {
        speedLevel = Math.min(speedLevel + 1, SPEED_LEVELS.length - 1);
        updatePower();
    }

    // cycle speed down (dpad down)
    public void speedDown() {
        speedLevel = Math.max(speedLevel - 1, 0);
        updatePower();
    }

    public int getSpeedLevel() {
        return speedLevel;
    }

    public double getCurrentSpeed() {
        return running ? SPEED_LEVELS[speedLevel] : 0.0;
    }

    // get speed as a label for telemetry
    public String getSpeedLabel() {
        if (!running) return "OFF";
        return String.format("%d%%", (int)(SPEED_LEVELS[speedLevel] * 100));
    }

    private void updatePower() {
        hw.flywheel.setPower(running ? SPEED_LEVELS[speedLevel] : 0.0);
    }

    public void stop() {
        running = false;
        speedLevel = 0;
        hw.flywheel.setPower(0);
    }
}
