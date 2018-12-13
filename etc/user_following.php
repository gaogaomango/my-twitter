<?php
  require("DBInfo.inc");

// http://0.0.0.0:8080/user_following.php?user_id=2&hollowing_user_id=1&op=1;
// op=1 for follow, op =2 unfollow

if($_GET['op'] == 1) {
  $query = "insert into following (user_id, following_user_id) values('". $_GET['user_id'] ."','". $_GET['following_user_id'] ."');";
} elseif($_GET['op'] == 2) {
  $query = "delete from following where user_id = '". $_GET['user_id'] ."' and following_user_id = '". $_GET['following_user_id'] ."';";
} else {
    print("{\"msg\":\"failed\"}");
}

  
  $result = mysqli_query($connect, $query);
  // var_dump($query);
  // var_dump($result);

  if(!result) {
    print("{\"msg\":\"failed\"}");
  } else {
    print("{\"msg\":\"success\"}");
  }

mysqli_free_result($result);
mysqli_close($connect);

?>
