package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

@TeleOp(name="Test_MotorEncoderTester", group="Calibrators")
//@Disabled
public class Test_MotorEncoderTester extends LinearOpMode
{
    public DcMotor testMotor = null;
    private CoreConfig m_coreConfig;

    public Test_MotorEncoderTester()
    {
        m_coreConfig = new CoreConfig(
                CoreConfig.ROBOT_TEAM.KAOS,
                CoreConfig.ROBOT_OPERATING_MODE.TELEOP,
                CoreConfig.ROBOT_STARTING_POSITION.RED_LEFT_BACK_1,
                CoreConfig.ROBOT_DRIVING_VIEW_MODE.STANDARD,
                telemetry, hardwareMap, this);
    }

    public Test_MotorEncoderTester(CoreConfig coreConfig)
    {
        m_coreConfig = coreConfig;
    }

    @Override
    public void runOpMode()
    {
        try
        {
            testMotor = m_coreConfig.OpMode.hardwareMap.get(DcMotor.class, "TestMotor");
            testMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            testMotor.setDirection(DcMotor.Direction.FORWARD);
            testMotor.setPower(0);
            testMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        catch (Exception ex)
        {
            // something went wrong
            m_coreConfig.Telemetry.addData(">", "Initializing Test Motor Exception: " + ex.getMessage() );
        }

        // Wait for the driver to press start
        int targetPosition = 10000;
        double power = 1;
        waitForStart();

        while (opModeIsActive())
        {
            if (testMotor != null)
            {
                if (gamepad1.a)
                {
                    power = 1;
                    testMotor.setPower(0);
                    testMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    testMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    testMotor.setTargetPosition(targetPosition);

                    sleep(1000);

                    testMotor.setPower(power);
                }
                if (gamepad1.b)
                {
                    testMotor.setPower(0);
                    testMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    testMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                }

                if (testMotor.getMode() == DcMotor.RunMode.RUN_USING_ENCODER)
                {
                    power = -gamepad1.left_stick_y;
                    testMotor.setPower(power);
                }

                m_coreConfig.Telemetry.addData("Target", "Target Position:  %7d", targetPosition);
                m_coreConfig.Telemetry.addData("Actual", "Current Position: %7d", testMotor.getCurrentPosition());
                m_coreConfig.Telemetry.addData("Speed", "%5.2f", power);
                m_coreConfig.Telemetry.update();
            }
        }
    }
}