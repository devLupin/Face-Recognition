import os

USER = 'Hyuntaek'
USER_ROOT = os.path.join('C:\\Users', USER)
PROJECT_PATH = os.path.join(USER_ROOT, 'Desktop\\Face recognition')

configurations = {
    1: dict(
        DATA = 'faces/lfw_split/train',
        ONNX_PATH = 'models/detection/ultra_light_640.onnx',
        LANDMARK_PATH = 'models/facial_landmarks/shape_predictor_68_face_landmarks.dat',
        EMBEDDING_PKL = 'embeddings/embeddings.pkl',
        THRESHOLD = 0.63
),
}