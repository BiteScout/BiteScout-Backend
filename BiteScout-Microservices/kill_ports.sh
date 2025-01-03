#!/bin/bash

# Ports extracted from the given list
ports=(
  8888
  8761
  8222
  8010
  8070
  8060
  8040
  8090
  8050
)

for port in "${ports[@]}"; do
  pid=$(lsof -ti :$port)
  if [ -n "$pid" ]; then
    echo "Killing process on port $port (PID $pid)..."
    kill -9 $pid
  else
    echo "No process found on port $port."
  fi
done
