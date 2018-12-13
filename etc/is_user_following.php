<?php
  require("DBInfo.inc");

// http://0.0.0.0:8080/is_user_following.php?user_id=2&following_user_id=1;

  $query = "select * from following where user_id = '". $_GET['user_id'] ."' and following_user_id = '". $_GET['following_user_id'] ."';";

  
  $result = mysqli_query($connect, $query);
  // var_dump($query);
  // var_dump($result);

  if(! $result)
{ die("Error in query");}

  $userInfo = array();
  while($row=mysqli_fetch_assoc($result)) {
    $userInfo[] = $row;
  }

  if($userInfo) {
    print("{\"msg\":\"success\",\"info\":". json_encode($userInfo) ."}");
  } else {
    print("{\"msg\":\"success\",\"info\":". json_encode($userInfo) ."}");
  }


mysqli_free_result($result);
mysqli_close($connect);

?>
