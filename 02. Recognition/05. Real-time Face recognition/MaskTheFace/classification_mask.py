import os
from tqdm import tqdm

dataset = ['train_masked']  # your dataset path

for i in tqdm(range(len(dataset)), desc='remove Unnecessary data'):
    dirs = os.listdir(dataset[i])
    for d in dirs:
        cur_dir = os.path.join(dataset[i], d)
        imgs = os.listdir(cur_dir)
        
        for img in imgs:
            img_path = os.path.join(cur_dir, img)
            if 'gas' in img:    # include 'gas' image
                os.remove(img_path)
            if 'blue' in img:   # include 'surgical_blue' image
                os.remove(img_path)
            if 'green' in img:  # include '0_surgical_green' image
                os.remove(img_path)