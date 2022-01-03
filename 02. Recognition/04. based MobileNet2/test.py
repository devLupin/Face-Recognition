#%%
from config import configurations

cfg = configurations[1]
TRAIN = cfg['TRAIN_PATH']
TEST = cfg['TEST_PATH']
TARGET_SIZE = cfg['TARGET_SIZE']
BATCH_SIZE = cfg['BATCH_SIZE']

print("=" * 60)
print("Overall Configurations:")
print(cfg)
print("=" * 60)
#%%
from tensorflow.keras import models
model = models.load_model('Model')
model.summary()

#%%
from  tensorflow.keras.preprocessing import image
import numpy as np

def pred(fname, target_size):
    img = image.load_img(fname, target_size=target_size)
    img_tensor = image.img_to_array(img)
    img_tensor = np.array(img_tensor,dtype="float32")

    img_tensor /= 255

    img_tensor = np.expand_dims(img_tensor, axis=0)
    return img_tensor

#%%
import os
from tensorflow.keras.preprocessing.image import ImageDataGenerator

train_image_generator = ImageDataGenerator(rescale=1./255)
train_data_gen = train_image_generator.flow_from_directory(batch_size=BATCH_SIZE,
                                                           directory=TRAIN,
                                                           shuffle=True,
                                                           target_size=TARGET_SIZE,
                                                           class_mode='categorical')

#%%
from tqdm import tqdm

train_class = train_data_gen.class_indices
labels = os.listdir(TEST)
y_pred = []
y_test = []
for label in tqdm(labels, desc='predict'):
    cur_path = os.path.join(TEST, label)
    for img in os.listdir(cur_path):
        x = os.path.join(cur_path, img)
        result = model.predict(pred(x, TARGET_SIZE))
        y_pred.append(np.argmax(result))
        y_test.append(train_class[label])

#%%
from sklearn.metrics import confusion_matrix
import matplotlib.pyplot as plt
import seaborn as sns

cm = confusion_matrix(y_test, y_pred)
norcm=cm/cm.astype(np.float).sum(axis=0)

#%%
plt.figure(figsize=(30,30))
sns.heatmap(norcm,  xticklabels=train_class, yticklabels=train_class, annot=True, fmt='.2%', linewidths=.5)
plt.title('confusion matrix', fontsize=20)
plt.xlabel('Predicted Label')
plt.ylabel('True Label')
plt.show()

# %%
print(norcm.shape)
# %%
from sklearn.metrics import accuracy_score
print(accuracy_score(y_true = y_test, y_pred=y_pred))
# %%
