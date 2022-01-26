from tensorflow.keras.layers import Layer, BatchNormalization
import tensorflow as tf
import math

class BatchNormalization(BatchNormalization):
    """Make trainable=False freeze BN for real (the og version is sad).
       ref: https://github.com/zzh8829/yolov3-tf2
    """
    def call(self, x, training=False):
        if training is None:
            training = tf.constant(False)
        training = tf.logical_and(training, self.trainable)
        return super().call(x, training)

class ArcMarginPenaltyLogists(Layer):
    """ArcMarginPenaltyLogists"""
    def __init__(self, num_outputs, margin=0.5, logist_scale=64, **kwargs):
        """
        입력과 독립적인 모든 초기화 수행
        """
        super(ArcMarginPenaltyLogists, self).__init__(**kwargs)
        self.num_outputs = num_outputs
        self.margin = margin
        self.logist_scale = logist_scale

    def build(self, input_shape):
        """
        입력 텐서의 shape에 기반한 초기화 수행
        """
        self.w = self.add_variable(
            "weights", shape=[int(input_shape[-1]), self.num_outputs])
        self.cos_m = tf.identity(math.cos(self.margin), name='cos_m')
        self.sin_m = tf.identity(math.sin(self.margin), name='sin_m')
        self.th = tf.identity(math.cos(math.pi - self.margin), name='th')
        self.mm = tf.multiply(self.sin_m, self.margin, name='mm')

    def call(self, embds, labels):
        """
        propagation
        """
        normed_embds = tf.nn.l2_normalize(embds, axis=1, name='normed_embd')
        normed_w = tf.nn.l2_normalize(self.w, axis=0, name='normed_weights')

        cos_t = tf.matmul(normed_embds, normed_w, name='cos_t')
        sin_t = tf.sqrt(1. - cos_t ** 2, name='sin_t')

        cos_mt = tf.subtract(
            cos_t * self.cos_m, sin_t * self.sin_m, name='cos_mt')

        cos_mt = tf.where(cos_t > self.th, cos_mt, cos_t - self.mm)

        mask = tf.one_hot(tf.cast(labels, tf.int32), depth=self.num_outputs,
                          name='one_hot_mask')

        logists = tf.where(mask == 1., cos_mt, cos_t)
        logists = tf.multiply(logists, self.logist_scale, 'arcface_logist')

        return logists