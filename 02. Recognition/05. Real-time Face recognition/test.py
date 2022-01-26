#%%
# ignore warnings
from silence_tensorflow import silence_tensorflow
silence_tensorflow()

#%%
from config import configurations

cfg = configurations[1]
# print(cfg)

DATA = 'C:\\Users\\Hyuntaek\\private\\k-face\\'
INPUT_SHAPE = cfg['INPUT_SHAPE']
TARGET_SIZE = cfg['TARGET_SIZE']

THRESHOLD = cfg['THRESHOLD']
# %%
from tensorflow.keras.utils import plot_model
from modules.model import loadModel

model = loadModel(INPUT_SHAPE)
# model.summary()
plot_model(model, show_shapes=True)
# %%
from tensorflow.keras import Model
output = model.get_layer('arc_margin_penalty_logists').output
face_descriptor = Model(inputs=model.layers[0].input, outputs=output)

# %%
import commons.functions as functions

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
from tqdm import tqdm

face_vectors = []

dirs = os.listdir(DATA)
for d in tqdm(dirs, desc='Get embedding....'):
    cur_dir = os.path.join(DATA, d)
    faces = os.listdir(cur_dir)

    for face in faces:
        cur_face = os.path.join(cur_dir, face)
        representation = represent(cur_face)
        
        vector = [d, representation]
        face_vectors.append(vector)

#%%
import commons.distance as dist
import numpy as np
from tqdm import tqdm

TP, FP, FN, TN = 0, 0, 0, 0

for src in tqdm(range(len(face_vectors)), desc='Evaluation...'):
    src_label = face_vectors[src][0]
    src_embd = face_vectors[src][1]
    
    for test in range(len(face_vectors)):
        if(src == test):
            continue
        
        test_label = face_vectors[test][0]
        test_embd = face_vectors[test][1]
        
        distance = dist.findCosineDistance(src_embd, test_embd)
        distance = np.float64(distance) #causes trobule for euclideans
        
        identified = False
        if distance <= THRESHOLD:
            identified = True

        if(src_label == test_label):
            if(identified):
                TP += 1
            else:
                FN += 1
        else:
            if(identified):
                FP += 1
            else:
                TN += 1

# %%
precision = TP / (TP+FP)
recall = TP / (TP+FN)
acc = (TP+TN) / (TP+FN+FP+TN)
f1_score = (2*precision*recall) / (precision+recall)

print(TP, FP, FN, TN)
print(f'precision: {precision}, recall: {recall}, acc: {acc}, f1-score: {f1_score}')







"""
Search best threshold
"""
# %%
import commons.distance as dist
import numpy as np
from tqdm import tqdm
import os

src = '7.jpg'

src_vectors = []
face_vectors = []

dirs = os.listdir(DATA)
for d in tqdm(dirs, desc='Get source embedding'):
    cur_dir = os.path.join(DATA, d)
    cur_face = os.path.join(cur_dir, src)
    representation = represent(cur_face)
    
    src_vectors.append([d, representation])

for d in tqdm(dirs, desc='Get test embedding....'):
    cur_dir = os.path.join(DATA, d)
    faces = os.listdir(cur_dir)

    for face in faces:
        if(face == src):
            continue
        
        cur_face = os.path.join(cur_dir, face)
        representation = represent(cur_face)
        
        vector = [d, representation]
        face_vectors.append(vector)

# %%

cnt = 0
idx = 0

s = []
for test in face_vectors:
    if(cnt==14):
        idx += 1

    distance = dist.findCosineDistance(src_vectors[idx][1], test[1])
    distance = np.float64(distance) #causes trobule for euclideans 
    s.append(distance)
# %%
min_value = min(s)
max_value = max(s)
avg_value = 0 if len(s) == 0 else sum(s)/len(s)

print(min_value, max_value, avg_value)
# %%

temp1 = represent('C:\\Users\\Hyuntaek\\private\\k-face\\19062421\\7.jpg')
temp2 = represent('C:\\Users\\Hyuntaek\\private\\k-face\\19062421\\7.jpg')

distance = dist.findCosineDistance(temp1, temp2)
distance = np.float64(distance)
print(distance)
# %%
