#%%
import os
from tqdm import tqdm
import numpy as np
from tensorflow.keras.preprocessing import image
from tensorflow import keras
from tensorflow.keras import models
from config import configurations

cfg = configurations[1]

MODEL_SAVE_PATH = cfg['MODEL_SAVE_PATH']
TEST = 'C:\\Users\\devLupin\\★ 작업\\face data\\' + 'sclab'


model = models.load_model(MODEL_SAVE_PATH)
model.summary()
# %%
descriptor = keras.Model(
    inputs=model.get_layer('mobilenetv2_1.00_224').input,
    outputs=model.get_layer('mobilenetv2_1.00_224').output
)

# %%
from utils import prerequisite, save_embedding, load_embedding

PATH = 'C:\\Users\\devLupin\\★ 작업\\face data\\' + 'sclab'
SAVE_PATH = 'C:\\Users\\devLupin\\Desktop\\04. based MobileNet2'

labels, npy_path = prerequisite(PATH, SAVE_PATH)
