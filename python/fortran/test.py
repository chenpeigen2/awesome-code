# import myflib
# import numpy as np
#
import ctypes as ct
#
#
# a = np.array([[1,2,3,4], [5,6,7,8]], order='F')
# print(myflib.foo(a))

# https://www.matecdev.com/posts/fortran-in-python.html

# import the shared library
fortlib = ct.CDLL('./myflib.so')

# Specify arguments and result types
fortlib.sum2.argtypes = [ct.POINTER(ct.c_double)]
fortlib.sum2.restype = ct.c_double

# Create a double and pass it to Fotran (by reference)
a = ct.c_double(5)
b = fortlib.sum2(ct.byref(a))
print(b)