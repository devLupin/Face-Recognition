import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'

import data.dataloader as dataset
from utils import load_yaml, get_ckpt_inf
from networks.losses import SoftmaxLoss
from networks.models import ArcFaceModel
import tensorflow as tf
from absl import app, logging

from silence_tensorflow import silence_tensorflow
silence_tensorflow()


def main(_):
    os.environ['CUDA_VISIBLE_DEVICES'] = '0'

    logger = tf.get_logger()
    logger.disabled = True
    logger.setLevel(logging.FATAL)

    cfg = load_yaml('./configs/config.yaml')

    model = ArcFaceModel(size=cfg['input_size'],
                         backbone_type=cfg['backbone_type'],
                         num_classes=cfg['num_classes'],
                         head_type=cfg['head_type'],
                         embd_shape=cfg['embd_shape'],
                         w_decay=cfg['w_decay'],
                         training=True)
    model.summary(line_length=80)

    # train dataset
    logging.info("load K-Face dataset.")
    dataset_len = cfg['num_samples']
    steps_per_epoch = dataset_len // cfg['batch_size']
    train_dataset = dataset.load_tfrecord_dataset(
        cfg['train_dataset'], cfg['batch_size'], cfg['binary_img'],
        is_ccrop=cfg['is_ccrop'])

    learning_rate = tf.constant(cfg['base_lr'])
    optimizer = tf.keras.optimizers.SGD(
        learning_rate=learning_rate, momentum=0.9, nesterov=True)
    loss_fn = SoftmaxLoss()

    ckpt_path = tf.train.latest_checkpoint('./checkpoints/' + cfg['sub_name'])
    if ckpt_path is not None:
        print("[*] load ckpt from {}".format(ckpt_path))
        model.load_weights(ckpt_path)
        epochs, steps = get_ckpt_inf(ckpt_path, steps_per_epoch)
    else:
        print("[*] training from scratch.")
        epochs, steps = 1, 1

    train_dataset = iter(train_dataset)
    summary_writer = tf.summary.create_file_writer("logs/" + cfg['sub_name'])

    # training loop
    while epochs <= cfg['epochs']:
        inputs, labels = next(train_dataset)
        
        with tf.GradientTape() as tape:
            logits = model(inputs, training=True)
            reg_loss = tf.reduce_sum(model.losses)
            pred_loss = loss_fn(labels, logits)
            total_loss = pred_loss + reg_loss

        grads = tape.gradient(total_loss, model.trainable_variables)
        optimizer.apply_gradients(zip(grads, model.trainable_variables))

        if steps % (cfg['save_steps'] // 10) == 0:
            verb_str = "Epoch {}/{}: {}/{}, loss={:.2f}, lr={:.4f}"
            print(verb_str.format(epochs, cfg['epochs'],
                                  steps % steps_per_epoch,
                                  steps_per_epoch,
                                  total_loss.numpy(),
                                  learning_rate.numpy()))

            with summary_writer.as_default():
                tf.summary.scalar(
                    'loss/total loss', total_loss, step=steps)
                tf.summary.scalar(
                    'loss/pred loss', pred_loss, step=steps)
                tf.summary.scalar(
                    'loss/reg loss', reg_loss, step=steps)
                tf.summary.scalar(
                    'learning rate', optimizer.lr, step=steps)

        if steps % cfg['save_steps'] == 0:
            print('[*] save ckpt file!')
            model.save_weights('checkpoints/{}/e_{}_b_{}.ckpt'.format(
                cfg['sub_name'], epochs, steps % steps_per_epoch))

        steps += 1
        epochs = steps // steps_per_epoch + 1

    print("[*] training done!")


if __name__ == '__main__':
    app.run(main)