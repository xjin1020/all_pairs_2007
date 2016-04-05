#!/bin/bash
echo "1k vectors"
java -jar all_pairs_2007.jar data/1000 1000 0.8
java -jar all_pairs_2007.jar data/1000 1000 0.9
java -jar all_pairs_2007.jar data/1000 1000 0.95
echo "10k vectors"
java -jar all_pairs_2007.jar data/10000 10000 0.8
java -jar all_pairs_2007.jar data/10000 10000 0.9
java -jar all_pairs_2007.jar data/10000 10000 0.95
echo "100k vectors"
java -jar all_pairs_2007.jar data/100000 100000 0.8
java -jar all_pairs_2007.jar data/100000 100000 0.9
java -jar all_pairs_2007.jar data/100000 100000 0.95
