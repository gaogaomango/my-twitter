<?php
  require("DBInfo.inc");

// http://0.0.0.0:8080/Login.php?email=masa@gmail.com&password=1234

  $query = "select * from login where email ='". $_GET['email'] ."' and password='". $_GET['password'] ."';";

  $result = mysqli_query($connect, $query);
  // var_dump($query);
  // var_dump($result);

  if(!result) {
    die('Error cannot run query');
  }

  $userInfo = array();
  while($row=mysqli_fetch_assoc($result)) {
    $userInfo[] = $row;
    break; // to be save;
  }

  if($userInfo) {
    print("{\"msg\":\"success\",\"info\":". json_encode($userInfo) ."}");
  } else {
    print("{\"msg\":\"failed\"}");
  }

mysqli_free_result($result);
mysqli_close($connect);

?>
