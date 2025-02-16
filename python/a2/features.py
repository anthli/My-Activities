# -*- coding: utf-8 -*-
"""
Created on Tue Sep 27 13:08:49 2016

@author: cs390mb

This file is used for extracting features over windows of tri-axial accelerometer
data. We recommend using helper functions like _compute_mean_features(window) to
extract individual features.

As a side note, the underscore at the beginning of a function is a Python
convention indicating that the function has private access (although in reality
it is still publicly accessible).

"""

import math
import numpy as np

"""
Computes the mean x, y and z acceleration over the given window.
"""
def _compute_mean_features(window):
  return np.mean(window, axis = 0)

def _compute_median_features(window):
  return np.median(window, axis = 0)

def _compute_std_features(window):
  return np.std(window, axis = 0)

def _compute_variance_features(window):
  return np.var(window, axis = 0)

def _compute_mean_magnitude_features(window):
  mean = _compute_mean_features(window)
  return np.sqrt(mean[0] ** 2 + mean[1] ** 2 + mean[2] ** 2)

def _compute_median_magnitude_features(window):
  median = _compute_median_features(window)
  return np.sqrt(median[0] ** 2 + median[1] ** 2 + median[2] ** 2)

def _compute_std_magnitude_features(window):
  std = _compute_std_features(window)
  return np.sqrt(std[0] ** 2 + std[1] ** 2 + std[2] ** 2)

def _compute_variance_magnitude_features(window):
  var = _compute_variance_features(window)
  return np.sqrt(var[0] ** 2 + var[1] ** 2 + var[2] ** 2)

def _compute_zero_crossing_features(window):
  xCol = window[:, 0]
  yCol = window[:, 1]
  zCol = window[:, 2]
  xZeroCrossings = ((xCol[:-1] * xCol[1:]) < 0).sum()
  yZeroCrossings = ((yCol[:-1] * yCol[1:]) < 0).sum()
  zZeroCrossings = ((zCol[:-1] * zCol[1:]) < 0).sum()
  return np.matrix([xZeroCrossings, yZeroCrossings, zZeroCrossings])

def _compute_min_features(window):
  return np.amin(window, axis = 0)

def _compute_max_features(window):
  return np.amax(window, axis = 0)

def _compute_fft_features(window):
  return np.fft.rfft(window, 5, axis = 0).astype(float)

def _compute_entropy(window):
  hist = np.histogram(window)
  return hist[0][0] * math.log10(hist[0][0])

def extract_features(window):
  """
  Here is where you will extract your features from the data over
  the given window. We have given you an example of computing
  the mean and appending it to the feature matrix X.

  Make sure that X is an N x d matrix, where N is the number
  of data points and d is the number of features.

  """

  x = []
  x = np.append(x, _compute_mean_features(window))
  x = np.append(x, _compute_median_features(window))
  x = np.append(x, _compute_std_features(window))
  x = np.append(x, _compute_mean_magnitude_features(window))
  x = np.append(x, _compute_median_magnitude_features(window))
  x = np.append(x, _compute_std_magnitude_features(window))
  x = np.append(x, _compute_zero_crossing_features(window))
  x = np.append(x, _compute_min_features(window))
  x = np.append(x, _compute_max_features(window))
  x = np.append(x, _compute_fft_features(window))
  x = np.append(x, _compute_entropy(window))

  return x