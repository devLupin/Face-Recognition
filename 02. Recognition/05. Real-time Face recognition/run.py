import os, errno
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'

from multiprocessing import Process
from absl import app, logging
import numpy as np
import tensorflow as tf

from networks.models import ArcFaceModel
from utils import load_yaml, l2_norm
import matplotlib.pyplot as plt
from sklearn.metrics import auc

from detector import mbv2_ssd_based as detector

CMP_IMG = 'temp.PNG'
THRESHOLD = 1.14


def silentRemove(file):
    try:
        os.remove(file)
    except OSError as e:
        if e.errno != errno.ENOENT:
            raise


def createProcess(function, args):
    if args is None:
        return Process(target=function)
    else:
        return Process(target=function, args=args)


def get_embedding(model, x):
    if len(x.shape) == 3:
        x = np.expand_dims(x, 0)
    embeds = model.predict(x)
    embeds = l2_norm(embeds)
    return embeds

def calculate_distance(embd1, embd2):
    diff = np.subtract(embd1, embd2)
    dist = np.sum(np.square(diff), axis=1)
    return dist

def is_same(dist):
    return np.less(dist, THRESHOLD)

def who_are_you(db_path, model):
    cmp_embd = get_embedding(model, CMP_IMG)
    
    identities = os.listdir(db_path)
    for id in identities:
        cur_dir = os.path.join(db_path + id)
        for img in os.listdir(cur_dir):
            cur_img = os.path.join(cur_dir, img)
            cur_embd = get_embedding(model, cur_img)
            
            who = is_same(calculate_distance(cmp_embd, cur_embd))
            if who is True:
                return id

    return 'Unknown'



def main():
    silentRemove(CMP_IMG)   # if remain compare image, remove

    os.environ['CUDA_VISIBLE_DEVICES'] = '0'

    logger = tf.get_logger()
    logger.disabled = True
    logger.setLevel(logging.FATAL)

    cfg = load_yaml('./configs/config.yaml')

    model = ArcFaceModel(size=cfg['input_size'],
                         backbone_type=cfg['backbone_type'],
                         training=False,
                         w_decay=cfg['w_decay'])
    
    ckpt_path = tf.train.latest_checkpoint('weights/checkpoints/' + cfg['sub_name'])
    if ckpt_path is not None:
        print("[*] load ckpt from {}".format(ckpt_path))
        model.load_weights(ckpt_path)
    else:
        print("[*] Cannot find ckpt from {}.".format(ckpt_path))
        exit()


    """
    수정 필요
        - 현재 얼굴 감지기만 실행되는 코드
        - 얼굴 감지기는 지속 수행되는 상태에서 기존 DB와 비교해서 만약 일치한다면? 어떻게 해야될지 고민하고 구현해야 함.
        
        - 임베딩 추출 함수의 인자가 이미지 경로인지, 넘파이 배열인지 확인 필요함
    """
    # Define threads
    face_detector = createProcess(detector.main, None)

    print("[*] start face detection")
    face_detector.start()

if __name__ == "__main__":
    main()