package aj.apps;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_contrib;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import java.io.File;
import java.util.Objects;

import static com.googlecode.javacv.cpp.opencv_core.cvFlip;

/**
 * Created by Anuraj on 5/1/2017.
 */
public class faceRecognitionSecurity {

    public static int initialise(int OPERATION_CODE) throws Exception {

        int toReturn = 0;

        File file = new File(".");
        String path1 = file.getAbsolutePath().substring(0, file.getAbsolutePath().length()-1) + "dataSet";
        String path2 = file.getAbsolutePath().substring(0, file.getAbsolutePath().length()-1) + "resources";
        System.out.println(path1);
        File dir1 = new File(path1);
        File dir2 = new File(path2);
        dir1.mkdirs();
        dir2.mkdirs();

        //Create canvas frame for displaying webcam.
        CanvasFrame canvas = new CanvasFrame("Webcam");

        //Set Canvas frame to close on exit
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

        //Declare FrameGrabber to import output from webcam
        FrameGrabber grabber = new OpenCVFrameGrabber("");

        try {
            //Start grabber to capture video
            grabber.start();

            //Declare grabbedImage as IplImage
            IplImage grabbedImage;

            //recogniser object
            opencv_contrib.FaceRecognizer faceRecognizer = null;
            //id
            //int id = 0;

            int counter = 0;
            while (true) {
                //inser grabed video fram to IplImage grabbedImage
                grabbedImage = grabber.grab();

                if(Objects.equals(OPERATION_CODE, 1)){
                    //Do Nothing
                }
                else if (Objects.equals(OPERATION_CODE, 2)){
                    opencv_core.CvSeq sign = operations.detectFace(grabbedImage);
                    operations.drawRect(grabbedImage, sign);
                }
                else if (Objects.equals(OPERATION_CODE, 3)){
                    counter = operations.createSampleData(grabbedImage, counter, cvTask.id);
                    if (counter > 50){
                        System.out.println(counter);
                        toReturn = 1;
                        break;
                    }
                }
                else if (Objects.equals(OPERATION_CODE, 4)){
                    if(counter == 0){
                        faceRecognizer = operations.trainRecogniser();
                        System.out.println("trained");
                        counter++;
                    }
                    if(faceRecognizer != null) {
                        toReturn = operations.recogniser(grabbedImage, faceRecognizer);
                    }
                    break;
                }
                else if (Objects.equals(OPERATION_CODE, 5)){
                    if(counter == 0){
                        faceRecognizer = operations.trainRecogniser();
                        System.out.println("trained");
                        counter++;
                    }
                    if(faceRecognizer != null) {
                        toReturn = operations.recogniser(grabbedImage, faceRecognizer);
                    }
                }
                else {
                    System.out.println("Enter a valid OPERATION_CODE");
                    break;
                }

                //Set canvas size as per dimentions of video frame.
                canvas.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());

                if (grabbedImage != null) {
                    //Flip image horizontally
                    cvFlip(grabbedImage, grabbedImage, 1);
                    //Show video frame in canvas
                    canvas.showImage(grabbedImage);
                }
            }
        }
        catch (Exception e) {
            System.out.println("Some exception...");
            e.printStackTrace();
        }

        grabber.stop();
        canvas.hide();
        return toReturn;
    }

}