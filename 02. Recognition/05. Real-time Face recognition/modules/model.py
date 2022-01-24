from tensorflow.keras.layers import (
    Dense,
    Dropout,
    Flatten,
    BatchNormalization,
)
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras import Model

def loadModel(input_shape=None):
	base_model = MobileNetV2(input_shape=input_shape,
                             include_top=False,
                             weights='imagenet')

	inputs = base_model.inputs[0]
	arcface_model = base_model.outputs[0]
	arcface_model = BatchNormalization(momentum=0.9, epsilon=2e-5)(arcface_model)
	arcface_model = Dropout(0.4)(arcface_model)
	arcface_model = Flatten()(arcface_model)
	arcface_model = Dense(
	    512, activation=None, use_bias=True, kernel_initializer="glorot_normal")(arcface_model)
	embedding = BatchNormalization(
	    momentum=0.9, epsilon=2e-5, name="embedding", scale=True)(arcface_model)
	model = Model(inputs, embedding, name=base_model.name)

	return model