--fixing typo in link licence to license
UPDATE template_attribute 
SET tooltip = replace(tooltip, 'http://www.opendatacommons.org/licences/pddl/1.0/', '<a target="_blank" rel="noopener noreferrer" href="https://www.opendatacommons.org/licenses/pddl/1.0/" class="text-link">https://www.opendatacommons.org/licenses/pddl/1.0/</a>');


--replacing ausgoal link
UPDATE template_attribute 
SET tooltip = replace(tooltip, 'http://www.ausgoal.gov.au/the-ausgoal-licence-suite', '<a target="_blank" rel="noopener noreferrer" href="https://creativecommons.org/licenses/" class="text-link">https://creativecommons.org/licenses/</a>');


--correcting typo mateiral to material
UPDATE template_attribute 
SET tooltip = replace(tooltip, 'mateiral', 'material');

--fixing data management link
UPDATE template_attribute 
SET tooltip = replace(tooltip, 'http://libguides.anu.edu.au/datamanagement/', '<a target="_blank" rel="noopener noreferrer" href="https://libguides.anu.edu.au/research-data-management" class="text-link">https://libguides.anu.edu.au/research-data-management</a>');

--making links open in new tab
UPDATE template_attribute 
SET tooltip = replace(tooltip, '<a href="https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/6BB427AB9696C225CA2574180004463E?opendocument" class="text-link">', 
			       '<a target="_blank" rel="noopener noreferrer" href="https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/6BB427AB9696C225CA2574180004463E?opendocument" class="text-link">');

UPDATE template_attribute 
SET tooltip = replace(tooltip, '<a href="https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/CF7ADB06FA2DFD69CA2574180004CB82?opendocument" class="text-link">', 
			       '<a target="_blank" rel="noopener noreferrer" href="https://www.abs.gov.au/Ausstats/abs@.nsf/Latestproducts/CF7ADB06FA2DFD69CA2574180004CB82?opendocument" class="text-link">');

UPDATE template_attribute 
SET tooltip = replace(tooltip, '<a href="http://www.loc.gov/catdir/cpso/lcco/" class="text-link">', 
			       '<a target="_blank" rel="noopener noreferrer" href="http://www.loc.gov/catdir/cpso/lcco/" class="text-link">');
			       
--updating text to link
UPDATE template_attribute 
SET tooltip = replace(tooltip, 'http://www.gnu.org/copyleft/gpl.html', '<a target="_blank" rel="noopener noreferrer" href="http://www.gnu.org/copyleft/gpl.html" class="text-link">http://www.gnu.org/copyleft/gpl.html</a>');

--fixing typo
UPDATE template_attribute 
SET tooltip = replace(tooltip, '<strong>General Public Licenc</strong>', '<strong>General Public Licence</strong>');
