import argparse
import pandas as pd
import matplotlib.pyplot as plt
import seaborn


parser = argparse.ArgumentParser()
parser.add_argument(
        'data_file',
        metavar='FILES',
        nargs=1,
        help='CSV data file with confusion matrix to plot.'
)
parser.add_argument(
        '-o', '--output',
        nargs=1,
        type=str,
        help='Output filename for the plot.'
)

def main():
    arguments = parser.parse_args()
    data_file = arguments.data_file[0]
    
    plt.figure(figsize=(6, 6))
    data = pd.read_csv(data_file, index_col=0)
    seaborn.heatmap(data, annot=True)
    plt.yticks(rotation=0)
    plt.xticks(rotation=90)
    plt.subplots_adjust(bottom=0.25, left=0.25)

    if arguments.output:
        plt.savefig(arguments.output[0])
    else:
        plt.show()


if __name__ == "__main__":
    main()
