import argparse

import dlib
import pandas as pd
import numpy as np
from tqdm import tqdm

from recognize import load_dataset
from landmarks import LandmarkNormalizer, DlibFaceRecognitionModel


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
        help='use dlib\'s face recognition model using model data file provided')
method_group = parser.add_mutually_exclusive_group(required=True)
method_group.add_argument('-n', '--normalized-landmarks',
        action='store_true',
        help='use normalized facial landmarks for face recognition')
method_group.add_argument('-r', '--recognition-model',
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
        if args.normalized_landmarks:
            recognition_model = LandmarkNormalizer()
        else:
            recognition_model = DlibFaceRecognitionModel(args.recognition_model[0])

        dataset = load_dataset(args.directory[0], face_detector, shape_predictor, recognition_model)
    else:
        print('Loading embeddings from {}'.format(args.embedding_file[0]))
        dataset = pd.read_csv(args.embedding_file[0], index_col=0)
    distance_matrix = calculate_pairwise_distances(dataset)
    confusion_matrix = calculate_confusion_matrix(dataset, distance_matrix)

    confusion_matrix_filename = args.out_file_prefix + '_confusion_matrix.csv'
    print('Saving confusion matrix to {}...'.format(confusion_matrix_filename))
    confusion_matrix.to_csv(confusion_matrix_filename)


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


def calculate_confusion_matrix(dataset, distance_matrix):
    print('Calculating confusion matrix...')
    labels = dataset['label'].unique()
    confusion_matrix = pd.DataFrame(0, index=labels, columns=labels)
    for i in range(len(distance_matrix)):
        distance_matrix.iloc[i, i] = np.inf
    best_matches = distance_matrix.idxmin()
    for label in labels:
        label_representatives = dataset['label'] == label
        label_least_distances = best_matches[label_representatives]
        print(label_least_distances)
        label_classifications = dataset.loc[label_least_distances, 'label'].value_counts()
        confusion_matrix[label] = label_classifications
    confusion_matrix = confusion_matrix.fillna(0).astype(int)
    return confusion_matrix


if __name__ == '__main__':
    main()
