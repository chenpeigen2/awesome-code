from __future__ import print_function
import torch

x = torch.empty(5, 3)
print(x)

x = torch.rand(5, 3)
print(x)

x = torch.zeros(5, 3, dtype=torch.long)
print(x)

x = torch.tensor([5.5, 3])
print(x)

# TENSOR ATTRIBUTES -->torch.dtype
# torch.Tensor 下的方法
x = x.new_ones(5, 3, dtype=torch.double)  # new_* methods take in sizes
print(x)

x = torch.randn_like(x, dtype=torch.float)  # override dtype!

# Returns the size of the self tensor. The returned value is a subclass of tuple.
print(type(x.size()))
print(x)

y = torch.rand(5, 3)
print(torch.add(x, y))

result = torch.empty(5, 3)
torch.add(x, y, out=result)
print(result)

y.add_(x)
print(y)

print(x[:, 1])

# Resizing: If you want to resize/reshape tensor, you can use torch.view:
x = torch.randn(4, 4)
y = x.view(16)
z = x.view(-1, 8)  # the size -1 is inferred from other dimensions

print(x.size(), y.size(), z.size())

# If you have a one element tensor, use .item() to get the value as a Python number
x = torch.randn(1)
print(x)
print(x.item())

# tensor --> numpy
a = torch.ones(5)
print(a)

a = torch.ones(5)
print(a)
b = a.numpy()
print(b)
# a b  the same object
a.add_(1)
print(a)
print(b)

# Converting NumPy Array to Torch Tensor
import numpy as np

a = np.ones(5)
b = torch.from_numpy(a)
np.add(a, 1, out=a)
print(a)
print(b)
