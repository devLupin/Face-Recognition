import os
from glob import glob
import cv2
from tqdm import tqdm
from PIL import Image
import splitfolders
import shutil
import numpy as np

def file_move(root_path, dest_path):
    """
        Conditions
            - ACC
                S001 : Normal
                S002 : Normal Glasses
                S003 : horn-rimmed glasses
            - Light
                L1 : 1000 lux
                L2 : 400 lux
                L3 : 200 lux
            - Expression
                E01 : Neutral
            - Camera(vertical, horizontal)
                C5 : 0, +30
                C6 : 0, +15
                C7 : 0, 0
                C8 : 0, -15
                C9 : 0, -30
    """
    ACC = ["S001"]
    LIGHT = ["L1", "L3"]
    EXPRESSION = ["E01", "E02", "E03"]
    CAMERA = ["C6", "C7", "C8", "C9"]

    allFolders = os.listdir(root_path)

    # for folder in allFolders:
    #     print(folder)

    for id in tqdm(allFolders):
        num = 0
        cur_id = os.path.join(root_path, id)
        cur_save_path = os.path.join(dest_path, id)

        os.makedirs(cur_save_path, exist_ok=True)   # If not exist, make directory. Else None
        for acc in ACC:
            cur_acc = os.path.join(cur_id, acc)
            for light in LIGHT:
                cur_light = os.path.join(cur_acc, light)
                for expression in EXPRESSION:
                    cur_expression = os.path.join(cur_light, expression)
                    for camera in CAMERA:
                        cur_camera = os.path.join(cur_expression, camera)

                        cur_img_src = cur_camera + ".jpg"
                        cur_img_dest = os.path.join(cur_save_path, str(num) + ".jpg")

                        cur_txt_src = cur_camera + '.txt'
                        cur_txt_dest = os.path.join(cur_save_path, str(num) + ".txt")

                        shutil.copyfile(cur_img_src, cur_img_dest)
                        shutil.copyfile(cur_txt_src, cur_txt_dest)

                        num += 1

def crop(data_path, dest_path):
    if not os.path.exists(dest_path):
        os.makedirs(dest_path)
    
    classes = os.listdir(data_path)
    
    for c in tqdm(classes, desc=f'cropping'):
        imgs = glob(data_path + '/' + c + '/' + '*.jpg')
        txts = glob(data_path + '/' + c + '/' + '*.txt')

        for i, (img, txt) in enumerate(zip(imgs, txts)):
            name = str(i)   # img name
            
            with open(txt, 'r') as f:
                bbox = f.read().split('\n')[7].split()
                bbox = list(map(int, bbox))
                (x, y, w, h) = bbox

                img = cv2.imread(img)
                img = img[y: y + h, x: x + w]
                img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
                img = cv2.resize(img, (112, 112))
                
                save_path = os.path.join(dest_path, c)
                if not os.path.exists(save_path):
                        os.makedirs(save_path)
                
                Image.fromarray(img).save(os.path.join(save_path, name + '.jpg'))

def split(cropped_path):
    splitfolders.ratio(cropped_path, output="datasets", seed=1337, ratio=(.8, .2))   # train : test = 8 : 2

def make_pair_numpy():
    ref_imgs = []
    query_imgs = []
    is_same = []

    with open("data/references.csv", 'w', encoding='utf-8') as f:
        f.write("references, queries, is_same\n")
        for _ in range(400):
            classes = os.listdir("datasets/val")

            ref_number = np.random.randint(24)
            ref_class = np.random.choice(classes)
            ref_name = os.path.join("datasets/val", ref_class, f"{ref_class}_{str(ref_number)}.jpg")
            f.write(f"{os.path.basename(ref_name)}, ")
            ref_imgs.append(np.array(Image.open(ref_name), dtype=np.uint8))

            is_pair = np.random.randint(2)
            query_number = np.random.randint(24)
            if is_pair:
                is_same.append(1)
                query_name = os.path.join("datasets/val", ref_class, f"{ref_class}_{str(query_number)}.jpg")
            else:
                is_same.append(0)
                classes.remove(ref_class)
                query_class = np.random.choice(classes)
                query_name = os.path.join("datasets/val", query_class, f"{query_class}_{str(query_number)}.jpg")

            f.write(f"{os.path.basename(query_name)}, {is_pair}\n")
            query_imgs.append(np.array(Image.open(query_name), dtype=np.uint8))

    ref_imgs = np.array(ref_imgs, dtype=np.uint8)
    query_imgs = np.array(query_imgs, dtype=np.uint8)
    is_same = np.array(is_same, dtype=np.uint8)

    if not os.path.exists('kface_val_npy'):
        os.makedirs('kface_val_npy')

    np.save('kface_val_npy/references.npy', ref_imgs)
    np.save('kface_val_npy/queries.npy', query_imgs)
    np.save('kface_val_npy/is_same.npy', is_same)


def main():
    root_path = "E:\\K-Face\\03. Middle_Resolution\\"     # K-Face dir path

    data_path = 'data'
    crop_path = 'cropped'
    
    # file_move(root_path, data_path)
    # crop(data_path, crop_path)
    # split(crop_path)

if __name__ == "__main__":
    main()