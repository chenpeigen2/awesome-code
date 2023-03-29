import torch
import torch.nn as nn
import torch.nn.functional as F
import torch.optim as optim
import torchvision
from torchvision import datasets, transforms
from torch.autograd import Variable
import numpy as np

import matplotlib.pyplot as plt

# Training settings
batch_size = 64

# MNIST Dataset
train_dataset = datasets.MNIST(root='./data/',
                               train=True,
                               transform=transforms.ToTensor(),
                               download=True)

test_dataset = datasets.MNIST(root='./data/',
                              train=False,
                              transform=transforms.ToTensor())

# Data Loader (Input Pipeline)
train_loader = torch.utils.data.DataLoader(dataset=train_dataset,
                                           batch_size=batch_size,
                                           shuffle=True)

test_loader = torch.utils.data.DataLoader(dataset=test_dataset,
                                          batch_size=1,
                                          shuffle=True)


class Net(nn.Module):

    def __init__(self):
        super(Net, self).__init__()
        self.l1 = nn.Linear(784, 520)
        self.l2 = nn.Linear(520, 320)
        self.l3 = nn.Linear(320, 240)
        self.l4 = nn.Linear(240, 120)
        self.l5 = nn.Linear(120, 10)

    def forward(self, x):
        # Flatten the data (n, 1, 28, 28) --> (n, 784)
        x = x.view(-1, 784)
        # 这里才是 训练的input 已经被锁定在了 （1，784）
        # 或者 （n,784）
        x = F.relu(self.l1(x))
        x = F.relu(self.l2(x))
        x = F.relu(self.l3(x))
        x = F.relu(self.l4(x))
        return F.log_softmax(self.l5(x), dim=1)
        # return self.l5(x)


model = Net()

optimizer = optim.SGD(model.parameters(), lr=0.01, momentum=0.5)


def train(epoch):
    # 每次输入barch_idx个数据
    for batch_idx, (data, target) in enumerate(train_loader):
        # data, target = Variable(data), Variable(target)
        optimizer.zero_grad()
        output = model(data)
        # loss
        # tensor([7, 4, 0, 1, 7, 2, 4, 5, 4, 1, 7, 5, 2, 1, 8, 7, 0, 7, 1, 8, 7, 1, 7, 1,
        # 7, 5, 6, 5, 4, 7, 0, 7, 3, 7, 7, 8, 7, 7, 7, 6, 6, 1, 6, 3, 1, 7, 6, 7,
        # 9, 0, 1, 8, 6, 9, 8, 5, 9, 0, 7, 8, 4, 6, 6, 3])
        loss = F.nll_loss(output, target)
        loss.backward()
        # update
        optimizer.step()
        if batch_idx % 200 == 0:
            print('Train Epoch: {} [{}/{} ({:.0f}%)]\tLoss: {:.6f}'.format(
                epoch, batch_idx * len(data), len(train_loader.dataset),
                       100. * batch_idx / len(train_loader), loss.item()))


def test():
    test_loss = 0
    correct = 0
    # 测试集
    for data, target in test_loader:
        # [64, 1, 28, 28] input
        # print(type(data), data.size())
        # data, target = Variable(data, volatile=True), Variable(target)
        output = model(data)
        # [64, 10] output
        print(output.size())
        # sum up batch loss
        test_loss += F.nll_loss(output, target).item()
        # get the index of the max
        pred = output.data.max(1, keepdim=True)[1]
        correct += pred.eq(target.data.view_as(pred)).cpu().sum()

    test_loss /= len(test_loader.dataset)
    print('\nTest set: Average loss: {:.4f}, Accuracy: {}/{} ({:.0f}%)\n'.format(
        test_loss, correct, len(test_loader.dataset),
        100. * correct / len(test_loader.dataset)))


# functions to show an image
def imgshow(img):
    # [3, 28, 28]
    img = np.transpose(img, (1, 2, 0))
    # print(img.shape)
    plt.imshow(img)
    plt.show()


def evaluate(img):
    dataiter = iter(test_loader)
    images, labels = dataiter.next()

    output = model(img)
    # output shape: torch.Size([1, 10])
    print("output shape:", output.shape)

    pred = output.max(1, keepdim=True)[1].item()

    print(pred)
    # [1, 1, 28, 28]
    # [0,1] 之间
    imgshow(torchvision.utils.make_grid(img))

    # [64, 10] output
    # [1, 1, 28, 28]


# for epoch in range(1, 6):
#     train(epoch)
#     test()
# torch.save(model, 'model.pkl')

model = torch.load('model.pkl')
# PIL
from PIL import Image

# Image path
im_path = "./data/nine.jpg"

# 1. path
img = Image.open(im_path)
img = img.resize((28, 28))
# img = img.convert('L')

# [n:{batch size}, 1, 28, 28] input
img = np.array(img)
img = 255 - img
img = img / 255

# img = np.expand_dims(img, 0).repeat(1, axis=0)
# img = np.expand_dims(img, 0).repeat(1, axis=0)
img = torch.from_numpy(img).type(torch.float32)
print(img.shape, "shape")
evaluate(img)
