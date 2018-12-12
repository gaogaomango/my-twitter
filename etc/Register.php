<?php
  require("DBInfo.inc");

// http://0.0.0.0:8080/Register.php?first_name=masa&email=masa@gmail.com&password=1234&picture_path=/Users/ogaimasataka/Downloads/IMG_8437.JPG
  $query = "insert into login (first_name, email, password, picture_path) values ('". $_GET['first_name'] ."', '". $_GET['email'] ."', '". $_GET['password'] ."', '". $_GET['picture_path'] ."');";
  // var_dump($query);

  $result = mysqli_query($connect, $query);
  // var_dump($result);

  if(!result) {
    $output = "{\"msg\":\"failed\"}";
  } else {
    $output = "{\"msg\":\"success\"}";
  }

print($output);

mysqli_close($connect);

?>
