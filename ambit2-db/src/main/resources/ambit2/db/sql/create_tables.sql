SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';


-- -----------------------------------------------------
-- Table `roles`  User roles
-- -----------------------------------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE  `roles` (
  `role_name` varchar(16) character set utf8 collate utf8_bin NOT NULL,
  PRIMARY KEY  (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `users` Users
-- -----------------------------------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE  `users` (
  `user_name` varchar(16) character set utf8 collate utf8_bin NOT NULL,
  `password` varchar(45) character set utf8 collate utf8_bin NOT NULL,
  `email` varchar(45) NOT NULL,
  `registration_status` enum('commenced','confirmed','deleted') NOT NULL default 'commenced',
  `registration_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `registration_id` blob,
  `title` varchar(6) character set utf8 collate utf8_bin NOT NULL default '""',
  `firstname` varchar(128) character set utf8 collate utf8_bin NOT NULL default '""',
  `lastname` varchar(128) character set utf8 collate utf8_bin NOT NULL default '""',
  `address` varchar(128) character set utf8 collate utf8_bin NOT NULL default '""',
  `country` varchar(128) character set utf8 collate utf8_bin NOT NULL default '""',
  `webpage` varchar(255) character set utf8 collate utf8_bin NOT NULL default '""',
  `affiliation` varchar(128) character set utf8 collate utf8_bin NOT NULL default '""',
  `keywords` varchar(128) default '""',
  `reviewer` tinyint(1) NOT NULL default '0' COMMENT 'true if wants to become a reviewer',
  PRIMARY KEY  (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `roles` Roles assigned to users
-- -----------------------------------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE  `user_roles` (
  `user_name` varchar(16) character set utf8 collate utf8_bin NOT NULL,
  `role_name` varchar(16) character set utf8 collate utf8_bin NOT NULL,
  PRIMARY KEY  (`user_name`,`role_name`),
  KEY `FK_user_roles_2` (`role_name`),
  CONSTRAINT `FK_user_roles_2` FOREIGN KEY (`role_name`) REFERENCES `roles` (`role_name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_user_roles_1` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -----------------------------------------------------
-- Table `references`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `catalog_references`;
CREATE TABLE  `catalog_references` (
  `idreference` int(11) unsigned NOT NULL auto_increment,
  `title` varchar(255) character set utf8 collate utf8_bin NOT NULL,
  `url` varchar(255) character set utf8 collate utf8_bin NOT NULL,
  `type` enum('Unknown','Dataset','Algorithm','Model','BibtexEntry','BibtexArticle','BibtexBook') NOT NULL default 'Dataset',
  PRIMARY KEY  (`idreference`),
  UNIQUE KEY `Index_2` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- -----------------------------------------------------
-- Table `chemicals`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `chemicals`;
CREATE TABLE  `chemicals` (
  `idchemical` int(11) unsigned NOT NULL auto_increment,
  `inchi` text character set latin1 collate latin1_bin,
  `smiles` text character set latin1 collate latin1_bin,
  `formula` varchar(64) default NULL,
  `hashcode` bigint(20) NOT NULL default '0',
  `label` enum('OK','UNKNOWN','ERROR') NOT NULL default 'UNKNOWN',
  PRIMARY KEY  (`idchemical`),
  KEY `sinchi` (`inchi`(760)),
  KEY `ssmiles` (`smiles`(760)),
  KEY `idchemical` (`idchemical`),
  KEY `inchi` (`inchi`(767)),
  KEY `formula` (`formula`),
  KEY `hashcode` USING BTREE (`hashcode`),
  KEY `Index_8` (`label`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------
-- Table `structure`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `structure` ;
CREATE TABLE  `structure` (
  `idstructure` int(11) unsigned NOT NULL auto_increment,
  `idchemical` int(11) unsigned NOT NULL,
  `structure` blob NOT NULL,
  `format` enum('SDF','CML','MOL') collate utf8_bin NOT NULL default 'CML',
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `user_name` varchar(16) collate utf8_bin default NULL,
  `type_structure` enum('NA','MARKUSH','SMILES','2D no H','2D with H','3D no H','3D with H','optimized','experimental') collate utf8_bin NOT NULL default 'NA',
  `label` enum('OK','UNKNOWN','ERROR') collate utf8_bin NOT NULL default 'UNKNOWN' COMMENT 'quality label',
  `atomproperties` blob,
  `preference` int(10) unsigned NOT NULL default '9999',
  PRIMARY KEY  (`idstructure`),
  KEY `FK_structure_2` (`user_name`),
  KEY `idchemical` USING BTREE (`idchemical`),
  KEY `Index_4` (`label`),
  KEY `Index_5` (`idstructure`,`user_name`),
  KEY `Index_6` USING BTREE (`idchemical`,`preference`,`idstructure`),
  KEY `Index_pref` USING BTREE (`preference`,`idchemical`),
  CONSTRAINT `fk_idchemical` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_structure_2` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


DELIMITER $
CREATE TRIGGER copy_history BEFORE UPDATE ON structure
  FOR EACH ROW BEGIN
   INSERT INTO history (idstructure,structure,format,updated,user_name,type_structure,label)
        SELECT idstructure,structure,format,updated,user_name,type_structure,label FROM structure
        WHERE structure.idstructure = OLD.idstructure;
  END $
DELIMITER ;

-- -----------------------------------------------------
-- Table `properties`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `properties`;
CREATE TABLE  `properties` (
  `idproperty` int(10) unsigned NOT NULL auto_increment,
  `idreference` int(11) unsigned NOT NULL default '0',
  `name` varchar(128) collate utf8_bin NOT NULL default '',
  `units` varchar(16) collate utf8_bin NOT NULL default '',
  `comments` varchar(128) collate utf8_bin NOT NULL default '',
  `islocal` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  USING BTREE (`idproperty`),
  UNIQUE KEY `ddictionary_name` USING BTREE (`name`,`idreference`),
  KEY `ddictionary_idref` (`idreference`),
  CONSTRAINT `FK_properties_1` FOREIGN KEY (`idreference`) REFERENCES `catalog_references` (`idreference`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `property_string` string values
-- -----------------------------------------------------
DROP TABLE IF EXISTS `property_string`;
CREATE TABLE  `property_string` (
  `idvalue_string` int(10) unsigned NOT NULL auto_increment,
  `value` varchar(255) collate utf8_bin NOT NULL,
  PRIMARY KEY  (`idvalue_string`),
  UNIQUE KEY `Index_3` (`value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- -----------------------------------------------------
-- Table `template` defines templates
-- -----------------------------------------------------
DROP TABLE IF EXISTS `template`;
CREATE TABLE  `template` (
  `idtemplate` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(128) collate utf8_bin default NULL,
  PRIMARY KEY  (`idtemplate`),
  UNIQUE KEY `template_list_index4157` USING BTREE (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


--
-- Dumping data for table `template`
--

LOCK TABLES `template` WRITE;
/*!40000 ALTER TABLE `template` DISABLE KEYS */;
INSERT INTO `template` VALUES (84,NULL),(7,'Acute dermal toxicity'),(8,'Acute inhalation toicity'),(9,'Acute oral toxicity'),(10,'Acute photoirritation'),(11,'Acute toxicity to fish (lethality)'),(12,'Adsorption/Desorption in sediment'),(13,'Adsorption/Desorption in soil '),(14,'Air- water partition coefficient (Henry`s law constant, H)'),(15,'BAF fish'),(16,'BAF other organisms '),(17,'BCF fish'),(18,'BCF other organisms '),(78,'Bioaccumulation'),(79,'Bioconcentration '),(19,'Biodegradation time frame (primary, ultimate degradation)'),(20,'Blood-brain barrier penetration'),(21,'Blood-lung barrier penetration'),(22,'Blood-testis barrier penetration'),(23,'Boiling point'),(89,'CasRN'),(24,'Carcinogenicity'),(91,'Names'),(27,'DNA-binding'),(86,'Descriptors'),(25,'Direct photolysis'),(26,'Dissociation constant (pKa)'),(1,'Ecotoxic effects'),(83,'Endocrine Activity'),(85,'Endpoints'),(2,'Environmental fate parameters '),(28,'Eye irritation/corrosion'),(29,'Gastrointestinal absorption'),(3,'Human health effects'),(30,'Hydrolysis '),(90,'IUPAC name'),(88,'Identifiers'),(31,'In vitro reproductive toxicity (e.g. embryotoxic effects in cell culture such as embryo stem cells)  '),(32,'In vivo pre-, peri-, post natal development and / or fertility (1 or 2 gen. Study or enhanced 1 gen study) '),(33,'In vivo pre-natal-developmental toxicity'),(34,'Indirect photolysis (OH-radical reaction, ozone-radical reaction, other)'),(35,'Long-term toxicity (survival, growth, reproduction)'),(36,'Long-term toxicity to Daphnia (lethality, inhibition of reproduction)'),(37,'Long-term toxicity to fish (egg/sac fry, growth inhibition of juvenile fish, early life stage, full life cycle)'),(38,'Melting point'),(39,'Metabolism (including metabolic clearance)'),(40,'Microbial inhibition (activated sludge respiration inhibition, inhibition of nitrification, other)'),(41,'Mutagenicity '),(42,'Octanol-air partition coefficient (Koa)'),(43,'Octanol-water distribution coefficient (D)'),(44,'Octanol-water partition coefficient (Kow)'),(45,'Ocular membrane penetration'),(46,'Organic carbon-sorption partition coefficient (organic carbon; Koc)'),(4,'Other'),(47,'Other (e.g. inhibition of specific enzymes involved in hormone synthesis or regulation, specify enzyme(s) and hormone)'),(48,'Oxidation '),(80,'Persistence: Abiotic degradation in air (Phototransformation)'),(81,'Persistence: Abiotic degradation in water'),(82,'Persistence: Biodegradation'),(49,'Photocarcinogenicity'),(50,'Photomutagenicity'),(51,'Photosensitisation'),(5,'Physicochemical effects '),(52,'Placental barrier penetration'),(53,'Protein-binding'),(54,'Ready/not ready biodegradability'),(55,'Receptor binding and gene expression (specify receptor)'),(56,'Receptor-binding (specify receptor)'),(57,'Repeated dose toxicity '),(58,'Respiratory sensitisation'),(59,'Short term toxicity (feeding, gavage, other)'),(61,'Short-term toxicity to Daphnia (immobilisation)'),(60,'Short-term toxicity to algae (inhibition of the exponential growth rate)'),(62,'Skin irritation /corrosion'),(63,'Skin penetration'),(64,'Skin sensitisation'),(65,'Surface tension'),(77,'Toxicity to birds'),(66,'Toxicity to earthworms (survival, growth, reproduction)'),(67,'Toxicity to plants (leaves, seed germination, root elongation)'),(68,'Toxicity to sediment organisms (survival, growth, reproduction)'),(69,'Toxicity to soil invertebrates (survival, growth, reproduction)'),(70,'Toxicity to soil microorganisms (inhibition of C-mineralisation, inhibition of N-mineralisation, other)'),(6,'Toxicokinetics '),(71,'Vapour pressure'),(72,'Vegetation-air partition coefficient'),(73,'Vegetation-soil partition coefficient'),(74,'Vegetation-water partition coefficient'),(75,'Water solubility'),(92,'Dataset');
/*!40000 ALTER TABLE `template` ENABLE KEYS */;
UNLOCK TABLES;

-- -----------------------------------------------------
-- Table `template_def` template definitions
-- -----------------------------------------------------
DROP TABLE IF EXISTS `template_def`;
CREATE TABLE  `template_def` (
  `idtemplate` int(10) unsigned NOT NULL,
  `idproperty` int(10) unsigned NOT NULL,
  `order` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  USING BTREE (`idtemplate`,`idproperty`),
  KEY `FK_template_def_2` (`idproperty`),
  CONSTRAINT `FK_template_def_2` FOREIGN KEY (`idproperty`) REFERENCES `properties` (`idproperty`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_template_def_1` FOREIGN KEY (`idtemplate`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- -----------------------------------------------------
-- Table `dictionary`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dictionary`;
CREATE TABLE  `dictionary` (
  `idsubject` int(10) unsigned NOT NULL,
  `relationship` enum('is_a','is_part_of') collate utf8_bin NOT NULL default 'is_a',
  `idobject` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`idsubject`,`relationship`,`idobject`),
  KEY `FK_dictionary_2` (`idobject`),
  CONSTRAINT `FK_dictionary_2` FOREIGN KEY (`idobject`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_dictionary_1` FOREIGN KEY (`idsubject`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `dictionary`
--

LOCK TABLES `dictionary` WRITE;
/*!40000 ALTER TABLE `dictionary` DISABLE KEYS */;
INSERT INTO `dictionary` VALUES (11,'is_a',1),(35,'is_a',1),(36,'is_a',1),(37,'is_a',1),(40,'is_a',1),(59,'is_a',1),(60,'is_a',1),(61,'is_a',1),(66,'is_a',1),(67,'is_a',1),(68,'is_a',1),(69,'is_a',1),(70,'is_a',1),(77,'is_a',1),(4,'is_a',2),(12,'is_a',2),(13,'is_a',2),(19,'is_a',2),(25,'is_a',2),(30,'is_a',2),(34,'is_a',2),(46,'is_a',2),(48,'is_a',2),(54,'is_a',2),(72,'is_a',2),(73,'is_a',2),(74,'is_a',2),(78,'is_a',2),(79,'is_a',2),(80,'is_a',2),(81,'is_a',2),(82,'is_a',2),(7,'is_a',3),(8,'is_a',3),(9,'is_a',3),(10,'is_a',3),(24,'is_a',3),(28,'is_a',3),(31,'is_a',3),(32,'is_a',3),(33,'is_a',3),(41,'is_a',3),(47,'is_a',3),(49,'is_a',3),(50,'is_a',3),(51,'is_a',3),(55,'is_a',3),(56,'is_a',3),(57,'is_a',3),(58,'is_a',3),(62,'is_a',3),(64,'is_a',3),(4,'is_a',4),(14,'is_a',5),(23,'is_a',5),(26,'is_a',5),(38,'is_a',5),(42,'is_a',5),(43,'is_a',5),(44,'is_a',5),(65,'is_a',5),(71,'is_a',5),(75,'is_a',5),(20,'is_a',6),(21,'is_a',6),(22,'is_a',6),(27,'is_a',6),(29,'is_a',6),(39,'is_a',6),(45,'is_a',6),(52,'is_a',6),(53,'is_a',6),(63,'is_a',6),(15,'is_a',78),(16,'is_a',78),(17,'is_a',79),(18,'is_a',79),(85,'is_part_of',84),(86,'is_part_of',84),(88,'is_part_of',84),(1,'is_a',85),(2,'is_a',85),(3,'is_a',85),(4,'is_a',85),(5,'is_a',85),(6,'is_a',85),(89,'is_a',88),(90,'is_a',88),(91,'is_a',88),(92,'is_part_of',84);
/*!40000 ALTER TABLE `dictionary` ENABLE KEYS */;
UNLOCK TABLES;

insert into template values (null,"Models");
insert into dictionary (idsubject,relationship,idobject)
SELECT t1.idtemplate,"is_a",t2.idtemplate FROM template t1
join template t2
where t1.name = "Models" and t2.name is null;

-- -----------------------------------------------------
DROP TABLE IF EXISTS `models`;
CREATE TABLE  `models` (
  `idmodel` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(255) collate utf8_bin NOT NULL,
  `idquery` int(10) unsigned default NULL COMMENT 'dataset',
  `predictors` int(10) unsigned NOT NULL COMMENT 'template for predictors',
  `dependent` int(10) unsigned NOT NULL COMMENT 'template for dependent variables',
  `content` longblob NOT NULL,
  `algorithm` varchar(255) collate utf8_bin NOT NULL default 'N/A' COMMENT 'URI of the algorithm',
  `mediatype` varchar(48) collate utf8_bin NOT NULL default 'application/java' COMMENT 'Content formats: JAVA_CLASS, WEKA_BASE64, PMML',
  `parameters` text collate utf8_bin COMMENT 'Model parameters',
  `predicted` int(10) unsigned NOT NULL COMMENT 'template for predicted variables',
  `hidden` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`idmodel`),
  UNIQUE KEY `Index_5` USING BTREE (`name`),
  KEY `FK_models_predictors` (`predictors`),
  KEY `FK_models_dataset` (`idquery`),
  KEY `FK_models_dependent` (`dependent`),
  KEY `Index_6` (`algorithm`),
  KEY `Index_7` (`parameters`(255)),
  KEY `FK_models_predicted` (`predicted`),
  CONSTRAINT `FK_models_dataset` FOREIGN KEY (`idquery`) REFERENCES `query` (`idquery`) ON UPDATE CASCADE,
  CONSTRAINT `FK_models_dependent` FOREIGN KEY (`dependent`) REFERENCES `template` (`idtemplate`) ON UPDATE CASCADE,
  CONSTRAINT `FK_models_predicted` FOREIGN KEY (`predicted`) REFERENCES `template` (`idtemplate`) ON UPDATE CASCADE,
  CONSTRAINT `FK_models_predictors` FOREIGN KEY (`predictors`) REFERENCES `template` (`idtemplate`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `tuples` for non-scalar values
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tuples`;
CREATE TABLE  `tuples` (
  `idtuple` int(10) unsigned NOT NULL auto_increment,
  `id_srcdataset` int(11) unsigned NOT NULL,
  PRIMARY KEY  (`idtuple`),
  KEY `FK_tuples_1` (`id_srcdataset`),
  CONSTRAINT `FK_tuples_1` FOREIGN KEY (`id_srcdataset`) REFERENCES `src_dataset` (`id_srcdataset`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `property_values` all values
-- v3.2 value_num changed to double instead of double(14,4)
-- -----------------------------------------------------
DROP TABLE IF EXISTS `property_values`;
CREATE TABLE  `property_values` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `idproperty` int(10) unsigned NOT NULL,
  `idstructure` int(10) unsigned NOT NULL,
  `user_name` varchar(16) collate utf8_bin NOT NULL,
  `status` enum('OK','UNKNOWN','ERROR','TRUNCATED') collate utf8_bin NOT NULL default 'UNKNOWN',
  `text` text collate utf8_bin,
  `idvalue_string` int(10) unsigned default NULL,
  `value_num` double default NULL,
  `idtype` enum('STRING','NUMERIC') collate utf8_bin NOT NULL default 'STRING',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `Index_1` USING BTREE (`idproperty`,`idstructure`),
  KEY `FK_property_values_1` (`user_name`),
  KEY `FK_property_values_2` (`idstructure`),
  KEY `Index_2` (`value_num`),
  KEY `FK_property_values_5` (`idvalue_string`),
  KEY `Index_3` USING BTREE (`idproperty`,`idtype`),
  KEY `Index_8` (`idproperty`,`idvalue_string`),
  CONSTRAINT `FK_property_values_1` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_property_values_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_property_values_3` FOREIGN KEY (`idproperty`) REFERENCES `properties` (`idproperty`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_property_values_5` FOREIGN KEY (`idvalue_string`) REFERENCES `property_string` (`idvalue_string`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- -----------------------------------------------------
-- Table `property_pairstruc` values, assigned for pair of structures
-- -----------------------------------------------------
DROP TABLE IF EXISTS `property_pairstruc`;
CREATE TABLE  `property_pairstruc` (
  `idstructure1` int(10) unsigned NOT NULL auto_increment COMMENT 'First structure id',
  `idstructure2` int(10) unsigned NOT NULL COMMENT 'Second structure id',
  `idproperty` int(10) unsigned NOT NULL COMMENT 'Property id',
  `user_name` varchar(16) collate utf8_bin NOT NULL COMMENT 'User',
  `status` enum('OK','UNKNOWN','ERROR','TRUNCATED') collate utf8_bin NOT NULL,
  `text` text collate utf8_bin COMMENT 'Text value, if longer than allowed by property_string',
  `idvalue_string` int(10) unsigned NOT NULL COMMENT 'link to property_string',
  `value_num` double NOT NULL COMMENT 'numeric value',
  `idtype` enum('STRING','NUMERIC') collate utf8_bin NOT NULL,
  PRIMARY KEY  USING BTREE (`idstructure1`,`idstructure2`,`idproperty`),
  KEY `FK_relationship_struc_2` (`idstructure2`),
  KEY `FK_relationship_struc_3` (`idproperty`),
  KEY `FK_relationship_struc_4` (`user_name`),
  KEY `FK_relationship_struc_5` (`idvalue_string`),
  CONSTRAINT `FK_relationship_struc_1` FOREIGN KEY (`idstructure1`) REFERENCES `structure` (`idstructure`),
  CONSTRAINT `FK_relationship_struc_2` FOREIGN KEY (`idstructure2`) REFERENCES `structure` (`idstructure`),
  CONSTRAINT `FK_relationship_struc_3` FOREIGN KEY (`idproperty`) REFERENCES `properties` (`idproperty`),
  CONSTRAINT `FK_relationship_struc_4` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`),
  CONSTRAINT `FK_relationship_struc_5` FOREIGN KEY (`idvalue_string`) REFERENCES `property_string` (`idvalue_string`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `quality_labels` 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `quality_labels`;
CREATE TABLE  `quality_labels` (
  `id` int(10) unsigned NOT NULL,
  `user_name` varchar(16) collate utf8_bin NOT NULL,
  `label` enum('OK','ProbablyOK','Unknown','ProbablyERROR','ERROR') collate utf8_bin NOT NULL default 'Unknown',
  `text` text collate utf8_bin,
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,  
  PRIMARY KEY  USING BTREE (`id`,`user_name`),
  KEY `FK_quality_labels_2` (`user_name`),
  KEY `FK_quality_labels_3` (`label`),
  CONSTRAINT `FK_quality_labels_1` FOREIGN KEY (`id`) REFERENCES `property_values` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_quality_labels_2` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `quality_pair` 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `quality_pair`;
CREATE TABLE  `quality_pair` (
  `idchemical` int(10) unsigned NOT NULL auto_increment,
  `idstructure` int(10) unsigned NOT NULL,
  `rel` int(10) unsigned NOT NULL default '0' COMMENT 'number of same structures',
  `user_name` varchar(16) collate utf8_bin NOT NULL,
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `TEXT` text collate utf8_bin,
  PRIMARY KEY  (`idchemical`,`idstructure`),
  KEY `FK_qpair_1` (`user_name`),
  KEY `FK_qpair_3` (`idstructure`),
  KEY `Index_4` (`TEXT`(255)),
  KEY `Index_5` USING BTREE (`idchemical`,`rel`),
  CONSTRAINT `FK_qpair_1` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_qpair_2` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_qpair_3` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `quality_structure` 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `quality_chemicals`;
CREATE TABLE  `quality_chemicals` (
  `idchemical` int(10) unsigned NOT NULL auto_increment,
  `num_sources` int(10) unsigned NOT NULL,
  `label` enum('Consensus','Majority','Unconfirmed','Ambiguous','Unknown') collate utf8_bin NOT NULL default 'Unknown',
  `num_structures` varchar(45) collate utf8_bin NOT NULL default '0',
  `text` varchar(255) collate utf8_bin NOT NULL,
  PRIMARY KEY  (`idchemical`),
  KEY `Index_4` (`num_structures`),
  KEY `Index_2` USING BTREE (`label`,`text`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
-- -----------------------------------------------------
-- Table `quality_structure` 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `quality_structure`;
CREATE TABLE  `quality_structure` (
  `idstructure` int(10) unsigned NOT NULL,
  `user_name` varchar(16) collate utf8_bin NOT NULL,
  `label` enum('OK','ProbablyOK','Unknown','ProbablyERROR','ERROR') collate utf8_bin NOT NULL default 'Unknown',
  `text` text collate utf8_bin,
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  USING BTREE (`idstructure`,`user_name`),
  KEY `FK_quality_struc_2` (`user_name`),
  KEY `FK_quality_struc_3` (`label`),
  CONSTRAINT `FK_quality_struc_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_quality_struc_2` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `property_tuples` tuples of properties
-- -----------------------------------------------------
DROP TABLE IF EXISTS `property_tuples`;
CREATE TABLE  `property_tuples` (
  `idtuple` int(10) unsigned NOT NULL,
  `id` int(10) unsigned NOT NULL,
  PRIMARY KEY  USING BTREE (`idtuple`,`id`),
  KEY `FK_property_tuples_2` (`id`),
  CONSTRAINT `FK_property_tuples_2` FOREIGN KEY (`id`) REFERENCES `property_values` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_property_tuple_1` FOREIGN KEY (`idtuple`) REFERENCES `tuples` (`idtuple`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `history`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `history`;

CREATE TABLE  `history` (
  `version` int(11) NOT NULL auto_increment,
  `idstructure` int(11) unsigned NOT NULL,
  `structure` blob NOT NULL,
  `format` enum('SDF','CML','MOL') collate utf8_bin NOT NULL default 'CML',
  `label` enum('OK','UNKNOWN','ERROR') collate utf8_bin NOT NULL default 'UNKNOWN' COMMENT 'quality label',
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `user_name` varchar(16) collate utf8_bin default NULL,
  `type_structure` enum('NA','MARKUSH','SMILES','2D no H','2D with H','3D no H','3D with H','optimized','experimental') collate utf8_bin NOT NULL default 'NA',
  PRIMARY KEY  (`version`),
  KEY `idstructure` (`idstructure`),
  KEY `f_idstructure` (`idstructure`),
  KEY `FK_history_1` (`user_name`),
  CONSTRAINT `FK_history_1` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `src_dataset` datasets
-- -----------------------------------------------------
DROP TABLE IF EXISTS `src_dataset`;
CREATE TABLE  `src_dataset` (
  `id_srcdataset` int(11) unsigned NOT NULL auto_increment,
  `name` varchar(255) collate utf8_bin NOT NULL default 'default',
  `user_name` varchar(16) collate utf8_bin default NULL,
  `idreference` int(11) unsigned NOT NULL,
  `created` timestamp NOT NULL default CURRENT_TIMESTAMP,  
  PRIMARY KEY  (`id_srcdataset`),
  UNIQUE KEY `src_dataset_name` (`name`),
  KEY `FK_src_dataset_1` (`user_name`),
  KEY `FK_src_dataset_2` (`idreference`),
  CONSTRAINT `FK_src_dataset_2` FOREIGN KEY (`idreference`) REFERENCES `catalog_references` (`idreference`) ON UPDATE CASCADE,
  CONSTRAINT `FK_src_dataset_1` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `struc_dataset` structures per dataset
-- -----------------------------------------------------
DROP TABLE IF EXISTS `struc_dataset`;
CREATE TABLE IF NOT EXISTS  `struc_dataset` (
  `idstructure` int unsigned  NOT NULL ,
  `id_srcdataset` int unsigned NOT NULL ,
  `created` timestamp NOT NULL default CURRENT_TIMESTAMP,  
  PRIMARY KEY  (`idstructure`,`id_srcdataset`),
  KEY `struc_dataset` (`id_srcdataset`),
  CONSTRAINT `struc_dataset_ibfk_1`
    FOREIGN KEY (`idstructure`)
    REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `struc_dataset_ibfk_2`
    FOREIGN KEY (`id_srcdataset`)
    REFERENCES `src_dataset` (`id_srcdataset`) ON DELETE CASCADE ON UPDATE CASCADE
);

-- -----------------------------------------------------
-- Table `sessions` User sessions
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sessions`;
CREATE TABLE  `sessions` (
  `idsessions` int(10) unsigned NOT NULL auto_increment,
  `user_name` varchar(16) collate utf8_bin NOT NULL,
  `started` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `completed` timestamp NOT NULL DEFAULT '2010-01-01 01:01:01',
  `title` varchar(45) collate utf8_bin NOT NULL default 'Default',
  PRIMARY KEY  (`idsessions`),
  UNIQUE KEY `Index_3` USING BTREE (`title`,`user_name`),
  KEY `FK_sessions_1` (`user_name`),
  CONSTRAINT `FK_sessions_1` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `query` User queries per session
-- -----------------------------------------------------
DROP TABLE IF EXISTS `query`;
CREATE TABLE  `query` (
  `idquery` int(10) unsigned NOT NULL auto_increment,
  `idsessions` int(10) unsigned NOT NULL,
  `name` text collate utf8_bin NOT NULL,
  `content` text collate utf8_bin NOT NULL,
  `idtemplate` int(10) unsigned default NULL,
  PRIMARY KEY  (`idquery`),
  UNIQUE KEY `Index_3` USING BTREE (`name`(255),`idsessions`),
  KEY `FK_query_1` (`idsessions`),
  KEY `FK_query_2` (`idtemplate`),
  CONSTRAINT `FK_query_2` FOREIGN KEY (`idtemplate`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_query_1` FOREIGN KEY (`idsessions`) REFERENCES `sessions` (`idsessions`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Structure queries';

-- -----------------------------------------------------
-- Table `query_results` Results of a user query
-- -----------------------------------------------------
DROP TABLE IF EXISTS `query_results`;
CREATE TABLE  `query_results` (
  `idquery` int(10) unsigned NOT NULL,
  `idchemical` int(10) unsigned NOT NULL,
  `idstructure` int(11) unsigned NOT NULL,
  `selected` tinyint(1) NOT NULL default '1',
  `metric` float(10,6) default NULL,
  `text` varchar(200) collate utf8_bin default NULL,
  PRIMARY KEY  (`idquery`,`idchemical`,`idstructure`),
  KEY `FK_query_results_2` (`idstructure`),
  KEY `FK_query_results_3` (`idchemical`),
  KEY `Index_4` USING BTREE (`idquery`,`metric`),
  KEY `Index_5` (`idquery`),
  KEY `Index_6` (`idquery`,`text`),
  CONSTRAINT `FK_query_results_1` FOREIGN KEY (`idquery`) REFERENCES `query` (`idquery`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_query_results_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_query_results_3` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `funcgroups` Functional groups
-- -----------------------------------------------------
DROP TABLE IF EXISTS `funcgroups`;
CREATE TABLE  `funcgroups` (
  `idfuncgroup` int(10) unsigned NOT NULL,
  `name` varchar(45) collate utf8_bin NOT NULL,
  `smarts` blob NOT NULL,
  `user_name` varchar(16) collate utf8_bin default NULL,
  PRIMARY KEY  (`idfuncgroup`),
  UNIQUE KEY `Index_2` (`name`),
  KEY `FK_funcgroups_1` (`user_name`),
  CONSTRAINT `FK_funcgroups_1` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `struc_fgroups` Functional groups per structure
-- -----------------------------------------------------
DROP TABLE IF EXISTS `struc_fgroups`;
CREATE TABLE  `struc_fgroups` (
  `idfuncgroup` int(10) unsigned NOT NULL,
  `idchemical` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  USING BTREE (`idfuncgroup`,`idchemical`),
  KEY `FK_struc_fgroups_1` (`idchemical`),
  CONSTRAINT `FK_struc_fgroups_2` FOREIGN KEY (`idfuncgroup`) REFERENCES `funcgroups` (`idfuncgroup`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_struc_fgroups_1` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `fp1024` 1024 length hashed fingerprints
-- -----------------------------------------------------
DROP TABLE IF EXISTS `fp1024`;
CREATE TABLE  `fp1024` (
  `idchemical` int(10) unsigned NOT NULL default '0',
  `fp1` bigint(20) unsigned NOT NULL default '0',
  `fp2` bigint(20) unsigned NOT NULL default '0',
  `fp3` bigint(20) unsigned NOT NULL default '0',
  `fp4` bigint(20) unsigned NOT NULL default '0',
  `fp5` bigint(20) unsigned NOT NULL default '0',
  `fp6` bigint(20) unsigned NOT NULL default '0',
  `fp7` bigint(20) unsigned NOT NULL default '0',
  `fp8` bigint(20) unsigned NOT NULL default '0',
  `fp9` bigint(20) unsigned NOT NULL default '0',
  `fp10` bigint(20) unsigned NOT NULL default '0',
  `fp11` bigint(20) unsigned NOT NULL default '0',
  `fp12` bigint(20) unsigned NOT NULL default '0',
  `fp13` bigint(20) unsigned NOT NULL default '0',
  `fp14` bigint(20) unsigned NOT NULL default '0',
  `fp15` bigint(20) unsigned NOT NULL default '0',
  `fp16` bigint(20) unsigned NOT NULL default '0',
  `time` int(10) unsigned default '0',
  `bc` int(6) NOT NULL default '0',
  `status` enum('invalid','valid','error') collate utf8_bin NOT NULL default 'invalid',
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `version` int(10) unsigned zerofill NOT NULL default '0000000000',
  PRIMARY KEY  (`idchemical`),
  KEY `fpall` (`fp1`,`fp2`,`fp3`,`fp4`,`fp5`,`fp6`,`fp7`,`fp8`,`fp9`,`fp10`,`fp11`,`fp12`,`fp13`,`fp14`,`fp15`,`fp16`),
  KEY `time` (`time`),
  KEY `status` (`status`),
  CONSTRAINT `fp1024_ibfk_1` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- -----------------------------------------------------
-- Table `fp1024_struc` 1024 length hashed fingerprints
-- -----------------------------------------------------

DROP TABLE IF EXISTS `fp1024_struc`;
CREATE TABLE  `fp1024_struc` (
  `idchemical` int(10) unsigned NOT NULL default '0',
  `idstructure` int(10) unsigned NOT NULL default '0',
  `fp1` bigint(20) unsigned NOT NULL default '0',
  `fp2` bigint(20) unsigned NOT NULL default '0',
  `fp3` bigint(20) unsigned NOT NULL default '0',
  `fp4` bigint(20) unsigned NOT NULL default '0',
  `fp5` bigint(20) unsigned NOT NULL default '0',
  `fp6` bigint(20) unsigned NOT NULL default '0',
  `fp7` bigint(20) unsigned NOT NULL default '0',
  `fp8` bigint(20) unsigned NOT NULL default '0',
  `fp9` bigint(20) unsigned NOT NULL default '0',
  `fp10` bigint(20) unsigned NOT NULL default '0',
  `fp11` bigint(20) unsigned NOT NULL default '0',
  `fp12` bigint(20) unsigned NOT NULL default '0',
  `fp13` bigint(20) unsigned NOT NULL default '0',
  `fp14` bigint(20) unsigned NOT NULL default '0',
  `fp15` bigint(20) unsigned NOT NULL default '0',
  `fp16` bigint(20) unsigned NOT NULL default '0',
  `time` int(10) unsigned default '0',
  `bc` int(6) NOT NULL default '0',
  `status` enum('invalid','valid','error') collate utf8_bin NOT NULL default 'invalid',
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `version` int(10) unsigned zerofill NOT NULL default '0000000000',
  PRIMARY KEY  USING BTREE (`idchemical`,`idstructure`),
  KEY `fpall` (`fp1`,`fp2`,`fp3`,`fp4`,`fp5`,`fp6`,`fp7`,`fp8`,`fp9`,`fp10`,`fp11`,`fp12`,`fp13`,`fp14`,`fp15`,`fp16`),
  KEY `time` (`time`),
  KEY `status` (`status`),
  KEY `fp1024struc_ibfk_2` (`idstructure`),
  CONSTRAINT `fp1024struc_ibfk_1` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fp1024struc_ibfk_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `sk1024` 1024 structural keys
-- -----------------------------------------------------
DROP TABLE IF EXISTS `sk1024`;
CREATE TABLE  `sk1024` (
  `idchemical` int(10) unsigned NOT NULL default '0',
  `fp1` bigint(20) unsigned NOT NULL default '0',
  `fp2` bigint(20) unsigned NOT NULL default '0',
  `fp3` bigint(20) unsigned NOT NULL default '0',
  `fp4` bigint(20) unsigned NOT NULL default '0',
  `fp5` bigint(20) unsigned NOT NULL default '0',
  `fp6` bigint(20) unsigned NOT NULL default '0',
  `fp7` bigint(20) unsigned NOT NULL default '0',
  `fp8` bigint(20) unsigned NOT NULL default '0',
  `fp9` bigint(20) unsigned NOT NULL default '0',
  `fp10` bigint(20) unsigned NOT NULL default '0',
  `fp11` bigint(20) unsigned NOT NULL default '0',
  `fp12` bigint(20) unsigned NOT NULL default '0',
  `fp13` bigint(20) unsigned NOT NULL default '0',
  `fp14` bigint(20) unsigned NOT NULL default '0',
  `fp15` bigint(20) unsigned NOT NULL default '0',
  `fp16` bigint(20) unsigned NOT NULL default '0',
  `time` int(10) unsigned default '0',
  `updated` timestamp NOT NULL default CURRENT_TIMESTAMP,  
  `bc` int(6) NOT NULL default '0',
  `status` enum('invalid','valid','error') collate utf8_bin NOT NULL default 'invalid',
  PRIMARY KEY  (`idchemical`),
  KEY `fpall` (`fp1`,`fp2`,`fp3`,`fp4`,`fp5`,`fp6`,`fp7`,`fp8`,`fp9`,`fp10`,`fp11`,`fp12`,`fp13`,`fp14`,`fp15`,`fp16`),
  KEY `time` (`time`),
  KEY `status` (`status`),
  CONSTRAINT `sk1024_ibfk_1` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- -----------------------------------------------------
-- Table `atom_distance`  Distance between atoms (discretized)
-- -----------------------------------------------------
DROP TABLE IF EXISTS `atom_distance`;
CREATE TABLE  `atom_distance` (
  `iddistance` int(10) unsigned NOT NULL auto_increment,
  `atom1` varchar(2) collate utf8_bin NOT NULL default 'C',
  `atom2` varchar(2) collate utf8_bin NOT NULL default 'C',
  `distance` int(10) NOT NULL default '0',
  PRIMARY KEY  (`iddistance`),
  UNIQUE KEY `atom1` (`atom1`,`atom2`,`distance`),
  KEY `distance` (`distance`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `atom_structure`  Distance between atoms (reference to atom_distance)
-- -----------------------------------------------------
DROP TABLE IF EXISTS `atom_structure`;
CREATE TABLE  `atom_structure` (
  `idstructure` int(11) unsigned NOT NULL default '0',
  `iddistance` int(11) unsigned NOT NULL auto_increment,
  PRIMARY KEY  (`iddistance`,`idstructure`),
  KEY `adistance` (`idstructure`),
  CONSTRAINT `atom_distance_fk_1` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `atom_distance_fk_2` FOREIGN KEY (`iddistance`) REFERENCES `atom_distance` (`iddistance`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- -----------------------------------------------------
-- Table `bookmarks` 
-- -----------------------------------------------------
DROP TABLE IF EXISTS `bookmark`;
CREATE TABLE  `bookmark` (
  `idbookmark` int(10) unsigned NOT NULL auto_increment,
  `creator` varchar(45) collate utf8_bin NOT NULL COMMENT 'dc:creator',
  `recalls` text collate utf8_bin NOT NULL COMMENT 'b:recalls Relates the bookmark with the resource that has been bookmarked. ',
  `hasTopic` varchar(45) collate utf8_bin NOT NULL COMMENT 'b:hasTopic Associates the bookmark with a Topic ',
  `title` varchar(45) collate utf8_bin NOT NULL COMMENT 'dc:title',
  `description` text collate utf8_bin NOT NULL COMMENT 'dc:description',
  `created` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP COMMENT 'a:created The date and time on which the bookmark was created. Format should be YYYY-MM-DDTHH:MM[:SS]TZD (see [DATETIME])',
  `date` timestamp NOT NULL DEFAULT '2010-01-01 01:01:01' COMMENT 'dc:date The date and time on which the bookmark was last modified. Format should be YYYY-MM-DDTHH:MM[:SS]TZD (see [DATETIME])',
  PRIMARY KEY  (`idbookmark`),
  KEY `Index_3` USING BTREE (`hasTopic`),
  KEY `Index_2` USING BTREE (`creator`,`hasTopic`,`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- -----------------------------------------------------
-- Table `version` Version
-- -----------------------------------------------------
DROP TABLE IF EXISTS `version`;
CREATE TABLE  `version` (
  `idmajor` int(5) unsigned NOT NULL,
  `idminor` int(5) unsigned NOT NULL,  
  `date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `comment` varchar(45),
  PRIMARY KEY  (`idmajor`,`idminor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
insert into version (idmajor,idminor,comment) values (3,2,"AMBIT2 schema");

-- -----------------------------------------------------
-- Sorts comma seperated strings
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `sortstring`;

DELIMITER $
CREATE FUNCTION `sortstring`(inString TEXT) RETURNS TEXT DETERMINISTIC
BEGIN
  DECLARE delim CHAR(1) DEFAULT ',';
  DECLARE strings INT DEFAULT 0;     -- number of substrings
  DECLARE forward INT DEFAULT 1;     -- index for traverse forward thru substrings
  DECLARE backward INT;   -- index for traverse backward thru substrings, position in calc. substrings
  DECLARE remain TEXT;               -- work area for calc. no of substrings
  DECLARE swap1 TEXT;                 -- left substring to swap
  DECLARE swap2 TEXT;                 -- right substring to swap
  SET remain = inString;
  SET backward = LOCATE(delim, remain);
  WHILE backward != 0 DO
    SET strings = strings + 1;
    SET backward = LOCATE(delim, remain);
    SET remain = SUBSTRING(remain, backward+1);
  END WHILE;
  IF strings < 2 THEN RETURN inString; END IF;
  REPEAT
    SET backward = strings;
    REPEAT
      SET swap1 = SUBSTRING_INDEX(SUBSTRING_INDEX(inString,delim,backward-1),delim,-1);
      SET swap2 = SUBSTRING_INDEX(SUBSTRING_INDEX(inString,delim,backward),delim,-1);
      IF  swap1 > swap2 THEN
        SET inString = TRIM(BOTH delim FROM CONCAT_WS(delim
        ,SUBSTRING_INDEX(inString,delim,backward-2)
        ,swap2,swap1
        ,SUBSTRING_INDEX(inString,delim,(backward-strings))));
      END IF;
      SET backward = backward - 1;
    UNTIL backward < 2 END REPEAT;
    SET forward = forward +1;
  UNTIL forward + 1 > strings
  END REPEAT;
RETURN inString;
END $
DELIMITER ;

-- -----------------------------------------------------
-- Generates sql given numerical and nominal property for a histogram of a numerical property 
-- -----------------------------------------------------

DROP FUNCTION IF EXISTS `sql_xtab`;
DELIMITER $$

CREATE FUNCTION sql_xtab(property_num VARCHAR(128),property_nom VARCHAR(128), query INT,bins DOUBLE) RETURNS TEXT READS SQL 
DATA 
begin
   set @x="";
   set @@group_concat_max_len=100000;
   select   concat(
                 'select  a-mod(a,',bins,') \"',property_num,'\"\n'
             ,   group_concat(distinct
                     concat(
                         ', sum(','\n'
                     ,   '     if(b=\"',value,'\"\n'
                     ,   '       ,  1','\n'
                     ,   '       ,   0','\n'
                     ,   '       )\n'
                     ,   '     )'
                     ,   ' "',value,'"\n'
                     )
                     order by value
                     separator ''
                 )
,',sum(if(b is null,  1,   0)) "N/A" '
,'from (','\n'
,'		select a,b from','\n'
,'		(','\n'
,'		select value_num as a,idchemical from query_results','\n'
,'		join property_values using(idstructure) join properties using(idproperty)','\n'
,'		where name = "',property_num,'" and idquery=',query,' and value_num is not null\n'
,'		group by idchemical,value_num','\n'
,'		) as X','\n'
,'		left join','\n'
,'		(','\n'
,'		select value as b,idchemical from query_results','\n'
,'		join property_values using(idstructure) join property_string using(idvalue_string) join properties using(idproperty)','\n'
,'		where name = "',property_nom,'" and idquery=',query,'\n'
,'		group by idchemical,value_num','\n'
,'		) as Y','\n'
,'		using(idchemical)','\n'
,') as p','\n'
,'group by a-mod(a,',bins,')','\n'
             ) s
       into @x
    from query_results join property_values using(idstructure) left join property_string using(idvalue_string) join properties using(idproperty)
    where name = property_nom and idquery=query;

    return @x;
end $$

DELIMITER ;

-- -----------------------------------------------------
-- Generates sql given numerical and nominal property for a histogram of a numerical property 
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `sql_dataset_xtab`;
DELIMITER $$

CREATE FUNCTION sql_dataset_xtab(property_num VARCHAR(128),property_nom VARCHAR(128), dataset INT,bins DOUBLE) RETURNS TEXT 
READS SQL DATA 
begin
   DECLARE x TEXT;
   set @@group_concat_max_len=100000;
   select   concat(
                 'select  a-mod(a,',bins,') \"',property_num,'\"\n'
             ,   group_concat(distinct
                     concat(
                         ', sum(','\n'
                     ,   '     if(b=\"',ifnull(text,value),'\"\n'
                     ,   '       ,  1','\n'
                     ,   '       ,   0','\n'
                     ,   '       )\n'
                     ,   '     )'
                     ,   ' "',value,'"\n'
                     )
                     order by ifnull(text,value)
                     separator ''
                 )
,',sum(if(b is null,  1,   0)) "N/A" '
,'from (','\n'
,'		select a,b from','\n'
,'		(','\n'
,'		select value_num as a,idchemical from structure join struc_dataset using(idstructure)','\n'
,'		join property_values using(idstructure) join properties using(idproperty)','\n'
,'		where name = "',property_num,'" and id_srcdataset=',dataset,' and value_num is not null\n'
,'		group by idchemical,value_num','\n'
,'		) as X','\n'
,'		left join','\n'
,'		(','\n'
,'		select ifnull(text,value) as b,idchemical from structure join struc_dataset using(idstructure)','\n'
,'		join property_values using(idstructure) join property_string using(idvalue_string) join properties using(idproperty)','\n'
,'		where name = "',property_nom,'" and id_srcdataset=',dataset,'\n'
,'		group by idchemical,value_num','\n'
,'		) as Y','\n'
,'		using(idchemical)','\n'
,') as p','\n'
,'group by a-mod(a,',bins,')','\n'
             ) s
       into @x
    from struc_dataset join property_values using(idstructure) left join property_string using(idvalue_string) join properties using(idproperty)
    where name = property_nom and id_srcdataset=dataset;

    return @x;
end $$

DELIMITER ;

DROP PROCEDURE IF EXISTS `p_dataset_xtab`;
DELIMITER $$

-- -----------------------------------------------------
-- Generates sql given numerical and nominal property for a histogram of a numerical property 
-- -----------------------------------------------------
CREATE PROCEDURE p_dataset_xtab(IN property_num VARCHAR(128),property_nom VARCHAR(128),q INT,bins DOUBLE)
LANGUAGE SQL
READS SQL DATA 
CONTAINS SQL
SQL SECURITY DEFINER
begin
   set @x="";
   select  sql_dataset_xtab(property_num,property_nom,q,bins) into @x;
   prepare  xtab  from     @x;
   execute  xtab;
   deallocate prepare  xtab;
end $$

DELIMITER ;
-- -----------------------------------------------------
-- Generates cross tab view  given numerical and nominal property
-- -----------------------------------------------------
DROP PROCEDURE IF EXISTS `p_xtab`;
DELIMITER $$

CREATE PROCEDURE p_xtab(IN property_num VARCHAR(128),property_nom VARCHAR(128),q INT,bins DOUBLE)
LANGUAGE SQL
READS SQL DATA 
CONTAINS SQL
SQL SECURITY DEFINER
begin
   set @x="";
   select  sql_xtab(property_num,property_nom,q,bins) into @x;
   prepare  xtab  from     @x;
   execute  xtab;
   deallocate prepare  xtab;
end $$

DELIMITER ;
-- -----------------------------------------------------
-- numeric property values
-- -----------------------------------------------------
DROP VIEW IF EXISTS `values_number`;
create view `values_number` as
SELECT id,idproperty,idstructure,value_num as value,status,user_name
FROM property_values where value_num is not null;

-- -----------------------------------------------------
-- string property values
-- -----------------------------------------------------

DROP VIEW IF EXISTS `values_string`;
create view `values_string` as
SELECT id,idproperty,idstructure,if(status="TRUNCATED",text,value) as value,status,user_name,name
FROM properties join property_values using(idproperty) join property_string using(idvalue_string)
where idvalue_string is not null;

-- -----------------------------------------------------
-- all property values
-- -----------------------------------------------------
DROP VIEW IF EXISTS `values_all`;
create view values_all as
SELECT idstructure,idproperty,name,null as value_string,value_num as value_number,idreference FROM properties join property_values using(idproperty) 
where value_num is not null 
union
SELECT idstructure,idproperty,name,value as value_string,null as value_number,idreference FROM properties join property_values using(idproperty) join property_string using(idvalue_string)
where idvalue_string is not null;

-- -----------------------------------------------------
-- ontology
-- -----------------------------------------------------
DROP VIEW IF EXISTS `ontology`;
create view ontology as
SELECT t1.idtemplate as subjectid,t2.idtemplate as objectid,t1.name as subject,relationship,t2.name as object FROM template as t1 join dictionary as d on t1.idtemplate=d.idsubject join template as t2 on d.idobject=t2.idtemplate;

-- -----------------------------------------------------
-- Template definitions
-- -----------------------------------------------------
DROP VIEW IF EXISTS `template_properties`;
create view template_properties as
SELECT idtemplate,template.name as template,idproperty,properties.name as property FROM template join template_def using(idtemplate) join properties using(idproperty);

-- -----------------------------------------------------
-- default users (guest, admin)
-- -----------------------------------------------------

-- insert into roles (role_name) values ("ambit_guest");
-- insert into roles (role_name) values ("ambit_admin");
-- insert into roles (role_name) values ("quality");
-- insert into users (user_name,password,email,lastname,registration_date,registration_status,keywords,webpage) values ("guest","084e0343a0486ff05530df6c705c8bb4","guest","Default guest user",now(),"confirmed","guest","http://ambit.acad.bg");
-- insert into users (user_name,password,email,lastname,registration_date,registration_status,keywords,webpage) values ("admin","21232f297a57a5a743894a0e4a801fc3","admin","Default admin user",now(),"confirmed","admin","http://ambit.acad.bg");
-- insert into user_roles (user_name,role_name) values ("guest","ambit_guest");
-- insert into user_roles (user_name,role_name) values ("admin","ambit_admin");

-- -----------------------------------------------------
-- default dictionary entries
-- -----------------------------------------------------
insert into catalog_references (idreference,title,url) values (1,"CAS Registry Number","http://www.cas.org");
-- insert into dictionary (iddictionary,idparent,name,idreference) values (null,0,"CAS",1);

insert into catalog_references (idreference,title,url) values (2,"IUPAC name","http://www.iupac.org");
-- insert into dictionary (iddictionary,idparent,name,idreference) values (null,0,"IUPAC Name",2);


-- Grants for ambit2@localhost
-- REVOKE ALL PRIVILEGES ON ambit2.* FROM 'admin'@'localhost';
-- REVOKE ALL PRIVILEGES ON ambit2.* FROM 'guest'@'localhost';
-- GRANT USAGE ON ambit2.* TO 'admin'@'localhost' IDENTIFIED BY PASSWORD '*4ACFE3202A5FF5CF467898FC58AAB1D615029441';
-- GRANT ALL PRIVILEGES ON ambit2.* TO 'admin'@'localhost' WITH GRANT OPTION;
-- GRANT SELECT, INSERT, UPDATE, DELETE, SHOW VIEW ON ambit2.* TO 'guest'@'localhost' IDENTIFIED BY PASSWORD '*11DB58B0DD02E290377535868405F11E4CBEFF58';
-- GRANT EXECUTE ON FUNCTION sortstring TO 'guest'@'localhost'
--- GRANT CREATE TEMPORARY TABLEs on ambit2.* to 'guest'@'%'

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
