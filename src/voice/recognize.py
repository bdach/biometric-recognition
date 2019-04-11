import argparse
import os
import re

from classifier import VoiceClassifier


parser = argparse.ArgumentParser(description=('Recognize a person\'s voice samples based on a set of training samples.'))
parser.add_argument(
        'training_samples',
        metavar='TRAIN_SAMPLE',
        type=str,
        nargs='+',
        help='List of training sample files.'
)
parser.add_argument(
        'test_sample',
        metavar='SAMPLE',
        type=str,
        nargs=1,
        help='Voice sample to recognize.'
)
parser.add_argument(
        '-t', '--threshold',
        type=float,
        nargs=1,
        help='Classification threshold.',
        default=[1e3]
)


def main():
    arguments = parser.parse_args()
    samples = get_samples(arguments.training_samples)
    classifier = VoiceClassifier(samples, arguments.threshold[0])
    label = classifier.classify(arguments.test_sample[0])
    if label:
        print('Recognition successful: recognized {}'.format(label))
    else:
        print('Voice sample not recognized. Sorry!')


def get_samples(files):
    regex = r'^.+(?P<label>speaker_\d{2})_.+\.wav$'
    pattern = re.compile(regex)
    after_regex = [pattern.match(filename) for filename in files]
    matches = [(match.group(0), match.group('label')) \
                for match in after_regex if match is not None]
    return matches


if __name__ == "__main__":
    main()

