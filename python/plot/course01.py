# import matplotlib.pyplot as plt
# import numpy as np
#
# plt.style.use('_mpl-gallery')
#
# # make data
# x = np.linspace(0, 10, 100)
# y = 1 + np.sin(x)
#
# print(type(x))
#
# # plot

#
# ax.plot(x, y, linewidth=2.0)
#
# # ax.set(xlim=(0, 8), xticks=np.arange(1, 8),
# #        ylim=(0, 8), yticks=np.arange(1, 8))
#
# plt.show()

import matplotlib.pyplot as plt
import numpy as np

x = np.linspace(0, 10, 100)
y = 1 + np.sin(x)

fig, ax = plt.subplots()

print(type(ax))
print(type(fig))

ax.plot(x, x, linewidth=2.0)

ax.set(xlim=(0, 8), xticks=np.arange(1, 8),
       ylim=(0, 8), yticks=np.arange(1, 8))

plt.plot(x, y)
plt.show()
