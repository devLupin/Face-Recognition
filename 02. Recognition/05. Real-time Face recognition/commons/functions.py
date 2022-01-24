from tensorflow.keras.preprocessing import image
import numpy as np
import cv2


def preprocess_face(fname, target_size=None):
    img = image.load_img(fname, target_size=target_size)

    img_pixels = image.img_to_array(img)
    img_pixels = np.expand_dims(img_pixels, axis=0)
    img_pixels /= 255  # normalize input in [0, 1]

    return img_pixels


def normalize_input(img):
    """
    Each pixel (ranged between [0, 255]) in RGB images is normalised
	by subtracting 127.5 then divided by 128.
    """
    img -= 127.5
    img /= 128
    return img