import argparse

import dlib
import pandas as pd
import numpy as np
from tqdm import tqdm

from recognize import load_dataset


parser = argparse.ArgumentParser(description='Confusion matrix, FAR and FRR testing script for facial recognition methods.')
input_group = parser.add_mutually_exclusive_group(required=True)
input_group.add_argument('-d', '--directory',
        metavar='TRAINING_SET_DIR',
        nargs=1,
        help='directory with the training face images to calculate embeddings for')
input_group.add_argument('-f', '--embedding-file',
        nargs=1,
        help='path to csv file with precalculated embeddings and labels')
parser.add_argument('-l', '--landmark-model',
        nargs=1,
        default='shape_predictor_68_face_landmarks.dat',
        help='path to the landmark prediction model data file')
parser.add_argument('-r', '--recognition-model',
        nargs=1,
        default='dlib_face_recognition_resnet_model_v1.dat',
        help='path to the face embedding model data file')
parser.add_argument('-o', '--out-file-prefix',
        nargs=1,
        type=str,
        default='facerec',
        help='prefix for output files produced by this script')


def main():
    args = parser.parse_args()
    if args.directory:
        face_detector = dlib.get_frontal_face_detector()
        shape_predictor = dlib.shape_predictor(args.landmark_model)
        recognition_model = dlib.face_recognition_model_v1(args.recognition_model)

        dataset = load_dataset(args.directory[0], face_detector, shape_predictor, recognition_model)
    else:
        print('Loading embeddings from {}'.format(args.embedding_file[0]))
        dataset = pd.read_csv(args.embedding_file[0], index_col=0)
    same_person_matrix = calculate_same_person_matrix(dataset)
    distance_matrix = calculate_pairwise_distances(dataset)
    error_rates = test_thresholds(same_person_matrix, distance_matrix)

    distance_matrix_filename = args.out_file_prefix + '_distance_matrix.csv'
    print('Saving distance matrix to {}...'.format(distance_matrix_filename))
    distance_matrix.to_csv(distance_matrix_filename)

    error_rates_filename = args.out_file_prefix + '_error_rates.csv'
    print('Saving error rates to {}...'.format(error_rates_filename))
    error_rates.to_csv(error_rates_filename)


def calculate_pairwise_distances(dataset):
    print('Calculating pairwise distances...')
    distance_matrix = pd.DataFrame(index=dataset.index, columns=dataset.index)
    count = len(dataset)
    with tqdm(dataset.iterrows(), total=count) as bar:
        for idx, row in bar:
            other_embeddings = dataset.iloc[:, :-1]
            distances = np.sum(np.power(other_embeddings - row[:-1], 2), axis=1)
            distance_matrix[row.name] = distances
    return distance_matrix


def calculate_same_person_matrix(dataset):
    print('Preparing target comparison matrix.')
    same_person_matrix = pd.DataFrame(False, index=dataset.index, columns=dataset.index)
    for idx, row in dataset.iterrows():
        same_person_rows = dataset['label'] == row['label']
        same_person_matrix.loc[row.name, same_person_rows] = True
    return same_person_matrix


def test_thresholds(same_person, distance_matrix):
    print('Testing recognition accuracy with variable thresholds...')
    thresholds = np.arange(0, 1.01, 0.05)
    rates = pd.DataFrame(index=thresholds, columns=['tar', 'trr', 'far', 'frr'])
    with tqdm(thresholds) as bar:
        for threshold in bar:
            bar.set_postfix(t=threshold)
            thresholded_dist = pd.DataFrame(False, index=distance_matrix.index, columns=distance_matrix.columns)
            thresholded_dist[distance_matrix <= threshold] = True

            not_same_person = np.logical_not(same_person)
            thresholded_dist_neg = np.logical_not(thresholded_dist)

            true_acceptance = np.sum(np.sum(np.logical_and(same_person, thresholded_dist)))
            true_rejection = np.sum(np.sum(np.logical_and(not_same_person, thresholded_dist_neg)))
            false_acceptance = np.sum(np.sum(np.logical_and(not_same_person, thresholded_dist)))
            false_rejection = np.sum(np.sum(np.logical_and(same_person, thresholded_dist_neg)))
            rates.loc[threshold, :] = [true_acceptance, true_rejection, false_acceptance, false_rejection]
    rates /= len(distance_matrix) ** 2
    return rates

if __name__ == '__main__':
    main()
