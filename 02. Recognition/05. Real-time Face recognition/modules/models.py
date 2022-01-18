import tensorflow as tf
from tensorflow.keras import Model
from tensorflow.keras.layers import (
    Dense,
    Dropout,
    Flatten,
    Input,
)
from tensorflow.keras.applications import (
    MobileNetV2,
    ResNet50
)

from .layers import (
    BatchNormalization,
    ArcMarginPenaltyLogists
)

def _regularizer(weights_decay=5e-4):
    return tf.keras.regularizers.l2(weights_decay)

def Backbone():
    """Backbone Model"""

    def backbone(x_in):
        return MobileNetV2(input_shape=x_in.shape[1:], include_top=False,
                           weights='imagenet')(x_in)

    return backbone

def OutputLayer(embd_shape, w_decay=5e-4, name='OutputLayer'):
    """Output Layer"""
    def output_layer(x_in):
        x = inputs = Input(x_in.shape[1:])
        x = BatchNormalization()(x)
        x = Dropout(rate=0.5)(x)
        x = Flatten()(x)
        x = Dense(embd_shape, kernel_regularizer=_regularizer(w_decay))(x)
        x = BatchNormalization()(x)
        return Model(inputs, x, name=name)(x_in)
    return output_layer

def ArcHead(num_classes, margin=0.5, logist_scale=64, name='ArcHead'):
    """Arc Head"""
    def arc_head(x_in, y_in):
        x = inputs1 = Input(x_in.shape[1:])
        y = Input(y_in.shape[1:])
        x = ArcMarginPenaltyLogists(num_classes=num_classes,
                                    margin=margin,
                                    logist_scale=logist_scale)(x, y)
        return Model((inputs1, y), x, name=name)((x_in, y_in))
    return arc_head

def ArcFaceModel(input_shape=(224, 224, 3), num_classes=None,margin=0.5, 
                 logist_scale=64, embd_shape=512, w_decay=5e-4, training=False):
    """Arc Face Model"""
    x = inputs = Input(input_shape, name='input_image')

    x = Backbone()(x)

    embds = OutputLayer(embd_shape, w_decay=w_decay)(x)

    if training:
        assert num_classes is not None
        labels = Input([], name='label')

        logist = ArcHead(num_classes=num_classes, margin=margin,
                         logist_scale=logist_scale)(embds, labels)
        
        return Model((inputs, labels), logist, name='arcface_model')
    else:
        return Model(inputs, embds, name='arcface_model')