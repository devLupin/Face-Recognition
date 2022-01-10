import os

DATA_NAME = 'normal_and_masked'
DATA_PATH = 'faces/' + DATA_NAME

configurations = {
    1: dict(
        TRAIN_PATH=DATA_PATH + '/train',
        VAL_PATH=DATA_PATH + '/val',
        TEST_PATH=DATA_PATH + '/test',
        BATCH_SIZE=32,
        EPOCHS=50,

        TARGET_SIZE=(224, 224),   # height, width
        INPUT_SHAPE=(224, 224, 3),

        CHECK_POINT='checkpoint_all/cp.ckpt',
        MODEL_SAVE_PATH='Model_all',
    ),
}