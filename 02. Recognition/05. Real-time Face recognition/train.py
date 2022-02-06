from silence_tensorflow import silence_tensorflow
silence_tensorflow()

import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'

from absl import app, flags, logging
from absl.flags import FLAGS
import tensorflow as tf
from tensorflow.keras.callbacks import ModelCheckpoint, TensorBoard

from modules.models import ArcFaceModel
from modules.losses import SoftmaxLoss
from modules.utils import set_memory_growth, load_yaml, get_ckpt_inf
import modules.dataset as dataset
from tensorflow.keras.utils import plot_model

def main(_):
    os.environ['CUDA_VISIBLE_DEVICES'] = '0'

    logger = tf.get_logger()
    logger.disabled = True
    logger.setLevel(logging.FATAL)
    set_memory_growth()

    cfg = load_yaml('./configs/arc_mbv2.yaml')

    model = ArcFaceModel(size=cfg['input_size'],
                         backbone_type=cfg['backbone_type'],
                         num_classes=cfg['num_classes'],
                         head_type=cfg['head_type'],
                         embd_shape=cfg['embd_shape'],
                         w_decay=cfg['w_decay'],
                         training=True)
    model.summary()
    plot_model(model, to_file='model.png')


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


    model.compile(optimizer=optimizer, loss=loss_fn)
    
    mc_callback = ModelCheckpoint(
        'checkpoints/' + cfg['sub_name'] + '/e_{epoch}_b_{batch}.ckpt',
        save_freq=cfg['save_steps'] * cfg['batch_size'], verbose=1,
        save_weights_only=True)
    
    tb_callback = TensorBoard(log_dir='logs/',
                              update_freq=cfg['batch_size'] * 5,
                              profile_batch=0)
    
    tb_callback._total_batches_seen = steps
    tb_callback._samples_seen = steps * cfg['batch_size']
    callbacks = [mc_callback, tb_callback]
    
    global history
    history = model.fit(train_dataset,
              epochs=cfg['epochs'],
              steps_per_epoch=steps_per_epoch,
              callbacks=callbacks,
              initial_epoch=epochs - 1)

    print("[*] training done!")
    
    history.history.keys()

    import matplotlib.pyplot as plt

    acc = history.history['accuracy']
    val_acc = history.history['val_accuracy']
    loss = history.history['loss']
    val_loss = history.history['val_loss']

    print(f'acc: {acc}, loss = {loss}')
    
    model.save('Model')

if __name__ == '__main__':
    app.run(main)