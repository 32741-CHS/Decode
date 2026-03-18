package org.firstinspires.ftc.teamcode.Tools;

import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * TheIntakeJutsu: Advanced ball intake and lift system.
 * Optimized for high-speed operation and compatible with Test2026.
 */
public class TheIntakeJutsu {
    private final MotorEx intakeMotor;
    private final Servo liftServo;

    // Configurable Servo Positions
    public static double LIFT_UP = 0.85;
    public static double LIFT_DOWN = 0.15;
    
    // Operational Speeds
    public static double INTAKE_SPEED = 1.0;
    public static double EJECT_SPEED = -0.9;

    public TheIntakeJutsu(MotorEx intakeMotor, Servo liftServo) {
        this.intakeMotor = intakeMotor;
        this.liftServo = liftServo;
        
        // Set motor behavior for better control
        this.intakeMotor.setZeroPowerBehavior(MotorEx.ZeroPowerBehavior.BRAKE);
    }

    /**
     * Intakes a ball by lowering the lift and spinning at max speed.
     */
    public void intakeBall() {
        liftServo.setPosition(LIFT_DOWN);
        intakeMotor.set(INTAKE_SPEED);
    }

    public void lift(String colour) {
        // Logic can be expanded here to change behavior for 'Blue', 'Red', or 'Yellow'
        liftServo.setPosition(LIFT_UP);
        intakeMotor.set(EJECT_SPEED);
    }

    public void stop() {
        intakeMotor.set(0);
        // Retract to down position by default for safety
        liftServo.setPosition(LIFT_DOWN);
    }

    /**
     * Manual control for fine-tuning during TeleOp.
     */
    public void setIntakePower(double power) {
        intakeMotor.set(power);
    }
}
