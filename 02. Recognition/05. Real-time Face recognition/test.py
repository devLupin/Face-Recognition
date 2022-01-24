#%%
# ignore warnings
from silence_tensorflow import silence_tensorflow
silence_tensorflow()

#%%
from config import configurations

cfg = configurations[1]

TRAIN = cfg['TRAIN_PATH']
TEST = cfg['TEST_PATH']
INPUT_SHAPE = cfg['INPUT_SHAPE']
TARGET_SIZE = cfg['TARGET_SIZE']

THRESHOLD = cfg['THRESHOLD']
# %%
from tensorflow.keras.utils import plot_model
from modules.model import loadModel

model = loadModel(INPUT_SHAPE)
model.summary()
plot_model(model, show_shapes=True)
# %%
from tensorflow.keras import Model
face_descriptor = Model(inputs=model.layers[0].input, outputs=model.layers[-1].output)

# %%
def represent(img_path):
    """
    Represents facial images as vectors.
    
    Params:
        img_path = exact image path, numpy array or based64 encoded images could be passed.
    """
    # normalization
    img = functions.preprocess_face(fname=img_path, target_size=TARGET_SIZE)
    img = functions.normalize_input(img=img)
    
    return (face_descriptor.predict(img)[0].tolist())
# %%
import os
import commons.functions as functions
import commons.distance as dist
import numpy as np

dirs = os.listdir(TRAIN)
for d in dirs:
    cur_dir = os.path.join(TRAIN, d)
    faces = os.listdir(cur_dir)
    
    for face in faces:
        cur_face = os.path.join(cur_dir, face)
        representation = represent(cur_face)

distance = dist.findCosineDistance(source_representation=representation, test_representation=representation)
distance = np.float64(distance) #causes trobule for euclideans
print(distance)
# %%
if distance <= THRESHOLD:
	identified = True
else:
	identified = False