import os

ROOT = os.getcwd()

configurations = {
    1: dict(
        TRAIN_DATA = os.path.join(ROOT, 'train'),
        TEST_DATA = os.path.join(ROOT, 'test'),
        VAL_DATA = os.path.join(ROOT, 'val'),
),
}
