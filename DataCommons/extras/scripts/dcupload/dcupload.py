#! /usr/bin/python

import os
import urllib
import httplib
import logging
import hashlib
import csv
import ConfigParser
import base64
from optparse import OptionParser

# Wrapper for file object that displays progress
class file_with_progress_updates(file):
	def __init__(self, filepath, mode):
		file.__init__(self, filepath, mode)
		self.seek(0, os.SEEK_END)
		self._total = self.tell()
		self.seek(0)
		
	def __len__(self):
		return self._total

	def read(self, size):
		data = file.read(self, size)
		try:
			print "\r" + str(self.tell() * 100 / self._total) + "%",
		except:
			pass
			
		return data

		
# Returns True if a file exists, False otherwise
def check_file_exists(filename):
	return os.path.isfile(filename)


# Reads a configuration file and returns the value of the specified key in the specified section.
def get_config_value(section, key):
	config = ConfigParser.ConfigParser()
	conffile = open("dcupload.conf")
	config.readfp(conffile)
	conffile.close()
	return config.get(section, key)


# Uploads a file to ANU Data Commons
def upload_file_to_datacommons(pid, localfilepath, targetfilename):
	host = get_config_value("datacommons", "host")
	connection = httplib.HTTPSConnection(host)
	md = calc_md5(localfilepath)
	headers = {"content-type": get_config_value("datacommons", "content-type"), "Content-MD5": md}
	
	try:
		auth_token = get_config_value("datacommons", "token")
		headers["X-Auth-Token"] = auth_token
	except:
		username_password = base64.encodestring("%s:%s" % (get_config_value("datacommons", "username"), get_config_value("datacommons", "password"))).replace('\n', '')
		headers["Authorization"] = "Basic %s" % username_password
	
	url = get_config_value("datacommons", "url") + pid + "/" + get_config_value("datacommons", "payload_dir") + "/" + urllib.quote(targetfilename)
	print "Uploading file to " + host + url + " ..."
	logging.info("Uploading file to " + host + url)
	file = file_with_progress_updates(localfilepath, "rb")
	connection.request("POST", url, file, headers)
	print
	print "File uploaded successfully."
	response = connection.getresponse()
	print "Response Code: [" + str(response.status) + "] " + response.reason
	print response.read()
	log_str = "[" + str(response.status) + "] " + response.reason
	if response.status == httplib.OK:
		logging.info(log_str)
	else:
		logging.error(log_str)

		
# Initialises the command line parser
def init_cmd_parser():
	usage = "Usage: %prog -p PID -f filename"
	parser = OptionParser(usage=usage, version="%prog 1.0")
	parser.add_option("-f", "--file", dest="filename", help="write report to FILE", metavar="FILE")
	parser.add_option("-p", "--pid", dest="pid", help="Identifier of record to which file is to be uploaded")
	(options, args) = parser.parse_args()
	return options

	
# Initialises logging.
def init_logging():
	logging.basicConfig(filename="dcupload.log", level=logging.DEBUG, format="%(asctime)s [%(levelname)s]:%(message)s")


# Calculates MD5 of a specified file while displaying progress
def calc_md5(filepath):
	print "Calculating MD5 hash... "
	blocksize = 65536
	total_blocks = os.path.getsize(filepath) / 65536
	file = open(filepath, "rb")
	m = hashlib.md5()

	buf = file.read(blocksize)
	blocks_read = 1
	while len(buf) > 0:
		m.update(buf)
		buf = file.read(blocksize)
		blocks_read = blocks_read + 1
		try:
			print "\r" + str(blocks_read * 100 / total_blocks) + "%",
		except:
			pass
	
	print
	md5 = m.hexdigest()
	print "MD5: " + md5
	return md5


# Reads a collection parameter file and retrieves a 
def get_pid(pidparam):
	if (not check_file_exists(pidparam)):
		pid = pidparam
	else:
		with open(pidparam, "rb") as csvfile:
			reader = csv.reader(csvfile, delimiter="=", quoting=csv.QUOTE_NONE)
			for row in reader:
				if (len(row) == 2 and row[0] == "pid"):
					pid = row[1]

	return pid
	
	
# Main function
def main():
	options = init_cmd_parser()
	print
	pid = get_pid(options.pid)
	upload_file_to_datacommons(pid, options.filename, os.path.basename(options.filename))
	
		
if __name__ == "__main__":
	main()