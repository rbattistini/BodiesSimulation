#!env zsh

: <<'DOC'
Questo script esegue un jar con un numero di core variabile; ogni
esecuzione considera sempre la stessa dimensione dell'input,
quindi i tempi misurati possono essere usati per calcolare speedup.
DOC

if [ $# != 4 ]; then
echo "Usage:
      perf_eval [jar] [nSteps] [nBodies] [nTries]"
else

CORES=$(grep -c processor < /proc/cpuinfo)
JAR_NAME=$1
NSTEPS=$2
NBODIES=$3
TRIES=$4
SCRIPT_DATA=$JAR_NAME".csv"

printf '"Threads", "Time in microseconds"\n' >> "${SCRIPT_DATA}"

for p in {1.."$CORES"}; do
    for _ in $(seq "$TRIES"); do
        EXEC_TIME="$( java -jar "$JAR_NAME" -nSteps "$NSTEPS" -nBodies "$NBODIES" -nWorkers "$p" )"
        EXEC_TIME=$(echo "$EXEC_TIME" | sed 's/time: //')
        printf "%i, " "$p" >> "$SCRIPT_DATA"
        printf "%f\n" "${EXEC_TIME}" >> "$SCRIPT_DATA"
    done
done

# convert decimal delimiter "," to "."
# sed 's/\([[:digit:]]\)\,\([[:digit:]]\)/\1.\2/g' $SCRIPT_DATA > $SCRIPT_DATA

fi
