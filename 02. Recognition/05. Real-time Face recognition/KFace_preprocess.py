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
            ref_number = np.random.randint(num_imgs)
            
            imgs = os.listdir(os.path.join(path, ref_class))
            cur_ref_img = imgs[ref_number]
            
            ref_name = os.path.join(path, ref_class, cur_ref_img)
            f.write(f"{ref_class}/{os.path.basename(ref_name)}, ")
            ref_imgs.append(np.array(Image.open(ref_name), dtype=np.uint8))

            is_pair = np.random.randint(2)
            
            if is_pair:
                is_same.append(1)
                query_class = ref_class
                num_query_imgs = num_imgs
                query_number = np.random.randint(num_query_imgs)
                cur_query_img = imgs[query_number]
                query_name = os.path.join(path, query_class, cur_query_img)
            else:
                is_same.append(0)
                classes.remove(ref_class)
                query_class = np.random.choice(classes)
                imgs = os.listdir(os.path.join(path, query_class))
                num_query_imgs = len(os.listdir(os.path.join(path, query_class)))
                query_number = np.random.randint(num_query_imgs)
                cur_query_img = imgs[query_number]
                query_name = os.path.join(path, query_class, cur_query_img)

            f.write(f"{query_class}/{os.path.basename(query_name)}, {is_pair}\n")
            query_imgs.append(np.array(Image.open(query_name), dtype=np.uint8))

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

def make_pair_custom(img_path, dataset_name):
    save_path = 'data/'+ dataset_name + '_npy'
    if not os.path.exists(save_path):
            os.makedirs(save_path)
    
    ref_imgs = []
    query_imgs = []
    is_same = []

    classes = os.listdir(img_path)

    with open(save_path + '/references.csv', 'w', encoding='utf-8') as f:
        f.write("references, queries, is_same\n")
        
        for i in range(len(classes)):
            for j in range(len(classes)):
                
                cur_ref_path = os.path.join(img_path, classes[i])
                cur_query_path = os.path.join(img_path, classes[j])
                
                ref = os.listdir(cur_ref_path)
                query = os.listdir(cur_query_path)
                
                for r in tqdm(ref):
                    for q in query:
                        if i==j and r==q:
                            continue
                        
                        f.write(f'{i}/{os.path.basename(r)}, {j}/{os.path.basename(q)}, ')
                        ref_imgs.append(np.array(Image.open(os.path.join(cur_ref_path, r))))
                        query_imgs.append(np.array(Image.open(os.path.join(cur_query_path, q))))
                        
                        if i==j:
                            is_pair=1
                        else:
                            is_pair=0
                            
                        f.write(f'{is_pair} \n')
                        is_same.append(is_pair)

        ref_imgs = np.array(ref_imgs, dtype=np.uint8)
        query_imgs = np.array(query_imgs, dtype=np.uint8)
        is_same = np.array(is_same, dtype=np.uint8)

        np.save(save_path + '/references.npy', ref_imgs)
        np.save(save_path + '/queries.npy', query_imgs)
        np.save(save_path + '/is_same.npy', is_same)


def main():
    # make_pair_numpy('data/datasets/val', 'data/kface_test_npy/', 400)
    # make_pair_numpy_benchmark()
    make_pair_custom('data/datasets/sclab_masked', 'sclab_masked')
    # make_pair_numpy('data/datasets/sclab_masked', 'data/scalb_masked_test_npy/', 6)


if __name__ == "__main__":
    main()