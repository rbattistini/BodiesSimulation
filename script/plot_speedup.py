import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import sys

n = len(sys.argv)
if n != 2:
    print("Usage: plot_speedup [input_csv_filename]")
    exit(0)

df = pd.read_csv(sys.argv[1] + ".csv", names=["threads", "time"], header=0)
df = df.groupby('threads').mean()

from matplotlib.pyplot import figure

figure(figsize=(12, 8))

plt.subplot(211)
plt.ylabel("Tempo di esecuzione")
plt.xlabel("Thread")
plt.xticks(np.arange(-1, 17, 1))
plt.plot(df.index, df.time)

plt.subplot(212)
speedup_df = df.apply(lambda x: df["time"][1] / x, axis=0)

plt.ylabel("Speedup")
plt.xlabel("Thread")
plt.xticks(np.arange(-1, 17, 1))
plt.plot(speedup_df.index, speedup_df.time)
plt.savefig(sys.argv[1] + ".svg", dpi=350)
