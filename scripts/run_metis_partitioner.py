#!/usr/bin/python

import metis_tools as mt
import sys


input_file = sys.argv[1]
UBfactor = sys.argv[2]
os_type_path = sys.argv[3]

with open(input_file, 'r') as f_in:
    lines = f_in.readlines();


mt_in_file = 'TEMP_GRAPH_FILE.hgr'

with open(mt_in_file, 'w') as f_out:
    f_out.writelines(lines[1:])
    
#given a input_graph.hgr file, number of partitions, and balancing constraint
#run metis to partition the graph

Nparts = int(lines[0].strip())
mt_stdout = mt.metis(os_type_path, mt_in_file, Nparts, UBfactor)

#read in in_file and out_file
out_file = mt_in_file + '.part.' + str(Nparts)

with open(mt_in_file, 'r') as f:
    in_lines = f.readlines()
in_lines = [l.strip() for l in in_lines]
in_lines = in_lines[2:] #skip header lines

with open(out_file, 'r') as f:
    out_lines = f.readlines()

out_lines = [int(l.strip()) for l in out_lines]

#get some extra data and verify that num_partitions in out_file == Nparts
edges = mt.findEdges(in_lines)
verts = range(1,len(out_lines) + 1)
num_partitions = len(set(out_lines))
if num_partitions != Nparts:
    print 'WARNING: DID NOT CALCULATE SPECIFIED NUMBER OF PARTITIONS'
    print 'LIKELY HAVE AN EMPTY BLOCK B/C THE GRAPH GIVEN WAS TOO SMALL FOR THE NUMBER OF DESIRED PARTITIONS'
    print 'Expected: %i and placed nodes in %i partitions' % (Nparts, num_partitions)
    
    
#get partition aka vertices assigned to different blocks
#1,1,0,1,0,0 means vertex 1 in p1, 2 in p1, 3 in p0, etc...

partition = [[] for i in range(Nparts)]
for v, partition_num in zip(verts, out_lines):
    partition[partition_num].append(v)

#write out a clustered graph file with graphviz
spanning_edges = mt.spanning_edges(edges, out_lines)
g = mt.cluster_graph(partition, edges, spanning_edges)

g.save('partitioned_graph.dot')



#write partition file back out for CelloTools to read

with open('partitioned_graph.txt', 'w') as out_file:
    out_lines = []
    for block_num in range(0,len(partition)):
        out_file.write('block:%i\n' % block_num)
        for v in partition[block_num]:
            out_file.write(str(v) + '\n')
            
         








