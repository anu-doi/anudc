/**
 * 20120620_create_select_code_table.sql
 * 
 * Australian National University Data Commons
 * 
 * This script is to perform updates to the users table in which the id column is added
 * and used as the primary key
 * 
 * 
 * Version	Date		Developer				Description
 * 0.1		16/05/2012	Genevieve Turner (GT)	Initial
 */

CREATE TABLE select_code (
	select_name		varchar(30)		NOT NULL
	,code			varchar(30)		NOT NULL
	,description	varchar(255)	NOT NULL
	,deprecated		boolean			NULL
	,PRIMARY KEY (select_name, code)
);

INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','01','01 - MATHEMATICAL SCIENCES');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','02','02 - PHYSICAL SCIENCES');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','03','03 - CHEMICAL SCIENCES');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','04','04 - EARTH SCIENCES');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','05','05 - ENVIRONMENTAL SCIENCES');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','06','06 - BIOLOGICAL SCIENCES');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','07','07 - AGRICULTURAL AND VETERINARY SCIENCES');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','08','08 - INFORMATION AND COMPUTING SCIENCES');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','09','09 - ENGINEERING');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','10','10 - TECHNOLOGY');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','11','11 - MEDICAL AND HEALTH SCIENCES');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','12','12 - BUILT ENVIRONMENT AND DESIGN');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','13','13 - EDUCATION');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','14','14 - ECONOMICS');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','15','15 - COMMERCE, MANAGEMENT, TOURISM AND SERVICES');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','16','16 - STUDIES IN HUMAN SOCIETY');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','17','17 - PSYCHOLOGY AND COGNITIVE SCIENCES');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','18','18 - LAW AND LEGAL STUDIES');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','19','19 - STUDIES IN CREATIVE ARTS AND WRITING');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','20','20 - LANGUAGE, COMMUNICATION AND CULTURE');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','21','21 - HISTORY AND ARCHAEOLOGY');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','22','22 - PHILOSOPHY AND RELIGIOUS STUDIES');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0101','0101 - Pure Mathematics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0102','0102 - Applied Mathematics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0103','0103 - Numerical and Computational Mathematics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0104','0104 - Statistics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0105','0105 - Mathematical Physics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0199','0199 - Other Mathematical Science');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010101','010101 - Algebra and Number Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010102','010102 - Algebraic and Differential Geometry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010103','010103 - Category Theory, K Theory, Homological Algebra');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010104','010104 - Combinatorics and Discrete Mathematics (excl. Physical Combinatorics)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010105','010105 - Group Theory and Generalisations');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010106','010106 - Lie Groups, Harmonic and Fourier Analysis');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010107','010107 - Mathematical Logic, Set Theory, Lattices and Universal Algebra');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010108','010108 - Operator Algebras and Functional Analysis');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010109','010109 - Ordinary Differential Equations, Difference Equations and Dynamical Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010110','010110 - Partial Differential Equations');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010111','010111 - Real and Complex Functions (incl. Several Variables)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010112','010112 - Topology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010199','010199 - Pure Mathematics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010201','010201 - Approximation Theory and Asymptotic Methods');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010202','010202 - Biological Mathematics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010203','010203 - Calculus of Variations, Systems Theory and Control Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010204','010204 - Dynamical Systems in Applications');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010205','010205 - Financial Mathematics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010206','010206 - Operations Research');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010207','010207 - Theoretical and Applied Mechanics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010299','010299 - Applied Mathematics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010301','010301 - Numerical Analysis');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010302','010302 - Numerical Solution of Differential and Integral Equations');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010303','010303 - Optimisation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010399','010399 - Numerical and Computational Mathematics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010401','010401 - Applied Statistics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010402','010402 - Biostatistics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010403','010403 - Forensic Statistics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010404','010404 - Probability Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010405','010405 - Statistical Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010406','010406 - Stochastic Analysis and Modelling');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010499','010499 - Statistics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010501','010501 - Algebraic Structures in Mathematical Physics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010502','010502 - Integrable Systems (Classical and Quantum)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010503','010503 - Mathematical Aspects of Classical Mechanics, Quantum Mechanics and Quantum Information Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010504','010504 - Mathematical Aspects of General Relativity');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010505','010505 - Mathematical Aspects of Quantum and Conformal Field Theory, Quantum Gravity and String Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010506','010506 - Statistical Mechanics, Physical Combinatorics and Mathematical Aspects of Condensed Matter');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','010599','010599 - Mathematical Physics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','019999','019999 - Mathematical Sciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0201','0201 - Astronomical and Space Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0202','0202 - Atomic, Molecular, Nuclear, Particle and Plasma Physics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0203','0203 - Classical Physics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0204','0204 - Condensed Matter Physics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0205','0205 - Optical Physics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0206','0206 - Quantum Physics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0299','0299 - 020101 Astrobiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020102','020102 - Astronomical and Space Instrumentation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020103','020103 - Cosmology and Extragalactic Astronomy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020104','020104 - Galactic Astronomy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020105','020105 - General Relativity and Gravitational Waves');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020106','020106 - High Energy Astrophysics; Cosmic Rays');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020107','020107 - Mesospheric, Ionospheric and Magnetospheric Physics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020108','020108 - Planetary Science (excl. Extraterrestrial Geology)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020109','020109 - Space and Solar Physics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020110','020110 - Stellar Astronomy and Planetary Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020199','020199 - Astronomical and Space Sciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020201','020201 - Atomic and Molecular Physics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020202','020202 - Nuclear Physics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020203','020203 - Particle Physics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020204','020204 - Plasma Physics; Fusion Plasmas; Electrical Discharges');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020299','020299 - Atomic, Molecular, Nuclear, Particle and Plasma Physics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020301','020301 - Acoustics and Acoustical Devices; Waves');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020302','020302 - Electrostatics and Electrodynamics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020303','020303 - Fluid Physics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020304','020304 - Thermodynamics and Statistical Physics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020399','020399 - Classical Physics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020401','020401 - Condensed Matter Characterisation Technique Development');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020402','020402 - Condensed Matter Imaging');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020403','020403 - Condensed Matter Modelling and Density Functional Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020404','020404 - Electronic and Magnetic Properties of Condensed Matter; Superconductivity');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020405','020405 - Soft Condensed Matter');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020406','020406 - Surfaces and Structural Properties of Condensed Matter');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020499','020499 - Condensed Matter Physics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020501','020501 - Classical and Physical Optics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020502','020502 - Lasers and Quantum Electronics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020503','020503 - Nonlinear Optics and Spectroscopy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020504','020504 - Photonics, Optoelectronics and Optical Communications');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020599','020599 - Optical Physics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020601','020601 - Degenerate Quantum Gases and Atom Optics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020602','020602 - Field Theory and String Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020603','020603 - Quantum Information, Computation and Communication');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020604','020604 - Quantum Optics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','020699','020699 - Quantum Physics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','029901','029901 - Biological Physics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','029902','029902 - Complex Physical Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','029903','029903 - Medical Physics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','029904','029904 - Synchrotrons; Accelerators; Instruments and Techniques');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','029999','029999 - Physical Sciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0301','0301 - Analytical Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0302','0302 - Inorganic Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0303','0303 - Macromolecular and Materials Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0304','0304 - Medicinal and Biomolecular Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0305','0305 - Organic Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0306','0306 - Physical Chemistry (incl. Structural)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0307','0307 - Theoretical and Computational Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0399','0399 - Other Chemical Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030101','030101 - Analytical Spectrometry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030102','030102 - Electroanalytical Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030103','030103 - Flow Analysis');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030104','030104 - Immunological and Bioassay Methods');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030105','030105 - Instrumental Methods (excl. Immunological and Bioassay Methods)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030106','030106 - Quality Assurance, Chemometrics, Traceability and Metrological Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030107','030107 - Sensor Technology (Chemical aspects)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030108','030108 - Separation Science');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030199','030199 - Analytical Chemistry not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030201','030201 - Bioinorganic Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030202','030202 - f-Block Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030203','030203 - Inorganic Green Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030204','030204 - Main Group Metal Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030205','030205 - Non-metal Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030206','030206 - Solid State Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030207','030207 - Transition Metal Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030299','030299 - Inorganic Chemistry not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030301','030301 - Chemical Characterisation of Materials');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030302','030302 - Nanochemistry and Supramolecular Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030303','030303 - Optical Properties of Materials');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030304','030304 - Physical Chemistry of Materials');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030305','030305 - Polymerisation Mechanisms');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030306','030306 - Synthesis of Materials');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030307','030307 - Theory and Design of Materials');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030399','030399 - Macromolecular and Materials Chemistry not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030401','030401 - Biologically Active Molecules');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030402','030402 - Biomolecular Modelling and Design');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030403','030403 - Characterisation of Biological Macromolecules');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030404','030404 - Cheminformatics and Quantitative Structure-Activity Relationships');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030405','030405 - Molecular Medicine');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030406','030406 - Proteins and Peptides');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030499','030499 - Medicinal and Biomolecular Chemistry not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030501','030501 - Free Radical Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030502','030502 - Natural Products Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030503','030503 - Organic Chemical Synthesis');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030504','030504 - Organic Green Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030505','030505 - Physical Organic Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030599','030599 - Organic Chemistry not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030601','030601 - Catalysis and Mechanisms of Reactions');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030602','030602 - Chemical Thermodynamics and Energetics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030603','030603 - Colloid and Surface Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030604','030604 - Electrochemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030605','030605 - Solution Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030606','030606 - Structural Chemistry and Spectroscopy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030607','030607 - Transport Properties and Non-equilibrium Processes');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030699','030699 - Physical Chemistry not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030701','030701 - Quantum Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030702','030702 - Radiation and Matter');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030703','030703 - Reaction Kinetics and Dynamics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030704','030704 - Statistical Mechanics in Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','030799','030799 - Theoretical and Computational Chemistry not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','039901','039901 - Environmental Chemistry (incl. Atmospheric Chemistry)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','039902','039902 - Forensic Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','039903','039903 - Industrial Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','039904','039904 - Organometallic Chemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','039999','039999 - Chemical Sciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0401','0401 - Atmospheric Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0402','0402 - Geochemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0403','0403 - Geology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0404','0404 - Geophysics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0405','0405 - Oceanography');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0406','0406 - Physical Geography and Environmental Geoscience');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0499','0499 - Other Earth Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040101','040101 - Atmospheric Aerosols');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040102','040102 - Atmospheric Dynamics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040103','040103 - Atmospheric Radiation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040104','040104 - Climate Change Processes');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040105','040105 - Climatology (excl. Climate Change Processes)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040106','040106 - Cloud Physics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040107','040107 - Meteorology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040108','040108 - Tropospheric and Stratospheric Physics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040199','040199 - Atmospheric Sciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040201','040201 - Exploration Geochemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040202','040202 - Inorganic Geochemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040203','040203 - Isotope Geochemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040204','040204 - Organic Geochemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040299','040299 - Geochemistry not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040301','040301 - Basin Analysis');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040302','040302 - Extraterrestrial Geology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040303','040303 - Geochronology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040304','040304 - Igneous and Metamorphic Petrology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040305','040305 - Marine Geoscience');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040306','040306 - Mineralogy and Crystallography');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040307','040307 - Ore Deposit Petrology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040308','040308 - Palaeontology (incl. Palynology)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040309','040309 - Petroleum and Coal Geology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040310','040310 - Sedimentology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040311','040311 - Stratigraphy (incl. Biostratigraphy and Sequence Stratigraphy)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040312','040312 - Structural Geology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040313','040313 - Tectonics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040314','040314 - Volcanology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040399','040399 - Geology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040401','040401 - Electrical and Electromagnetic Methods in Geophysics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040402','040402 - Geodynamics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040403','040403 - Geophysical Fluid Dynamics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040404','040404 - Geothermics and Radiometrics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040405','040405 - Gravimetrics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040406','040406 - Magnetism and Palaeomagnetism');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040407','040407 - Seismology and Seismic Exploration');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040499','040499 - Geophysics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040501','040501 - Biological Oceanography');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040502','040502 - Chemical Oceanography');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040503','040503 - Physical Oceanography');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040599','040599 - Oceanography not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040601','040601 - Geomorphology and Regolith and Landscape Evolution');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040602','040602 - Glaciology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040603','040603 - Hydrogeology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040604','040604 - Natural Hazards');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040605','040605 - Palaeoclimatology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040606','040606 - Quaternary Environments');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040607','040607 - Surface Processes');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040608','040608 - Surfacewater Hydrology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','040699','040699 - Physical Geography and Environmental Geoscience not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','049999','049999 - Earth Sciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0501','0501 - Ecological Applications');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0502','0502 - Environmental Science and Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0503','0503 - Soil Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0599','0599 - Other Environmental Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050101','050101 - Ecological Impacts of Climate Change');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050102','050102 - Ecosystem Function');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050103','050103 - Invasive Species Ecology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050104','050104 - Landscape Ecology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050199','050199 - Ecological Applications not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050201','050201 - Aboriginal and Torres Strait Islander Environmental Knowledge');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050202','050202 - Conservation and Biodiversity');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050203','050203 - Environmental Education and Extension');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050204','050204 - Environmental Impact Assessment');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050205','050205 - Environmental Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050206','050206 - Environmental Monitoring');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050207','050207 - Environmental Rehabilitation (excl. Bioremediation)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050208','050208 - Maori Environmental Knowledge');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050209','050209 - Natural Resource Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050210','050210 - Pacific Peoples Environmental Knowledge');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050211','050211 - Wildlife and Habitat Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050299','050299 - Environmental Science and Management not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050301','050301 - Carbon Sequestration Science');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050302','050302 - Land Capability and Soil Degradation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050303','050303 - Soil Biology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050304','050304 - Soil Chemistry (excl. Carbon Sequestration Science)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050305','050305 - Soil Physics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','050399','050399 - Soil Sciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','059999','059999 - Environmental Sciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0601','0601 - Biochemistry and Cell Biology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0602','0602 - Ecology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0603','0603 - Evolutionary Biology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0604','0604 - Genetics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0605','0605 - Microbiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0606','0606 - Physiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0607','0607 - Plant Biology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0608','0608 - Zoology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0699','0699 - Other Biological Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060101','060101 - Analytical Biochemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060102','060102 - Bioinformatics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060103','060103 - Cell Development, Proliferation and Death');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060104','060104 - Cell Metabolism');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060105','060105 - Cell Neurochemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060106','060106 - Cellular Interactions (incl. Adhesion, Matrix, Cell Wall)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060107','060107 - Enzymes');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060108','060108 - Protein Trafficking');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060109','060109 - Proteomics and Intermolecular Interactions (excl. Medical Proteomics)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060110','060110 - Receptors and Membrane Biology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060111','060111 - Signal Transduction');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060112','060112 - Structural Biology (incl. Macromolecular Modelling)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060113','060113 - Synthetic Biology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060114','060114 - Systems Biology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060199','060199 - Biochemistry and Cell Biology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060201','060201 - Behavioural Ecology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060202','060202 - Community Ecology (excl. Invasive Species Ecology)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060203','060203 - Ecological Physiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060204','060204 - Freshwater Ecology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060205','060205 - Marine and Estuarine Ecology (incl. Marine Ichthyology)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060206','060206 - Palaeoecology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060207','060207 - Population Ecology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060208','060208 - Terrestrial Ecology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060299','060299 - Ecology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060301','060301 - Animal Systematics and Taxonomy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060302','060302 - Biogeography and Phylogeography');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060303','060303 - Biological Adaptation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060304','060304 - Ethology and Sociobiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060305','060305 - Evolution of Developmental Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060306','060306 - Evolutionary Impacts of Climate Change');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060307','060307 - Host-Parasite Interactions');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060308','060308 - Life Histories');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060309','060309 - Phylogeny and Comparative Analysis');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060310','060310 - Plant Systematics and Taxonomy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060311','060311 - Speciation and Extinction');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060399','060399 - Evolutionary Biology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060401','060401 - Anthropological Genetics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060402','060402 - Cell and Nuclear Division');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060403','060403 - Developmental Genetics (incl. Sex Determination)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060404','060404 - Epigenetics (incl. Genome Methylation and Epigenomics)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060405','060405 - Gene Expression (incl. Microarray and other genome-wide approaches)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060406','060406 - Genetic Immunology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060407','060407 - Genome Structure and Regulation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060408','060408 - Genomics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060409','060409 - Molecular Evolution');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060410','060410 - Neurogenetics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060411','060411 - Population, Ecological and Evolutionary Genetics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060412','060412 - Quantitative Genetics (incl. Disease and Trait Mapping Genetics)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060499','060499 - Genetics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060501','060501 - Bacteriology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060502','060502 - Infectious Agents');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060503','060503 - Microbial Genetics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060504','060504 - Microbial Ecology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060505','060505 - Mycology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060506','060506 - Virology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060599','060599 - Microbiology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060601','060601 - Animal Physiology - Biophysics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060602','060602 - Animal Physiology - Cell');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060603','060603 - Animal Physiology - Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060604','060604 - Comparative Physiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060699','060699 - Physiology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060701','060701 - Phycology (incl. Marine Grasses)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060702','060702 - Plant Cell and Molecular Biology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060703','060703 - Plant Developmental and Reproductive Biology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060704','060704 - Plant Pathology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060705','060705 - Plant Physiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060799','060799 - Plant Biology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060801','060801 - Animal Behaviour');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060802','060802 - Animal Cell and Molecular Biology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060803','060803 - Animal Developmental and Reproductive Biology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060804','060804 - Animal Immunology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060805','060805 - Animal Neurobiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060806','060806 - Animal Physiological Ecology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060807','060807 - Animal Structure and Function');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060808','060808 - Invertebrate Biology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060809','060809 - Vertebrate Biology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','060899','060899 - Zoology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','069901','069901 - Forensic Biology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','069902','069902 - Global Change Biology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','069999','069999 - Biological Sciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0701','0701 - Agriculture, Land and Farm Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0702','0702 - Animal Production');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0703','0703 - Crop and Pasture Production');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0704','0704 - Fisheries Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0705','0705 - Forestry Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0706','0706 - Horticultural Production');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0707','0707 - Veterinary Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0799','0799 - Other Agricultural and Veterinary Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070101','070101 - Agricultural Land Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070102','070102 - Agricultural Land Planning');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070103','070103 - Agricultural Production Systems Simulation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070104','070104 - Agricultural Spatial Analysis and Modelling');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070105','070105 - Agricultural Systems Analysis and Modelling');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070106','070106 - Farm Management, Rural Management and Agribusiness');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070107','070107 - Farming Systems Research');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070108','070108 - Sustainable Agricultural Development');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070199','070199 - Agriculture, Land and Farm Management not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070201','070201 - Animal Breeding');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070202','070202 - Animal Growth and Development');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070203','070203 - Animal Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070204','070204 - Animal Nutrition');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070205','070205 - Animal Protection (Pests and Pathogens)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070206','070206 - Animal Reproduction');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070207','070207 - Humane Animal Treatment');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070299','070299 - Animal Production not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070301','070301 - Agro-ecosystem Function and Prediction');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070302','070302 - Agronomy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070303','070303 - Crop and Pasture Biochemistry and Physiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070304','070304 - Crop and Pasture Biomass and Bioproducts');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070305','070305 - Crop and Pasture Improvement (Selection and Breeding)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070306','070306 - Crop and Pasture Nutrition');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070307','070307 - Crop and Pasture Post Harvest Technologies (incl. Transportation and Storage)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070308','070308 - Crop and Pasture Protection (Pests, Diseases and Weeds)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070399','070399 - Crop and Pasture Production not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070401','070401 - Aquaculture');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070402','070402 - Aquatic Ecosystem Studies and Stock Assessment');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070403','070403 - Fisheries Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070404','070404 - Fish Pests and Diseases');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070405','070405 - Fish Physiology and Genetics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070406','070406 - Post-Harvest Fisheries Technologies (incl. Transportation)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070499','070499 - Fisheries Sciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070501','070501 - Agroforestry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070502','070502 - Forestry Biomass and Bioproducts');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070503','070503 - Forestry Fire Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070504','070504 - Forestry Management and Environment');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070505','070505 - Forestry Pests, Health and Diseases');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070506','070506 - Forestry Product Quality Assessment');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070507','070507 - Tree Improvement (Selection and Breeding)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070508','070508 - Tree Nutrition and Physiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070509','070509 - Wood Fibre Processing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070510','070510 - Wood Processing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070599','070599 - Forestry Sciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070601','070601 - Horticultural Crop Growth and Development');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070602','070602 - Horticultural Crop Improvement (Selection and Breeding)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070603','070603 - Horticultural Crop Protection (Pests, Diseases and Weeds)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070604','070604 - Oenology and Viticulture');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070605','070605 - Post Harvest Horticultural Technologies (incl. Transportation and Storage)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070699','070699 - Horticultural Production not elsewhere classified ');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070701','070701 - Veterinary Anaesthesiology and Intensive Care');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070702','070702 - Veterinary Anatomy and Physiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070703','070703 - Veterinary Diagnosis and Diagnostics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070704','070704 - Veterinary Epidemiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070705','070705 - Veterinary Immunology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070706','070706 - Veterinary Medicine');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070707','070707 - Veterinary Microbiology (excl. Virology)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070708','070708 - Veterinary Parasitology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070709','070709 - Veterinary Pathology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070710','070710 - Veterinary Pharmacology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070711','070711 - Veterinary Surgery');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070712','070712 - Veterinary Virology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','070799','070799 - Veterinary Sciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','079901','079901 - Agricultural Hydrology (Drainage, Flooding, Irrigation, Quality, etc.)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','079902','079902 - Fertilisers and Agrochemicals (incl. Application)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','079999','079999 - Agricultural and Veterinary Sciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0801','0801 - Artificial Intelligence and Image Processing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0802','0802 - Computation Theory and Mathematics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0803','0803 - Computer Software');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0804','0804 - Data Format');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0805','0805 - Distributed Computing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0806','0806 - Information Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0807','0807 - Library and Information Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0899','0899 - Other Information and Computing Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080101','080101 - Adaptive Agents and Intelligent Robotics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080102','080102 - Artificial Life');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080103','080103 - Computer Graphics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080104','080104 - Computer Vision');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080105','080105 - Expert Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080106','080106 - Image Processing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080107','080107 - Natural Language Processing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080108','080108 - Neural, Evolutionary and Fuzzy Computation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080109','080109 - Pattern Recognition and Data Mining');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080110','080110 - Simulation and Modelling');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080111','080111 - Virtual Reality and Related Simulation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080199','080199 - Artificial Intelligence and Image Processing not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080201','080201 - Analysis of Algorithms and Complexity');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080202','080202 - Applied Discrete Mathematics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080203','080203 - Computational Logic and Formal Languages');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080204','080204 - Mathematical Software');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080205','080205 - Numerical Computation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080299','080299 - Computation Theory and Mathematics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080301','080301 - Bioinformatics Software');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080302','080302 - Computer System Architecture');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080303','080303 - Computer System Security');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080304','080304 - Concurrent Programming');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080305','080305 - Multimedia Programming');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080306','080306 - Open Software');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080307','080307 - Operating Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080308','080308 - Programming Languages');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080309','080309 - Software Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080399','080399 - Computer Software not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080401','080401 - Coding and Information Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080402','080402 - Data Encryption');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080403','080403 - Data Structures');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080404','080404 - Markup Languages');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080499','080499 - Data Format not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080501','080501 - Distributed and Grid Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080502','080502 - Mobile Technologies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080503','080503 - Networking and Communications');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080504','080504 - Ubiquitous Computing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080505','080505 - Web Technologies (excl. Web Search)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080599','080599 - Distributed Computing not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080601','080601 - Aboriginal and Torres Strait Islander Information and Knowledge Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080602','080602 - Computer-Human Interaction');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080603','080603 - Conceptual Modelling');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080604','080604 - Database Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080605','080605 - Decision Support and Group Support Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080606','080606 - Global Information Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080607','080607 - Information Engineering and Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080608','080608 - Information Systems Development Methodologies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080609','080609 - Information Systems Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080610','080610 - Information Systems Organisation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080611','080611 - Information Systems Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080612','080612 - Interorganisational Information Systems and Web Services');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080613','080613 - Maori Information and Knowledge Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080614','080614 - Pacific Peoples Information and Knowledge Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080699','080699 - Information Systems not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080701','080701 - Aboriginal and Torres Strait Islander Knowledge Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080702','080702 - Health Informatics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080703','080703 - Human Information Behaviour');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080704','080704 - Information Retrieval and Web Search');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080705','080705 - Informetrics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080706','080706 - Librarianship');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080707','080707 - Organisation of Information and Knowledge Resources');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080708','080708 - Records and Information Management (excl. Business Records and Information Management)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080709','080709 - Social and Community Informatics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','080799','080799 - Library and Information Studies not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','089999','089999 - Information and Computing Sciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0901','0901 - Aerospace Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0902','0902 - Automotive Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0903','0903 - Biomedical Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0904','0904 - Chemical Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0905','0905 - Civil Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0906','0906 - Electrical and Electronic Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0907','0907 - Environmental Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0908','0908 - Food Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0909','0909 - Geomatic Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0910','0910 - Manufacturing Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0911','0911 - Maritime Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0912','0912 - Materials Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0913','0913 - Mechanical Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0914','0914 - Resources Engineering and Extractive Metallurgy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0915','0915 - Interdisciplinary Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','0999','0999 - Other Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090101','090101 - Aerodynamics (excl. Hypersonic Aerodynamics)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090102','090102 - Aerospace Materials');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090103','090103 - Aerospace Structures');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090104','090104 - Aircraft Performance and Flight Control Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090105','090105 - Avionics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090106','090106 - Flight Dynamics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090107','090107 - Hypersonic Propulsion and Hypersonic Aerodynamics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090108','090108 - Satellite, Space Vehicle and Missile Design and Testing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090199','090199 - Aerospace Engineering not elsewhere classified ');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090201','090201 - Automotive Combustion and Fuel Engineering (incl. Alternative/Renewable Fuels)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090202','090202 - Automotive Engineering Materials');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090203','090203 - Automotive Mechatronics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090204','090204 - Automotive Safety Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090205','090205 - Hybrid Vehicles and Powertrains');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090299','090299 - Automotive Engineering not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090301','090301 - Biomaterials');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090302','090302 - Biomechanical Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090303','090303 - Biomedical Instrumentation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090304','090304 - Medical Devices');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090305','090305 - Rehabilitation Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090399','090399 - Biomedical Engineering not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090401','090401 - Carbon Capture Engineering (excl. Sequestration)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090402','090402 - Catalytic Process Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090403','090403 - Chemical Engineering Design');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090404','090404 - Membrane and Separation Technologies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090405','090405 - Non-automotive Combustion and Fuel Engineering (incl. Alternative/Renewable Fuels)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090406','090406 - Powder and Particle Technology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090407','090407 - Process Control and Simulation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090408','090408 - Rheology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090409','090409 - Wastewater Treatment Processes');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090410','090410 - Water Treatment Processes');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090499','090499 - Chemical Engineering not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090501','090501 - Civil Geotechnical Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090502','090502 - Construction Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090503','090503 - Construction Materials');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090504','090504 - Earthquake Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090505','090505 - Infrastructure Engineering and Asset Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090506','090506 - Structural Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090507','090507 - Transport Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090508','090508 - Water Quality Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090509','090509 - Water Resources Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090599','090599 - Civil Engineering not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090601','090601 - Circuits and Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090602','090602 - Control Systems, Robotics and Automation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090603','090603 - Industrial Electronics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090604','090604 - Microelectronics and Integrated Circuits');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090605','090605 - Photodetectors, Optical Sensors and Solar Cells');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090606','090606 - Photonics and Electro-Optical Engineering (excl. Communications)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090607','090607 - Power and Energy Systems Engineering (excl. Renewable Power)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090608','090608 - Renewable Power and Energy Systems Engineering (excl. Solar Cells)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090609','090609 - Signal Processing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090699','090699 - Electrical and Electronic Engineering not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090701','090701 - Environmental Engineering Design');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090702','090702 - Environmental Engineering Modelling');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090703','090703 - Environmental Technologies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090799','090799 - Environmental Engineering not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090801','090801 - Food Chemistry and Molecular Gastronomy (excl. Wine)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090802','090802 - Food Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090803','090803 - Food Nutritional Balance');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090804','090804 - Food Packaging, Preservation and Safety');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090805','090805 - Food Processing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090806','090806 - Wine Chemistry and Wine Sensory Science');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090899','090899 - Food Sciences not elsewhere classified ');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090901','090901 - Cartography');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090902','090902 - Geodesy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090903','090903 - Geospatial Information Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090904','090904 - Navigation and Position Fixing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090905','090905 - Photogrammetry and Remote Sensing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090906','090906 - Surveying (incl. Hydrographic Surveying)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','090999','090999 - Geomatic Engineering not elsewhere classified ');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091001','091001 - CAD/CAM Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091002','091002 - Flexible Manufacturing Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091003','091003 - Machine Tools');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091004','091004 - Machining');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091005','091005 - Manufacturing Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091006','091006 - Manufacturing Processes and Technologies (excl. Textiles)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091007','091007 - Manufacturing Robotics and Mechatronics (excl. Automotive Mechatronics)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091008','091008 - Manufacturing Safety and Quality');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091009','091009 - Microtechnology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091010','091010 - Packaging, Storage and Transportation (excl. Food and Agricultural Products)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091011','091011 - Precision Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091012','091012 - Textile Technology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091099','091099 - Manufacturing Engineering not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091101','091101 - Marine Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091102','091102 - Naval Architecture');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091103','091103 - Ocean Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091104','091104 - Ship and Platform Hydrodynamics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091105','091105 - Ship and Platform Structures');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091106','091106 - Special Vehicles');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091199','091199 - Maritime Engineering not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091201','091201 - Ceramics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091202','091202 - Composite and Hybrid Materials');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091203','091203 - Compound Semiconductors');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091204','091204 - Elemental Semiconductors');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091205','091205 - Functional Materials');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091206','091206 - Glass');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091207','091207 - Metals and Alloy Materials');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091208','091208 - Organic Semiconductors');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091209','091209 - Polymers and Plastics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091210','091210 - Timber, Pulp and Paper');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091299','091299 - Materials Engineering not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091301','091301 - Acoustics and Noise Control (excl. Architectural Acoustics)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091302','091302 - Automation and Control Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091303','091303 - Autonomous Vehicles');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091304','091304 - Dynamics, Vibration and Vibration Control');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091305','091305 - Energy Generation, Conversion and Storage Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091306','091306 - Microelectromechanical Systems (MEMS)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091307','091307 - Numerical Modelling and Mechanical Characterisation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091308','091308 - Solid Mechanics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091309','091309 - Tribology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091399','091399 - Mechanical Engineering not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091401','091401 - Electrometallurgy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091402','091402 - Geomechanics and Resources Geotechnical Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091403','091403 - Hydrometallurgy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091404','091404 - Mineral Processing/Beneficiation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091405','091405 - Mining Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091406','091406 - Petroleum and Reservoir Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091407','091407 - Pyrometallurgy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091499','091499 - Resources Engineering and Extractive Metallurgy not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091501','091501 - Computational Fluid Dynamics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091502','091502 - Computational Heat Transfer');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091503','091503 - Engineering Practice');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091504','091504 - Fluidisation and Fluid Mechanics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091505','091505 - Heat and Mass Transfer Operations');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091506','091506 - Nuclear Engineering (incl. Fuel Enrichment and Waste Processing and Storage)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091507','091507 - Risk Engineering (excl. Earthquake Engineering)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091508','091508 - Turbulent Flows');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','091599','091599 - Interdisciplinary Engineering not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','099901','099901 - Agricultural Engineering');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','099902','099902 - Engineering Instrumentation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','099999','099999 - Engineering not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1001','1001 - Agricultural Biotechnology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1002','1002 - Environmental Biotechnology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1003','1003 - Industrial Biotechnology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1004','1004 - Medical Biotechnology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1005','1005 - Communications Technologies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1006','1006 - Computer Hardware');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1007','1007 - Nanotechnology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1099','1099 - Other Technology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100101','100101 - Agricultural Biotechnology Diagnostics (incl. Biosensors)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100102','100102 - Agricultural Marine Biotechnology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100103','100103 - Agricultural Molecular Engineering of Nucleic Acids and Proteins');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100104','100104 - Genetically Modified Animals');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100105','100105 - Genetically Modified Field Crops and Pasture');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100106','100106 - Genetically Modified Horticulture Plants');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100107','100107 - Genetically Modified Trees');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100108','100108 - Livestock Cloning');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100109','100109 - Transgenesis');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100199','100199 - Agricultural Biotechnology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100201','100201 - Biodiscovery');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100202','100202 - Biological Control');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100203','100203 - Bioremediation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100204','100204 - Environmental Biotechnology Diagnostics (incl. Biosensors)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100205','100205 - Environmental Marine Biotechnology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100206','100206 - Environmental Molecular Engineering of Nucleic Acids and Proteins');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100299','100299 - Environmental Biotechnology not elsewhere classified ');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100301','100301 - Biocatalysis and Enzyme Technology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100302','100302 - Bioprocessing, Bioproduction and Bioproducts');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100303','100303 - Fermentation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100304','100304 - Industrial Biotechnology Diagnostics (incl. Biosensors)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100305','100305 - Industrial Microbiology (incl. Biofeedstocks)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100306','100306 - Industrial Molecular Engineering of Nucleic Acids and Proteins');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100399','100399 - Industrial Biotechnology not elsewhere classified ');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100401','100401 - Gene and Molecular Therapy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100402','100402 - Medical Biotechnology Diagnostics (incl. Biosensors)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100403','100403 - Medical Molecular Engineering of Nucleic Acids and Proteins');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100404','100404 - Regenerative Medicine (incl. Stem Cells and Tissue Engineering)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100499','100499 - Medical Biotechnology not elsewhere classified ');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100501','100501 - Antennas and Propagation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100502','100502 - Broadband and Modem Technology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100503','100503 - Computer Communications Networks');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100504','100504 - Data Communications');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100505','100505 - Microwave and Millimetrewave Theory and Technology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100506','100506 - Optical Fibre Communications');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100507','100507 - Optical Networks and Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100508','100508 - Satellite Communications');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100509','100509 - Video Communications');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100510','100510 - Wireless Communications');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100599','100599 - Communications Technologies not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100601','100601 - Arithmetic and Logic Structures');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100602','100602 - Input, Output and Data Devices');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100603','100603 - Logic Design');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100604','100604 - Memory Structures');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100605','100605 - Performance Evaluation; Testing and Simulation of Reliability');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100606','100606 - Processor Architectures');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100699','100699 - Computer Hardware not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100701','100701 - Environmental Nanotechnology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100702','100702 - Molecular and Organic Electronics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100703','100703 - Nanobiotechnology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100704','100704 - Nanoelectromechanical Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100705','100705 - Nanoelectronics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100706','100706 - Nanofabrication, Growth and Self Assembly');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100707','100707 - Nanomanufacturing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100708','100708 - Nanomaterials');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100709','100709 - Nanomedicine');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100710','100710 - Nanometrology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100711','100711 - Nanophotonics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100712','100712 - Nanoscale Characterisation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100713','100713 - Nanotoxicology, Health and Safety');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','100799','100799 - Nanotechnology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','109999','109999 - Technology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1101','1101 - Medical Biochemistry and Metabolomics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1102','1102 - Cardiorespiratory Medicine and Haematology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1103','1103 - Clinical Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1104','1104 - Complementary and Alternative Medicine');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1105','1105 - Dentistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1106','1106 - Human Movement and Sports Science');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1107','1107 - Immunology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1108','1108 - Medical Microbiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1109','1109 - Neurosciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1110','1110 - Nursing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1111','1111 - Nutrition and Dietetics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1112','1112 - Oncology and Carcinogenesis');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1113','1113 - Ophthalmology and Optometry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1114','1114 - Paediatrics and Reproductive Medicine');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1115','1115 - Pharmacology and Pharmaceutical Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1116','1116 - Medical Physiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1117','1117 - Public Health and Health Services');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1199','1199 - Other Medical and Health Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110101','110101 - Medical Biochemistry: Amino Acids and Metabolites');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110102','110102 - Medical Biochemistry: Carbohydrates');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110103','110103 - Medical Biochemistry: Inorganic Elements and Compounds');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110104','110104 - Medical Biochemistry: Lipids');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110105','110105 - Medical Biochemistry: Nucleic Acids');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110106','110106 - Medical Biochemistry: Proteins and Peptides (incl. Medical Proteomics)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110107','110107 - Metabolic Medicine');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110199','110199 - Medical Biochemistry and Metabolomics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110201','110201 - Cardiology (incl. Cardiovascular Diseases)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110202','110202 - Haematology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110203','110203 - Respiratory Diseases');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110299','110299 - Cardiorespiratory Medicine and Haematology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110301','110301 - Anaesthesiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110302','110302 - Clinical Chemistry (diagnostics)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110303','110303 - Clinical Microbiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110304','110304 - Dermatology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110305','110305 - Emergency Medicine');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110306','110306 - Endocrinology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110307','110307 - Gastroenterology and Hepatology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110308','110308 - Geriatrics and Gerontology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110309','110309 - Infectious Diseases');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110310','110310 - Intensive Care');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110311','110311 - Medical Genetics (excl. Cancer Genetics)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110312','110312 - Nephrology and Urology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110313','110313 - Nuclear Medicine');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110314','110314 - Orthopaedics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110315','110315 - Otorhinolaryngology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110316','110316 - Pathology (excl. Oral Pathology)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110317','110317 - Physiotherapy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110318','110318 - Podiatry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110319','110319 - Psychiatry (incl. Psychotherapy)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110320','110320 - Radiology and Organ Imaging');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110321','110321 - Rehabilitation and Therapy (excl. Physiotherapy)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110322','110322 - Rheumatology and Arthritis');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110323','110323 - Surgery');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110324','110324 - Venereology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110399','110399 - Clinical Sciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110401','110401 - Chiropractic');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110402','110402 - Naturopathy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110403','110403 - Traditional Aboriginal and Torres Strait Islander Medicine and Treatments');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110404','110404 - Traditional Chinese Medicine and Treatments');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110405','110405 - Traditional Maori Medicine and Treatments');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110499','110499 - Complementary and Alternative Medicine not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110501','110501 - Dental Materials and Equipment');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110502','110502 - Dental Therapeutics, Pharmacology and Toxicology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110503','110503 - Endodontics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110504','110504 - Oral and Maxillofacial Surgery');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110505','110505 - Oral Medicine and Pathology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110506','110506 - Orthodontics and Dentofacial Orthopaedics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110507','110507 - Paedodontics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110508','110508 - Periodontics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110509','110509 - Special Needs Dentistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110599','110599 - Dentistry not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110601','110601 - Biomechanics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110602','110602 - Exercise Physiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110603','110603 - Motor Control');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110604','110604 - Sports Medicine');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110699','110699 - Human Movement and Sports Science not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110701','110701 - Allergy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110702','110702 - Applied Immunology (incl. Antibody Engineering, Xenotransplantation and T-cell Therapies)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110703','110703 - Autoimmunity');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110704','110704 - Cellular Immunology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110705','110705 - Humoural Immunology and Immunochemistry');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110706','110706 - Immunogenetics (incl. Genetic Immunology)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110707','110707 - Innate Immunity');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110708','110708 - Transplantation Immunology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110709','110709 - Tumour Immunology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110799','110799 - Immunology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110801','110801 - Medical Bacteriology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110802','110802 - Medical Infection Agents (incl. Prions)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110803','110803 - Medical Parasitology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110804','110804 - Medical Virology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110899','110899 - Medical Microbiology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110901','110901 - Autonomic Nervous System');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110902','110902 - Cellular Nervous System');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110903','110903 - Central Nervous System');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110904','110904 - Neurology and Neuromuscular Diseases');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110905','110905 - Peripheral Nervous System');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110906','110906 - Sensory Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','110999','110999 - Neurosciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111001','111001 - Aged Care Nursing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111002','111002 - Clinical Nursing: Primary (Preventative)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111003','111003 - Clinical Nursing: Secondary (Acute Care)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111004','111004 - Clinical Nursing: Tertiary (Rehabilitative)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111005','111005 - Mental Health Nursing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111006','111006 - Midwifery');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111099','111099 - Nursing not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111101','111101 - Clinical and Sports Nutrition');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111102','111102 - Dietetics and Nutrigenomics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111103','111103 - Nutritional Physiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111104','111104 - Public Nutrition Intervention');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111199','111199 - Nutrition and Dietetics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111201','111201 - Cancer Cell Biology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111202','111202 - Cancer Diagnosis');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111203','111203 - Cancer Genetics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111204','111204 - Cancer Therapy (excl. Chemotherapy and Radiation Therapy)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111205','111205 - Chemotherapy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111206','111206 - Haematological Tumours');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111207','111207 - Molecular Targets');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111208','111208 - Radiation Therapy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111209','111209 - Solid Tumours');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111299','111299 - Oncology and Carcinogenesis not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111301','111301 - Ophthalmology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111302','111302 - Optical Technology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111303','111303 - Vision Science');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111399','111399 - Ophthalmology and Optometry not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111401','111401 - Foetal Development and Medicine');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111402','111402 - Obstetrics and Gynaecology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111403','111403 - Paediatrics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111404','111404 - Reproduction');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111499','111499 - Paediatrics and Reproductive Medicine not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111501','111501 - Basic Pharmacology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111502','111502 - Clinical Pharmacology and Therapeutics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111503','111503 - Clinical Pharmacy and Pharmacy Practice');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111504','111504 - Pharmaceutical Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111505','111505 - Pharmacogenomics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111506','111506 - Toxicology (incl. Clinical Toxicology)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111599','111599 - Pharmacology and Pharmaceutical Sciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111601','111601 - Cell Physiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111602','111602 - Human Biophysics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111603','111603 - Systems Physiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111699','111699 - Medical Physiology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111701','111701 - Aboriginal and Torres Strait Islander Health');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111702','111702 - Aged Health Care');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111703','111703 - Care for Disabled');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111704','111704 - Community Child Health');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111705','111705 - Environmental and Occupational Health and Safety');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111706','111706 - Epidemiology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111707','111707 - Family Care');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111708','111708 - Health and Community Services');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111709','111709 - Health Care Administration');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111710','111710 - Health Counselling');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111711','111711 - Health Information Systems (incl. Surveillance)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111712','111712 - Health Promotion');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111713','111713 - Maori Health');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111714','111714 - Mental Health');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111715','111715 - Pacific Peoples Health');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111716','111716 - Preventive Medicine');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111717','111717 - Primary Health Care');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111718','111718 - Residential Client Care');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','111799','111799 - Public Health and Health Services not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','119999','119999 - Medical and Health Sciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1201','1201 - Architecture');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1202','1202 - Building');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1203','1203 - Design Practice and Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1204','1204 - Engineering Design');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1205','1205 - Urban and Regional Planning');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1299','1299 - Other Built Environment and Design');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120101','120101 - Architectural Design');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120102','120102 - Architectural Heritage and Conservation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120103','120103 - Architectural History and Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120104','120104 - Architectural Science and Technology (incl. Acoustics, Lighting, Structure and Ecologically Sustainable Design)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120105','120105 - Architecture Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120106','120106 - Interior Design');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120107','120107 - Landscape Architecture');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120199','120199 - Architecture not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120201','120201 - Building Construction Management and Project Planning');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120202','120202 - Building Science and Techniques');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120203','120203 - Quantity Surveying');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120299','120299 - Building not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120301','120301 - Design History and Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120302','120302 - Design Innovation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120303','120303 - Design Management and Studio and Professional Practice');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120304','120304 - Digital and Interaction Design');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120305','120305 - Industrial Design');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120306','120306 - Textile and Fashion Design');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120307','120307 - Visual Communication Design (incl. Graphic Design)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120399','120399 - Design Practice and Management not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120401','120401 - Engineering Design Empirical Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120402','120402 - Engineering Design Knowledge');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120403','120403 - Engineering Design Methods');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120404','120404 - Engineering Systems Design');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120405','120405 - Models of Engineering Design');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120499','120499 - Engineering Design not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120501','120501 - Community Planning');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120502','120502 - History and Theory of the Built Environment (excl. Architecture)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120503','120503 - Housing Markets, Development, Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120504','120504 - Land Use and Environmental Planning');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120505','120505 - Regional Analysis and Development');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120506','120506 - Transport Planning');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120507','120507 - Urban Analysis and Development');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120508','120508 - Urban Design');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','120599','120599 - Urban and Regional Planning not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','129999','129999 - Built Environment and Design not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1301','1301 - Education Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1302','1302 - Curriculum and Pedagogy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1303','1303 - Specialist Studies in Education');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1399','1399 - Other Education');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130101','130101 - Continuing and Community Education');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130102','130102 - Early Childhood Education (excl. Maori)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130103','130103 - Higher Education');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130104','130104 - Kura Kaupapa Maori (Maori Primary Education)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130105','130105 - Primary Education (excl. Maori)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130106','130106 - Secondary Education');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130107','130107 - Te Whariki (Maori Early Childhood Education)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130108','130108 - Technical, Further and Workplace Education');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130199','130199 - Education systems not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130201','130201 - Creative Arts, Media and Communication Curriculum and Pedagogy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130202','130202 - Curriculum and Pedagogy Theory and Development');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130203','130203 - Economics, Business and Management Curriculum and Pedagogy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130204','130204 - English and Literacy Curriculum and Pedagogy (excl. LOTE, ESL and TESOL)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130205','130205 - Humanities and Social Sciences Curriculum and Pedagogy (excl. Economics, Business and Management)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130206','130206 - Kohanga Reo (Maori Language Curriculum and Pedagogy)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130207','130207 - LOTE, ESL and TESOL Curriculum and Pedagogy (excl. Maori)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130208','130208 - Mathematics and Numeracy Curriculum and Pedagogy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130209','130209 - Medicine, Nursing and Health Curriculum and Pedagogy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130210','130210 - Physical Education and Development Curriculum and Pedagogy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130211','130211 - Religion Curriculum and Pedagogy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130212','130212 - Science, Technology and Engineering Curriculum and Pedagogy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130213','130213 - Vocational Education and Training Curriculum and Pedagogy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130299','130299 - Curriculum and Pedagogy not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130301','130301 - Aboriginal and Torres Strait Islander Education');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130302','130302 - Comparative and Cross-Cultural Education');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130303','130303 - Education Assessment and Evaluation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130304','130304 - Educational Administration, Management and Leadership');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130305','130305 - Educational Counselling');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130306','130306 - Educational Technology and Computing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130307','130307 - Ethnic Education (excl. Aboriginal and Torres Strait Islander, Maori and Pacific Peoples)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130308','130308 - Gender, Sexuality and Education');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130309','130309 - Learning Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130310','130310 - Maori Education (excl. Early Childhood and Primary Education)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130311','130311 - Pacific Peoples Education');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130312','130312 - Special Education and Disability');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130313','130313 - Teacher Education and Professional Development of Educators');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','130399','130399 - Specialist Studies in Education not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','139999','139999 - Education not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1401','1401 - Economic Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1402','1402 - Applied Economics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1403','1403 - Econometrics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1499','1499 - Other Economics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140101','140101 - History of Economic Thought');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140102','140102 - Macroeconomic Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140103','140103 - Mathematical Economics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140104','140104 - Microeconomic Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140199','140199 - Economic Theory not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140201','140201 - Agricultural Economics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140202','140202 - Economic Development and Growth');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140203','140203 - Economic History');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140204','140204 - Economics of Education');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140205','140205 - Environment and Resource Economics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140206','140206 - Experimental Economics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140207','140207 - Financial Economics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140208','140208 - Health Economics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140209','140209 - Industry Economics and Industrial Organisation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140210','140210 - International Economics and International Finance');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140211','140211 - Labour Economics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140212','140212 - Macroeconomics (incl. Monetary and Fiscal Theory)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140213','140213 - Public Economics- Public Choice');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140214','140214 - Public Economics- Publically Provided Goods');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140215','140215 - Public Economics- Taxation and Revenue');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140216','140216 - Tourism Economics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140217','140217 - Transport Economics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140218','140218 - Urban and Regional Economics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140219','140219 - Welfare Economics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140299','140299 - Applied Economics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140301','140301 - Cross-Sectional Analysis');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140302','140302 - Econometric and Statistical Methods');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140303','140303 - Economic Models and Forecasting');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140304','140304 - Panel Data Analysis');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140305','140305 - Time-Series Analysis');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','140399','140399 - Econometrics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','149901','149901 - Comparative Economic Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','149902','149902 - Ecological Economics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','149903','149903 - Heterodox Economics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','149999','149999 - Economics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1501','1501 - Accounting, Auditing and Accountability');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1502','1502 - Banking, Finance and Investment');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1503','1503 - Business and Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1504','1504 - Commercial Services');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1505','1505 - Marketing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1506','1506 - Tourism');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1507','1507 - Transportation and Freight Services');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1599','1599 - Other Commerce, Management, Tourism and Services');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150101','150101 - Accounting Theory and Standards');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150102','150102 - Auditing and Accountability');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150103','150103 - Financial Accounting');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150104','150104 - International Accounting');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150105','150105 - Management Accounting');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150106','150106 - Sustainability Accounting and Reporting');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150107','150107 - Taxation Accounting');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150199','150199 - Accounting, Auditing and Accountability not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150201','150201 - Finance');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150202','150202 - Financial Econometrics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150203','150203 - Financial Institutions (incl. Banking)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150204','150204 - Insurance Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150205','150205 - Investment and Risk Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150299','150299 - Banking, Finance and Investment not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150301','150301 - Business Information Management (incl. Records, Knowledge and Information Management, and Intelligence)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150302','150302 - Business Information Systems');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150303','150303 - Corporate Governance and Stakeholder Engagement');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150304','150304 - Entrepreneurship');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150305','150305 - Human Resources Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150306','150306 - Industrial Relations');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150307','150307 - Innovation and Technology Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150308','150308 - International Business');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150309','150309 - Logistics and Supply Chain Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150310','150310 - Organisation and Management Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150311','150311 - Organisational Behaviour');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150312','150312 - Organisational Planning and Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150313','150313 - Quality Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150314','150314 - Small Business Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150399','150399 - Business and Management not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150401','150401 - Food and Hospitality Services');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150402','150402 - Hospitality Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150403','150403 - Real Estate and Valuation Services');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150404','150404 - Sport and Leisure Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150499','150499 - Commercial Services not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150501','150501 - Consumer-Oriented Product or Service Development');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150502','150502 - Marketing Communications');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150503','150503 - Marketing Management (incl. Strategy and Customer Relations)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150504','150504 - Marketing Measurement');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150505','150505 - Marketing Research Methodology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150506','150506 - Marketing Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150507','150507 - Pricing (incl. Consumer Value Estimation)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150599','150599 - Marketing not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150601','150601 - Impacts of Tourism');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150602','150602 - Tourism Forecasting');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150603','150603 - Tourism Management');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150604','150604 - Tourism Marketing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150605','150605 - Tourism Resource Appraisal');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150606','150606 - Tourist Behaviour and Visitor Experience');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150699','150699 - Tourism not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150701','150701 - Air Transportation and Freight Services');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150702','150702 - Rail Transportation and Freight Services');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150703','150703 - Road Transportation and Freight Services');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','150799','150799 - Transportation and Freight Services not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','159999','159999 - Commerce, Management, Tourism and Services not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1601','1601 - Anthropology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1602','1602 - Criminology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1603','1603 - Demography');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1604','1604 - Human Geography');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1605','1605 - Policy and Administration');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1606','1606 - Political Science');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1607','1607 - Social Work');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1608','1608 - Sociology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1699','1699 - Other Studies in Human Society');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160101','160101 - Anthropology of Development');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160102','160102 - Biological (Physical) Anthropology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160103','160103 - Linguistic Anthropology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160104','160104 - Social and Cultural Anthropology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160199','160199 - Anthropology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160201','160201 - Causes and Prevention of Crime');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160202','160202 - Correctional Theory, Offender Treatment and Rehabilitation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160203','160203 - Courts and Sentencing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160204','160204 - Criminological Theories');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160205','160205 - Police Administration, Procedures and Practice');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160206','160206 - Private Policing and Security Services');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160299','160299 - Criminology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160301','160301 - Family and Household Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160302','160302 - Fertility');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160303','160303 - Migration');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160304','160304 - Mortality');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160305','160305 - Population Trends and Policies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160399','160399 - Demography not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160401','160401 - Economic Geography');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160402','160402 - Recreation, Leisure and Tourism Geography');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160403','160403 - Social and Cultural Geography');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160404','160404 - Urban and Regional Studies (excl. Planning)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160499','160499 - Human Geography not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160501','160501 - Aboriginal and Torres Strait Islander Policy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160502','160502 - Arts and Cultural Policy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160503','160503 - Communications and Media Policy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160504','160504 - Crime Policy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160505','160505 - Economic Development Policy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160506','160506 - Education Policy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160507','160507 - Environment Policy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160508','160508 - Health Policy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160509','160509 - Public Administration');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160510','160510 - Public Policy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160511','160511 - Research, Science and Technology Policy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160512','160512 - Social Policy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160513','160513 - Tourism Policy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160514','160514 - Urban Policy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160599','160599 - Policy and Administration not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160601','160601 - Australian Government and Politics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160602','160602 - Citizenship');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160603','160603 - Comparative Government and Politics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160604','160604 - Defence Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160605','160605 - Environmental Politics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160606','160606 - Government and Politics of Asia and the Pacific');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160607','160607 - International Relations');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160608','160608 - New Zealand Government and Politics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160609','160609 - Political Theory and Political Philosophy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160699','160699 - Political Science not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160701','160701 - Clinical Social Work Practice');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160702','160702 - Counselling, Welfare and Community Services');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160703','160703 - Social Program Evaluation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160799','160799 - Social Work not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160801','160801 - Applied Sociology, Program Evaluation and Social Impact Assessment');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160802','160802 - Environmental Sociology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160803','160803 - Race and Ethnic Relations');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160804','160804 - Rural Sociology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160805','160805 - Social Change');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160806','160806 - Social Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160807','160807 - Sociological Methodology and Research Methods');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160808','160808 - Sociology and Social Studies of Science and Technology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160809','160809 - Sociology of Education');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160810','160810 - Urban Sociology and Community Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','160899','160899 - Sociology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','169901','169901 - Gender Specific Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','169902','169902 - Studies of Aboriginal and Torres Strait Islander Society');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','169903','169903 - Studies of Asian Society');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','169904','169904 - Studies of Maori Society');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','169905','169905 - Studies of Pacific Peoples'' Societies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','169999','169999 - Studies in Human Society not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1701','1701 - Psychology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1702','1702 - Cognitive Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1799','1799 - Other Psychology and Cognitive Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170101','170101 - Biological Psychology (Neuropsychology, Psychopharmacology, Physiological Psychology)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170102','170102 - Developmental Psychology and Ageing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170103','170103 - Educational Psychology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170104','170104 - Forensic Psychology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170105','170105 - Gender Psychology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170106','170106 - Health, Clinical and Counselling Psychology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170107','170107 - Industrial and Organisational Psychology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170108','170108 - Kaupapa Maori Psychology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170109','170109 - Personality, Abilities and Assessment');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170110','170110 - Psychological Methodology, Design and Analysis');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170111','170111 - Psychology of Religion');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170112','170112 - Sensory Processes, Perception and Performance');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170113','170113 - Social and Community Psychology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170114','170114 - Sport and Exercise Psychology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170199','170199 - Psychology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170201','170201 - Computer Perception, Memory and Attention');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170202','170202 - Decision Making');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170203','170203 - Knowledge Representation and Machine Learning');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170204','170204 - Linguistic Processes (incl. Speech Production and Comprehension)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170205','170205 - Neurocognitive Patterns and Neural Networks');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','170299','170299 - Cognitive Sciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','179999','179999 - Psychology and Cognitive Sciences not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1801','1801 - Law');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1802','1802 - Maori Law');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1899','1899 - Other Law and Legal Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180101','180101 - Aboriginal and Torres Strait Islander Law');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180102','180102 - Access to Justice');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180103','180103 - Administrative Law');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180104','180104 - Civil Law and Procedure');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180105','180105 - Commercial and Contract Law');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180106','180106 - Comparative Law');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180107','180107 - Conflict of Laws (Private International Law)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180108','180108 - Constitutional Law');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180109','180109 - Corporations and Associations Law');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180110','180110 - Criminal Law and Procedure');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180111','180111 - Environmental and Natural Resources Law');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180112','180112 - Equity and Trusts Law');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180113','180113 - Family Law');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180114','180114 - Human Rights Law');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180115','180115 - Intellectual Property Law');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180116','180116 - International Law (excl. International Trade Law)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180117','180117 - International Trade Law');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180118','180118 - Labour Law');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180119','180119 - Law and Society');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180120','180120 - Legal Institutions (incl. Courts and Justice Systems)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180121','180121 - Legal Practice, Lawyering and the Legal Profession');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180122','180122 - Legal Theory, Jurisprudence and Legal Interpretation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180123','180123 - Litigation, Adjudication and Dispute Resolution');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180124','180124 - Property Law (excl. Intellectual Property Law)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180125','180125 - Taxation Law');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180126','180126 - Tort Law');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180199','180199 - Law not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180201','180201 - Nga Tikanga Maori (Maori Customary Law)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180202','180202 - Te Maori Whakahaere Rauemi (Maori Resource Law))');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180203','180203 - Te Tiriti o Waitangi (The Treaty of Waitangi)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180204','180204 - Te Ture Whenua (Maori Land Law)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','180299','180299 - Maori Law not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','189999','189999 - Law and Legal Studies not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1901','1901 - Art Theory and Criticism');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1902','1902 - Film, Television and Digital Media');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1903','1903 - Journalism and Professional Writing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1904','1904 - Performing Arts and Creative Writing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1905','1905 - Visual Arts and Crafts');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','1999','1999 - Other Studies in Creative Arts and Writing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190101','190101 - Art Criticism');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190102','190102 - Art History');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190103','190103 - Art Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190104','190104 - Visual Cultures');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190199','190199 - Art Theory and Criticism not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190201','190201 - Cinema Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190202','190202 - Computer Gaming and Animation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190203','190203 - Electronic Media Art');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190204','190204 - Film and Television');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190205','190205 - Interactive Media');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190299','190299 - Film, Television and Digital Media not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190301','190301 - Journalism Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190302','190302 - Professional Writing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190303','190303 - Technical Writing');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190399','190399 - Journalism and Professional Writing not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190401','190401 - Aboriginal and Torres Strait Islander Performing Arts');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190402','190402 - Creative Writing (incl. Playwriting)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190403','190403 - Dance');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190404','190404 - Drama, Theatre and Performance Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190405','190405 - Maori Performing Arts');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190406','190406 - Music Composition');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190407','190407 - Music Performance');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190408','190408 - Music Therapy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190409','190409 - Musicology and Ethnomusicology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190410','190410 - Pacific Peoples Performing Arts');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190499','190499 - Performing Arts and Creative Writing not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190501','190501 - Crafts');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190502','190502 - Fine Arts (incl. Sculpture and Painting)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190503','190503 - Lens-based Practice');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190504','190504 - Performance and Installation Art');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','190599','190599 - Visual Arts and Crafts not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','199999','199999 - Studies in Creative Arts and Writing not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','2001','2001 - Communication and Media Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','2002','2002 - Cultural Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','2003','2003 - Language Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','2004','2004 - Linguistics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','2005','2005 - Literary Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','2099','2099 - Other Language, Communication and Culture');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200101','200101 - Communication Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200102','200102 - Communication Technology and Digital Media Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200103','200103 - International and Development Communication');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200104','200104 - Media Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200105','200105 - Organisational, Interpersonal and Intercultural Communication');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200199','200199 - Communication and Media Studies not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200201','200201 - Aboriginal and Torres Strait Islander Cultural Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200202','200202 - Asian Cultural Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200203','200203 - Consumption and Everyday Life');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200204','200204 - Cultural Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200205','200205 - Culture, Gender, Sexuality');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200206','200206 - Globalisation and Culture');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200207','200207 - Maori Cultural Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200208','200208 - Migrant Cultural Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200209','200209 - Multicultural, Intercultural and Cross-cultural Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200210','200210 - Pacific Cultural Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200211','200211 - Postcolonial Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200212','200212 - Screen and Media Culture');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200299','200299 - Cultural Studies not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200301','200301 - Early English Languages');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200302','200302 - English Language');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200303','200303 - English as a Second Language');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200304','200304 - Central and Eastern European Languages (incl. Russian)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200305','200305 - Latin and Classical Greek Languages');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200306','200306 - French Language');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200307','200307 - German Language');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200308','200308 - Iberian Languages');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200309','200309 - Italian Language');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200310','200310 - Other European Languages');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200311','200311 - Chinese Languages');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200312','200312 - Japanese Language');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200313','200313 - Indonesian Languages');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200314','200314 - South-East Asian Languages (excl. Indonesian)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200315','200315 - Indian Languages');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200316','200316 - Korean Language');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200317','200317 - Other Asian Languages (excl. South-East Asian)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200318','200318 - Middle Eastern Languages');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200319','200319 - Aboriginal and Torres Strait Islander Languages');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200320','200320 - Pacific Languages');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200321','200321 - Te Reo Maori (Maori Language)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200322','200322 - Comparative Language Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200323','200323 - Translation and Interpretation Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200399','200399 - Language Studies not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200401','200401 - Applied Linguistics and Educational Linguistics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200402','200402 - Computational Linguistics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200403','200403 - Discourse and Pragmatics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200404','200404 - Laboratory Phonetics and Speech Science');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200405','200405 - Language in Culture and Society (Sociolinguistics)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200406','200406 - Language in Time and Space (incl. Historical Linguistics, Dialectology)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200407','200407 - Lexicography');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200408','200408 - Linguistic Structures (incl. Grammar, Phonology, Lexicon, Semantics)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200499','200499 - Linguistics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200501','200501 - Aboriginal and Torres Strait Islander Literature');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200502','200502 - Australian Literature (excl. Aboriginal and Torres Strait Islander Literature)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200503','200503 - British and Irish Literature');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200504','200504 - Maori Literature');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200505','200505 - New Zealand Literature (excl. Maori Literature)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200506','200506 - North American Literature');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200507','200507 - Pacific Literature');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200508','200508 - Other Literatures in English');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200509','200509 - Central and Eastern European Literature (incl. Russian)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200510','200510 - Latin and Classical Greek Literature');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200511','200511 - Literature in French');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200512','200512 - Literature in German');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200513','200513 - Literature in Italian');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200514','200514 - Literature in Spanish and Portuguese');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200515','200515 - Other European Literature');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200516','200516 - Indonesian Literature');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200517','200517 - Literature in Chinese');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200518','200518 - Literature in Japanese');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200519','200519 - South-East Asian Literature (excl. Indonesian)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200520','200520 - Indian Literature');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200521','200521 - Korean Literature');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200522','200522 - Other Asian Literature (excl. South-East Asian)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200523','200523 - Middle Eastern Literature');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200524','200524 - Comparative Literature Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200525','200525 - Literary Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200526','200526 - Stylistics and Textual Analysis');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','200599','200599 - Literary Studies not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','209999','209999 - Language, Communication and Culture not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','2101','2101 - Archaeology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','2102','2102 - Curatorial and Related Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','2103','2103 - Historical Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','2199','2199 - Other History and Archaeology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210101','210101 - Aboriginal and Torres Strait Islander Archaeology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210102','210102 - Archaeological Science');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210103','210103 - Archaeology of Asia, Africa and the Americas');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210104','210104 - Archaeology of Australia (excl. Aboriginal and Torres Strait Islander)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210105','210105 - Archaeology of Europe, the Mediterranean and the Levant');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210106','210106 - Archaeology of New Guinea and Pacific Islands (excl. New Zealand)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210107','210107 - Archaeology of New Zealand (excl. Maori)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210108','210108 - Historical Archaeology (incl. Industrial Archaeology)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210109','210109 - Maori Archaeology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210110','210110 - Maritime Archaeology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210199','210199 - Archaeology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210201','210201 - Archival, Repository and Related Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210202','210202 - Heritage and Cultural Conservation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210203','210203 - Materials Conservation');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210204','210204 - Museum Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210299','210299 - Curatorial and Related Studies not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210301','210301 - Aboriginal and Torres Strait Islander History');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210302','210302 - Asian History');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210303','210303 - Australian History (excl. Aboriginal and Torres Strait Islander History)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210304','210304 - Biography');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210305','210305 - British History');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210306','210306 - Classical Greek and Roman History');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210307','210307 - European History (excl. British, Classical Greek and Roman)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210308','210308 - Latin American History');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210309','210309 - Maori History');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210310','210310 - Middle Eastern and African History');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210311','210311 - New Zealand History');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210312','210312 - North American History');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210313','210313 - Pacific History (excl. New Zealand and Maori)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','210399','210399 - Historical Studies not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','219999','219999 - History and Archaeology not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','2201','2201 - Applied Ethics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','2202','2202 - History and Philosophy of Specific Fields');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','2203','2203 - Philosophy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','2204','2204 - Religion and Religious Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','2299','2299 - Other Philosophy and Religious Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220101','220101 - Bioethics (human and animal)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220102','220102 - Business Ethics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220103','220103 - Ethical Use of New Technology (e.g. Nanotechnology, Biotechnology)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220104','220104 - Human Rights and Justice Issues');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220105','220105 - Legal Ethics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220106','220106 - Medical Ethics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220107','220107 - Professional Ethics (incl. police and research ethics)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220199','220199 - Applied Ethics not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220201','220201 - Business and Labour History');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220202','220202 - History and Philosophy of Education');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220203','220203 - History and Philosophy of Engineering and Technology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220204','220204 - History and Philosophy of Law and Justice');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220205','220205 - History and Philosophy of Medicine');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220206','220206 - History and Philosophy of Science (incl. Non-historical Philosophy of Science)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220207','220207 - History and Philosophy of the Humanities');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220208','220208 - History and Philosophy of the Social Sciences');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220209','220209 - History of Ideas');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220210','220210 - History of Philosophy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220299','220299 - History and Philosophy of Specific Fields not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220301','220301 - Aesthetics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220302','220302 - Decision Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220303','220303 - Environmental Philosophy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220304','220304 - Epistemology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220305','220305 - Ethical Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220306','220306 - Feminist Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220307','220307 - Hermeneutic and Critical Theory');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220308','220308 - Logic');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220309','220309 - Metaphysics');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220310','220310 - Phenomenology');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220311','220311 - Philosophical Psychology (incl. Moral Psychology and Philosophy of Action)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220312','220312 - Philosophy of Cognition');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220313','220313 - Philosophy of Language');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220314','220314 - Philosophy of Mind (excl. Cognition)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220315','220315 - Philosophy of Religion');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220316','220316 - Philosophy of Specific Cultures (incl. Comparative Philosophy)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220317','220317 - Poststructuralism');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220318','220318 - Psychoanalytic Philosophy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220319','220319 - Social Philosophy');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220399','220399 - Philosophy not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220401','220401 - Christian Studies (incl. Biblical Studies and Church History)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220402','220402 - Comparative Religious Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220403','220403 - Islamic Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220404','220404 - Jewish Studies');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220405','220405 - Religion and Society');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220406','220406 - Studies in Eastern Religious Traditions');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220407','220407 - Studies in Religious Traditions (excl. Eastern, Jewish, Christian and Islamic Traditions)');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','220499','220499 - Religion and Religious Studies not elsewhere classified');
INSERT INTO select_code (select_name, code, description)
VALUES ('anzforSubject','229999','229999 - Philosophy and Religious Studies not elsewhere classified');
