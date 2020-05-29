import cv2

import numpy as np
maximum = 0
pos = 0
i = 1
f = open("Count.txt", "r")
k = int(f.read())
for i in range(i, k):
    original = cv2.imread("uploads/"+str(k)+".jpg")
    duplicate = cv2.imread("uploads/"+str(i)+".jpg")
    cv2.resize(original, (480, 640))
    cv2.resize(duplicate, (480, 640))
    sift = cv2.xfeatures2d.SIFT_create()
    kp_1, desc_1 = sift.detectAndCompute(original, None)
    kp_2, desc_2 = sift.detectAndCompute(duplicate, None)

    number_keypoints = 0
    if len(kp_1) <= len(kp_2):
        number_keypoints = len(kp_1)
    else:
        number_keypoints = len(kp_2)

    index_params = dict(algorithm=0, trees=5)
    search_params = dict()

    flann = cv2.FlannBasedMatcher(index_params, search_params)
    matches = flann.knnMatch(desc_1, desc_2, k=2)

    good_points = []
    for m, n in matches:
        if m.distance < 0.6*n.distance:
            good_points.append(m)
    accurate = len(good_points)/number_keypoints*100

    if accurate > maximum:
        pos = i
        maximum = accurate

with open("Pos.txt", "w") as text_file:
    print(pos, file=text_file)
with open("Percent.txt", "w") as text_file:
    print(maximum, file=text_file)
