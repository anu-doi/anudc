#! /usr/bin/python

"""
ANU Data Commons

This script downloads meteorological data from AAO's website, extracts data
for a given date range, or for a specific date from that file, adds it to the
weekly dataset text.

Usage:

1)	MetData.py
	Downloads the raw metadata if required. If the Output file doesn't exist, it extracts the previous day's data from the file and saves in the Output file.
	If the Output File exists, reads the last date in that file and downloads the data for the day after.
	
2)	MetData.py 2013-12-31
	Downloads the raw metadata if required and extracts the data for the specified date. Dates are in YYYY-MM-DD format.
	
3)	MetData.py 2013-12-30 2013-12-31
	Downloads the raw metadata if required and extracts the data for the specified date range. Dates are in YYYY-MM-DD format.
	
4)	MetData.py -upload
	Renames the output file in the format Dataset-YYYYMMDD-YYYYMMDD.txt and uploads it to a record in ANU Data Commons. 

"""

import os.path
import urllib
import re
import sys
import ConfigParser
import httplib
import shutil
import logging
from datetime import datetime
from datetime import date
from datetime import timedelta


# Returns a config value for a specified section and key
def get_config_value(section, key):
	config = ConfigParser.ConfigParser()
	config.readfp(open('metdata.conf'))
	return config.get(section, key)


# Returns True if a file exists, False otherwise
def check_file_exists(filename):
	return os.path.isfile(filename)


# Returns the date on the last line of a file.
def read_last_date_in_file(filename):
	lastLine = read_last_line_in_file(filename)
	return datetime.strptime(lastLine[:10], '%Y-%m-%d').date()


# Returns the date on the first line of a file.	
def read_first_date_in_file(filename):
	firstLine = read_first_line_in_file(filename)
	return datetime.strptime(firstLine[:10], '%Y-%m-%d').date()
	

# Returns the last line in a file
def read_last_line_in_file(filename):
	file = open(filename)
	for line in file:
		lastLine = line
		
	return lastLine


# Returns the first line in a file
def read_first_line_in_file(filename):
	file = open(filename)
	for line in file:
		firstLine = line
		break
		
	return firstLine


# Downloads the met data file from aao.gov.au
def download_weather_data_file():
	metDataUrl = get_config_value('metserver', 'url')
	fileSaveAs = 'metdata' + datetime.now().strftime('%Y%m%d') + '.txt'
	print 'Checking if met data already exists...',
	if not check_file_exists(fileSaveAs):
		print '[DOESN\'T EXIST]'
		print 'Downloading ' + metDataUrl + ' and saving as ' + fileSaveAs + '...',
		urllib.urlretrieve(metDataUrl, fileSaveAs)
		print '[OK]'
	else:
		print '[EXISTS]'
		print 'Metdata file ' + fileSaveAs + ' already exists. Will not redownload.'
		
	return fileSaveAs


# Extracts meteorological data from the downloaded file	for a specified date range and appends to the outputFile
def extract_lines_from_met_file(filename, dateFrom, dateTo, outputFile):
	# Regular expression to match against and extract info from the date string of a raw data file.
	dateLineRegExp = re.compile('^\" (\\d{2})-(\\d{2})-(\\d{4})\\.\"\\s*$')

	rawdata = open(filename, 'r')
	output = open(outputFile, 'a')
	
	# Iterate through each line in the raw data file. If it's a date line and the date is within
	# the range of dates whose data is required, then write the date and data line in output file.
	include_line = False
	num_lines_extracted = 0
	for line in rawdata:
		if include_line == True:
			# print line
			output.write(lineDate.isoformat() + '\t' + line.strip() + '\n')
			include_line = False
			num_lines_extracted += 1
			continue
			
		m = dateLineRegExp.match(line)
		if m:
			mm = m.groups()[0]
			dd = m.groups()[1]
			yyyy = m.groups()[2]
			lineDate = date(int(yyyy), int(mm), int(dd))
			if lineDate >= dateFrom and lineDate <= dateTo:
				include_line = True

	rawdata.close()
	output.close()
	return num_lines_extracted


