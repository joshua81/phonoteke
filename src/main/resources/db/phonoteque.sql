/*
DROP TABLE musicdb.document;
DROP TABLE musicdb.event;

delete from musicdb.document;
delete from musicdb.event;
*/

CREATE TABLE musicdb.document (
  `id` varchar(200) NOT NULL,
  `url` varchar(200) NOT NULL,
  `type` varchar(10) NOT NULL,
  `band` varchar(100) DEFAULT NULL,
  `album` varchar(200) DEFAULT NULL,
  `content` longtext NOT NULL,
  `creation_date` date DEFAULT NULL,
  `cover` varchar(200) NOT NULL,
  `author` varchar(100) NOT NULL,
  `genre` varchar(100) DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  `label` varchar(100) DEFAULT NULL,
  `vote` float DEFAULT NULL,
  `milestone` bit(1) DEFAULT NULL,
  `mbBandId` varchar(100) DEFAULT NULL,
  `mbAlbumId` varchar(100) DEFAULT NULL,
  `spotifyfAlbumId` varchar(100) DEFAULT NULL,
  `amazonAlbumId` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_UNIQUE` (`url`),
  KEY `type_IDX` (`type`),
  KEY `band_IDX` (`band`),
  KEY `album_IDX` (`album`),
  KEY `cdate_IDX` (`creation_date`),
  KEY `author_IDX` (`author`),
  KEY `genre_IDX` (`genre`),
  KEY `year_IDX` (`year`),
  KEY `label_IDX` (`label`),
  KEY `milestone_IDX` (`milestone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE musicdb.event (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mbBandId` varchar(100) DEFAULT NULL,
  `name` varchar(200) NOT NULL,
  `date` date NOT NULL,
  `location` varchar(60) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `band_IDX` (`bandId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
