for i in {0..30..10}; do
  port=$((8101 + $i))
  kill `sudo lsof -t -i :$port`
done