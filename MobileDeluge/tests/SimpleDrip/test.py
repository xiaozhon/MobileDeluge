

import TOSSIM
import sys

t = TOSSIM.Tossim([])
m = t.mac();
r = t.radio();
t.init()


t.addChannel("EasyDiss", sys.stdout)
t.addChannel("DissImp", sys.stdout)
t.addChannel("DripStateP", sys.stdout)


f = open("topo.txt", "r")
lines = f.readlines()
for line in lines:
  s = line.split()
  if (len(s) > 0):
    if (s[0] == "gain"):
      r.add(int(s[1]), int(s[2]), float(s[3]))

for i in range(0, 2):
  m = t.getNode(i);
  for j in range (0, 2000):	# noise trace, should be larger than 100, otherwise segmentation fault will occur
    m.addNoiseTraceReading(-105)
  m.createNoiseModel()
  m.bootAtTime((t.ticksPerSecond() / 50) * i + 43);
i = 0
while (i < 1000):
  i = i + 1
  t.runNextEvent()

