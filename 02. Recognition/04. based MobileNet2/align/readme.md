# Face alignment
****
## Reference

The code of [face.evoLVe](#Introduction) is released under the MIT License.

****
### Requirements

```shell
conda install pytorch==1.7.0 torchvision==0.8.0 cudatoolkit=10.1 -c pytorch
```

****
### Usage
* This section is based on the work of [MTCNN](https://arxiv.org/pdf/1604.02878.pdf).

* Face alignment API (perform face detection, landmark localization and alignment with affine transformations on a whole database folder ```source_root``` with the directory structure as demonstrated in Sec. [Usage](#Usage), and store the aligned results to a new folder ```dest_root``` with the same directory structure): 
  ```
  # python face_align.py -source_root [source_root] -dest_root [dest_root] -crop_size [crop_size]
  python face_align.py -source_root './data' -dest_root './data_Aligned' -crop_size 224
  ```

****
### Citation 
```
@article{wang2021face,
title={Face. evoLVe: A High-Performance Face Recognition Library},
author={Wang, Qingzhong and Zhang, Pengfei and Xiong, Haoyi and Zhao, Jian},
journal={arXiv preprint arXiv:2107.08621},
year={2021}
}
```

