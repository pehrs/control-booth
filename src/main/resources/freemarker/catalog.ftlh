<html>
<head>
  <title>Welcome!</title>
  <style>
th {
  text-align: left;
  border-bottom: 3px solid gray;
  padding-left: 0.3em;
  padding-right: 0.3em;
}
td {
  align: left;
  border: none;
  padding-left: 0.4em;
  padding-right: 0.4em;
}
  </style>
</head>
<body>
  <h1>
    Welcome ${user}<#if user == "Big Joe">, our beloved leader</#if>!
  </h1>
  <table>
    <tr>
      <th>Type</th>
      <th>Variant</th>
      <th>Name</th>
    </tr>
  <#list catalog as entity>
      <tr>
        <td>${entity.entityType}</td>
        <td>${entity.entityVariant}</td>
        <td><a href="#">${entity.name}</a></td>
      </tr>
  </#list>
  </table>
</body>
</html>