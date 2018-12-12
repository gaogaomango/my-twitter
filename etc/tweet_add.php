<?php
  require("DBInfo.inc");

// http://10.0.2.2:8080/tweet_add.php?user_id=16&tweet_text=hogehoge&tweet_picture=/Users/ogaimasataka/Downloads/IMG_8437.JPG

  $query = "insert into tweets(user_id, tweet_text, tweet_picture) values ('". $_GET['user_id'] ."', '". $_GET['tweet_text'] ."', '". $_GET['tweet_picture'] ."');";

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
