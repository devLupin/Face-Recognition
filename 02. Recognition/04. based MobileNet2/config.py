from train import CHECK_POINT

DATA_NAME = 'K-Face_all'
DATA_PATH = 'faces/' + DATA_NAME

configurations = {
    1: dict(
        TRAIN_PATH=DATA_PATH + '/train',
        VAL_PATH=DATA_PATH + '/val',
        TEST_PATH=DATA_PATH + '/test',
        BATCH_SIZE=32,
        EPOCHS=30,

        TARGET_SIZE=(224, 224),   # height, width
        INPUT_SHAPE=(224, 224, 3),

        CHECK_POINT='Checkpoint/' + DATA_NAME + '/cp.ckpt',
        MODEL_SAVE_PATH='Model/' + DATA_NAME,
    ),
}