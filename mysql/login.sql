DROP TABLE IF EXISTS `idfatable`;
CREATE TABLE `idfatable` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idfa` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idfa` (`idfa`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Structure for table "roletable"
#

DROP TABLE IF EXISTS `roletable`;
CREATE TABLE `roletable` (
  `roleid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `rolename` varchar(45) NOT NULL,
  PRIMARY KEY (`roleid`),
  KEY `Index_rolename` (`rolename`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Structure for table "uniontable"
#

DROP TABLE IF EXISTS `usertable`;
CREATE TABLE `usertable` (
  `userid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `authuserid` varchar(128) NOT NULL DEFAULT '',
  `userlevel` int(11) NOT NULL DEFAULT '0',
  `authname` varchar(45) NOT NULL DEFAULT '',
  `idfa` varchar(255) DEFAULT NULL,
  `forbiden` int(11) DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`userid`),
  KEY `authuserid` (`authuserid`,`authname`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Function "createrole"
#

DROP FUNCTION IF EXISTS `createrole`;
CREATE FUNCTION `createrole`( vDBIndex INT(11), vRoleName VARCHAR(45) ) RETURNS bigint(20)
BEGIN
    DECLARE tRoleID BIGINT;
    select roleid into tRoleID from roletable where rolename = vRoleName;
    if tRoleID is null then
        INSERT INTO roletable ( rolename ) VALUES ( vRoleName );
        SELECT roleid INTO tRoleID FROM roletable WHERE rolename = vRoleName;
        SET tRoleID = ( vDBIndex << 34 ) | tRoleID;
        UPDATE roletable SET roleid=tRoleID WHERE rolename = vRoleName;
        RETURN tRoleID;
    end if;

    RETURN -1;
END;

DROP FUNCTION IF EXISTS `login`;
CREATE DEFINER=`root`@`localhost` FUNCTION `login`( vDBIndex INT(11), vAuthUserID VARCHAR(45), vAuthName VARCHAR(45), vIDFA VARCHAR(255) ) RETURNS bigint(20)
BEGIN
    DECLARE tUserID BIGINT;    
    SELECT userid INTO tUserID FROM usertable WHERE authuserid = vAuthUserID AND authname = vAuthName;
    if tUserID is null then    
        if vAuthName = 'evo' then
            INSERT INTO usertable ( authuserid, authname, idfa, userlevel, forbiden, reason ) VALUES ( vAuthUserID, vAuthName, vIDFA, 6, 0, "" );            
        else        
            INSERT INTO usertable ( authuserid, authname, idfa, userlevel, forbiden, reason ) VALUES ( vAuthUserID, vAuthName, vIDFA, 0, 0, "" );            
        end if;   

        SELECT userid INTO tUserID FROM usertable WHERE authuserid = vAuthUserID AND authname = vAuthName;
        SET tUserID = ( vDBIndex << 34 ) | tUserID;
        UPDATE usertable SET userid=tUserID WHERE authuserid = vAuthUserID AND authname = vAuthName;
        RETURN tUserID;
    end if;    

    UPDATE usertable SET idfa=vIDFA WHERE authuserid = vAuthUserID AND authname = vAuthName;
    RETURN tUserID;
END;

