import torch
import torch.nn.functional as F

x = torch.arange(2, 5)  # return the tensor

print(x)

print(type(x))

y = F.one_hot(x)  # normalize the tensor

print(type(y))

print(y)

help(y)

print(torch.is_tensor(y))
