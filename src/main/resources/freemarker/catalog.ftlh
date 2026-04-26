<html>
<head>
  <title>Welcome!</title>
</head>
<body>
  <h1>
    Welcome ${user}<#if user == "Big Joe">, our beloved leader</#if>!
  </h1>
  <#list catalog as entity>
      <p>${entity}</p>
  </#list>
</body>
</html>