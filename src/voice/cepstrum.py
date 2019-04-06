import numpy as np

from scipy.io import wavfile
import scipy.fftpack as fft
from scipy import signal

import matplotlib.pyplot as plt


fft_samples = 2048
hop_size = 15
mel_filter_count = 16
dct_filter_count = 40


def calculate_MFCC(sample_file):
    """
    Calculates the mel-frequency cepstral coefficients for the supplied voice sample.

    :param sample_file: Path to the file with the voice sample to process.
    :type sample_file: str
    """
    sample_rate, raw_audio = wavfile.read(sample_file)
    normalized_audio = normalize(raw_audio)
    filtered_audio = apply_preemphasis(normalized_audio)
    framed_audio = frame_audio(filtered_audio, fft_samples, hop_size, sample_rate)
    windowed_audio = apply_window_func(framed_audio, 'hamming', fft_samples)
    audio_fft = apply_fft(windowed_audio, fft_samples)
    audio_power = calculate_power(audio_fft)

    filter_points, mel_frequencies = get_filter_points(0, 22050, mel_filter_count, fft_samples, sample_rate)
    filters = get_filters(filter_points, mel_frequencies, fft_samples)
    filtered_audio = np.dot(filters, np.transpose(audio_power))
    log_audio = 10.0 * np.log10(filtered_audio)

    dct_coefficients = get_dct_coefficients(dct_filter_count, mel_filter_count)
    cepstral_terms = np.dot(dct_coefficients, log_audio)
    return cepstral_terms.transpose()


def normalize(audio):
    """
    Normalizes the input vector to the [0;1] range.

    :param audio: Input signal vector to normalize.
    :type audio: np.ndarray
    :returns: Input vector, normalized to the [0;1] range.
    :rtype: np.ndarray
    """
    normalized = audio / np.max(audio)
    return normalized


def apply_preemphasis(audio, a=0.97):
    """
    Applies pre-emphasis to the audio signal.
    Pre-emphasis is a form of filtering that weakens low frequencies and amplifies high frequencies.
    The first sample is discarded.

    :param audio: Input signal to apply pre-emphasis to.
    :type audio: np.ndarray
    :returns: Input signal after pre-emphasis.
    :rtype: np.ndarray
    """
    return audio[1:] - a * audio[0:-1]


def frame_audio(audio, fft_samples=2048, hop_size=15, sample_rate=44100):
    """
    Divides the signal into multiple short frames to be processed during recognition.

    :param audio: Input signal to divide into frames.
    :type audio: np.ndarray
    :param fft_samples: Target number of samples in the signal Fourier transform.
    :type fft_samples: int
    :param hop_size: Length of time between frames, in milliseconds.
                     Frames can overlap (this is even desirable).
    :type hop_size: int
    :param sample_rate: Sampling rate of the signal in hertzs.
    :type sample_rate: int
    :returns: Input signal after framing.
              Frames are distributed along the first axis.
    :rtype: np.ndarray
    """
    audio = np.pad(audio, int(fft_samples / 2), mode='reflect')
    frame_length = np.round(sample_rate * hop_size / 1000).astype(int)
    frame_count = int((len(audio) - fft_samples) / frame_length) + 1
    frames = np.zeros((frame_count, fft_samples))

    for n in range(frame_count):
        start = n * frame_length
        frames[n] = audio[start:start + fft_samples]

    return frames


def apply_window_func(framed_audio, func_type='hamming', fft_samples=2048):
    """
    Applies a windowing function to the framed signal.

    :param framed_audio: The signal after framing.
    :type framed_audio: np.ndarray
    :param func_type: Type of windowing function to use, as provided by the :func:`scipy.signal.get_window` function.
    :type func_type: str
    :param fft_samples: Target number of samples in the FFT transform.
    :type fft_samples: int
    :returns: Framed signal with applied windowing function.
    :rtype: np.ndarray
    """
    window = signal.get_window(func_type, fft_samples, fftbins=True)
    return np.transpose(framed_audio * window)


