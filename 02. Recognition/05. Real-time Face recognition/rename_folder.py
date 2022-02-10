import os

path = 'C:\\Users\\Hyuntaek\\Desktop\\my project\\data\\datasets\\'

train = path + 'train'
test = path + 'val'

os.chdir(test)


cnt = 0
for i in os.listdir():
    os.rename(i, str(cnt))
    cnt+=1