import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'

from absl import app, logging
import cv2
import numpy as np
import tensorflow as tf

from modules.models import ArcFaceModel
from modules.utils import set_memory_growth, load_yaml, l2_norm


def main(_argv):
    os.environ['CUDA_VISIBLE_DEVICES'] = '0'

    logger = tf.get_logger()
    logger.disabled = True
    logger.setLevel(logging.FATAL)
    set_memory_growth()

    cfg = load_yaml('./configs/arc_mbv2.yaml')
    test_path = cfg['test_dataset']

    model = ArcFaceModel(size=cfg['input_size'],
                         training=False,
                         w_decay=cfg['w_decay'])

    ckpt_path = tf.train.latest_checkpoint('./checkpoints/' + cfg['sub_name'])
    if ckpt_path is not None:
        print("[*] load ckpt from {}".format(ckpt_path))
        model.load_weights(ckpt_path)
    else:
        print("[*] Cannot find ckpt from {}.".format(ckpt_path))
        exit()

    if test_path:
        print("[*] Encode {} to ./output_embeds.npy".format(test_path))
        img = cv2.imread(test_path)
        img = cv2.resize(img, (cfg['input_size'], cfg['input_size']))
        img = img.astype(np.float32) / 255.
        img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        if len(img.shape) == 3:
            img = np.expand_dims(img, 0)
        embeds = l2_norm(model(img))
        np.save('./output_embeds.npy', embeds)


if __name__ == '__main__':
    try:
        app.run(main)
    except SystemExit:
        pass