# Uploads a file to ANU Data Commons
def upload_file_to_datacommons(localfilepath, targetfilename):
	host = get_config_value('datacommons', 'host')
	connection = httplib.HTTPSConnection(host)
	headers = {'content-type': get_config_value('datacommons', 'content-type'), 'X-Auth-Token': get_config_value('datacommons', 'token')}
	url = get_config_value('datacommons', 'url') + get_config_value('datacommons', 'pid') + '/' + get_config_value('datacommons', 'payload_dir') + '/' + targetfilename
	print 'Uploading file to ' + host + url + ' ...',
	logging.info('Uploading file to ' + host + url)
	connection.request('POST', url, open(localfilepath, 'rb'), headers)
	print '[OK]'
	response = connection.getresponse()
	print 'Response Code: ', response.status, response.reason
	log_str = '[' + str(response.status) + '] ' + response.reason
	if response.status == httplib.OK:
		logging.info(log_str)
	else:
		logging.error(log_str)


# Create a backup of the output file
def backup_output_file(output_filename):
	if check_file_exists(output_filename):
		shutil.copy(output_filename, output_filename + '.bak')
	

# Verify the presence of 1440 rows in the dataset for each day in the dataset. 1 row for each minute of the
# day. If less, log a warning. 
def verify_dataset_size(num_lines_extracted, num_days):
	num_lines_expected = num_days * 1440
	if num_lines_extracted != num_lines_expected:
		print 'WARNING: Dataset may be incomplete, ' + str(num_lines_expected) + ' lines expected.'
		logging.warn('Dataset may be incomplete, ' + str(num_lines_extracted) + ' lines extracted. ' + str(num_lines_expected) + ' lines expected.')
	else:
		logging.info(str(num_lines_extracted) + ' lines extracted.')

# Initialises logging.
def init_logging():
	logging.basicConfig(filename='MetData.log', level=logging.DEBUG, format='%(asctime)s [%(levelname)s]:%(message)s')
	

# Main
def main():
	init_logging()
	output_filename = 'Output.txt'
	if len(sys.argv) >= 2 and sys.argv[1].lower() == '-upload':
		uploaded_filename = 'Dataset-' + read_first_date_in_file(output_filename).strftime('%Y%m%d') + '-' + read_last_date_in_file(output_filename).strftime('%Y%m%d') + '.txt'
		upload_file_to_datacommons(output_filename, uploaded_filename)
		shutil.move(output_filename, uploaded_filename)
	else:
		# Download the met data file
		rawDataFile = download_weather_data_file()
	
		# Based on the command line parameters, determine what to do.
		if len(sys.argv) == 1:
			# If no command line arguments, and output file exists read last date from output file extract next day's data. If
			# output file doesn't exist then get the previous day's data.
			num_days = 1
			if check_file_exists(output_filename):
				lastDate = read_last_date_in_file(output_filename)
				start_date = lastDate + timedelta(days=num_days)
				end_date = start_date
			else:
				start_date = date.today() - timedelta(days=num_days)
				end_date = start_date
		elif len(sys.argv) == 2:
			# If only one date is specified as a command line argument then extract data for that date only.
			num_days = 1
			start_date = datetime.strptime(sys.argv[1], '%Y-%m-%d').date()
			end_date = start_date
		elif len(sys.argv) == 3:
			# If start and end date are specified as command line arguments then extract data for that date range.
			start_date = datetime.strptime(sys.argv[1], '%Y-%m-%d').date()
			end_date = datetime.strptime(sys.argv[2], '%Y-%m-%d').date()
			num_days = (end_date - start_date).days + 1
			
		logging.info('Extracting data for dates ' + start_date.strftime('%Y-%m-%d') + ' to ' + end_date.strftime('%Y-%m-%d'))
		print 'Required data (' + str(num_days) + ' days):'
		print '\tBegin date: ' + start_date.strftime('%Y-%m-%d')
		print '\tEnd date:   ' + end_date.strftime('%Y-%m-%d')
	
		backup_output_file(output_filename)
			
		num_lines_extracted = extract_lines_from_met_file(rawDataFile, start_date, end_date, output_filename)
		print 'Added ' + str(num_lines_extracted) + ' lines to output file.'
		verify_dataset_size(num_lines_extracted, num_days)


if __name__ == '__main__':
	main()