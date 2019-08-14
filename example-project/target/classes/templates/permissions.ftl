<html>
<head>
    <title>Permissions</title>
</head>
<body>


<style type="text/css">
    .tg {
        border-collapse: collapse;
        border-spacing: 0;
        border-color: #999;
    }

    .tg td {
        font-family: Arial, sans-serif;
        font-size: 14px;
        padding: 10px 5px;
        border-style: solid;
        border-width: 1px;
        overflow: hidden;
        word-break: normal;
        border-color: #999;
        color: #444;
        background-color: #F7FDFA;
    }

    .tg th {
        font-family: Arial, sans-serif;
        font-size: 14px;
        font-weight: normal;
        padding: 10px 5px;
        border-style: solid;
        border-width: 1px;
        overflow: hidden;
        word-break: normal;
        border-color: #999;
        color: #fff;
        background-color: #26ADE4;
    }

    .tg .tg-84g4 {
        font-size: 20px;
        text-align: left;
        vertical-align: top
    }

    .new-select-style select {
        border-radius: 0;
        background: #F7FDFA;
        height: 34px;
        padding: 5px;
        border: 2;
        border-color: #26ADE4;
        font-size: 16px;
        line-height: 1;
        -webkit-appearance: none;
        width: 500px;
    }

    .button {
        background-color: #26ade4;
        border: none;
        color: white;
        padding: 15px 25px;
        text-align: center;
        font-size: 16px;
        cursor: pointer;
    }

    .button:hover {
        background-color: #186f93;
    }

</style>


<h1>Attributes</h1>
<table class="tg">
    <thead>
    <th class="tg-84g4">Attribute Name</th>
    <th class="tg-84g4">id</th>
    <th class="tg-84g4">type</th>
    <th class="tg-84g4">category</th>
    <th class="tg-84g4">multiple</th>
    <th class="tg-84g4">title</th>
    <th class="tg-84g4">Allowable Values</th>
    </thead>
    <tbody>
    <#list attributes?keys as key>
    <tr>
        <td><b>${key}</b></td>
        <td>${attributes[key].id}</td>
        <td>${attributes[key].type}</td>
        <td>${attributes[key].category}</td>
        <td>${attributes[key].multiple?c}</td>
        <td>${(attributes[key].title)!}</td>
        <td><#list attributes[key].allowableValues as value> ${value} , </#list></td>
    </tr>
    </#list>
    </tbody>
</table>


<h1>Resources</h1>
<table class="tg">
    <thead>
    <th class="tg-84g4">Resource Name</th>
    <th class="tg-84g4">ID</th>
    <th class="tg-84g4">Title</th>
    <th class="tg-84g4">Actions</th>
    <th class="tg-84g4">Attributes</th>
    </thead>
    <tbody>
    <#list resources?keys as key>
    <tr>
        <td><b>${key}</b></td>
        <td>${(resources[key].id)!}</td>
        <td>${(resources[key].title)!}</td>
        <td><#list (resources[key].actions)! as value> ${value} , </#list></td>
        <td><#list (resources[key].attributes)! as value> ${value} </br> </#list></td>
    </tr>
    </#list>
    </tbody>
</table>


<h1>Policies</h1>
<table class="tg">
    <thead>
    <th class="tg-84g4">ID</th>
    <th class="tg-84g4">Title</th>
    <th class="tg-84g4">Target</th>
    <th class="tg-84g4">Return Attributes</th>
    <th class="tg-84g4">rules</th>
    </thead>
    <tbody>
    <#list policies as policy>
    <tr>
        <td>${policy.id}</td>
        <td>${policy.title}</td>
        <td>${policy.target}</td>
        <td>${policy.returnAttributes?join(", ")}</td>
        <td>
            <#list policy.rules as rule>
                ID= ${rule.id} </br>
                Title=  ${rule.title} </br>
                Effect= ${rule.effect} </br>
                Operation= ${rule.operation} </br>
                Conditions = <#list rule.conditions as condition>
                </br> &nbsp; &nbsp; &nbsp; ${condition}
            </#list>
            </#list>

        </td>
    </tr>
    </#list>
    </tbody>
</table>

</body>
</html>