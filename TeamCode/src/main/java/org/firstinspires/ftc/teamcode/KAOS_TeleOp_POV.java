package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Arrays;

@TeleOp(name="KAOS_TeleOp_POV", group="KAOS - TeleOp")
//@Disabled
public class KAOS_TeleOp_POV extends LinearOpMode
{
    private BaseHardware m_robot;
    private CoreConfig m_coreConfig;

    private ElapsedTime runtime = new ElapsedTime();

    public KAOS_TeleOp_POV()
    {
        m_coreConfig = new CoreConfig(
            CoreConfig.ROBOT_TEAM.KAOS,
            CoreConfig.ROBOT_OPERATING_MODE.TELEOP,
            CoreConfig.ROBOT_STARTING_POSITION.RED_LEFT_BACK_1,
            CoreConfig.ROBOT_DRIVING_VIEW_MODE.POV,
            telemetry, hardwareMap, this);
        m_robot = new BaseHardware(m_coreConfig);
    }

    public KAOS_TeleOp_POV(CoreConfig coreConfig)
    {
        m_coreConfig = coreConfig;
        m_robot = new BaseHardware(m_coreConfig);
    }

    @Override
    public void runOpMode()
    {
        double leftfront;
        double rightfront;
        double leftrear;
        double rightrear;

        if (!m_robot.init())
        {
            telemetry.addData("Error", "Hardware Init Failed");
            telemetry.update();
            sleep(8000);  // sleep for 8 seconds so the error can be read
            return;
        }

        // initialize the devices
        m_robot.InitRelicGripper();
        m_robot.InitRelicArmKnuckle();
        m_robot.RaiseLeftJewelArm();
        m_robot.RaiseRightJewelArm();
        m_robot.CenterRightJewelArmFlicker();
        m_robot.CenterLeftJewelArmFlicker();

        // need to keep track of the encoders on these motors for the software limits we are imposing
        if (m_robot.relicArmMotor != null)
        {
            m_robot.relicArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            m_robot.relicArmMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        if (m_robot.liftMotor != null)
        {
            m_robot.liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            m_robot.liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        // start all drive motors in float mode
        if (m_robot.rightFrontMotor != null)
        {
            m_robot.rightFrontMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
        if (m_robot.rightRearMotor != null)
        {
            m_robot.rightRearMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
        if (m_robot.leftFrontMotor != null)
        {
            m_robot.leftFrontMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
        if (m_robot.leftRearMotor != null)
        {
            m_robot.leftRearMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            // Run wheels in POV mode (note: The joystick goes negative when pushed forwards, so negate it)
            // In this mode the Left stick moves the m_robot fwd and back, the Right stick turns left and right.
            // This way it's also easy to just drive straight, or just turn.
            leftfront = -gamepad1.left_stick_y + gamepad1.right_stick_x + gamepad1.left_stick_x;
            leftrear = -gamepad1.left_stick_y - gamepad1.left_stick_x + gamepad1.right_stick_x;
            rightfront = -gamepad1.left_stick_y - gamepad1.right_stick_x - gamepad1.left_stick_x;
            rightrear = -gamepad1.left_stick_y + gamepad1.left_stick_x - gamepad1.right_stick_x;

            double[] motorPower = {rightfront, rightfront, leftrear, rightrear};
            Arrays.sort(motorPower);
            if (motorPower[3] > 1) {
                leftfront /= motorPower[3];
                leftrear /= motorPower[3];
                rightfront /= motorPower[3];
                rightrear /= motorPower[3];
            }

            // if left trigger is down, then scale back power
            if (gamepad1.left_trigger > 0)
            {
                leftfront = leftfront * .35;
                leftrear = leftrear * .35;
                rightfront = rightfront * .35;
                rightrear = rightrear * .35;
            }

            if ((m_robot.rightFrontMotor != null) && (m_robot.rightRearMotor != null) &&
                (m_robot.leftFrontMotor != null) && (m_robot.leftRearMotor != null))
            {
                if (gamepad1.left_bumper) {
                    // turn on motor break mode
                    m_robot.rightFrontMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    m_robot.rightRearMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    m_robot.leftFrontMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    m_robot.leftRearMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                } else if (gamepad1.right_bumper) {
                    // turn off motor break mode and put to float mode
                    m_robot.rightFrontMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                    m_robot.rightRearMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                    m_robot.leftFrontMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                    m_robot.leftRearMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                }
            }

            if ((m_robot.rightFrontMotor != null) && (m_robot.rightRearMotor != null) &&
                (m_robot.leftFrontMotor != null) && (m_robot.leftRearMotor != null))
            {
                m_robot.leftFrontMotor.setPower(leftfront);
                m_robot.rightFrontMotor.setPower(rightfront);
                m_robot.leftRearMotor.setPower(leftrear);
                m_robot.rightRearMotor.setPower(rightrear);
            }

//            if (!m_coreConfig.CompetitionMode)
//            {
//                if ((m_robot.rightFrontMotor != null) && (m_robot.rightRearMotor != null) &&
//                    (m_robot.leftFrontMotor != null) && (m_robot.leftRearMotor != null) &&
//                    (m_robot.relicArmMotor != null) && (m_robot.liftMotor != null)) {
//
//                    telemetry.addData("LeftFrontPower", "%5.2f", leftfront);
//                    telemetry.addData("RightFrontPower", "%5.2f", rightfront);
//                    telemetry.addData("LeftRearPower", "%5.2f", leftrear);
//                    telemetry.addData("RightRearPower", "%5.2f", rightrear);
//
//                    telemetry.addData("LeftFrontPosition", "%7d", m_robot.leftFrontMotor.getCurrentPosition());
//                    telemetry.addData("RightFrontPosition", "%7d", m_robot.rightFrontMotor.getCurrentPosition());
//                    telemetry.addData("LeftRearPosition", "%7d", m_robot.leftRearMotor.getCurrentPosition());
//                    telemetry.addData("RightRearPosition", "%7d", m_robot.rightRearMotor.getCurrentPosition());
//
//                    telemetry.addData("RelicArmMotorPosition", "%7d", m_robot.relicArmMotor.getCurrentPosition());
//                    telemetry.addData("RelicArmMotorPower", "%5.2f", gamepad2.right_stick_y);
//                    telemetry.addData("LiftMotorPosition", "%7d", m_robot.liftMotor.getCurrentPosition());
//                    telemetry.addData("LiftMotorPower", "%5.2f", gamepad2.left_stick_y);
//                }
//            }

            // run the intake conveyor
            if ((m_robot.leftIntakeMotor != null) && (m_robot.rightIntakeMotor != null))
            {
                if ((gamepad2.right_trigger > 0) && (gamepad2.left_trigger <= 0))
                {
                    // run the intake IN to pull in a relic
                    m_robot.leftIntakeMotor.setPower(1);
                    m_robot.rightIntakeMotor.setPower(-1);
                }
                else if ((gamepad2.right_trigger <= 0) && (gamepad2.left_trigger > 0))
                {
                    // run the intake OUT to pull in a relic
                    m_robot.leftIntakeMotor.setPower(-1);
                    m_robot.rightIntakeMotor.setPower(1);
                }
                else
                {
                    m_robot.leftIntakeMotor.setPower(0);
                    m_robot.rightIntakeMotor.setPower(0);
                }
            }

            // open and close the relic grabber
            if (gamepad2.dpad_down)
            {
                m_robot.CloseRelicGripper();
            }
            else  if(gamepad2.dpad_up)
            {
                m_robot.OpenRelicGripper();
            }

            // raise/lower relic knuckle
            if (gamepad2.x)
            {
                m_robot.LowerRelicArmKnuckle2();
            }
            if (gamepad2.b)
            {
                m_robot.HoverRelicArmKnuckle();
            }
            if (gamepad2.y)
            {
                m_robot.RaiseRelicArmKnuckle();
            }
            if (gamepad2.a)
            {
                m_robot.LowerRelicArmKnuckle();
            }

            // raise/lower the lift
            if (m_robot.liftMotor != null)
            {
                double liftMotorPower = -gamepad2.left_stick_y;
                m_robot.liftMotor.setPower(liftMotorPower);
            }

            // extend/retract arm
            if (m_robot.relicArmMotor != null)
            {
                double relicArmMotorPower = -gamepad2.right_stick_y * .85;
                m_robot.relicArmMotor.setPower(relicArmMotorPower);
            }

            // run the extend, drop and retract sequence
            if (gamepad2.left_bumper)
            {
                // stop all other motors since we can;t multi-thread this routine
                if ((m_robot.rightFrontMotor != null) && (m_robot.rightRearMotor != null) &&
                    (m_robot.leftFrontMotor != null) && (m_robot.leftRearMotor != null))
                {
                    m_robot.rightRearMotor.setPower(0);
                    m_robot.rightFrontMotor.setPower(0);
                    m_robot.leftRearMotor.setPower(0);
                    m_robot.leftFrontMotor.setPower(0);
                    m_robot.liftMotor.setPower(0);
                }

                // this should work now as it it set to run without encoders so the sign of the
                // encoder count won't dictate the direction of the motor any more as it does in
                // RunMotorByEncoder (+/- on the motor power is ignored with run with encoders)
                m_robot.HoverRelicArmKnuckle();
                m_robot.RunRelicArmByTime(.8, 1.8);
                m_robot.LowerRelicArmKnuckle();
                m_coreConfig.OpMode.sleep(400);
                m_robot.OpenRelicGripper();
                m_coreConfig.OpMode.sleep(800);
                m_robot.RaiseRelicArmKnuckle(true);
                m_coreConfig.OpMode.sleep(400);
                m_robot.RunRelicArmByTime(-.9, 1.7);
            }

            // ******* TESTING CODE - remove before match *******
//            if (!m_coreConfig.CompetitionMode)
//            {
//                if (gamepad1.a)
//                {
//                    m_robot.RaiseRightJewelArm();
//                }
//                if (gamepad1.b)
//                {
//                    m_robot.LowerRightJewelArm();
//                }
//
//                if (gamepad1.x)
//                {
//                    m_robot.RaiseLeftJewelArm();
//                }
//                if (gamepad1.y)
//                {
//                    m_robot.LowerLeftJewelArm();
//                }
//
//                if (gamepad1.left_bumper)
//                {
//                    if (gamepad1.dpad_up)
//                    {
//                        m_robot.CenterLeftJewelArmFlicker();
//                    }
//                    if (gamepad1.dpad_left)
//                    {
//                        m_robot.PushLeftJewelArmFlicker();
//                    }
//                    if (gamepad1.dpad_right)
//                    {
//                        m_robot.PullLeftJewelArmFlicker();
//                    }
//                }
//                else if (gamepad1.right_bumper)
//                {
//                    if (gamepad1.dpad_up)
//                    {
//                        m_robot.CenterRightJewelArmFlicker();
//                    }
//                    if (gamepad1.dpad_left)
//                    {
//                        m_robot.PushRightJewelArmFlicker();
//                    }
//                    if (gamepad1.dpad_right)
//                    {
//                        m_robot.PullRightJewelArmFlicker();
//                    }
//                }
//                else
//                {
//                    if (gamepad1.dpad_right)
//                    {
//                        m_coreConfig.StartingPosition = CoreConfig.ROBOT_STARTING_POSITION.RED_LEFT_BACK_1;
//                        BaseHardware.COLOR_RESULT color = m_robot.ReadJewelColor(0, false);
//                        telemetry.addData("Color", color.toString());
//                    }
//                    if (gamepad1.dpad_left)
//                    {
//                        m_coreConfig.StartingPosition = CoreConfig.ROBOT_STARTING_POSITION.BLUE_RIGHT_FRONT_3;
//                        BaseHardware.COLOR_RESULT color = m_robot.ReadJewelColor(0, false);
//                        telemetry.addData("Color", color.toString());
//                    }
//                }
//            }
//            telemetry.update();
        }
    }
}
