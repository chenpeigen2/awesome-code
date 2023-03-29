import torch
import torch.nn.functional as F  # 包含激励函数
import matplotlib.pyplot as plt

# 建立数据集
x = torch.unsqueeze(torch.linspace(-1, 1, 100), dim=1)  # x data (tensor), shape=(100, 1)
y = x.pow(2) + 0.2 * torch.rand(x.size())  # noisy y data (tensor), shape=(100, 1)


# [1,2,3,4,5,6,7,8,9]---一维数据  [[1,2,3,4,5,6,7,8,9]]---二维数据
# torch只会处理二维及以上数据

# torch can only train on Variable, so convert them to Variable
# The code below is deprecated in Pytorch 0.4. Now, autograd directly supports tensors
# x, y = Variable(x), Variable(y)

# 散点图
# plt.scatter(x.data.numpy(), y.data.numpy())
# plt.show()

# 建立神经网络
# 先定义所有的层属性(__init__()), 然后再一层层搭建(forward(x))层于层的关系链接
class Net(torch.nn.Module):  # 继承 torch 的 Module
    # init 定义层级
    def __init__(self, n_feature, n_hidden, n_output):
        super().__init__()  # 继承 __init__ 功能
        # 定义每层用什么样的形式
        self.hidden = torch.nn.Linear(n_feature, n_hidden)  # hidden layer  # 隐藏层线性输出
        self.predict = torch.nn.Linear(n_hidden, n_output)  # output layer  # 输出层线性输出 ==== 定义层数

    # forward 激活层级 ，俗称连接层级
    def forward(self, x):  # 这同时也是 Module 中的 forward 功能
        # 正向传播输入值, 神经网络分析出输出值
        x = F.relu(self.hidden(x))  # activation function for hidden layer # 激励函数(隐藏层的线性值)
        x = self.predict(x)  # linear output   # 输出值
        return x  # ==== 定义每层关系


net = Net(n_feature=1, n_hidden=10, n_output=1)  # define the network
# print(net)  # net architecture == 显示神经网络结构
# Net(
#   (hidden): Linear(in_features=1, out_features=10, bias=True)
#   (predict): Linear(in_features=10, out_features=1, bias=True)
# )
# 搭建完神经网络后，对 神经网路参数（net.parameters()） 进行优化
# (1.选择优化器 optimizer 是训练的工具
optimizer = torch.optim.SGD(net.parameters(), lr=0.15)  # 传入 net 的所有参数, 学习率
# (2.选择优化的目标函数
loss_func = torch.nn.MSELoss()  # this is for regression mean squared loss  # 预测值和真实值的误差计算公式 (均方差)

plt.ion()  # something about plotting
# (3.开始训练网络
for t in range(200):
    prediction = net(x)  # input x and predict based on x  # 喂给 net 训练数据 x, 输出预测值
    # loss is a module
    loss = loss_func(prediction, y)  # must be (1. nn output, 2. target)   # 计算两者的误差

    optimizer.zero_grad()  # clear gradients for next train      # 清空上一步的残余更新参数值
    # 如果你想计算导数，你可以调用 Tensor.backward()。
    loss.backward()  # backpropagation, compute gradients  # 误差反向传播, 计算参数更新值
    optimizer.step()  # apply gradients                     # 将参数更新值施加到 net 的 parameters 上

    if t % 5 == 0:
        # plot and show learning process
        plt.cla()
        plt.scatter(x.data.numpy(), y.data.numpy())
        plt.plot(x.data.numpy(), prediction.data.numpy(), 'r-', lw=5)
        plt.text(0.5, 0, 'Loss=%.4f' % loss.data.numpy(), fontdict={'size': 20, 'color': 'red'})
        plt.pause(0.1)

plt.ioff()
plt.show()
