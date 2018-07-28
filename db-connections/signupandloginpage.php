<?php

    if (array_key_exists('name', $_POST) OR array_key_exists('password', $_POST)) {
        
        $link = mysqli_connect("server", "dbname", "password");

            if (mysqli_connect_error()) {
        
                die ("There was an error connecting to the database");
        
            } 
        
        
        if ($_POST['name'] == '') {
            
            echo "<p>name is required.</p>";
            
        } else if ($_POST['password'] == '') {
            
            echo "<p>Password is required.</p>";
            
        } else {
            
            $query = "SELECT `id` FROM `users` WHERE name = '".mysqli_real_escape_string($link, $_POST['name'])."'";
            
            $result = mysqli_query($link, $query);
            
            if (mysqli_num_rows($result) > 0) {
                
                echo "<p>That name has already been taken.</p>";
                
            } else {
                
                $query = "INSERT INTO `users` (`name`, `password`) VALUES ('".mysqli_real_escape_string($link, $_POST['name'])."', '".mysqli_real_escape_string($link, $_POST['password'])."')";
                
                if (mysqli_query($link, $query)) {
                    
                    echo "<p>You have been signed up!";
                    
                } else {
                    
                    echo "<p>There was a problem signing you up - please try again later.</p>";
                    
                }
                
            }
            
        }
        
        
    }

    


?>

<form method = "post">

    <input name="name" type="text" placeholder="name">
    
    <input name="password" type="password" placeholder="Password">
    
    <input type="submit" value = "Sign up">

</form>