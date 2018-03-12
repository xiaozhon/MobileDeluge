
import sys

inf = open("2014-3-17_deluge_pkts_test3.txt", "r")

outf = open("deluge_pkts_statistics.txt", "a")

lines = inf.readlines()

ADV = 0
REQ = 0
data = 0

for line in lines:
	s = line.split()
	
	if (len(s) > 0):
		if s[12] == "50":
			ADV = ADV + 1
		if s[12] == "51":
			REQ = REQ + 1
		if s[12] == "52":
			data = data + 1

outf.writelines("ADV: \t" + str(ADV) + "\nREQ: \t" + str(REQ) + "\ndata:\t" + str(data) + '\n')

outf.close()
inf.close()
