package testAutomation.recording;


import org.monte.media.Format;
import org.monte.media.FormatKeys;
import org.monte.media.Registry;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import testAutomation.testdata.TestCaseNameManager;
import testAutomation.testdata.TestConfig;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.monte.media.AudioFormatKeys.EncodingKey;
import static org.monte.media.AudioFormatKeys.FrameRateKey;
import static org.monte.media.AudioFormatKeys.KeyFrameIntervalKey;
import static org.monte.media.AudioFormatKeys.MediaTypeKey;
import static org.monte.media.AudioFormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.*;

public class MonteScreenRecorder extends testAutomation.recording.ScreenRecorder {
    private ScreenRecorder screenRecorder = null;

    public MonteScreenRecorder startRecording() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formater = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
        try {

            //Create a instance of GraphicsConfiguration to get the Graphics configuration
            //of the Screen. This is needed for ScreenRecorder class.
            GraphicsConfiguration gc = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration();

            screenRecorder = new MonteCustomScreenRecorder(
                    gc,
                    gc.getBounds(),
                    getFileFormat(),
                    getScreenFormat(),
                    getMouseFormat(),
                    null,
                    new File(TestConfig.OUTPUT_DIRECTORY + "\\video_output"),
                    outputFileName);

            if (!screenRecorder.getState().equals(ScreenRecorder.State.RECORDING)) {
                screenRecorder.start();
            }
            return this;

        } catch (Exception e) {
           e.printStackTrace();
           return null;
        }
    }

    public void stopRecording() {
        if (screenRecorder==null) {
            return;
        }
        try {
            if (screenRecorder.getState()!=null && screenRecorder.getState().equals(ScreenRecorder.State.RECORDING)) {
                screenRecorder.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private Format getFileFormat(){
        return new Format(
                MediaTypeKey, MediaType.FILE,
                MimeTypeKey, MIME_AVI);
    }

    private Format getScreenFormat(){
        return new Format(
                MediaTypeKey, MediaType.VIDEO,
                EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                DepthKey, 24,
                FrameRateKey, Rational.valueOf(30),
                QualityKey, 1.0f,
                KeyFrameIntervalKey, 30 * 60);
    }

    private Format getMouseFormat(){
        return new Format(
                MediaTypeKey,
                FormatKeys.MediaType.VIDEO,
                EncodingKey,
                "black",
                FrameRateKey,
                Rational.valueOf(30));
    }

    class MonteCustomScreenRecorder extends ScreenRecorder {

        private String name;

        public MonteCustomScreenRecorder(GraphicsConfiguration cfg,
                                         Rectangle captureArea, Format fileFormat, Format screenFormat,
                                         Format mouseFormat, Format audioFormat, File movieFolder,
                                         String name) throws IOException, AWTException {
            super(cfg, captureArea, fileFormat, screenFormat, mouseFormat,
                    audioFormat, movieFolder);
            this.name = name;
        }

        @Override
        protected File createMovieFile(Format fileFormat) throws IOException {
            if (!movieFolder.exists()) {
                movieFolder.mkdirs();
            } else if (!movieFolder.isDirectory()) {
                throw new IOException("\"" + movieFolder + "\" is not a directory.");
            }

            return new File(movieFolder, name + "."
                    + Registry.getInstance().getExtension(fileFormat));
        }
    }
}