def apply_fft(windowed_audio, fft_samples=2048):
    """
    Applies the Fast Fourier Transform to the input signal.

    :param windowed_audio: The signal after windowing.
    :type windowed_audio: np.ndarray
    :param fft_samples: Target number of samples in the FFT transform.
    :type fft_samples: int
    :returns: The result of the Fourier transform for the signal supplied.
              Results for each window are aligned along the first axis.
    :rtype: np.ndarray
    """
    window_count = windowed_audio.shape[1]
    audio_fft = np.empty((fft_samples, window_count), dtype=np.complex64, order='F')

    for n in range(window_count):
        audio_fft[:, n] = fft.fft(windowed_audio[:, n], axis=0)

    return np.transpose(audio_fft)


def calculate_power(audio_fft):
    """
    Calculates the power of the signal after the Fourier transform.

    :param audio_fft: The windowed Fourier transform of the input signal.
    :type audio_fft: np.ndarray
    :returns: Vector with the powers of the signal along frequencies.
    :rtype: np.ndarray
    """
    return np.square(np.abs(audio_fft))


def freq_to_mel(frequency):
    """
    Converts frequencies in hertzs to the mel scale.

    :param frequency: Frequency to convert.
    :returns: Frequency converted to the mel scale.
    """
    return 2595.0 + np.log10(1.0 + frequency / 700.0)


def mel_to_freq(mels):
    """
    Converts frequencies in the mel scale to hertzs.

    :param mels: Frequency in the mel scale.
    :returns: Frequency converted to hertzs.
    """
    return (10.0 ** (mels - 2595.0) - 1.0) * 700.0


def get_filter_points(fmin, fmax, mel_filter_count, fft_samples, sample_rate=44100):
    """
    Returns the intermediate points for the triangular mel-scale filters.

    :param fmin: Start of the frequency range within which the filters should be created.
    :type fmin: int
    :param fmax: End of the frequency range within which the filters should be created.
    :type fmax: int
    :param mel_filter_count: Number of mel filters to create intermediate points for.
    :type mel_filter_count: int
    :param fft_samples: Number of samples in the Fourier transform.
    :type fft_samples: int
    :param sample_rate: Sampling rate of the signal.
    :type sample_rate: int
    :returns: List of intermediate points for the filters.
    :rtype: np.ndarray
    """
    fmin_mel = freq_to_mel(fmin)
    fmax_mel = freq_to_mel(fmax)

    mels = np.linspace(fmin_mel, fmax_mel, num=mel_filter_count + 2)
    frequencies = mel_to_freq(mels)

    return np.floor(fft_samples / sample_rate * frequencies).astype(int), frequencies


def get_filters(filter_points, mel_frequencies, fft_samples):
    """
    Returns the triangular mel-scale filters based on the points provided.

    :param filter_points: List of intermediate points for the filters.
    :type filter_points: np.ndarray
    :param mel_frequencies: List of frequencies in the mel-scale corresponding to the filter points.
    :type mel_frequencies: np.ndarray
    :param fft_samples: Number of samples in the Fourier transform.
    :type fft_samples: int
    :returns: Array with filters.
    :rtype: np.ndarray
    """
    filter_count = len(filter_points) - 2
    filters = np.zeros((filter_count, fft_samples))

    for n in range(filter_count):
        start = filter_points[n]
        middle = filter_points[n + 1]
        end = filter_points[n + 2]
        filters[n, start:middle] = np.linspace(0, 1, middle - start)
        filters[n, middle:end] = np.linspace(1, 0, end - middle)

    enorm = 2.0 / (mel_frequencies[2:filter_count + 2] - mel_frequencies[:filter_count])
    filters *= enorm[:, np.newaxis]

    return filters


def get_dct_coefficients(dct_filter_count, filter_length):
    """
    Returns the coefficients for the discrete cosine transform (DCT).

    :param dct_filter_count: Number of DCT filters to generate.
    :type dct_filter_count: int
    :param filter_length: Length of each filter.
    :type filter_length: int
    :returns: Coefficients for the discrete cosine transform.
    :rtype: np.ndarray
    """
    basis = np.empty((dct_filter_count, filter_length))
    basis[0, :] = 1.0 / np.sqrt(filter_length)

    samples = np.arange(1, 2 * filter_length, 2) * np.pi / (2.0 * filter_length)

    for i in range(1, dct_filter_count):
        basis[i, :] = np.cos(i * samples) * np.sqrt(2.0 / filter_length)

    return basis

