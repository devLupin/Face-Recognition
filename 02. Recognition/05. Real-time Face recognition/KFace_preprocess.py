import numpy as np
import os
import cv2
from PIL import Image
from tqdm import tqdm
from glob import glob

from utils import crop_face_from_id


def make_pair_numpy(path=None, save_path=None, num_classes=None):
    if not os.path.exists(save_path):
        os.makedirs(save_path)
    
    ref_imgs = []
    query_imgs = []
    is_same = []

    with open(save_path + "references.csv", 'w', encoding='utf-8') as f:
        f.write("references, queries, is_same\n")
        for _ in tqdm(range(num_classes), desc='make_pair_numpy()'):
            classes = os.listdir(path)
            ref_class = np.random.choice(classes)
            num_imgs = len(os.listdir(os.path.join(path, ref_class)))
            if num_imgs==0:
                continue
            ref_number = np.random.randint(num_imgs)
            
            imgs = os.listdir(os.path.join(path, ref_class))
            cur_ref_img = imgs[ref_number]
            
            ref_name = os.path.join(path, ref_class, cur_ref_img)

            is_pair = np.random.randint(2)
            
            if is_pair:
                query_class = ref_class
                num_query_imgs = num_imgs
                if num_query_imgs==0:
                    continue
                query_number = np.random.randint(num_query_imgs)
                cur_query_img = imgs[query_number]
                query_name = os.path.join(path, query_class, cur_query_img)
            else:
                classes.remove(ref_class)
                query_class = np.random.choice(classes)
                imgs = os.listdir(os.path.join(path, query_class))
                num_query_imgs = len(os.listdir(os.path.join(path, query_class)))
                if num_query_imgs==0:
                    continue
                query_number = np.random.randint(num_query_imgs)
                cur_query_img = imgs[query_number]
                query_name = os.path.join(path, query_class, cur_query_img)

            f.write(f"{ref_class}/{os.path.basename(ref_name)}, ")
            f.write(f"{query_class}/{os.path.basename(query_name)}, {is_pair}\n")
            
            ref_imgs.append(np.array(Image.open(ref_name), dtype=np.uint8))
            query_imgs.append(np.array(Image.open(query_name), dtype=np.uint8))
            is_same.append(is_pair)

    ref_imgs = np.array(ref_imgs, dtype=np.uint8)
    query_imgs = np.array(query_imgs, dtype=np.uint8)
    is_same = np.array(is_same, dtype=np.uint8)

    np.save(save_path + 'references.npy', ref_imgs)
    np.save(save_path + 'queries.npy', query_imgs)
    np.save(save_path + 'is_same.npy', is_same)


def make_pair_numpy_benchmark():
    ref_imgs = []
    query_imgs = []
    is_same = []
    ref_img_paths = glob("ids/*.jpg")
    query_img_paths = glob("faces/*.jpg")

    with open("references_benchmark.csv", 'w', encoding='utf-8') as f:
        f.write("references, queries, is_same\n")
        for i, ref_name in enumerate(ref_img_paths):
            print(f"Iter {i+1:2d}: {os.path.basename(ref_name)}")
            # random_ref = np.random.randint(40)
            # random_query = np.random.randint(40)
            # ref_name = ref_img_paths[random_ref]
            # query_name = query_img_paths[random_query]
            for query_name in tqdm(query_img_paths):
                f.write(f"{os.path.basename(ref_name)}, {os.path.basename(query_name)}, ")
                if os.path.basename(ref_name) == os.path.basename(query_name):
                    is_pair = 1
                else:
                    is_pair = 0
                f.write(f"{is_pair}\n")

                ref_img = cv2.imread(ref_name)
                ref_img = crop_face_from_id(ref_img, weight_path="../weights")
                ref_img = cv2.resize(ref_img, (112, 112))
                query_img = cv2.imread(query_name)
                query_img = crop_face_from_id(query_img, weight_path="../weights")
                query_img = cv2.resize(query_img, (112, 112))

                ref_imgs.append(ref_img)
                query_imgs.append(query_img)
                is_same.append(is_pair)

        ref_imgs = np.array(ref_imgs, dtype=np.uint8)
        query_imgs = np.array(query_imgs, dtype=np.uint8)
        is_same = np.array(is_same, dtype=np.uint8)

        if not os.path.exists('benchmark_npy'):
            os.makedirs('benchmark_npy')

        np.save('benchmark_npy/references.npy', ref_imgs)
        np.save('benchmark_npy/queries.npy', query_imgs)
        np.save('benchmark_npy/is_same.npy', is_same)


def main():
    # make_pair_numpy('data/datasets/val', 'data/kface_test_npy/', 400)
    # make_pair_numpy('data/datasets/val_masked', 'data/kface_test_masked_npy/', 400)
    make_pair_numpy('data/datasets/lfw_masked', 'data/lfw_masked_test_npy/', 5749)
    # make_pair_numpy('data/datasets/val_2n', 'data/kface_test_2n_npy/', 800)
    
    # make_pair_numpy_benchmark()


if __name__ == "__main__":
    main()