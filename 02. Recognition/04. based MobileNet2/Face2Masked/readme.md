# **Convert face dataset to masked dataset**


## **refer to : [MaskTheFace](https://github.com/aqeelanwar/MaskTheFace)**



## How to install MaskTheFace
### Clone the repository
```
git clone https://github.com/devLupin/MaskTheFace.git
```

### Install required packages
Use the following command

```shell
conda create -n [your_env] python=3.8
conda activate [your_env]

cd Face2Masked
conda install -c conda-forge dlib
pip install -r requirements.txt
```

## How to run MaskTheFace

```shell
cd Face2Masked
python mask_the_face.py --path 'data_path' --mask_type 'all'
```
### Arguments
|       Argument       |                         Explanation                          |
| :------------------: | :----------------------------------------------------------: |
|         path         | Path to the image file or a folder containing images to be masked |
|      mask_type       | Select the mask to be applied. Available options are 'N95', 'surgical_blue', 'surgical_green', 'cloth', 'empty' and 'inpaint'. The details of these mask types can be seen in the image above. More masks will be added |
|       pattern        | Selects the pattern to be applied to the mask-type selected above. The textures are available in the masks/textures folder. User can add more textures. |
|    pattern_weight    | Selects the intensity of the pattern to be applied on the mask. The value should be between 0 (no texture strength) to 1 (maximum texture strength) |
|        color         | Selects the color to be applied to the mask-type selected above. The colors are provided as hex values. |
|     color_weight     | Selects the intensity of the color to be applied on the mask. The value should be between 0 (no color strength) to 1 (maximum color strength) |
|         code         | Can be used to create specific mask formats at random. More can be found in the section below. |
|       verbose        | If set to True, will be used to display useful messages during masking |
| write_original_image | If used, the original unmasked image will also be saved in the masked image folder along with processed masked image |

## Supported Masks:
### Mask Types:
Currently MaskTheFace supports the following 4 mask types
1. Surgical
2. N95
3. KN95
4. Cloth
5. Gas

---

## Citation
If you find this repository useful, please use following citation
```
@misc{anwar2020masked,
title={Masked Face Recognition for Secure Authentication},
author={Aqeel Anwar and Arijit Raychowdhury},
year={2020},
eprint={2008.11104},
archivePrefix={arXiv},
primaryClass={cs.CV}
} 
```


## License
This project is licensed under the MIT License - see the [LICENSE.md](LICENSE) file for details