#!/usr/bin/perl
#
# publish_iris.pl
#
# Australian National University Data Commons
#
# Submits data to the IRIS DMC
#
# Version	Date		Developer		Description
# 0.1		20/11/2012	Genevieve Turner (GT)	Initial
#

use DBI;

my $logfilelocation = "/path/to/logs/logfile.txt";
my $recordlocation = "/path/to/bags/pid/data";
my $statefilelocation = "-w /path/to/statefile";
# Please note for production use the -p flag should not be there and the host:port will need to be updated
my $miniseedcall = "/path/to/miniseed/miniseed2dmc -p localhost:9000";

my $dbh = DBI->connect("DBI:Pg:dbname=datacommonsdb;host=localhost","user","password",{'RaiseError' => 1});

my $sth = $dbh->prepare('SELECT * FROM publish_iris WHERE status = ? ORDER BY publish_date LIMIT 1') or die "Couldn't prepare statement: " . $dbh->errstr;
my $uth = $dbh->prepare('UPDATE publish_iris SET status = ? WHERE pid = ? AND status = ?');

$sth->execute('IN PROGRESS') or die "Could not execute statement: " . $sth->errstr;

# Check if there is an in progress row and if it has finished
my @data;
if (@data = $sth->fetchrow_array()) {
	my $pid = $data[0];
	my $publish_date = $data[1];
	my $status = $data[2];
	open(LOGFILE,$logfilelocation);
	if (grep{/All data transmitted/} <LOGFILE>) {
		# TODO add sending dataless seed file
		$uth->execute('COMPLETE',$pid,'IN PROGRESS');
	}
	else {
		open (PS, "ps -ef | grep miniseed2dmc | grep -v grep | ") || die "Failed: $!\n";
		@lines = <PS>;
		$isMiniseed2dmcRunning = true;
		close(PS);
		$num = @lines;
		if ($num == 0) {
			# restart miniseed if the record is in progress and data has not finished being transmitted
			startMiniseed($pid);
		}
		exit 0;
	}
}
$sth->finish();

$sth->execute('INCOMPLETE') or die "Could not execute statement: " . $sth->errstr;
# check if there are any incomplete iris publishes to occur
if (@data = $sth->fetchrow_array()) {
	my $pid = $data[0];
	my $publish_date = $data[1];
	my $status = $data[2];
	$uth->execute('IN PROGRESS', $pid, 'INCOMPLETE') or die "Could not update record: " . $uth->errstr;
	$uth->finish();
	$testprogram = "ls -l";
	$testlogfile = "testlog.txt";
	
	startMiniseed($pid);
}

$sth->finish;
$dbh->disconnect;

sub startMiniseed {
	$pid = $_[0];
	$pid =~ s/:/_/g;
	$recordlocation =~ s/pid/$pid/g;
	system("$miniseedcall $statefilelocation $recordlocation >$logfilelocation");
	return;
}
