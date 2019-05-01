import argparse
import os

import dlib
import pandas as pd
import numpy as np
from tqdm import tqdm


parser = argparse.ArgumentParser(description='Face recognition using dlib and pre-trained models.')
input_group = parser.add_mutually_exclusive_group(required=True)
input_group.add_argument('-d', '--directory',
        metavar='TRAINING_SET_DIR',
        nargs=1,
        help='directory with the training face images to calculate embeddings for')
input_group.add_argument('-f', '--embedding-file',
        nargs=1,
        help='path to csv file with precalculated embeddings and labels')
parser.add_argument('test_image',
        metavar='TEST_IMAGE',
        nargs=1,
        help='image of face to identify')
parser.add_argument('-l', '--landmark-model',
        nargs=1,
        default='shape_predictor_68_face_landmarks.dat',
        help='path to the landmark prediction model data file')
parser.add_argument('-r', '--recognition-model',
        nargs=1,
        default='dlib_face_recognition_resnet_model_v1.dat',
        help='path to the face embedding model data file')
parser.add_argument('-t', '--threshold',
        nargs=1,
        type=float,
        default=0.6,
        help='distance threshold for classification')
parser.add_argument('-o', '--output-embeddings',
        nargs=1,
        type=str,
        help='path under which to store the training set embeddings csv file')


def main():
    args = parser.parse_args()
    face_detector = dlib.get_frontal_face_detector()
    shape_predictor = dlib.shape_predictor(args.landmark_model)
    recognition_model = dlib.face_recognition_model_v1(args.recognition_model)

    if args.directory:
        dataset = load_dataset(args.directory[0], face_detector, shape_predictor, recognition_model)
    else:
        print('Loading embeddings from {}.'.format(args.embedding_file[0]))
        dataset = pd.read_csv(args.embedding_file[0], index_col=0)
    if args.output_embeddings:
        print('\t* Embeddings written to {}.'.format(args.output_embeddings[0]))
        dataset.to_csv(args.output_embeddings[0])
    test_embeddings = get_embeddings_from_test_image(args.test_image[0], face_detector, shape_predictor, recognition_model)
    classify_faces(dataset, test_embeddings, args.threshold)


def load_dataset(training_set, detector, predictor, recognizer):
    label_file = os.path.join(training_set, 'labels.csv')
    labels = pd.read_csv(label_file, index_col=0)
    embeddings = pd.DataFrame(index=labels.index, columns=range(128))
    
    print('Generating training embeddings.')
    with tqdm(labels.iterrows(), total=len(labels)) as bar:
        for idx, row in bar:
            image_path = os.path.join(training_set, row.name)
            image = dlib.load_rgb_image(image_path)
            faces = detector(image)
            if len(faces) != 1:
                bar.write('\t! Training image {} has zero or multiple recognizable faces, skipping'.format(row.name))
                continue
            face = faces[0]
            landmarks = predictor(image, face)
            embedding = recognizer.compute_face_descriptor(image, landmarks)
            embeddings.loc[row.name, :] = list(embedding)
    embeddings['label'] = labels['label']
    embeddings = embeddings.dropna()
    print('\t* {} training embeddings generated in total.'.format(len(embeddings)))
    return embeddings


def get_embeddings_from_test_image(test_image_path, detector, predictor, recognizer):
    print('Finding faces on test image...')
    test_image = dlib.load_rgb_image(test_image_path)
    test_embeddings = []
    faces = detector(test_image)
    print('\t* {} faces found in the test image.'.format(len(faces)))
    print('Generating embeddings for faces found.')
    with tqdm(faces) as bar:
        for face in bar:
            landmarks = predictor(test_image, face)
            embedding = recognizer.compute_face_descriptor(test_image, landmarks)
            test_embeddings.append(pd.Series(list(embedding)))
    if len(test_embeddings) == 1:
        return pd.DataFrame(test_embeddings[0]).transpose()
    return pd.concat(test_embeddings)


def classify_faces(train, test, threshold):
    print('Classifying faces found.')
    with tqdm(test.iterrows(), total=len(test)) as bar:
        for idx, row in bar:
            train_embeddings = train.iloc[:, :-1]
            # cast to int for safety - reimport from csv file screws up the index (strings instead of integers)
            train_embeddings.columns = train_embeddings.columns.astype(int)
            distances = np.sum(np.power(train_embeddings - row, 2), axis=1)
            thresholded_dists = distances[distances < threshold].sort_values(ascending=True)
            if len(thresholded_dists) > 1:
                best_match = thresholded_dists.idxmin()
                best_match_label = train.loc[best_match, 'label']
                bar.write('\t* Face #{index}: recognized as person with label #{label} (score = {score})'.format(
                    index=idx + 1,
                    label=best_match_label,
                    score=thresholded_dists.min()))
            else:
                bar.write('\t* Face #{index}: not recognized any of the people in the train set (all scores above threshold'.format(index=idx + 1))


if __name__ == '__main__':
    main()
