<?php

	// $salt = "isefjfehi2736582KUFED"; if you use salt instead

	$row['id'] = 73;

	echo md5(md5($row['id'])."password");

?>