<?php

    //setcookie("userId", "1234", time() + 60 * 60 * 24);

    setcookie("useId", "test", time() - 60 * 60);

    // $_COOKIE["userId"] = "test";

    echo $_COOKIE["userId"];

?>