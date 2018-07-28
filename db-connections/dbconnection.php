<?php

    mysqli_connect("server", "db", "password");

    if (mysqli_connect_error()) {
        
        echo "There was an error connecting to the database";
        
    } else {
        
        echo "Database connection successful!";
        
    }


?>