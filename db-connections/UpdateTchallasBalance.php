<?php

    $link = mysqli_connect("shareddb1b.hosting.stackcp.net", "cl29-users-bzh", "YTTMcfhK.", "cl29-users-bzh");

    if (mysqli_connect_error()) {
        
        die ("There was an error connecting to the database");
        
    } 

    // $query = "INSERT INTO `users` (`name`, `password`, `balance`) VALUES('T'Challa@gmail.com', 'verysecurehaha',`1000`)";

    $query = "UPDATE `users` SET balance = '900' WHERE name = 'T/'Challa' LIMIT 1";

    mysqli_query($link, $query);

    $query = "SELECT * FROM users";

    if ($result = mysqli_query($link, $query)) {
        
        $row = mysqli_fetch_array($result);
        
        echo "the account of ".$row[1]." now has a balance of ".$row[3];
        
    }


?>