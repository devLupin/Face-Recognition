from tensorflow.keras.preprocessing import image
import os
import numpy as np
from tqdm import tqdm

def make_dirs(PATH, labels):
    """
        Create the necessary directories
        
        args:
            -PATH: root path
            -labels: id list
    """
    npy_path = os.path.join(PATH, 'npys')
    os.makedirs(npy_path, exist_ok=True)

    for label in labels:
        path = os.path.join(npy_path, label)
        os.makedirs(path, exist_ok=True)
    
    return npy_path

def prerequisite(PATH, SAVE_PATH):
    """
        return labels, npy_path
        
        args:
            
    """
    labels = os.listdir(PATH)
    return labels, make_dirs(SAVE_PATH, labels)

def get_embedding(fname, target_size=(224, 224)):
    """
        return embedding vector
        
        args:
            -fname: filepath + filename
    """
    img = image.load_img(fname, target_size=target_size)
    img_tensor = image.img_to_array(img)
    img_tensor = np.array(img_tensor, dtype="float32")

    img_tensor /= 255

    img_tensor = np.expand_dims(img_tensor, axis=0)
    return img_tensor

def save_embedding(PATH, labels, npy_path):
    """
        Save embedding vector to PATH
        
        args:
            -PATH: root path
            -labels: id list
            -npy_path : return make_dirs()

    """
    for i in range(len(labels)):
        cur_dir = os.path.join(PATH, labels[i])
        imgs = os.listdir(cur_dir)

        cnt = 0
        for img in tqdm(imgs, desc=f'{labels[i]} to embedding...'):
            cur_img = os.path.join(cur_dir, img)
            pixel = get_embedding(cur_img)

            save_path = os.path.join(npy_path, labels[i])
            save_path = os.path.join(save_path, str(cnt))
            np.save(save_path, pixel)
            cnt+=1

def load_embedding(path):
    return np.load(path)

"""
def load_embedding(labels, npy_path):
    ret = np.array([])
    
    for i in range(len(labels)):
        cur_dir = os.path.join(npy_path, labels[i])
        imgs = os.listdir(cur_dir)
        for img in tqdm(imgs, desc=f'[{labels[i]}] npy load...'):
            path = os.path.join(cur_dir, img)
            cur_npy = np.load(path)
            ret = np.append(ret, cur_npy)
    return ret
"""