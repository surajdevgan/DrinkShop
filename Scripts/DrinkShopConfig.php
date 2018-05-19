<?php
$dbhost = 'localhost';
$dbuser = 'u265299461_suraj';
$dbpass = 'qwertyui';
$db = 'u265299461_atari';

@mysql_connect($dbhost, $dbuser, $dbpass);
@mysql_select_db($db);
?>