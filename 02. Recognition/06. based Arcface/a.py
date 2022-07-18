import numpy as np
import tensorflow as tf

a= np.random.laplace(0.5, 0.05, 1)
a = tf.convert_to_tensor(a, dtype=np.float32)
print(a)