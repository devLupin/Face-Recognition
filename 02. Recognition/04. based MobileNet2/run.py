import cv2
import numpy as np
import onnx
import onnxruntime as ort

from config import configurations
from tensorflow.keras import models
from tensorflow.keras.preprocessing import image
import numpy as np
import os

MAX = 10


def area_of(left_top, right_bottom):
    """
    Compute the areas of rectangles given two corners.
    Args:
        left_top (N, 2): left top corner.
        right_bottom (N, 2): right bottom corner.
    Returns:
        area (N): return the area.
    """
    hw = np.clip(right_bottom - left_top, 0.0, None)
    return hw[..., 0] * hw[..., 1]


def iou_of(boxes0, boxes1, eps=1e-5):
    """
    Return intersection-over-union (Jaccard index) of boxes.
    Args:
        boxes0 (N, 4): ground truth boxes.
        boxes1 (N or 1, 4): predicted boxes.
        eps: a small number to avoid 0 as denominator.
    Returns:
        iou (N): IoU values.
    """
    overlap_left_top = np.maximum(boxes0[..., :2], boxes1[..., :2])
    overlap_right_bottom = np.minimum(boxes0[..., 2:], boxes1[..., 2:])

    overlap_area = area_of(overlap_left_top, overlap_right_bottom)
    area0 = area_of(boxes0[..., :2], boxes0[..., 2:])
    area1 = area_of(boxes1[..., :2], boxes1[..., 2:])
    return overlap_area / (area0 + area1 - overlap_area + eps)


def hard_nms(box_scores, iou_threshold, top_k=-1, candidate_size=200):
    """
    Perform hard non-maximum-supression to filter out boxes with iou greater
    than threshold
    Args:
        box_scores (N, 5): boxes in corner-form and probabilities.
        iou_threshold: intersection over union threshold.
        top_k: keep top_k results. If k <= 0, keep all the results.
        candidate_size: only consider the candidates with the highest scores.
    Returns:
        picked: a list of indexes of the kept boxes
    """
    scores = box_scores[:, -1]
    boxes = box_scores[:, :-1]
    picked = []
    indexes = np.argsort(scores)
    indexes = indexes[-candidate_size:]
    while len(indexes) > 0:
        current = indexes[-1]
        picked.append(current)
        if 0 < top_k == len(picked) or len(indexes) == 1:
            break
        current_box = boxes[current, :]
        indexes = indexes[:-1]
        rest_boxes = boxes[indexes, :]
        iou = iou_of(
            rest_boxes,
            np.expand_dims(current_box, axis=0),
        )
        indexes = indexes[iou <= iou_threshold]

    return box_scores[picked, :]


def predict(width, height, confidences, boxes, prob_threshold, iou_threshold=0.5, top_k=-1):
    """
    Select boxes that contain human faces
    Args:
        width: original image width
        height: original image height
        confidences (N, 2): confidence array
        boxes (N, 4): boxes array in corner-form
        iou_threshold: intersection over union threshold.
        top_k: keep top_k results. If k <= 0, keep all the results.
    Returns:
        boxes (k, 4): an array of boxes kept
        labels (k): an array of labels for each boxes kept
        probs (k): an array of probabilities for each boxes being in corresponding labels
    """
    boxes = boxes[0]
    confidences = confidences[0]
    picked_box_probs = []
    picked_labels = []
    for class_index in range(1, confidences.shape[1]):
        probs = confidences[:, class_index]
        mask = probs > prob_threshold
        probs = probs[mask]
        if probs.shape[0] == 0:
            continue
        subset_boxes = boxes[mask, :]
        box_probs = np.concatenate(
            [subset_boxes, probs.reshape(-1, 1)], axis=1)
        box_probs = hard_nms(box_probs,
                             iou_threshold=iou_threshold,
                             top_k=top_k,
                             )
        picked_box_probs.append(box_probs)
        picked_labels.extend([class_index] * box_probs.shape[0])
    if not picked_box_probs:
        return np.array([]), np.array([]), np.array([])
    picked_box_probs = np.concatenate(picked_box_probs)
    picked_box_probs[:, 0] *= width
    picked_box_probs[:, 1] *= height
    picked_box_probs[:, 2] *= width
    picked_box_probs[:, 3] *= height
    return picked_box_probs[:, :4].astype(np.int32), np.array(picked_labels), picked_box_probs[:, 4]


