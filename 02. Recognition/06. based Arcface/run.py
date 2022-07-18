import os, errno
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'

from multiprocessing import Process
from absl import logging
import numpy as np
import tensorflow as tf

from networks.models import ArcFaceModel
from utils import load_yaml, l2_norm

import cv2
from PIL import Image

from silence_tensorflow import silence_tensorflow
silence_tensorflow()

from detector import mbv2_ssd_based as detector

CMP_IMG = 'temp.PNG'
THRESHOLD = 0.01


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

def who_are_you(db_path):
    while(True):
        if not os.path.isfile(CMP_IMG):
            continue
        
        # cmp_img = cv2.imread(CMP_IMG, cv2.IMREAD_COLOR)
        # cv2.imshow('', cmp_img)
        
        cmp_embd = np.array(Image.open(CMP_IMG), dtype=np.uint8)
        cmp_embd = get_embedding(model, cmp_embd)

        identities = os.listdir(db_path)
        for id in identities:
            cur_dir = os.path.join(db_path + id)
            for img in os.listdir(cur_dir):
                cur_img = os.path.join(cur_dir, img)

                cur_embd = np.array(Image.open(cur_img), dtype=np.uint8)
                cur_embd = get_embedding(model, cur_embd)

                who = is_same(calculate_distance(cmp_embd, cur_embd))
                if who is True:
                    print(f'id: {id}')
                    return id

        return 'Unknown'



def main():
    silentRemove(CMP_IMG)   # if remain compare image, remove

    os.environ['CUDA_VISIBLE_DEVICES'] = '0'

    logger = tf.get_logger()
    logger.disabled = True
    logger.setLevel(logging.FATAL)

    cfg = load_yaml('./configs/config.yaml')

    global model
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


    # Define threads
    face_detector = createProcess(detector.main, None)
    face_recognizer = Process(target=who_are_you, args=('./sclab',))
    
    print("[*] start face detection")
    face_detector.start()
    face_recognizer.start()
    
    face_recognizer.join()
    

if __name__ == "__main__":
    main()