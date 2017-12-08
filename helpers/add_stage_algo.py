#!/usr/bin/env python

"""add_stage.py
"""

from __future__ import print_function
import os
import sys
import argparse
import csv

def validateParamType(paramType):
	rtn = False
	if (paramType == 'boolean'):
		rtn = True
	if (paramType == 'byte'):
		rtn = True
	if (paramType == 'char'):
		rtn = True
	if (paramType == 'short'):
		rtn = True
	if (paramType == 'int'):
		rtn = True
	if (paramType == 'long'):
		rtn = True
	if (paramType == 'float'):
		rtn = True
	if (paramType == 'double'):
		rtn = True
	if (paramType == 'string'):
		rtn = True
	return rtn

def main(arguments):
	# parse arguments
	parser = argparse.ArgumentParser(
		description=__doc__,
		formatter_class=argparse.RawDescriptionHelpFormatter)
	parser.add_argument('-i','--infile', help="Input file")
	parser.add_argument('-d','--projectDir', help="Project Directory", default="../Partitioner/")
	#parser.add_argument('-o', '--outfile', help="Output file",
		#default=sys.stdout, type=argparse.FileType('w'))
	args = parser.parse_args(arguments)

	# get filename
	filename = args.infile
	# ProjectDirectory
	ProjectDirectory = args.projectDir

	# info
	StagePrefix = None
	StageName = None
	AlgorithmName = None
	AlgorithmType = None
	params = []

	# parse CSV
	with open(filename) as csvfile:
		csvreader = csv.reader(csvfile, delimiter=',')
		for i, row in enumerate(csvreader):
			name = row[0]
			value = row[1]
			# get StagePrefix
			if name == "StagePrefix":
				StagePrefix = value
			# get StageName
			elif name == "StageName":
				StageName = value
			# get AlgorithmName
			elif name == "AlgorithmName":
				AlgorithmName = value
			# get AlgorithmType
			elif name == "AlgorithmType":
				AlgorithmType = value
			else:
				paramType = row[2]
				if (validateParamType(paramType)):
					param = [name, paramType, value]
					params.append(param)
				else:
					print (name + " has an invalid type")
	

	## create stage if does not exist
	## if so copy 

if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
