from cepstrum import calculate_MFCC

import numpy as np
import pandas as pd


class VoiceClassifier:
    """
    Performs classification of voice samples in order to identify the speaker.
    """
    def __init__(self, train_files, threshold=1e3):
        """
        Initializes the classifier.

        :param train_files: List of (label, path) tuples indicating the files to add to the training set.
        :param threshold: Threshold for distances between samples.
                          Training test samples that exceed this threshold during classification will be discarded.
        :type threshold: int
        """
        self.threshold = threshold
        self.train_samples = [(label, calculate_MFCC(filename)) for (filename, label) in train_files]


    def _frame_distance(self, coeffs1, frame_coeffs2):
        """
        Calculates the minimum distance between a frame from a sample being classified
        and frames from one of the training samples.

        :param coeffs1: Training sample to compare with.
        :type coeffs1: np.ndarray
        :param coeffs2: Frame of the sample being classified to compare.
        :type coeffs2: np.ndarray
        :returns: The minimum over the distances of the classified frame and all of the training frames.
        :rtype: float
        """
        return np.min(np.sum(np.power(coeffs1 - frame_coeffs2, 2), axis=1))


    def _sample_score(self, sample1, sample2):
        """
        Scores the similarity between two samples.

        :param sample1: Training sample to compare.
        :type sample1: np.ndarray
        :param sample2: Sample to classify.
        :type sample2: np.ndarray
        :returns: The similarity score of the two samples.
        :rtype: float
        """
        return np.mean(np.apply_along_axis(lambda row: self._frame_distance(sample2, row), 1, sample1))


    def calculate_scores(self, sample):
        """
        Calculates the similarity between stored training samples and the supplied sample to classify.

        :param sample: Sample to compare.
        :type sample: np.ndarray
        :returns: Data frame with similarity scores for the sample.
        :rtype: pd.DataFrame
        """
        scores = [(label, self._sample_score(sample, train_sample)) for (label, train_sample) in self.train_samples]
        df = pd.DataFrame.from_records(scores, columns=['label', 'score'])
        return df.sort_values(by='score')


    def classify(self, sample_filename, k=3):
        """
        Classifies the sample provided using a k-NN classifier with thresholding.

        :param sample_filename: Filename of sample to compare.
        :type sample_filename: str
        :returns: The label returned by the classifier.
                  Can be None if the classifier rejects all training samples during the thresholding.
        """
        sample = calculate_MFCC(sample_filename)
        all_scores = self.calculate_scores(sample)
        thresholded_scores = all_scores[all_scores['score'] <= self.threshold]
        if len(thresholded_scores) == 0:
            return None
        top_scores = thresholded_scores.head(k)
        labels = top_scores.groupby('label').count()
        scores = labels['score']
        if sum(scores == np.max(scores)) > 1:
            return None
        return scores.idxmax()

