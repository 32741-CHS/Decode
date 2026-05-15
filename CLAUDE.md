# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this repo is

The FTC (FIRST Tech Challenge) SDK for the DECODE (2025–2026) season — an Android app (`com.qualcomm.ftcrobotcontroller`) that runs on a Control Hub or Android phone and drives a competition robot. The SDK itself is consumed as Maven artifacts (`org.firstinspires.ftc:*:11.1.0`); this repo is the *team-modifiable shell* around it.

Requires Android Studio Ladybug (2024.2) or later. JDK 21 (bundled with Ladybug) is used, but `sourceCompatibility`/`targetCompatibility` are pinned to Java 1.8 so code stays runnable in the on-robot OnBotJava environment. Don't raise these.

## Build / install / run

There is no test suite — verification is "does it build" and "does it run on the robot." Use the Gradle wrapper:

```bash
./gradlew assembleDebug              # build debug APK for the robot controller
./gradlew :TeamCode:assembleDebug    # build only the team module (faster iteration)
./gradlew installDebug               # build + install on attached Android device via adb
./gradlew clean
```

Typical inner loop is to use Android Studio's Run button against an attached Control Hub / phone over USB or ADB-over-WiFi. The deployable artifact is the `FtcRobotController` app; `TeamCode` is an Android library that gets linked into it.

## Module layout — what goes where

Two-module Gradle build (see `settings.gradle`):

- **`FtcRobotController/`** — the Android *application* module. **Do not modify team code here.** Contains:
  - `internal/` — the actual app entry points (`FtcRobotControllerActivity`, `FtcOpModeRegister`, `PermissionValidatorWrapper`). Modify only if you really know what you're doing.
  - `external/samples/` — read-only reference OpModes (63 files). **Never edit these in place.** The workflow is: copy a sample into `TeamCode/.../teamcode/` (Android Studio's copy-paste auto-renames the class), then modify the copy. See `FtcRobotController/src/main/java/org/firstinspires/ftc/robotcontroller/external/samples/readme.md` for the sample-naming convention (`Basic*`, `Sensor*`, `Robot*`, `Concept*`).
- **`TeamCode/`** — Android *library* module, package `org.firstinspires.ftc.teamcode`. **This is where all team code goes.** Has `OpModeAnnotationProcessor.jar` in `TeamCode/lib/` that picks up `@TeleOp` / `@Autonomous` annotations at compile time and registers OpModes for the Driver Station menu.

`build.common.gradle` is shared build config and reads `versionCode` / `versionName` from `FtcRobotController/src/main/AndroidManifest.xml` — that manifest is the single source of truth for app version. `build.dependencies.gradle` pins the FTC SDK artifact versions (currently `11.1.0`).

## OpMode model (the runtime abstraction)

OpModes are the unit of robot behavior. An OpMode is a Java class annotated with `@TeleOp(name=..., group=...)` or `@Autonomous(...)` that extends `LinearOpMode` (linear/imperative style with `waitForStart()` + `while (opModeIsActive())`) or `OpMode` (iterative `init()`/`loop()` style). The annotation processor auto-discovers them — no manual registration needed. Adding `@Disabled` hides an OpMode from the Driver Station menu.

Hardware is accessed via `hardwareMap.get(DcMotor.class, "name")` where `"name"` must match a port name configured in the FTC Robot Controller app's robot configuration on the device — these strings are a contract with on-device configuration, not the codebase, so renaming them in code without updating the robot's config will break runtime hardware lookup.

## Editing conventions specific to this SDK

- **Don't edit `FtcRobotController/external/samples/`** — they're reference material that gets updated when the SDK is rev'd. All team work happens in `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`.
- **Don't bump `build.dependencies.gradle` SDK versions** casually — those track FTC's annual SDK releases and need to stay in sync with the firmware on the Control Hub.
- **Don't edit `build.common.gradle`** unless absolutely necessary; team-specific build customization belongs in `TeamCode/build.gradle`.
- Source compatibility is Java 1.8. OnBotJava (the in-browser editor that some teams use alongside Android Studio) only supports 1.8 — anything that builds here should also be parseable by OnBotJava if teams want to edit it on the robot.
