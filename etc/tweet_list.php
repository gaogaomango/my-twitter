<?php
  require("DBInfo.inc");

// http://10.0.2.2:8080/tweet_list.php?user_id=14&start_form=1&op=1
// case 1: search op = 3, query = ? , startFrom=20

// TODO: SQL injection
if($_GET['op'] == 1) {
  $query = "select * from user_tweets where user_id in (select following_user_id from following where user_id=". $_GET['user_id'] .") or user_id=". $_GET['user_id'] ." order by tweet_date DESC LIMIT 20 OFFSET ". $_GET['start_form'].";";

} elseif($_GET['op'] == 2) {
  $query = "select * from user_tweets where user_id= ". $_GET['user_id'] ." ORDER BY tweet_date DESC LIMIT 20 OFFSET ". $_GET['start_form'].";";

} elseif($_GET['op'] == 3) {
  $query = "select * from tweets where tweet_text like '%". $_GET['query'] ."%' LIMIT 20 OFFSET ". $_GET['start_form'].";";
} 


  var_dump($query);

  $result = mysqli_query($connect, $query);
  var_dump($result);

  $tweetInfo = array();
  while($row=mysqli_fetch_assoc($result)) {
    $tweetInfo[] = $row;
    break; // to be save;
  }

  if($tweetInfo) {
    print("{\"msg\":\"success\",\"info\":". json_encode($tweetInfo) ."}");
  } else {
    print("{\"msg\":\"failed\"}:\"");
  }

mysqli_free_result($result);
mysqli_close($connect);

?>
