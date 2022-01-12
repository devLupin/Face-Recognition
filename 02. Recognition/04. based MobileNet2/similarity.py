import numpy as np

def cosine_distance(img1, img2):
    a = np.matmul(np.transpose(img1), img2)
    b = np.sum(np.multiply(img1, img1))
    c = np.sum(np.multiply(img2, img2))
    return 1 - (a / (np.sqrt(b) * np.sqrt(c)))

def euclidean_distance(img1, img2):
    euclidean_distance = img1 - img2
    euclidean_distance = np.sum(np.multiply(euclidean_distance, euclidean_distance))
    euclidean_distance = np.sqrt(euclidean_distance)
    return euclidean_distance
