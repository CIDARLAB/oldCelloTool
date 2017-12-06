#!/Users/jaipadmakumar/anaconda2/bin/python
import graphviz as gv
import re
import subprocess



def cluster_graph(partition, edges, spans = []):
	z = gv.Digraph(format='svg')
	for part_num in range(len(partition)):
		cluster_name = 'cluster_' + str(part_num)
		with z.subgraph(name = cluster_name) as c:
			for v in partition[part_num]:
				c.node(str(v))


	if spans:
		for e in edges:
			if e not in spans:
				z.edge(str(e[0]), str(e[1]))
			else:
				z.edge(str(e[0]), str(e[1]), color='orange')
	else:
		for e in edges:
			z.edge(str(e[0]), str(e[1]))
	
	#z.body.append('{rank=same; 63;64;65;66;67;68;69}')
	#z.body.append('{rank=same; 59;60;61;62}')    
	z.attr(size='15,10')
	z.attr(splines='ortho')
	z.attr(newrank='true')
	#z.attr(compound='True')
	return z

def metis(os_type_path, hGraph, Nparts, UBfactor=1, Nruns=10, CType=1, RType=1, Vcycle=3, Reconst=0, dbglvl=0):
	cmd = [os_type_path+'hmetis', hGraph, Nparts, UBfactor, Nruns, CType, RType, Vcycle, Reconst, dbglvl]
	cmd = [str(arg) for arg in cmd]
	proc = subprocess.Popen(cmd, stdout=subprocess.PIPE)
	stdout, err = proc.communicate()
	return stdout   


def hyperedge_cut(stdout):
	for char in stdout:
		lst.append(char)
	clean_out = ''.join(lst).split('\n')
	for l in clean_out:
		match = re.findall('Hyperedge Cut:\s+(\d+)', l)
		if match:
			cut_count = int(match[0])
	return cut_count


def getAdjMap(verts, edges):
	adj = {i:[] for i in verts}
	for e in edges:
		adj[e[0]].append(e[1])
	return adj
	
def findEdges(in_lines):
	pattern = re.compile('(\d[\d]*)')
	edges = [(int(re.findall(pattern, l)[0]), int(re.findall(pattern,l)[1])) for l in in_lines]
	return edges


def find_all_paths(graph, start, end, path=[]):
	'''Finds all possible paths in graph between start and end. Graph should be passed as an adjacency dictionary.'''
	path = path + [start]
	if start == end:
		return [path]
	if not graph.has_key(start):
		return []
	paths = []
	for node in graph[start]:
		if node not in path:
			newpaths = find_all_paths(graph, node, end, path)
			for newpath in newpaths:
				paths.append(newpath)
	return paths   

def spanning_edges(edges, outfile_lines):
	spans = []
	v_map = {i:None for i in range(1,len(outfile_lines) + 1)}
	for v, l in zip(v_map.keys(), outfile_lines):
		v_map[v] = int(l)
	
	for e in edges:
		if v_map[e[0]] != v_map[e[1]]:
			spans.append(e)
	return spans

def partition(hgraph, num_partitions, imbalance, dbglvl=8):
	metis_stdout = metis(hgraph, num_partitions, UBfactor=imbalance, dbglvl=dbglvl)
	out_file = hgraph + '.part.' + str(num_partitions)
	with open(out_file, 'r') as f:
		out_lines = f.readlines()

	out_lines = [int(l.strip()) for l in out_lines]
	verts = range(1,len(out_lines) + 1)
	num_partitions = len(set(out_lines))
	pattern = re.compile('(\d[\d]*)')
	edges = [(int(re.findall(pattern, l)[0]), int(re.findall(pattern,l)[1])) for l in in_lines[2:]]
	spans = spanning_edges(edges, out_lines)
	print 'spanning edges: ' , spans
	#print verts

	partition = [[] for i in range(num_partitions)]
	for v, partition_num in zip(verts, out_lines):
		partition[partition_num].append(v)
		#print v, partition_num

	graph = cluster_graph(partition, edges, spans=spans)
	return graph, metis_stdout, spans
