#!/bin/bash
echo "10k vectors"
echo "2_2:"
java -Xmx13000m -jar all_pairs_2007_2_2.jar data/10000 10000 0.5
java -Xmx13000m -jar all_pairs_2007_2_2.jar data/10000 10000 0.7
java -Xmx13000m -jar all_pairs_2007_2_2.jar data/10000 10000 0.8
java -Xmx13000m -jar all_pairs_2007_2_2.jar data/10000 10000 0.9
java -Xmx13000m -jar all_pairs_2007_2_2.jar data/10000 10000 0.95
echo "2_3:"
java -Xmx13000m -jar all_pairs_2007_2_3.jar data/10000 10000 0.5
java -Xmx13000m -jar all_pairs_2007_2_3.jar data/10000 10000 0.7
java -Xmx13000m -jar all_pairs_2007_2_3.jar data/10000 10000 0.8
java -Xmx13000m -jar all_pairs_2007_2_3.jar data/10000 10000 0.9
java -Xmx13000m -jar all_pairs_2007_2_3.jar data/10000 10000 0.95
#echo "100k vectors"
#java -Xmx13000m -jar all_pairs_2007_2_2.jar data/100000 100000 0.95
#java -Xmx13000m -jar all_pairs_2007_2_2.jar data/100000 100000 0.9
#java -Xmx13000m -jar all_pairs_2007_2_2.jar data/100000 100000 0.8
#java -Xmx13000m -jar all_pairs_2007_2_2.jar data/100000 100000 0.7
#java -Xmx13000m -jar all_pairs_2007_2_2.jar data/100000 100000 0.5
