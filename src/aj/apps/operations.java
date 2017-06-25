package aj.apps;

import com.googlecode.javacv.cpp.opencv_contrib;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import java.io.File;
import java.io.FilenameFilter;

import static com.googlecode.javacv.cpp.opencv_contrib.createLBPHFaceRecognizer;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

/**
 * Created by Anuraj on 5/1/2017.
 */
public class operations {

    public static final String file = "resources/haarcascade_frontalface_default.xml";

    public static CvSeq detectFace(IplImage src){
        boolean ans = false;
        //cropped = cvCreateImage(cvGetSize(src), src.depth(), src.nChannels());*/

        //IplImage img = cvLoadImage("resources/lena.jpg");
        CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(file));
        CvMemStorage storage = CvMemStorage.create();
        CvSeq sign = cvHaarDetectObjects(
                src,
                cascade,
                storage,
                1.5,
                3,
                CV_HAAR_DO_CANNY_PRUNING);

        cvClearMemStorage(storage);

        return sign;
    }

    public static CvRect drawRect(IplImage src, CvSeq sign){
        CvRect r = null;
        int total_Faces = sign.total();

        for(int i = 0; i < total_Faces; i++){
            r = new CvRect(cvGetSeqElem(sign, i));
            cvRectangle (
                    src,
                    cvPoint(r.x(), r.y()),
                    cvPoint(r.width() + r.x(), r.height() + r.y()),
                    CvScalar.RED,
                    2,
                    CV_AA,
                    0);
        }
        return r;
    }

    public static int createSampleData(IplImage src, int counter, int id){

        CvSeq sign = detectFace(src);
        CvRect r = drawRect(src, sign);

        if(r != null) {
            IplImage test = cvCreateImage(cvGetSize(src), src.depth(), src.nChannels());
            cvCopy(src, test);
            // Creating rectangle by which bounds image will be cropped
            //CvRect r = new CvRect(100, 100, 200, 200);
            // After setting ROI (Region-Of-Interest) all processing will only be done on the ROI
            cvSetImageROI(test, r);

            cvSaveImage("dataSet/"+id+"-user_"+counter+".png", test);
            counter++;
        }

        return counter;
    }

    static opencv_contrib.FaceRecognizer trainRecogniser(){
        String trainingDir = "dataSet";
        //opencv_core.IplImage testImage = cvLoadImage("resources/user.png");
        File root = new File(trainingDir);

        FilenameFilter pngFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".png");
            }
        };

        File[] imageFiles = root.listFiles(pngFilter);

        opencv_core.MatVector images = new opencv_core.MatVector(imageFiles.length);

        int[] labels = new int[imageFiles.length];

        int counter = 0;
        int label;

        opencv_core.IplImage img;
        opencv_core.IplImage grayImg;

        for (File image : imageFiles) {
            img = cvLoadImage(image.getAbsolutePath());

            label = Integer.parseInt(image.getName().split("\\-")[0]);

            grayImg = opencv_core.IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);

            cvCvtColor(img, grayImg, CV_BGR2GRAY);

            images.put(counter, grayImg);

            labels[counter] = label;

            counter++;
        }

        opencv_contrib.FaceRecognizer faceRecognizer = createLBPHFaceRecognizer();
        // FaceRecognizer faceRecognizer = createEigenFaceRecognizer();
        // FaceRecognizer faceRecognizer = createLBPHFaceRecognizer()
        faceRecognizer.train(images, labels);
        return faceRecognizer;
    }

    static int recogniser(IplImage testImage, opencv_contrib.FaceRecognizer faceRecognizer){
        opencv_core.IplImage greyTestImage = opencv_core.IplImage.create(testImage.width(), testImage.height(), IPL_DEPTH_8U, 1);
        cvCvtColor(testImage, greyTestImage, CV_BGR2GRAY);

        CvRect r = drawRect(testImage, detectFace(greyTestImage));
        IplImage test = null;
        if(r != null) {
            System.out.println("face detected");
            test = cvCreateImage(cvGetSize(greyTestImage), greyTestImage.depth(), greyTestImage.nChannels());
            cvCopy(greyTestImage, test);
            cvSetImageROI(test, r);
        }

        int id = 0;
        if (test != null){
            id = faceRecognizer.predict(test);
        }
        System.out.println(id);

        CvFont font = new CvFont(CV_FONT_HERSHEY_SCRIPT_SIMPLEX, 1, 1);
        cvPutText (testImage, String.valueOf(id), cvPoint(20,20), font, CvScalar.BLUE);
        return id;
    }
}