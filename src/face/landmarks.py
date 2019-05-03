import dlib
import numpy as np

class LandmarkNormalizer:
    result_length = 136

    @staticmethod
    def compute_face_descriptor(image, landmarks):
        xs = np.array([point.x for point in landmarks.parts()])
        ys = np.array([point.y for point in landmarks.parts()])

        # point no. 36 is left corner of left eyelid
        # point no. 45 is right corner of right eyelid
        slope_x = (ys[45] - ys[36]) / (xs[45] - xs[36])
        # point no. 27 is top of nose ridge
        # point no. 33 is bottom of nose ridge
        slope_y = (xs[33] - xs[27]) / (ys[33] - ys[27])

        xs_skewed = xs - slope_y * ys
        ys_skewed = ys - slope_x * xs

        width = xs_skewed.max() - xs_skewed.min()
        height = ys_skewed.max() - ys_skewed.min()

        xs_scaled =  (xs_skewed - xs_skewed.min()) / width  - 0.5
        # Y axis inverted for human purposes only - shouldn't matter in practice
        ys_scaled = -(ys_skewed - ys_skewed.min()) / height + 0.5

        return np.concatenate((xs_scaled, ys_scaled))


class DlibFaceRecognitionModel:
    result_length = 128

    def __init__(self, model_file=''):
        self.model = dlib.face_recognition_model_v1(model_file)

    def compute_face_descriptor(self, image, landmarks):
        return self.model.compute_face_descriptor(image, landmarks)
