#%%
import tensorflow_addons as tfa
from tensorflow.keras.utils import plot_model
from tensorflow.keras.callbacks import EarlyStopping, ModelCheckpoint
from tensorflow.keras.applications.mobilenet_v2 import MobileNetV2
from tensorflow.keras.optimizers import SGD
from tensorflow.keras.layers import Dense, GlobalAveragePooling2D
from tensorflow.keras.models import Sequential
import matplotlib.pyplot as plt
from tensorflow.keras.preprocessing.image import ImageDataGenerator
import os
from config import configurations

cfg = configurations[1]
TRAIN = cfg['TRAIN_PATH']
VAL = cfg['VAL_PATH']
TARGET_SIZE = cfg['TARGET_SIZE']
BATCH_SIZE = cfg['BATCH_SIZE']
EPOCHS = cfg['EPOCHS']
INPUT_SHAPE = cfg['INPUT_SHAPE']

CHECK_POINT = cfg['CHECK_POINT']
MODEL_SAVE_PATH = cfg['MODEL_SAVE_PATH']

print("=" * 60)
print("Overall Configurations:")
print(cfg)
print("=" * 60)

#%%
# Data preparing


classes = os.listdir(TRAIN)

train_image_generator = ImageDataGenerator(
    rescale=1./255)  # Generator for our training data
val_image_generator = ImageDataGenerator(rescale=1./255)

train_data_gen = train_image_generator.flow_from_directory(batch_size=BATCH_SIZE,
                                                           directory=TRAIN,
                                                           shuffle=True,
                                                           target_size=TARGET_SIZE,
                                                           class_mode='categorical')
val_data_gen = val_image_generator.flow_from_directory(batch_size=BATCH_SIZE,
                                                       directory=VAL,
                                                       shuffle=True,
                                                       target_size=TARGET_SIZE,
                                                       class_mode='categorical')

num_of_samples = train_data_gen.samples
print(num_of_samples)
# %%
# Image displaying

sample_training_images, _ = next(train_data_gen)
# %%


def plot_images(img_arr):
    fig, axes = plt.subplots(1, 5, figsize=(20, 20))
    axes = axes.flatten()

    for img, ax in zip(img_arr, axes):
        ax.imshow(img)
        ax.axis('off')

    plt.tight_layout()
    plt.show()


plot_images(sample_training_images[:5])
# %%

# %%
model = Sequential()
model.add(MobileNetV2(input_shape=INPUT_SHAPE,
                      include_top=False,
                      weights='imagenet'))
model.add(GlobalAveragePooling2D())
model.add(Dense(512, activation='relu'))
model.add(Dense(400, activation='softmax'))
#%%
model.compile(optimizer=SGD(learning_rate=0.001, momentum=0.85),
              loss='categorical_crossentropy',
              metrics=['accuracy'])
# %%
model.summary()
# %%
plot_model(model, show_shapes=True)

# %%

stop = EarlyStopping(patience=5)
checkpoint = ModelCheckpoint(CHECK_POINT, monitor='val_accuracy',
                             save_weights_only=True, mode='max', verbose=1, save_best_only=True)
tqdm_callback = tfa.callbacks.TQDMProgressBar()
# callbacks = [stop, checkpoint, tqdm_callback]
callbacks = [checkpoint]
# %%
history = model.fit(train_data_gen,
                    validation_data=val_data_gen,
                    steps_per_epoch=int(num_of_samples/BATCH_SIZE),
                    callbacks=callbacks,
                    epochs=EPOCHS)
# %%
history.history.keys()
# %%
acc = history.history['accuracy']
val_acc = history.history['val_accuracy']
loss = history.history['loss']
val_loss = history.history['val_loss']

epochs = range(len(acc))

plt.plot(epochs, acc, 'r', label='Training acc')
plt.plot(epochs, val_acc, 'b', label='Validation acc')
plt.title('Training and validation accuracy')
plt.legend()

plt.figure()

plt.plot(epochs, loss, 'r', label='Training loss')
plt.plot(epochs, val_loss, 'b', label='Validation loss')
plt.title('Training and validation loss')
plt.legend()

plt.show()
# %%
model.save(MODEL_SAVE_PATH)
# %%
