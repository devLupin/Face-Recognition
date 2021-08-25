#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <android/log.h>

using namespace cv;
using namespace std;

float resize(Mat img_src, Mat &img_resize, int resize_width){

    float scale = resize_width / (float)img_src.cols ;
    if (img_src.cols > resize_width) {
        int new_height = cvRound(img_src.rows * scale);
        resize(img_src, img_resize, Size(resize_width, new_height));
    }
    else {
        img_resize = img_src;
    }
    return scale;
}


extern "C"
JNIEXPORT jlong JNICALL
Java_com_devlupin_acquisition_CaptureActivity_loadCascade(JNIEnv *env, jobject type,
                                                          jstring cascadeFileName_) {
    // TODO: implement loadCascade()
    const char *nativeFileNameString = env->GetStringUTFChars(cascadeFileName_, 0);

    string baseDir("/storage/emulated/0/");
    baseDir.append(nativeFileNameString);
    const char *pathDir = baseDir.c_str();

    jlong ret = 0;
    ret = (jlong) new CascadeClassifier(pathDir);
    if (((CascadeClassifier *) ret)->empty()) {
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 실패  %s", nativeFileNameString);
    }
    else
        __android_log_print(ANDROID_LOG_DEBUG, "native-lib :: ",
                            "CascadeClassifier로 로딩 성공 %s", nativeFileNameString);


    env->ReleaseStringUTFChars(cascadeFileName_, nativeFileNameString);

    return ret;
}extern "C"
JNIEXPORT void JNICALL
Java_com_devlupin_acquisition_CaptureActivity_detect(JNIEnv *env, jobject type,
                                                     jlong cascadeClassifier_face,
                                                     jlong cascadeClassifier_eye,
                                                     jlong matAddrInput, jlong matAddrResult) {
    // TODO: implement detect()
    const int SX = 570;
    const int SY = 10;
    const int EX = SX + 750;
    const int EY = SY + 800;

    Mat &img_input = *(Mat *) matAddrInput;
    Mat &img_result = *(Mat *) matAddrResult;

    img_result = img_input.clone();

    std::vector<Rect> faces;
    Mat img_gray;

    cvtColor(img_input, img_gray, COLOR_BGR2GRAY);
    equalizeHist(img_gray, img_gray);

    Mat img_resize;
    float resizeRatio = resize(img_gray, img_resize, 640);

    //-- Detect faces
    ((CascadeClassifier *) cascadeClassifier_face)->detectMultiScale( img_resize, faces, 1.1, 2, 0|CASCADE_SCALE_IMAGE, Size(30, 30) );

    for (int i = 0; i < faces.size(); i++) {
        double real_facesize_x = faces[i].x / resizeRatio;
        double real_facesize_y = faces[i].y / resizeRatio;
        double real_facesize_width = faces[i].width / resizeRatio;
        double real_facesize_height = faces[i].height / resizeRatio;

        Point center( real_facesize_x + real_facesize_width / 2, real_facesize_y + real_facesize_height/2);
        ellipse(img_result, center, Size( real_facesize_width / 2, real_facesize_height / 2), 0, 0, 360,
                Scalar(255, 0, 255), 30, 8, 0);

        // 절대 값을 지정하여 사각 프레임 그리기
        rectangle(img_result, Point(SX, SY), Size(EX, EY), Scalar(0, 255, 0), 8, 8, 0);
    }

}