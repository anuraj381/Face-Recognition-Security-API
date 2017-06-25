package aj.apps;

/**
 * Created by Anuraj on 6/25/2017.
 */
public class test {

    public static void main(String[] args) throws Exception {

        faceRecognitionSecurity obj = new faceRecognitionSecurity();
        cvTask.id = 33;
        int result = obj.initialise(cvTask.CREATE_DATASET);

        if(result == 1){
            System.out.println("dataset created");
        }

    }

}
