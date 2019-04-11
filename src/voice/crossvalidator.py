import argparse
import os
import re
import pandas as pd
import numpy as np

np.random.seed(2019)

from classifier import VoiceClassifier


PHRASES = ['biometria', 'chrzaszcz', 'poniedzialek', 'wycieczka']


parser = argparse.ArgumentParser(description=('Helper script used to perform cross-validation '
                                              'testing of the voice classifier.'))
parser.add_argument(
        'samples',
        metavar='SAMPLE_DIR',
        type=str,
        nargs=1,
        help='Directory containing the voice samples.'
)
parser.add_argument(
        'sample_count',
        metavar='N',
        type=int,
        nargs=1,
        help='Number of samples available per speaker and phrase.'
)
parser.add_argument(
        'phrase',
        metavar='PHRASE',
        type=str,
        choices=PHRASES,
        nargs=1,
        help='Sample phrase to perform cross-validation on.'
)


def main():
    arguments = parser.parse_args()
    sample_dir = arguments.samples[0]
    phrase = arguments.phrase[0]
    sample_count = arguments.sample_count[0]
    samples = get_samples(sample_dir, phrase)
    labels = samples['label'].sort_values().unique()
    threshold_rates = []
    for threshold in range(500, 1501, 100):
        print('Running cross-validation for threshold = {}...'.format(threshold))
        confusion_matrix = pd.DataFrame(0, index=labels, columns=labels)
        false_rejection = false_acceptance = correct = 0
        for sample_test in range(1, sample_count + 1):
            train_samples = samples.loc[samples['sample_no'] != sample_test, ['filename', 'label']]
            test_samples = samples.loc[samples['sample_no'] == sample_test, ['filename', 'label']]
            classifier = VoiceClassifier(pd.DataFrame.to_records(train_samples, index=False), threshold)
            for _, row in test_samples.iterrows():
                returned_label = classifier.classify(row['filename'])
                if returned_label:
                    confusion_matrix.loc[row['label'], returned_label] += 1
                    if returned_label == row['label']:
                        correct += 1
                    else:
                        false_acceptance += 1
                else:
                    false_rejection += 1
        confusion_matrix.to_csv('confusion_matrix_{}_t={}.csv'.format(phrase, threshold))
        threshold_rates.append((threshold, correct, false_acceptance, false_rejection))
    threshold_rates = pd.DataFrame.from_records(threshold_rates, columns=['threshold', 'correct', 'false_accepts', 'false_rejects'])
    threshold_rates.to_csv('acceptance_rates_{}.csv'.format(phrase), index=False)
    print(threshold_rates)


def get_samples(sample_dir, phrase):
    files = os.listdir(sample_dir)
    regex = r'(?P<label>speaker_\d{2})_' + phrase + r'_(?P<sample_no>\d+)\.wav'
    pattern = re.compile(regex)
    after_regex = [pattern.match(filename) for filename in files]
    matches = [(os.path.join(sample_dir, match.group(0)), match.group('label'), int(match.group('sample_no'))) \
                for match in after_regex if match is not None]
    return pd.DataFrame.from_records(matches, columns=['filename', 'label', 'sample_no'])


if __name__ == "__main__":
    main()
