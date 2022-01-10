#%%
from sklearn.metrics import accuracy_score
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.metrics import confusion_matrix
import os
from tqdm import tqdm
from tensorflow.keras.preprocessing.image import ImageDataGenerator
import numpy as np
from tensorflow.keras.preprocessing import image
from tensorflow.keras import models
from config import configurations

cfg = configurations[1]
TEST = cfg['TEST_PATH']

TARGET_SIZE = cfg['TARGET_SIZE']
BATCH_SIZE = cfg['BATCH_SIZE']
EPOCHS = cfg['EPOCHS']
INPUT_SHAPE = cfg['INPUT_SHAPE']

MODEL_SAVE_PATH = cfg['MODEL_SAVE_PATH']

print("=" * 60)
print("Overall Configurations:")
print(cfg)
print("=" * 60)
#%%
model = models.load_model(MODEL_SAVE_PATH)
model.summary()

#%%


def pred(fname, target_size):
    img = image.load_img(fname, target_size=target_size)
    img_tensor = image.img_to_array(img)
    img_tensor = np.array(img_tensor, dtype="float32")

    img_tensor /= 255

    img_tensor = np.expand_dims(img_tensor, axis=0)
    return img_tensor


#%%

test_image_generator = ImageDataGenerator(rescale=1./255)
test_data_gen = test_image_generator.flow_from_directory(batch_size=BATCH_SIZE,
                                                         directory=TEST,
                                                         shuffle=True,
                                                         target_size=TARGET_SIZE,
                                                         class_mode='categorical')

#%%

test_class = test_data_gen.class_indices
labels = os.listdir(TEST)
y_pred = []
y_test = []
for label in tqdm(labels, desc='predict'):
    cur_path = os.path.join(TEST, label)
    for img in os.listdir(cur_path):
        x = os.path.join(cur_path, img)
        result = model.predict(pred(x, TARGET_SIZE))
        y_pred.append(np.argmax(result))
        y_test.append(test_class[label])

#%%

cm = confusion_matrix(y_test, y_pred)
norcm = cm/cm.astype(np.float).sum(axis=0)

#%%
plt.figure(figsize=(30, 30))
sns.heatmap(norcm,  xticklabels=test_class, yticklabels=test_class,
            annot=True, fmt='.2%', linewidths=.5)
plt.title('confusion matrix', fontsize=20)
plt.xlabel('Predicted Label')
plt.ylabel('True Label')
plt.show()

# %%
print(norcm.shape)
# %%
print(accuracy_score(y_true=y_test, y_pred=y_pred))


# %%
result = model.predict(pred('19062621.jpg', TARGET_SIZE))
labels = os.listdir(TEST)
y_pred = np.argmax(result)
print(result[0][y_pred])
print()
print(labels[np.argmax(result)])
# y_pred.append(np.argmax(result))
# y_test.append(train_class[label])
# %%