def pred(fname, target_size):
    """
    predict identity using temp image from web cam
    
    -args
        - fname : temp images
        - target_size : image size(224, 224)
    """
    img = image.load_img(fname, target_size=target_size)
    img_tensor = image.img_to_array(img)
    img_tensor = np.array(img_tensor, dtype="float32")

    img_tensor /= 255

    img_tensor = np.expand_dims(img_tensor, axis=0)
    return img_tensor


def main(ort_session, input_name):

    cfg = configurations[1]
    TRAIN = cfg['TRAIN_PATH']
    TARGET_SIZE = cfg['TARGET_SIZE']

    MODEL_SAVE_PATH = cfg['MODEL_SAVE_PATH']

    print("=" * 60)
    print("Overall Configurations:")
    print(cfg)
    print("=" * 60)

    model = models.load_model(MODEL_SAVE_PATH)
    people = os.listdir(TRAIN)

    video_capture = cv2.VideoCapture(0)

    cur_x, cur_y, cur_width, cur_height = 0, 0, 0, 0
    prev_x, prev_y, prev_width, prev_height = 0, 0, 0, 0
    sec = 0

    while True:
        ret, frame = video_capture.read()

        if frame is not None:
            h, w, _ = frame.shape
            # preprocess img acquired
            img = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)  # convert bgr to rgb
            img = cv2.resize(img, (640, 480))  # resize
            img_mean = np.array([127, 127, 127])
            img = (img - img_mean) / 128
            img = np.transpose(img, [2, 0, 1])
            img = np.expand_dims(img, axis=0)
            img = img.astype(np.float32)

            confidences, boxes = ort_session.run(None, {input_name: img})
            boxes, labels, probs = predict(w, h, confidences, boxes, 0.7)

            num_of_person = boxes.shape[0]

            if(num_of_person == 1):
                if(probs[0] < 0.989):
                    continue

                box = boxes[0, :]
                x1, y1, x2, y2 = box

                if(sec == 10):
                    cur_x, cur_y, cur_width, cur_height = x1, y1, x2, y2

                if(sec == 30):
                    prev_x, prev_y, prev_width, prev_height = x1, y1, x2, y2

                    if(abs(cur_x - prev_x) < MAX and
                       abs(cur_y - prev_y) < MAX and
                       abs(cur_width - prev_width) < MAX and
                       abs(cur_height - prev_height) < MAX):
                        print('capture')

                        roi = frame[y1:y1+y2, x1:x2]    # roi
                        resized = cv2.resize(
                            roi, (224, 224))   # for MobileNetV2
                        # save temp image
                        cv2.imwrite('temp.jpg', resized)

                        result = model.predict(pred('temp.jpg', TARGET_SIZE))
                        cur_y = np.argmax(result)
                        cur_acc = result[0][cur_y]
                        cur_name = people[cur_y]

                        print(cur_acc)
                        if(cur_acc < 0.949):
                            continue

                        print(cur_name)

                    sec = 0

                cv2.rectangle(frame, (x1, y1), (x2, y2), (0, 255, 0), 4)

                sec += 1

            cv2.imshow('Video', frame)

        # Hit 'q' on the keyboard to quit!
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    # Release handle to the webcam
    video_capture.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    onnx_path = 'ultra_light_640.onnx'
    onnx_model = onnx.load(onnx_path)
    ort_session = ort.InferenceSession(onnx_path)

    main(ort_session, ort_session.get_inputs()[0].name)