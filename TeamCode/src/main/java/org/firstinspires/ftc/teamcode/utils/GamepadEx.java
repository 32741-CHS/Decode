package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.robotcore.hardware.Gamepad;

// wraps a gamepad so we can detect button presses and toggles easily
// call update() once per loop, then check wasPressed() or wasToggled()
public class GamepadEx {

    public final Button a      = new Button();
    public final Button b      = new Button();
    public final Button x      = new Button();
    public final Button y      = new Button();
    public final Button lb     = new Button();
    public final Button rb     = new Button();
    public final Button back   = new Button();
    public final Button start  = new Button();
    public final Button dpadUp    = new Button();
    public final Button dpadDown  = new Button();
    public final Button dpadLeft  = new Button();
    public final Button dpadRight = new Button();

    public void update(Gamepad gamepad) {
        a.update(gamepad.a);
        b.update(gamepad.b);
        x.update(gamepad.x);
        y.update(gamepad.y);
        lb.update(gamepad.left_bumper);
        rb.update(gamepad.right_bumper);
        back.update(gamepad.back);
        start.update(gamepad.start);
        dpadUp.update(gamepad.dpad_up);
        dpadDown.update(gamepad.dpad_down);
        dpadLeft.update(gamepad.dpad_left);
        dpadRight.update(gamepad.dpad_right);
    }

    public static class Button {
        private boolean prev = false;
        private boolean toggleState = false;
        boolean current;

        // true the frame you just pressed it
        public boolean wasPressed() {
            return current && !prev;
        }

        // true the frame you just let go
        public boolean wasReleased() {
            return !current && prev;
        }

        // true while held
        public boolean isHeld() {
            return current;
        }

        // flips each time you press, returns current state
        public boolean wasToggled() {
            if (wasPressed()) {
                toggleState = !toggleState;
            }
            return toggleState;
        }

        public void setToggle(boolean state) {
            toggleState = state;
        }

        public boolean getToggle() {
            return toggleState;
        }

        void update(boolean pressed) {
            prev = current;
            current = pressed;
        }
    }
}
