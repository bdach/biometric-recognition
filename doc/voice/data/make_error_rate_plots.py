import argparse
import pandas as pd
import matplotlib.pyplot as plt


parser = argparse.ArgumentParser()
parser.add_argument(
        'data_files',
        metavar='FILES',
        nargs='+',
        help='List of CSV data files with error rates to plot.'
)
parser.add_argument(
        '-o', '--output',
        nargs=1,
        type=str,
        help='Output filename for the plot.'
)

def main():
    arguments = parser.parse_args()
    data_files = arguments.data_files
    
    nrows = (1 + len(data_files) // 2)
    plt.figure(figsize=(10, 13))
    for (idx, data_file) in enumerate(data_files):
        data = pd.read_csv(data_file)
        plt.subplot(nrows, 2, idx + 1)
        plt.plot(data['threshold'], data['false_accepts'])
        plt.plot(data['threshold'], data['false_rejects'])
        plt.legend(['False accept', 'False reject'])
        plt.xlabel('Próg odległości przy klasyfikacji')
        plt.ylabel('Liczba wystąpień')
        phrase = data_file.replace('acceptance_rates_', '').replace('.csv', '')
        plt.title(phrase)

    plt.tight_layout()
    if arguments.output:
        plt.savefig(arguments.output[0])
    else:
        plt.show()


if __name__ == "__main__":
    main()
