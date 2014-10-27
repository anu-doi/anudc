#!/usr/bin/perl

# Australian National University Data Commons
# Copyright (C) 2014  The Australian National University
# 
# This file is part of Australian National University Data Commons.
# 
# Australian National University Data Commons is free software: you
# can redistribute it and/or modify it under the terms of the GNU
# General Public License as published by the Free Software Foundation,
# either version 3 of the License, or (at your option) any later
# version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
# 
# @author: Genevieve Turner

=pod

=head1 NAME

check_solr_indexation.pl

=head1 DESCRIPTION

This file checks if whether items are contained within the database and have been indexed in SOLR.
If they exist in the database but not in SOLR an attempt is made to reindex the item if it has not
been deleted.

=head2 Methods

=cut

use LWP::UserAgent;
use XML::XPath;
use DBI;

$Dbuser = "dcuser";
$Dbpassword = "dcpassword";
$Dbname = "datacommonsdb";
$Solr_Rows_Number = 1000;
$Fedora_User = "fedoraAdmin";
$Fedora_Password = "fedoraAdmin";
$Item_Prefix = "test";

$Hostname = "localhost";
$Port = "8080";

=pod

=over

=item get_xml(prefix, start_number, rows_number)

Returns the XML retrieved from a SOLR query for the items

=back

=cut
sub get_xml {
	$prefix = @_[0];
	$start_number = @_[1];
	$rows_number = @_[2];
		
	my $ua = LWP::UserAgent->new;
	$ua->agent("");
	
	$url = "http://$Hostname:$Port/solr/select?q=id:$prefix*&start=$start_number&rows=$rows_number&fl=id";
	my $request = HTTP::Request->new(GET => $url);
	$request->content_type('application/xml');

	my $response = $ua->request($request);
	if (!$response->is_success) {
		print $response->status_line, "\n";
		die("Error retrieving information from SOLR");
	}
	$xml_string = $response->content;
	return $xml_string;
}

=pod

=over

=item get_solr_id_list()

Get the list of item id's from SOLR

=back

=cut
sub get_solr_id_list {
	$start_number = 0;
	
	$xml_string = get_xml($Item_Prefix, $start_number, $Solr_Rows_Number);
	
	$number_found = get_number_of_elements($xml_string);
	
	@solr_list = ();
	@secondary_solr_list = get_list_from_solr_xml($xml_string);
	push @solr_list, @secondary_solr_list;
	$start_number = $start_number + $Solr_Rows_Number;
	while ($start_number < $number_found) {
		$xml_string = get_xml($Item_Prefix, $start_number, $Solr_Rows_Number);
		@secondary_solr_list = get_list_from_solr_xml($xml_string);
		push @solr_list, @secondary_solr_list;
		$start_number = $start_number + $Solr_Rows_Number;
	}
	
	return @solr_list
}

=pod

=over

=item get_number_of_elements(xml_string)

Get the number of items that have been found from the SOLR query

=back

=cut
sub get_number_of_elements {
	$xml_string = @_[0];
	my $xp = XML::XPath->new(xml => $xml_string);
	
	my $nodeset = $xp->find('/response/result/@numFound');
	my $number_found = 0;
	foreach my $node ($nodeset->get_nodelist) {
		$number_found = $node->getNodeValue;
	}
	return $number_found;
}

=pod

=over

=item get_list_from_solr_xml(xml_string)

Get the list of item id's from the XML returned from SOLR

=back

=cut
sub get_list_from_solr_xml {
	$xml_string = @_[0];
	@solr_list_to_return = ();
	my $xp = XML::XPath->new(xml => $xml_string);
	
	my $nodeset = $xp->find('/response/result/doc/str[@name="id"]/text()');
	foreach my $node ($nodeset->get_nodelist) {
		$item_id = $node->getNodeValue;
		push @solr_list_to_return, $item_id
	}
	return @solr_list_to_return;
}

=pod

=over

=item get_db_list

Get a list of item id's from the database

=back

=cut
sub get_db_list() {
	@db_list = ();
	
	$dbh = DBI->connect("dbi:Pg:dbname=$Dbname",$Dbuser,$Dbpassword);
	
	my $sth = $dbh->prepare("SELECT pid FROM fedora_object");
	my $rv = $sth->execute();
	
	while (@data = $sth->fetchrow_array()) {
		push @db_list, @data[0];
	}
	
	return @db_list;
}

=pod

=over

=item check_and_index_missing_items(difference_list)

Check if the missing ID's exist in Fedora Commons and if they are active, if so index the items

=back

=cut
sub check_and_index_missing_items {
	@diff_list = @_;
	
	my $ua = LWP::UserAgent->new;
	$ua->agent("");
	
	foreach $pid (@diff_list) {
		$url = "http://$Hostname:$Port/fedora/objects/$pid?format=xml";
		
		my $request = HTTP::Request->new(GET => $url);
		$request->content_type('application/xml');
		$request->authorization_basic($Fedora_User,$Fedora_Password);
		
		my $response = $ua->request($request);
		if ($response->is_success) {
			$xml_string = $response->content;
			my $xp = XML::XPath->new(xml => $xml_string);
			$xp->set_namespace("fedora","http://www.fedora.info/definitions/1/0/access/");
			my $nodeset = $xp->find('/fedora:objectProfile/fedora:objState/text()');
			foreach my $node ($nodeset->get_nodelist) {
				$value = $node->getNodeValue;
				if ("A" eq $value) {
					print "The index for item $pid needs to be reindexed\n";
					update_index($pid);
				}
				else {
					print "Item $pid has the status of deleted.\n";
				}
			}
		}
		else {
			print "Error retrieving fedora object for $pid. This object may not exist in Fedora Commons.\n";
		}
	}

}

=pod

=over

=item update_index(item_id)

Update the index for the given id

=back

=cut
sub update_index {
	$pid = @_[0];
	
	$url = "http://$Hostname:$Port/fedoragsearch/rest?operation=updateIndex&action=fromPid&value=$pid";
	
	my $ua = LWP::UserAgent->new;
	$ua->agent("");
	my $request = HTTP::Request->new(GET => $url);
	$request->content_type('application/xml');
	$request->authorization_basic($Fedora_User,$Fedora_Password);
	my $response = $ua->request($request);
	if ($response->is_success) {
		print "Index Updated Successfully\n";
	}
	else {
		print "Index update failed\n";
	}
}

=pod

=over

=item get_differences(array_1, array_2)

Returns an array of items that exist in array 2, but not in array 1.

=back

=cut
sub get_differences {
	@array_1 = @{@_[0]};
	@array_2 = @{@_[1]};
	
	@diff = ();
	foreach $value (@array_2) {
		if (!grep( /^$value$/, @array_1)) {
			push(@diff, $value);
		}
	}
	return @diff;
}

@solr_id_list = get_solr_id_list();

@db_id_list = get_db_list();

@diff = get_differences(\@solr_id_list, \@db_id_list);
check_and_index_missing_items(@diff);

=pod

=head1 LICENSE

Australian National University Data Commons
Copyright (C) 2014  The Australian National University

This file is part of Australian National University Data Commons.

Australian National University Data Commons is free software: you
can redistribute it and/or modify it under the terms of the GNU
General Public License as published by the Free Software Foundation,
either version 3 of the License, or (at your option) any later
version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

=head1 AUTHOR

Genevieve Turner

=cut